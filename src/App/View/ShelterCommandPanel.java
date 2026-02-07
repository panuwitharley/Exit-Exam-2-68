package App.View;

import App.Model.Citizen;
import App.Model.Shelter;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class ShelterCommandPanel extends JPanel {
    
    // ฝั่งซ้าย รายชื่อ shelter
    private JList<String> shelterList;
    private DefaultListModel<String> shelterListModel;
    
    // ตรงกลาง ตารางคนที่เข้าพักแล้ว
    private JTable deployedTable;
    private DefaultTableModel deployedModel;
    private JLabel lblShelterInfo;
    private JProgressBar capacityBar; // หลอดความจุ
    
    // ฝั่งขวา ตารางคนรอ
    private JTable poolTable;
    private DefaultTableModel poolModel;
    
    // ปุ่มย้ายคน
    private JButton btnDeploy;
    private JButton btnUndeploy;

    public ShelterCommandPanel() {
        // แบ่งเป็น 3 ส่วน (ซ้าย, กลาง, ขวา)
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Panel ซ้าย รายชื่อ Shelter
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));
        leftPanel.setBorder(new TitledBorder("รายชื่อศูนย์พักพิง (Shelters)"));
        
        shelterListModel = new DefaultListModel<>();
        shelterList = new JList<>(shelterListModel);
        shelterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shelterList.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        leftPanel.add(new JScrollPane(shelterList), BorderLayout.CENTER);

        // 2. Panel กลาง คนที่เข้าพักแล้ว & ความจุ
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new TitledBorder("ผู้เข้าพักในศูนย์ (Occupants)"));

        // แสดงชื่อศูนย์และ Capacity
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        lblShelterInfo = new JLabel("กรุณาเลือกศูนย์พักพิง...");
        lblShelterInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblShelterInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        capacityBar = new JProgressBar(0, 100);
        capacityBar.setStringPainted(true); // แสดงตัวเลข % หรือข้อความ
        capacityBar.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        infoPanel.add(lblShelterInfo);
        infoPanel.add(capacityBar);
        centerPanel.add(infoPanel, BorderLayout.NORTH);

        // ตารางแสดงคนในศูนย์
        String[] cols = {"ID", "Name", "Risk", "Status"};
        deployedModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        deployedTable = new JTable(deployedModel);
        centerPanel.add(new JScrollPane(deployedTable), BorderLayout.CENTER);

        // 3. Panel ขวา รายชื่อคนรอ
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        rightPanel.setBorder(new TitledBorder("รายการรอดำเนินการ (Waiting List)"));
        
        poolModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        poolTable = new JTable(poolModel);
        rightPanel.add(new JScrollPane(poolTable), BorderLayout.CENTER);

        // 4. Panel ปุ่มกด
        JPanel actionPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        btnUndeploy = new JButton("ย้ายออก (Unassign) >>");
        btnDeploy = new JButton("<< ย้ายเข้า (Deploy)");
        
        actionPanel.add(btnUndeploy);
        actionPanel.add(btnDeploy);
        
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(centerPanel, BorderLayout.CENTER);
        centerContainer.add(actionPanel, BorderLayout.EAST); // ปุ่มอยู่ขวาของตารางกลาง

        add(leftPanel, BorderLayout.WEST);
        add(centerContainer, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    // Getters
    public JList<String> getShelterList() { return shelterList; }
    public JTable getDeployedTable() { return deployedTable; }
    public JTable getPoolTable() { return poolTable; }
    public JButton getBtnDeploy() { return btnDeploy; }
    public JButton getBtnUndeploy() { return btnUndeploy; }

    // อัปเดตรายชื่อ Shelter ใน List ซ้ายมือ
    public void updateShelterList(List<Shelter> shelters) {
        int selected = shelterList.getSelectedIndex();
        shelterListModel.clear();
        
        for(Shelter s : shelters) {
            String label = String.format("%s (Risk: %d) [%d/%d]", 
                s.getName(), s.getRiskLevel(), s.getCurrentOccupancy(), s.getCapacity());
            shelterListModel.addElement(label);
        }
        
        if(selected >= 0 && selected < shelters.size()) {
            shelterList.setSelectedIndex(selected);
        }
    }
    
    // อัปเดตตารางกลาง (Deployed) ตาม Shelter ที่เลือก
    public void updateDeployedView(Shelter s) {
        if(s == null) {
            lblShelterInfo.setText("กรุณาเลือกศูนย์พักพิง");
            capacityBar.setValue(0);
            capacityBar.setString("");
            deployedModel.setRowCount(0);
            return;
        }
        
        // อัปเดต Header และ Progress Bar
        lblShelterInfo.setText(s.getName() + " (Risk Lv." + s.getRiskLevel() + ")");
        capacityBar.setMaximum(s.getCapacity());
        capacityBar.setValue(s.getCurrentOccupancy());
        capacityBar.setString(s.getCurrentOccupancy() + " / " + s.getCapacity());

        // เปลี่ยนสี Bar ถ้าเต็มให้เป็นสีแดง
        if(s.isFull()) capacityBar.setForeground(Color.RED);
        else capacityBar.setForeground(new Color(0, 150, 0)); // สีเขียว

        // เติมข้อมูลลงตาราง
        deployedModel.setRowCount(0);
        for(Citizen c : s.getOccupants()) {
            deployedModel.addRow(new Object[]{
                c.getId(), c.getName(), 
                c.hasHealthRisk() ? "High Risk" : "Normal", 
                "Deployed"
            });
        }
    }
    
    // อัปเดตตารางขวา
    public void updatePoolView(List<Citizen> unassignedList) {
        poolModel.setRowCount(0);
        for(Citizen c : unassignedList) {
            poolModel.addRow(new Object[]{
                c.getId(), c.getName(), 
                c.hasHealthRisk() ? "High Risk" : "Normal", 
                "Waiting"
            });
        }
    }
}