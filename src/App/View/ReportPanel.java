package App.View;

import App.Model.Citizen;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel {
    
    private JTable reportTable;
    private DefaultTableModel tableModel;
    
    // ส่วนแสดงผล Dashboard สรุป (Total, Success, Failed)
    private JLabel lblTotal, lblSuccess, lblFailed;

    public ReportPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // ส่วนที่ 1 Dashboard ด้านบน (Smart Stats) 
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setPreferredSize(new Dimension(0, 80)); // กำหนดความสูง
        
        // สร้างการ์ดตัวเลข 3 ช่อง
        lblTotal = createStatCard("Total Citizens", Color.BLUE);
        lblSuccess = createStatCard("Safe & Sheltered", new Color(0, 150, 0)); // สีเขียว
        lblFailed = createStatCard("Failed / Issues", Color.RED); // สีแดง
        
        statsPanel.add(lblTotal);
        statsPanel.add(lblSuccess);
        statsPanel.add(lblFailed);
        
        add(statsPanel, BorderLayout.NORTH);

        // ส่วนที่ 2 ตารางแสดงข้อมูลด้านล่าง
        String[] cols = {"ID", "Name", "Age", "Type", "Health Risk", "Status", "Message"};
        tableModel = new DefaultTableModel(cols, 0) {
             @Override
             public boolean isCellEditable(int row, int column) { return false; }
        };
        reportTable = new JTable(tableModel);

        // เปลี่ยนสีตัวหนังสือในตารางตามสถานะ (เขียว/แดง)
        reportTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // เช็คสถานะจาก Column Status
                String status = (String) table.getModel().getValueAt(row, 5);
                
                if ("ASSIGNED".equals(status)) {
                    c.setForeground(new Color(0, 120, 0)); // สีเขียวเข้ม
                } else {
                    c.setForeground(Color.RED); // สีแดง
                }
                return c;
            }
        });

        add(new JScrollPane(reportTable), BorderLayout.CENTER);
    }
    
    // ฟังก์ชันช่วยสร้างการ์ดตัวเลข
    private JLabel createStatCard(String title, Color color) {
        JLabel lbl = new JLabel("<html><center>" + title + "<br><font size=6>0</font></center></html>");
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(color, 2));
        lbl.setForeground(color);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        return lbl;
    }

    // รับข้อมูลมาแสดงผล
    public void updateReport(List<Citizen> successList, List<Citizen> failedList) {
        tableModel.setRowCount(0);
        
        // 1. ใส่ข้อมูลคนได้ที่พัก
        for(Citizen c : successList) {
            tableModel.addRow(new Object[]{
                c.getId(), c.getName(), c.getAge(), c.getType(),
                c.hasHealthRisk() ? "Yes" : "No",
                "ASSIGNED", "Safe in Shelter"
            });
        }

        // 2. ใส่ข้อมูลคนไม่ได้ที่พัก
        for(Citizen c : failedList) {
            String issue = "Full Capacity";
            if(c.hasHealthRisk()) issue = "Risk Mismatch (Need Safer Place)";
            
            tableModel.addRow(new Object[]{
                c.getId(), c.getName(), c.getAge(), c.getType(),
                c.hasHealthRisk() ? "Yes" : "No",
                "FAILED", issue
            });
        }
        
        // 3. อัปเดตตัวเลข Dashboard 
        int total = successList.size() + failedList.size();
        lblTotal.setText("<html><center>Total Citizens<br><font size=6>" + total + "</font></center></html>");
        lblSuccess.setText("<html><center>Safe & Sheltered<br><font size=6>" + successList.size() + "</font></center></html>");
        lblFailed.setText("<html><center>Failed / Issues<br><font size=6>" + failedList.size() + "</font></center></html>");
    }
}