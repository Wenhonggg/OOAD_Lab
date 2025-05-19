import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ImageButton extends JButton {
    private JFrame frame;
    private String type;
    private String filePath;

    public ImageButton(JFrame f, int type) {
        super();
        frame = f;
        
        String projectRoot = System.getProperty("user.dir");
        
        switch (type) {
            case 0:
                this.type = "Animal";
                filePath = projectRoot + File.separator + "assets" + File.separator + "animals";
                break;
            case 1:
                this.type = "Flower";
                filePath = projectRoot + File.separator + "assets" + File.separator + "flowers";
                break;
            case 2:
                this.type = "Custom";
                filePath = projectRoot + File.separator + "assets" + File.separator + "custom";
                break;
            default:
                break;
        }
        
        setText(this.type);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showImages();
            }
        });
    }

    private void showImages() {
        JDialog dialog = new JDialog(frame, "Insert Image");
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Click on an image to add it to the canvas!");
        panel.add(label, BorderLayout.NORTH);

        // placeholder while images are loading
        JLabel loadingLabel = new JLabel("Loading images...", SwingConstants.CENTER);
        panel.add(loadingLabel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);

        // load images in a background thread
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            JPanel imagesPanel;

            @Override
            protected Void doInBackground() {
                File folder = new File(filePath);
                File[] files = folder.listFiles();
                if (files != null) {
                    int fileCount = files.length;

                    imagesPanel = new JPanel(new GridLayout(Math.ceilDiv(fileCount, 3), 3, 10, 10));
                    for (File file : files) {
                        final String imagePath = file.getAbsolutePath();
                        Image originalImage = new ImageIcon(imagePath).getImage();
                        Image scaledImage = originalImage.getScaledInstance(132, 132, java.awt.Image.SCALE_SMOOTH);
                        
                        // Create a panel to contain the image and add a border on hover
                        JPanel imageContainer = new JPanel(new BorderLayout());
                        imageContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                        
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imageContainer.add(imageLabel, BorderLayout.CENTER);
                        
                        // Add mouse listeners for visual feedback and selection
                        imageContainer.addMouseListener(new java.awt.event.MouseAdapter() {
                            @Override
                            public void mouseEntered(java.awt.event.MouseEvent evt) {
                                // Change border on hover to highlight selection
                                imageContainer.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
                                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                                ));
                                imageContainer.setCursor(new Cursor(Cursor.HAND_CURSOR));
                                imageContainer.revalidate();
                                imageContainer.repaint();
                            }
                            
                            @Override
                            public void mouseExited(java.awt.event.MouseEvent evt) {
                                // Remove border when not hovering
                                imageContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                                imageContainer.revalidate();
                                imageContainer.repaint();
                            }
                            
                            @Override
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                // Find the left canvas and add the image to it
                                Component[] components = frame.getContentPane().getComponents();
                                for (Component component : components) {
                                    if (component instanceof JPanel) {
                                        JPanel mainPanel = (JPanel) component;
                                        Component[] mainPanelComponents = mainPanel.getComponents();
                                        for (Component mainPanelComponent : mainPanelComponents) {
                                            if (mainPanelComponent instanceof JPanel && 
                                                ((JPanel) mainPanelComponent).getComponent(0) instanceof LeftCanvas) {
                                                LeftCanvas leftCanvas = (LeftCanvas) ((JPanel) mainPanelComponent).getComponent(0);
                                                leftCanvas.addImage(imagePath);
                                                dialog.dispose();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                            
                            @Override
                            public void mousePressed(java.awt.event.MouseEvent evt) {
                                // Show "clicking" effect with a darker border
                                imageContainer.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(0, 86, 179), 3),
                                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                                ));
                            }
                            
                            @Override
                            public void mouseReleased(java.awt.event.MouseEvent evt) {
                                // Return to hover state after click is released
                                imageContainer.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
                                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                                ));
                            }
                        });
                        
                        imagesPanel.add(imageContainer);
                    }
                }
                if (type == "Custom" && files.length == 0) {
                    imagesPanel = new JPanel(new BorderLayout());
                    JLabel emptyFolderLabel = new JLabel("Library is currently empty.");
                    emptyFolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imagesPanel.add(emptyFolderLabel, BorderLayout.CENTER);
                    JButton importBtn = new JButton("Import");
                    importBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // TODO allow user to actually import image from local directory
                            System.out.println("Importing image");
                        };
                    });
                    JPanel importBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    importBtnPanel.add(importBtn);
                    imagesPanel.add(importBtnPanel, BorderLayout.NORTH);
                }
                return null;
            }

            @Override
            protected void done() {
                panel.remove(loadingLabel);
                if (imagesPanel != null) {
                    JScrollPane scrollPane = new JScrollPane(imagesPanel);
                    scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));
                    
                    // Increase scrolling speed
                    scrollPane.getVerticalScrollBar().setUnitIncrement(16);
                    scrollPane.getVerticalScrollBar().setBlockIncrement(128);
                    scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
                    scrollPane.getHorizontalScrollBar().setBlockIncrement(128);
                    
                    panel.add(scrollPane, BorderLayout.CENTER);
                } else {
                    JLabel errorLabel = new JLabel("Failed to load images", SwingConstants.CENTER);
                    panel.add(errorLabel, BorderLayout.CENTER);
                }
                panel.revalidate();
                panel.repaint();
            }
        };
        worker.execute();
    }
}
