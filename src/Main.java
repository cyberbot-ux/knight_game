import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Knight's Tour");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 720); // extra height for title bar padding

            program_manager manager = new program_manager();
            frame.add(manager.getBoard());
            /// //
            frame.setVisible(true);
        });
    }
}
