package App.View;

import App.Model.Citizen;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RegisterPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public RegisterPanel() {
        setLayout(new BorderLayout());

        String[] columns = {"ID", "Name", "Age", "Risk Group", "Type"};
        
        model = new DefaultTableModel(columns, 0) {

            // กำหนดชนิดข้อมูลของแต่ละคอลัมน์
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (getRowCount() > 0 && getValueAt(0, columnIndex) != null) {
                    return getValueAt(0, columnIndex).getClass();
                }
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        // เรียงลำดับข้อมูล
        table.setAutoCreateRowSorter(true);

        // ใส่ Scrollbar
        JScrollPane scrollPane = new JScrollPane(table);

        add(new JLabel("รายชื่อประชาชนที่ลงทะเบียนทั้งหมด"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // เมธอดสำหรับรับข้อมูลรายคนมาแสดงผลใหม่
    public void updateTable(List<Citizen> citizens) {
        // ล้างข้อมูลเก่าในตารางออกให้หมดก่อน
        model.setRowCount(0);
        
        // ลูปเอาข้อมูลแต่ละคนมาใส่แถวใหม่
        for (Citizen c : citizens) {
            Object[] row = {
                c.getId(),
                c.getName(),
                c.getAge(),
                // แปลงสถานะ boolean เป็นข้อความ
                c.hasHealthRisk() ? "Yes (High Risk)" : "No",
                c.getType()
            };
            model.addRow(row);
        }
    }

    // ดึง ID ของแถวที่เลือกอยู่
    public String getSelectedCitizenId() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return null;
        return table.getValueAt(selectedRow, 0).toString();
    }

    // จับการคลิกเมาส์
    public void addTableMouseListener(java.awt.event.MouseListener listener) {
        this.table.addMouseListener(listener);
    }
    
    // อัปเดตข้อมูลตาราง
    public void updateTableData(List<Citizen> citizens) {
        updateTable(citizens);
    }
}