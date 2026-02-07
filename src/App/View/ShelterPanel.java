package App.View;

import App.Model.Shelter;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ShelterPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ShelterPanel() {
        setLayout(new BorderLayout());
        
        String[] columns = {"ID", "Shelter Name", "Risk Level", "Occupied", "Capacity", "Status"};
        
        model = new DefaultTableModel(columns, 0) {

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
        // เรียงลำดับข้อมูลเมื่อคลิกหัวตาราง
        table.setAutoCreateRowSorter(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        
        add(new JLabel("สถานะศูนย์พักพิงล่าสุด"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // List ข้อมูลศูนย์พักพิงมาอัปเดต
    public void updateTable(List<Shelter> shelters) {
        // ล้างข้อมูลเก่าออก
        model.setRowCount(0);
        
        for (Shelter s : shelters) {
            // คำนวณสถานะข้อความว่าเต็มหรือว่าง
            String status = s.isFull() ? "FULL" : "Available";
            
            Object[] row = {
                s.getShelterId(),
                s.getName(),
                s.getRiskLevel(),
                s.getCurrentOccupancy(),
                s.getMaxCapacity(),
                status
            };
            model.addRow(row);
        }
    }
}