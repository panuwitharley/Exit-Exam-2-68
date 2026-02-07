package App.Model;

import java.util.ArrayList;
import java.util.List;

public class Shelter {
    private String shelterId;
    private String name;
    private int capacity;      // ความจุสูงสุดที่รับได้
    private int riskLevel;     // ระดับความเสี่ยงของพื้นที่
    
    private int currentOccupancy;
    private List<Citizen> occupants;

    public Shelter(String shelterId, String name, int capacity, int riskLevel) {
        this.shelterId = shelterId;
        this.name = name;
        this.capacity = capacity;
        this.riskLevel = riskLevel;
        this.currentOccupancy = 0;
        this.occupants = new ArrayList<>();
    }

    public String getShelterId() { return shelterId; }
    public String getName() { return name; }
    public int getRiskLevel() { return riskLevel; }
    public int getCurrentOccupancy() { return currentOccupancy; }
    public List<Citizen> getOccupants() { return occupants; }

    public int getCapacity() { 
        return capacity; 
    }

    public int getMaxCapacity() {
        return capacity;
    }

    // ตรวจสอบว่าที่พักเต็มหรือยัง
    public boolean isFull() {
        return currentOccupancy >= capacity;
    }

    // เพิ่มคนเข้าพักถ้ายังไม่เต็ม
    public void addOccupant(Citizen c) {
        if (!isFull()) {
            occupants.add(c);
            currentOccupancy++;
        }
    }

    // ลบคนออกจากที่พัก
    public void removeOccupant(String citizenId) {
        for (int i = 0; i < occupants.size(); i++) {
            if (occupants.get(i).getId().equalsIgnoreCase(citizenId)) {
                occupants.remove(i);
                currentOccupancy--;
                break;
            }
        }
    }
    
    @Override
    public String toString() {
        return name + " (Risk: " + riskLevel + ")";
    }
}