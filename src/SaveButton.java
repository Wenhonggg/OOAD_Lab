
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;
import java.util.Enumeration;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Color;


public class SaveButton extends JButton {
    JFrame frame;
    //LeftCanvas leftCanvas;
    //RightCanvas rightCanvas;

    public SaveButton(JFrame f, LeftCanvas leftCanvas, RightCanvas rightCanvas) {
        super();
        setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/saveIcon.png"), 30, 30)));
        frame = f;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog saveDialog = new JDialog(frame, "Save drawing");
                saveDialog.setSize(300, 200);
                saveDialog.setLocationRelativeTo(frame);
                JPanel contentPanel = new JPanel(new BorderLayout());
                JPanel optionsPanel = new JPanel();

                ButtonGroup canvasBtnGrp = new ButtonGroup();
                JPanel chooseCanvasPanel = createBtnPanel("Choose a canvas to save:", canvasBtnGrp, "Left canvas",
                        "Right canvas");
                ButtonGroup fileTypeBtnGrp = new ButtonGroup();
                JPanel chooseFileTypePanel = createBtnPanel("Choose a file type:", fileTypeBtnGrp, "Save as PNG",
                        "Save as JPG");

                optionsPanel.add(chooseCanvasPanel);
                optionsPanel.add(chooseFileTypePanel);

                JPanel okPanel = new JPanel();
                okPanel.setSize(100, 30);
                JButton okBtn = new JButton("OK");
                
                okBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (canvasBtnGrp.getSelection() != null && fileTypeBtnGrp.getSelection() != null) {
                            // TODO replace with actual saving logic
                            //System.out.println("Artwork is being saved...");
                            //saveDialog.dispose();
                            String selectedCanvas = null;
                            for (Enumeration<AbstractButton> buttons = canvasBtnGrp.getElements(); buttons.hasMoreElements(); ) {
                                AbstractButton button = buttons.nextElement();
                                if (button.isSelected()) {
                                    selectedCanvas = button.getText(); // "Left canvas" or "Right canvas"
                                    break;
                                }
                            }

                            String selectedFormat = null;
                            for (Enumeration<AbstractButton> buttons = fileTypeBtnGrp.getElements(); buttons.hasMoreElements(); ) {
                                AbstractButton button = buttons.nextElement();
                                if (button.isSelected()) {
                                    selectedFormat = button.getText(); // "Save as PNG" or "Save as JPG"
                                    break;
                                }
                            }

                            if ("Left canvas".equals(selectedCanvas)) {
                                //export left canvas
                                System.out.println("User choose: Left canvas");
                                if ("Save as PNG".equals(selectedFormat)) {
                                    //save as png
                                    System.out.println("User choose to save as PNG");
                                    saveCanvasAsPNG(leftCanvas, frame);
                                } else if ("Save as JPG".equals(selectedFormat)) {
                                    //save as jpg
                                    System.out.println("User choose to save as JPG");
                                    saveCanvasAsJPG(leftCanvas, frame);
                                }
                            } else if ("Right canvas".equals(selectedCanvas)) {
                                //export right canvas
                                System.out.println("User choose: Right canvas");
                                if ("Save as PNG".equals(selectedFormat)) {
                                    //save as png
                                    System.out.println("User choose to save as PNG");
                                    saveCanvasAsPNG(rightCanvas, frame);
                                } else if ("Save as JPG".equals(selectedFormat)) {
                                    //save as jpg
                                    System.out.println("User choose to save as JPG");
                                    saveCanvasAsJPG(rightCanvas, frame);
                                }
                            }
                            
                        } else {
                            JOptionPane.showMessageDialog(saveDialog,
                                    "Please select a canvas to save and the preferred file type!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }

                    private void saveCanvasAsPNG(JPanel canvas, JFrame parentFrame) {
                        saveCanvasImage(canvas, parentFrame, "png");
                    }

                    private void saveCanvasAsJPG(JPanel canvas, JFrame parentFrame) {
                        saveCanvasImage(canvas, parentFrame, "jpg");
                    }

                    private void saveCanvasImage(JPanel canvas, JFrame parentFrame, String format) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save Image");

                        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            format.toUpperCase() + " Image (*." + format + ")", format);
                        fileChooser.setFileFilter(filter);

                        int userSelection = fileChooser.showSaveDialog(parentFrame);
                        if (userSelection != JFileChooser.APPROVE_OPTION) return;

                        File file = fileChooser.getSelectedFile();
                        if (!file.getName().toLowerCase().endsWith("." + format)) {
                            file = new File(file.getAbsolutePath() + "." + format);
                        }

                        try {
                            Dimension size = canvas.getSize();
                            if (size.width <= 0 || size.height <= 0) {
                                size = canvas.getPreferredSize();
                            }

                            BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2d = image.createGraphics();
                            g2d.setColor(Color.WHITE); // optional background color
                            g2d.fillRect(0, 0, size.width, size.height);
                            canvas.paint(g2d);
                            g2d.dispose();

                            ImageIO.write(image, format, file);
                            JOptionPane.showMessageDialog(parentFrame, "Image saved successfully.");
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(parentFrame, "Failed to save image: " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                okPanel.add(okBtn);
                contentPanel.add(optionsPanel, BorderLayout.CENTER);
                contentPanel.add(okPanel, BorderLayout.SOUTH);
                saveDialog.add(contentPanel);
                saveDialog.setVisible(true);
            }
        });
    }

    private JPanel createBtnPanel(String labelText, ButtonGroup grp, String text1, String text2) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JRadioButton btn1 = new JRadioButton(text1);
        JRadioButton btn2 = new JRadioButton(text2);
        JLabel label = new JLabel(labelText);
        grp.add(btn1);
        grp.add(btn2);

        JPanel btnPanel = new JPanel();
        btnPanel.add(btn1);
        btnPanel.add(btn2);

        label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        btnPanel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(btnPanel);
        return panel;
    }
}
