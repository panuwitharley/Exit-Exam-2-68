import App.Controller.EmergencyController;
import App.View.MainFrame;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                setUIFont(new FontUIResource("Tahoma", Font.PLAIN, 14));
            } catch (Exception e) {
                e.printStackTrace();
            }

            MainFrame frame = new MainFrame();
            EmergencyController controller = new EmergencyController(frame);
            controller.run();
        });
    }

    // เปลี่ยน Font ทุก Component ในโปรแกรม
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, f);
            }
        }
    }
}