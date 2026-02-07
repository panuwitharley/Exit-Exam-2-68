package App.Model;

public class Citizen {
    private String id;
    private String name;
    private int age;
    private boolean hasHealthRisk;
    private String type;

    public Citizen(String id, String name, int age, boolean hasHealthRisk, String type) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.hasHealthRisk = hasHealthRisk;
        this.type = type;
    }

    // ดึงข้อมูลและแก้ไขข้อมูล
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getType() { return type; }

    // เช็คความเสี่ยงทางสุขภาพ
    public boolean hasHealthRisk() { 
        return hasHealthRisk; 
    }

    // ตั้งค่าความเสี่ยงหม่
    public void setHasHealthRisk(boolean hasHealthRisk) { 
        this.hasHealthRisk = hasHealthRisk; 
    }
    
    public boolean isSick() {
        return hasHealthRisk;
    }

    @Override
    public String toString() {
        return name + " (" + age + "y)";
    }
}