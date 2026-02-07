package Data;

import App.Model.Citizen;
import App.Model.Shelter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvLoader {

    // โหลดข้อมูลคนจาก CSV
    public List<Citizen> loadCitizens(String filePath) {
        List<Citizen> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {
                // แยกข้อมูล
                String[] data = line.split(",");

                // กัน error กรณีบรรทัดนั้นข้อมูลไม่ครบ
                if (data.length < 5) continue;

                // ดึงข้อมูลแต่ละช่องมาตัดช่องว่างทิ้ง
                String id = data[0].trim();
                String name = data[1].trim();
                
                // แปลงข้อความเป็นตัวเลข
                int age = Integer.parseInt(data[2].trim());
                
                // ตรวจสอบสถานะป่วย ถ้าเขียนว่า Sick ให้ป็นจริง
                boolean isSick = data[3].trim().equalsIgnoreCase("Sick");
                
                String type = data[4].trim();

                // สร้าง Citizen 
                list.add(new Citizen(id, name, age, isSick, type));
            }
        } catch (Exception e) {
            // ถ้ามี error ให้ออก console
            e.printStackTrace();
        }
        return list;
    }

    // โหลดข้อมูลศูนย์พักพิงจาก CSV
    public List<Shelter> loadShelters(String filePath) {
        List<Shelter> list = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            br.readLine();
            
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                // เช็คว่าข้อมูลครบ 4 ช่องตามที่ Shelter ต้องการ
                if (data.length < 4) continue;

                String id = data[0].trim();
                String name = data[1].trim();

                // แปลงความจุและระดับความเสี่ยงเป็นตัวเลข
                int capacity = Integer.parseInt(data[2].trim()); 
                int riskLevel = Integer.parseInt(data[3].trim());

                // สร้าง Shelter
                list.add(new Shelter(id, name, capacity, riskLevel));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}