package App.Controller;

import App.Model.*;
import App.View.*;
import Data.CsvLoader;
import Data.CsvSaver;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.*;

public class EmergencyController {
    
    // กำหนดชื่อไฟล์ข้อมูลที่ใช้ในระบบ
    private final String FILE_CITIZENS = "Data/citizens.csv";
    private final String FILE_SHELTERS = "Data/shelters.csv";
    private final String FILE_OUTPUT_STATE = "Data/assignments.csv"; // ไฟล์เก็บว่าใครอยู่ที่ไหน
    private final String FILE_OUTPUT_LOG = "Data/activity_log.csv";       // ไฟล์เก็บประวัติการทำงาน

    private List<Citizen> citizensDB;
    private List<Shelter> sheltersDB;
    private List<Assignment> assignmentsDB;
    
    private CsvLoader dataLoader;
    private CsvSaver dataSaver;
    private MainFrame mainFrame;

    public EmergencyController(MainFrame frame) {
        this.mainFrame = frame;
        this.dataLoader = new CsvLoader();
        this.dataSaver = new CsvSaver(); 
        
        this.citizensDB = new ArrayList<>();
        this.sheltersDB = new ArrayList<>();
        this.assignmentsDB = new ArrayList<>();
        
        setupShelterCommandListeners();
    }
    
    public void run() {
        try {
            // โหลดข้อมูลคนและที่พักจากไฟล์ CSV เข้ามาในหน่วยความจำ
            this.citizensDB = dataLoader.loadCitizens(FILE_CITIZENS);
            this.sheltersDB = dataLoader.loadShelters(FILE_SHELTERS);
        } catch (Exception e) { 
            e.printStackTrace(); 
        }

        // ลบข้อมูลคนที่ซ้ำกันออก
        removeDuplicateCitizens();
        // แสดงข้อมูลในตารางฝั่งซ้าย
        mainFrame.getRegPanel().updateTable(citizensDB);
        
        // ลองโหลดข้อมูลการ assign เดิม
        boolean isLoaded = loadExistingAssignments();

        if (!isLoaded) {
            // ถ้าไม่มีไฟล์เดิม ให้ระบบ auto assign
            System.out.println("ไม่พบข้อมูลเดิม เริ่มต้นจัดสรรอัตโนมัติ");
            allocateLogic(); 
        } else {
            System.out.println("โหลดข้อมูลการจัดสรรเดิมเรียบร้อย");
        }
        
        // บันทึกสถานะล่าสุดและรีเฟรชหน้าจอทั้งหมด
        saveAndRefreshAll();
        
        mainFrame.setVisible(true);
    }

    // อ่านไฟล์ Assignments ที่มีเพื่อดึงคนกลับเข้าที่พัก
    private boolean loadExistingAssignments() {
        File file = new File(FILE_OUTPUT_STATE);
        if (!file.exists()) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            assignmentsDB.clear();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 2) continue;

                String cid = data[0].trim();
                String sid = data[1].trim();
                
                Citizen c = findCitizenById(cid);
                Shelter s = findShelterById(sid);

