package Data;

import App.Model.Assignment;
import App.Model.Citizen;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvSaver {

    // บันทึกการจับคู่คนกับที่พักลงไฟล์
    public void saveAssignments(String filePath, List<Assignment> assignments) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Assignment a : assignments) {
                bw.write(a.getCitizenId() + "," + a.getShelterId());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // บันทึกข้อมูลคนทั้งหมดลงไฟล์ รวมสถานะป่วยล่าสุด
    public void saveCitizens(String filePath, List<Citizen> citizens) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            
            bw.write("ID,Name,Age,Health,Type");
            bw.newLine();

            for (Citizen c : citizens) {
                // แปลง boolean กลับเป็น Sick หรือ Healthy
                String healthStatus = c.hasHealthRisk() ? "Sick" : "Healthy";
                
                String line = String.format("%s,%s,%d,%s,%s",
                        c.getId(),
                        c.getName(),
                        c.getAge(),
                        healthStatus,
                        c.getType()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}