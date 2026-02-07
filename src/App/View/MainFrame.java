package App.View;

import javax.swing.*;

public class MainFrame extends JFrame {
    
    private JTabbedPane tabbedPane;
    private RegisterPanel regPanel;
    private ShelterCommandPanel shelterCmdPanel;
    private ReportPanel reportPanel;

    public MainFrame() {
        // ชื่อโปรแกรม ขนาด 
        setTitle("Emergency Shelter Allocation System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // จัดให้อยู่กลางจอ

        // ตัวจัดการแท็บ
        tabbedPane = new JTabbedPane();

        // หน้าจอส่วนที่ 1 ตารางรายชื่อ
        regPanel = new RegisterPanel();
        tabbedPane.addTab("1. Registration Data", regPanel);

        // หน้าจอส่วนที่ 2 หน้าจัดการคำสั่ง
        shelterCmdPanel = new ShelterCommandPanel();
        tabbedPane.addTab("2. Shelter Command Center", shelterCmdPanel);

        // สร้างหน้าจอส่วนที่ 3 รายงานผล
        reportPanel = new ReportPanel();
        tabbedPane.addTab("3. Smart Analysis Report", reportPanel);

        // แท็บทั้งหมดใส่ลงใน main frame
        add(tabbedPane);
    }

    // ส่งหน้าจอย่อยออกไปให้ Controller ใช้งาน
    public RegisterPanel getRegPanel() { return regPanel; }
    public ShelterCommandPanel getShelterCmdPanel() { return shelterCmdPanel; }
    public ReportPanel getReportPanel() { return reportPanel; }
}