                // เช็คว่าคนกับที่พักยังอยู่ในระบบมั้ย
                if (c != null && s != null) {
                    s.addOccupant(c);
                    assignmentsDB.add(new Assignment(cid, sid));
                }
            }
            return !assignmentsDB.isEmpty();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // auto assign
    private void allocateLogic() {
        assignmentsDB.clear();
        
        // เรียงลำดับความสำคัญ เด็กและคนแก่ก่อน
        citizensDB.sort((c1, c2) -> {
            boolean p1 = isPriority(c1);
            boolean p2 = isPriority(c2);
            return Boolean.compare(p2, p1); 
        });

        for (Citizen c : citizensDB) {
            for (Shelter s : sheltersDB) {
                // ถ้าเต็มแล้วให้ข้ามไปที่อื่น
                if (s.isFull()) continue;
                
                // ถ้าป่วย ห้ามไปที่ที่มีความเสี่ยงสูง
                if (c.hasHealthRisk() && s.getRiskLevel() > 1) continue;

                // เข้าพักได้
                s.addOccupant(c);
                assignmentsDB.add(new Assignment(c.getId(), s.getShelterId()));
                
                // บันทึก log ว่า auto
                logActivity("AUTO_ASSIGN", c.getId(), s.getShelterId());
                break;
            }
        }
    }

    // จัดการ Event ปุ่มบนจอ
    private void setupShelterCommandListeners() {
        ShelterCommandPanel view = mainFrame.getShelterCmdPanel();
        
        // เลือก Shelter ใน List แสดงรายชื่อคนใน Shelter นั้น
        view.getShelterList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshCenterPanel();
        });

        // ปุ่มเอาคนออก
        view.getBtnUndeploy().addActionListener(e -> {
            int row = view.getDeployedTable().getSelectedRow();
            int sIdx = view.getShelterList().getSelectedIndex();
            
            if(row == -1 || sIdx == -1) {
                JOptionPane.showMessageDialog(mainFrame, "กรุณาเลือกคนที่ต้องการเอาออก");
                return;
            }
            
            String cid = view.getDeployedTable().getValueAt(row, 0).toString().trim();
            Shelter s = sheltersDB.get(sIdx);
            
            // ลบคนออกจากที่พักและออกจากรายการ assignment
            s.removeOccupant(cid);
            assignmentsDB.removeIf(a -> a.getCitizenId().equalsIgnoreCase(cid));
            
            logActivity("UNASSIGN", cid, s.getShelterId());
            saveAndRefreshAll();
        });

        // ปุ่มเอาคนเข้า
        view.getBtnDeploy().addActionListener(e -> {
            int row = view.getPoolTable().getSelectedRow();
            int sIdx = view.getShelterList().getSelectedIndex();
            
            if(row == -1) {
                JOptionPane.showMessageDialog(mainFrame, "กรุณาเลือกคนจากรายการรอ");
                return;
            }
            if(sIdx == -1) {
                JOptionPane.showMessageDialog(mainFrame, "กรุณาเลือกสถานที่พักพิง");
                return;
            }
            
            String cid = view.getPoolTable().getValueAt(row, 0).toString().trim();
            Citizen c = findCitizenById(cid);
            Shelter s = sheltersDB.get(sIdx);
            
            // ตรวจสอบเงื่อนไขก่อนย้ายเข้า
            if(s.isFull()) {
                JOptionPane.showMessageDialog(mainFrame, "ที่พักเต็มแล้ว");
                return;
            }
            if(c.hasHealthRisk() && s.getRiskLevel() > 1) {
                JOptionPane.showMessageDialog(mainFrame, "แจ้งเตือน: ไม่สามารถย้ายผู้ป่วยไปพื้นที่เสี่ยงสูงได้");
                return;
            }
            
            s.addOccupant(c);
            assignmentsDB.add(new Assignment(c.getId(), s.getShelterId()));
            
            logActivity("MANUAL_ASSIGN", c.getId(), s.getShelterId());
            saveAndRefreshAll();
        });
        
        // ดับเบิ้ลคลิกที่ตารางรายชื่อเพื่อเปลี่ยนสถานะสุขภาพ
        mainFrame.getRegPanel().addTableMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable t = (JTable)e.getSource();
                    String cid = t.getValueAt(t.getSelectedRow(), 0).toString();
                    promptHealthUpdate(cid);
                }
            }
        });
    }

    // หน้าต่างถามยืนยันการเปลี่ยนสถานะป่วย
    private void promptHealthUpdate(String citizenId) {
        String[] options = {"Healthy", "SICK", "Cancel"};
        int choice = JOptionPane.showOptionDialog(mainFrame, "อัปเดตสถานะสุขภาพของ " + citizenId, "สถานะสุขภาพ",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0 || choice == 1) {
            Citizen c = findCitizenById(citizenId);
            if(c != null) {
                boolean isSick = (choice == 1);
                c.setHasHealthRisk(isSick);
                
                // แจ้งเตือนถ้าป่วยแล้วดันอยู่ในที่เสี่ยง
                Assignment a = findAssignmentByCitizenId(citizenId);
                if(a != null && isSick) {
                     Shelter s = findShelterById(a.getShelterId());
                     if(s.getRiskLevel() > 1) {
                         JOptionPane.showMessageDialog(mainFrame, "คำเตือน: ผู้ป่วยอยู่ในพื้นที่เสี่ยงสูง");
                     }
                }
                
                logActivity("UPDATE_HEALTH", c.getId(), isSick ? "SICK" : "HEALTHY");
                mainFrame.getRegPanel().updateTable(citizensDB);
                saveAndRefreshAll();
            }
        }
    }
    
    // บันทึกประวัติการทำงานลงไฟล์ text แบบต่อท้ายไปเรื่อยๆ
    private void logActivity(String action, String citizenId, String detail) {
        try (FileWriter fw = new FileWriter(FILE_OUTPUT_LOG, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.println(timeStamp + "," + action + "," + citizenId + "," + detail);
            
        } catch (IOException e) {
            System.err.println("เขียน Log ไม่สำเร็จ: " + e.getMessage());
        }
    }

    // รีเฟรชข้อมูลในตารางกลาง
    private void refreshCenterPanel() {
        ShelterCommandPanel view = mainFrame.getShelterCmdPanel();
        int idx = view.getShelterList().getSelectedIndex();
        if(idx >= 0) view.updateDeployedView(sheltersDB.get(idx));
        else view.updateDeployedView(null);
    }
    
    // รีเฟรชข้อมูลตารางขวา เฉพาะคนที่ยังไม่มีที่พัก
    private void refreshRightPanel() {
        Set<String> assignedIds = assignmentsDB.stream()
            .map(a -> a.getCitizenId().trim().toUpperCase())
            .collect(Collectors.toSet());
            
        List<Citizen> unassigned = new ArrayList<>();
        for(Citizen c : citizensDB) {
            if(!assignedIds.contains(c.getId().trim().toUpperCase())) {
                unassigned.add(c);
            }
        }
        mainFrame.getShelterCmdPanel().updatePoolView(unassigned);
    }

    // บันทึกข้อมูลทุกอย่างและอัปเดตหน้าจอ
    private void saveAndRefreshAll() {
        // บันทึกว่าใครอยู่ที่ไหน
        dataSaver.saveAssignments(FILE_OUTPUT_STATE, assignmentsDB);
        
        // บันทึกข้อมูลคนที่มีการแก้ไขสถานะป่วยกลับ csv
        dataSaver.saveCitizens(FILE_CITIZENS, citizensDB);
        
        mainFrame.getShelterCmdPanel().updateShelterList(sheltersDB);
        refreshCenterPanel();
        refreshRightPanel();
        generateReport();
    }
    
    // สรุปคนได้ที่พักกับคนไม่ได้ที่พัก
    private void generateReport() {
        List<Citizen> success = new ArrayList<>();
        List<Citizen> failed = new ArrayList<>();
        Set<String> assignedIds = assignmentsDB.stream().map(Assignment::getCitizenId).collect(Collectors.toSet());
        for (Citizen c : citizensDB) {
            if (assignedIds.contains(c.getId())) success.add(c);
            else failed.add(c);
        }
        mainFrame.getReportPanel().updateReport(success, failed);
    }
    
    private void removeDuplicateCitizens() {
        Set<String> ids = new HashSet<>();
        List<Citizen> unique = new ArrayList<>();
        for (Citizen c : citizensDB) {
            if (ids.add(c.getId())) unique.add(c);
        }
        this.citizensDB = unique;
    }

    private boolean isPriority(Citizen c) { 
        return c.getAge() < 15 || c.getAge() > 60; 
    }
    
    private Citizen findCitizenById(String id) { 
        return citizensDB.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst().orElse(null); 
    }
    private Shelter findShelterById(String id) { 
        return sheltersDB.stream().filter(s -> s.getShelterId().equalsIgnoreCase(id)).findFirst().orElse(null); 
    }
    private Assignment findAssignmentByCitizenId(String cid) { 
        return assignmentsDB.stream().filter(a -> a.getCitizenId().equalsIgnoreCase(cid)).findFirst().orElse(null); 
    }
}