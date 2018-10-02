/*
 * Application by Kavin Nguyen
 * 2016
 * This is a basic companion app for 3x3 puzzle cubes
 */
import javax.swing.JFrame;
import javax.swing.UIManager;

public class Main{
    public static void main(String[] args) {
        
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e) {
           // handle exception
        }
        
        CFrame frame = new CFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setVisible(true);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setFocusable(true);
    }
}
