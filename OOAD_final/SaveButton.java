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
    LeftCanvas leftCanvas;
    RightCanvas rightCanvas;

    public SaveButton(JFrame f, LeftCanvas leftCanvas, RightCanvas rightCanvas) {
        super();
        setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/saveIcon.png"), 30, 30)));
        frame = f;
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
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
                                    saveLeftCanvasAsPNG(SaveButton.this.leftCanvas, frame);
                                } else if ("Save as JPG".equals(selectedFormat)) {
                                    //save as jpg
                                    System.out.println("User choose to save as JPG");
                                    saveLeftCanvasAsJPG(SaveButton.this.leftCanvas, frame);
                                }
                            } else if ("Right canvas".equals(selectedCanvas)) {
                                //export right canvas
                                System.out.println("User choose: Right canvas");
                                if ("Save as PNG".equals(selectedFormat)) {
                                    //save as png
                                    System.out.println("User choose to save as PNG");
                                    saveRightCanvasAsPNG(SaveButton.this.rightCanvas, frame);
                                } else if ("Save as JPG".equals(selectedFormat)) {
                                    //save as jpg
                                    System.out.println("User choose to save as JPG");
                                    saveRightCanvasAsJPG(SaveButton.this.rightCanvas, frame);
                                }
                            }
                            saveDialog.dispose(); // Close the dialog after saving
                            
                        } else {
                            JOptionPane.showMessageDialog(saveDialog,
                                    "Please select a canvas to save and the preferred file type!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }

                    // Separate methods for left canvas (uses original logic)
                    private void saveLeftCanvasAsPNG(LeftCanvas canvas, JFrame parentFrame) {
                        saveLeftCanvasImage(canvas, parentFrame, "png");
                    }

                    private void saveLeftCanvasAsJPG(LeftCanvas canvas, JFrame parentFrame) {
                        saveLeftCanvasImage(canvas, parentFrame, "jpg");
                    }

                    // Separate methods for right canvas (uses custom saveToImage method)
                    private void saveRightCanvasAsPNG(RightCanvas canvas, JFrame parentFrame) {
                        saveRightCanvasImage(canvas, parentFrame, "png");
                    }

                    private void saveRightCanvasAsJPG(RightCanvas canvas, JFrame parentFrame) {
                        saveRightCanvasImage(canvas, parentFrame, "jpg");
                    }

                    // Original method for LeftCanvas
                    private void saveLeftCanvasImage(LeftCanvas canvas, JFrame parentFrame, String format) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save Image");

                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            SwingUtilities.updateComponentTreeUI(fileChooser);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                        UIManager.put("FileChooser.background", new Color(240, 240, 245));
                        UIManager.put("FileChooser.foreground", new Color(50, 50, 50));
                        UIManager.put("FileChooser.selectionBackground", new Color(116, 184, 252));
                        UIManager.put("FileChooser.selectionForeground", Color.WHITE);
                        
                        fileChooser.setPreferredSize(new Dimension(700, 500));

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
                            g2d.setColor(Color.WHITE);
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

                    // NEW method specifically for RightCanvas
                    private void saveRightCanvasImage(RightCanvas canvas, JFrame parentFrame, String format) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save Image");

                        try {
                            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            SwingUtilities.updateComponentTreeUI(fileChooser);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                        UIManager.put("FileChooser.background", new Color(240, 240, 245));
                        UIManager.put("FileChooser.foreground", new Color(50, 50, 50));
                        UIManager.put("FileChooser.selectionBackground", new Color(116, 184, 252));
                        UIManager.put("FileChooser.selectionForeground", Color.WHITE);
                        
                        fileChooser.setPreferredSize(new Dimension(700, 500));

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
                            // Debug output before saving
                            System.out.println("=== SAVE BUTTON DEBUG ===");
                            System.out.println("About to save RightCanvas with " + canvas.toString());
                            canvas.printInstructions(); // This calls the debug method in RightCanvas
                            
                            // Use the custom saveToImage method from RightCanvas
                            BufferedImage image = canvas.saveToImage();
                            
                            // Debug: Print image info
                            System.out.println("Image created with dimensions: " + image.getWidth() + "x" + image.getHeight());
                            
                            ImageIO.write(image, format, file);
                            System.out.println("Image written to file: " + file.getAbsolutePath());
                            System.out.println("=========================");
                            
                            JOptionPane.showMessageDialog(parentFrame, "Image saved successfully to:\n" + file.getAbsolutePath());
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

    // Method to update canvas references when new canvases are created
    public void updateCanvases(LeftCanvas leftCanvas, RightCanvas rightCanvas) {
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
        System.out.println("=== SAVE BUTTON CANVAS UPDATE ===");
        System.out.println("Updated LeftCanvas: " + leftCanvas);
        System.out.println("Updated RightCanvas: " + rightCanvas);
        System.out.println("==================================");
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