import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private int canvasWidth = 650;
    private int canvasHeight = 730;
    private LeftCanvas leftCanvas; // Add this field

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }

    public MainFrame() {
        setTitle("Drawing Studio Pro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showDimensionDialog();
        setupCanvases();
        pack();
        setLocationRelativeTo(null);
    }

    private void showDimensionDialog() {
        JCheckBox defaultCheck = new JCheckBox("Use default size (650x730)");
        JTextField widthField = new JTextField("650");
        JTextField heightField = new JTextField("730");

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Width (max:650):"));
        panel.add(widthField);
        panel.add(new JLabel("Height (max:730):"));
        panel.add(heightField);
        panel.add(new JLabel(""));
        panel.add(defaultCheck);

        int result = JOptionPane.showConfirmDialog(
            null, panel, "Set Left Canvas Dimensions",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION && !defaultCheck.isSelected()) {
            try {
                int inputWidth = Integer.parseInt(widthField.getText());
                int inputHeight = Integer.parseInt(heightField.getText());

                if (inputWidth > canvasWidth || inputHeight > canvasHeight) {
                    JOptionPane.showMessageDialog(this, "Your input is too large. Using default size");
                } else {
                    canvasWidth = inputWidth;
                    canvasHeight = inputHeight;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input! Using default size.");
            }
        }
    }

    private void setupCanvases() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(Color.GRAY);

        leftCanvas = new LeftCanvas(canvasWidth, canvasHeight); // Store reference
        DrawingToolPanel dtp = new DrawingToolPanel(this);
        RightCanvas rightCanvas = new RightCanvas(dtp);

        JPanel leftWrapper = new JPanel(new GridBagLayout());
        leftWrapper.setBackground(Color.GRAY);
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.weightx = 1.0;
        gbcLeft.weighty = 1.0;
        gbcLeft.anchor = GridBagConstraints.CENTER;
        leftWrapper.add(leftCanvas, gbcLeft);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setBackground(Color.GRAY);
        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.gridx = 0;
        gbcRight.gridy = 0;
        gbcRight.weightx = 1.0;
        gbcRight.weighty = 1.0;
        gbcRight.anchor = GridBagConstraints.CENTER;
        rightWrapper.add(rightCanvas, gbcRight);

        mainPanel.add(leftWrapper);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        mainPanel.add(rightWrapper);

        add(mainPanel);
        
        // Add toolbar after canvases are created
        Toolbar tb = new Toolbar(this, leftCanvas, rightCanvas); // Pass canvases to toolbar
        add(tb, BorderLayout.NORTH);

        tb.createNewRightCanvas();
    }
    
    // Add getter method
    public LeftCanvas getLeftCanvas() {
        return leftCanvas;
    }

    // Add this method to update the reference
    public void setLeftCanvas(LeftCanvas newCanvas) {
        this.leftCanvas = newCanvas;
    }
}