import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private int canvasWidth = 600;
    private int canvasHeight = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    public MainFrame() {
        setTitle("Drawing Studio Pro");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        showDimensionDialog();
        setupCanvases();
        Toolbar tb = new Toolbar(this);
        add(tb, BorderLayout.NORTH); 
    }

    private void showDimensionDialog() {
        JCheckBox defaultCheck = new JCheckBox("Use default size (600x800)");
        JTextField widthField = new JTextField("600");
        JTextField heightField = new JTextField("800");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Width:"));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);
        panel.add(new JLabel(""));
        panel.add(defaultCheck);

        int result = JOptionPane.showConfirmDialog(
            null,
            panel,
            "Set Canvas Dimensions",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            if (!defaultCheck.isSelected()) {
                try {
                    canvasWidth = Integer.parseInt(widthField.getText());
                    canvasHeight = Integer.parseInt(heightField.getText());
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Invalid input! Using default dimensions.");
                }
            }
        }
    }

    private void setupCanvases() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
    mainPanel.setBackground(Color.GRAY);

    LeftCanvas leftCanvas = new LeftCanvas(canvasWidth, canvasHeight);
    RightCanvas rightCanvas = new RightCanvas();

    JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    leftWrapper.setBackground(Color.GRAY);
    leftWrapper.add(leftCanvas);

    JPanel rightWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    rightWrapper.setBackground(Color.GRAY);
    rightWrapper.add(rightCanvas);

    mainPanel.add(leftWrapper);
    mainPanel.add(Box.createRigidArea(new Dimension(10, 0))); // optional spacing
    mainPanel.add(rightWrapper);

    add(mainPanel);
}

}

class CanvasPanel extends JPanel {
    public CanvasPanel(int width, int height) {
        System.out.println("CanvasPanel width = " + width + ", height = " + height);
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }
    
}

// Custom Left Canvas
class LeftCanvas extends CanvasPanel {
    public LeftCanvas(int width, int height) {
        super(width, height);
        System.out.println("left");
    }
}

// Custom Right Canvas with fixed size
class RightCanvas extends CanvasPanel {
    private static final int FIXED_WIDTH = 600;
    private static final int FIXED_HEIGHT = 800;

    public RightCanvas() {
        super(FIXED_WIDTH, FIXED_HEIGHT);
        System.out.println("right");
    }
}


