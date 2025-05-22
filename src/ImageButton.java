import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
        dialog.setSize(517, 500);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Drag and drop an image to the left canvas! ");
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

        // Add Import button only for Custom image type
        if (type == "Custom") {
            JButton importBtn = new JButton("Import");
            importBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Import an Image");
                    
                    // Set to DETAILS_VIEW explicitly
                    try {
                        // Use the default file view
                        fileChooser.setFileView(fileChooser.getUI().getFileView(fileChooser));
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        
                        // Set the view type using UIManager
                        UIManager.put("FileChooser.viewTypeProperty", "detailsView");
                        
                        // Optional: For newer Java versions you can try this instead of reflection
                        fileChooser.putClientProperty("JFileChooser.viewType", "details");
                    } catch (Exception ex) {
                        // Fallback if customization fails
                        System.out.println("Could not set details view: " + ex.getMessage());
                    }
                    
                    // Set preferred size to make it taller than wide
                    fileChooser.setPreferredSize(new Dimension(700, 500));
                    
                    // Set file filter to only show image files
                    fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                        @Override
                        public boolean accept(File f) {
                            if (f.isDirectory()) {
                                return true;
                            }
                            String name = f.getName().toLowerCase();
                            return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                                   name.endsWith(".png") || name.endsWith(".gif");
                        }
                        
                        @Override
                        public String getDescription() {
                            return "Image files (*.jpg, *.jpeg, *.png, *.gif)";
                        }
                    });
                    
                    int result = fileChooser.showOpenDialog(dialog);
                    
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        
                        try {
                            // Get destination folder (custom folder)
                            File customFolder = new File(filePath);
                            if (!customFolder.exists()) {
                                customFolder.mkdirs();
                            }
                            
                            // Create destination file with original name
                            File destFile = new File(customFolder, selectedFile.getName());
                            
                            // Check if file already exists
                            if (destFile.exists()) {
                                int overwrite = JOptionPane.showConfirmDialog(
                                    dialog,
                                    "File already exists. Overwrite?",
                                    "Confirm Overwrite",
                                    JOptionPane.YES_NO_OPTION
                                );
                                
                                if (overwrite != JOptionPane.YES_OPTION) {
                                    return;
                                }
                            }
                            
                            // Copy the file
                            java.nio.file.Files.copy(
                                selectedFile.toPath(),
                                destFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );
                            
                            // Refresh the image dialog to show the new image
                            dialog.dispose();
                            showImages();
                            
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(
                                dialog,
                                "Error importing image: " + ex.getMessage(),
                                "Import Error",
                                JOptionPane.ERROR_MESSAGE
                            );
                            ex.printStackTrace();
                        }
                    }
                }
            });
            btnPanel.add(importBtn);
        }

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
                
                // Create panel with grid layout regardless of image type
                if (files != null) {
                    int fileCount = Math.max(1, files.length); // Ensure at least 1 cell for empty custom folder
                    imagesPanel = new JPanel(new GridLayout(Math.ceilDiv(fileCount, 3), 3, 10, 10));
                    
                    // If files exist, add them to the grid
                    if (files.length > 0) {
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
                            
                            // Add mouse listeners for visual feedback, selection, and drag
                            MouseDragHandler dragHandler = new MouseDragHandler(imageContainer, imagePath, dialog, frame);
                            imageContainer.addMouseListener(dragHandler);
                            imageContainer.addMouseMotionListener(dragHandler);
                            
                            imagesPanel.add(imageContainer);
                        }
                    } 
                    // Handle empty custom folder case
                    else if (type == "Custom") {
                        JLabel emptyFolderLabel = new JLabel("Library is currently empty.");
                        emptyFolderLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imagesPanel.add(emptyFolderLabel);
                    }
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
    
    // New inner class to handle both click and drag-and-drop functionality
    private class MouseDragHandler extends java.awt.event.MouseAdapter {
        private JPanel imageContainer;
        private String imagePath;
        private JDialog dialog;
        private JFrame mainFrame;
        private Point dragStart;
        private JWindow dragImage;
        private boolean isDragging = false;
        
        public MouseDragHandler(JPanel container, String path, JDialog dlg, JFrame frame) {
            imageContainer = container;
            imagePath = path;
            dialog = dlg;
            mainFrame = frame;
        }
        
        @Override
        public void mouseEntered(MouseEvent evt) {
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
        public void mouseExited(MouseEvent evt) {
            // Only remove border if not dragging
            if (!isDragging) {
                imageContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                imageContainer.revalidate();
                imageContainer.repaint();
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            // Keep original click-to-add behavior
            findAndAddImageToLeftCanvas(imagePath);
            dialog.dispose();
        }
        
        @Override
        public void mousePressed(MouseEvent evt) {
            // Store drag start position and show clicking effect
            dragStart = evt.getPoint();
            imageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        }
        
        @Override
        public void mouseReleased(MouseEvent evt) {
            // Handle drag end and drop
            if (isDragging) {
                isDragging = false;
                if (dragImage != null) {
                    dragImage.dispose();
                    dragImage = null;
                }
                
                // Get screen position
                Point screenPos = evt.getLocationOnScreen();
                
                // Find if we're over the left canvas
                LeftCanvas leftCanvas = findLeftCanvas();
                if (leftCanvas != null) {
                    Point canvasPos = leftCanvas.getLocationOnScreen();
                    Rectangle canvasBounds = new Rectangle(
                        canvasPos.x, 
                        canvasPos.y, 
                        leftCanvas.getWidth(), 
                        leftCanvas.getHeight()
                    );
                    
                    // If dropped on the canvas
                    if (canvasBounds.contains(screenPos)) {
                        // Add the image to the canvas at specific position
                        Point relativePos = new Point(
                            screenPos.x - canvasPos.x - 75, // Center image horizontally (150/2)
                            screenPos.y - canvasPos.y - 75  // Center image vertically (150/2)
                        );
                        
                        addImageToCanvasAt(leftCanvas, imagePath, relativePos);
                        dialog.dispose();
                    }
                }
            }
            
            // Reset border to hover state
            imageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        }
        
        @Override
        public void mouseDragged(MouseEvent evt) {
            if (dragStart != null) {
                // Start dragging after moving a bit
                int dragThreshold = 5;
                Point currentPoint = evt.getPoint();
                if (!isDragging && 
                    (Math.abs(currentPoint.x - dragStart.x) > dragThreshold || 
                     Math.abs(currentPoint.y - dragStart.y) > dragThreshold)) {
                    
                    // Begin drag operation
                    isDragging = true;
                    
                    // Create a drag image
                    if (dragImage == null) {
                        dragImage = new JWindow();
                        JLabel dragLabel = new JLabel();
                        
                        Image originalImage = new ImageIcon(imagePath).getImage();
                        Image scaledImage = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        dragLabel.setIcon(new ImageIcon(scaledImage));
                        
                        dragImage.add(dragLabel);
                        dragImage.pack();
                        dragImage.setOpacity(0.7f);
                    }
                }
                
                // Update drag image position if dragging
                if (isDragging && dragImage != null) {
                    Point screenLoc = evt.getLocationOnScreen();
                    dragImage.setLocation(
                        screenLoc.x - (dragImage.getWidth() / 2),
                        screenLoc.y - (dragImage.getHeight() / 2)
                    );
                    dragImage.setVisible(true);
                }
            }
        }
        
        // Helper methods
        private LeftCanvas findLeftCanvas() {
            Component[] components = mainFrame.getContentPane().getComponents();
            for (Component component : components) {
                if (component instanceof JPanel) {
                    JPanel mainPanel = (JPanel) component;
                    Component[] mainPanelComponents = mainPanel.getComponents();
                    for (Component mainPanelComponent : mainPanelComponents) {
                        if (mainPanelComponent instanceof JPanel && 
                            ((JPanel) mainPanelComponent).getComponent(0) instanceof LeftCanvas) {
                            return (LeftCanvas) ((JPanel) mainPanelComponent).getComponent(0);
                        }
                    }
                }
            }
            return null;
        }
        
        private void findAndAddImageToLeftCanvas(String path) {
            LeftCanvas leftCanvas = findLeftCanvas();
            if (leftCanvas != null) {
                leftCanvas.addImage(path);
            }
        }
        
        private void addImageToCanvasAt(LeftCanvas canvas, String path, Point position) {
            // We need to add this method to LeftCanvas
            if (canvas != null) {
                canvas.addImageAt(path, position.x, position.y);
            }
        }
    }
}
