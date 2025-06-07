import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public abstract class ImageButton extends JButton {
    protected JFrame frame;
    protected String type;
    protected String filePath;

    public ImageButton(JFrame f) {
        super();
        frame = f;
        initialize();
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showImages();
            }
        });
    }

    protected abstract void initialize();
    protected abstract void performSpecialAction(Object canvas, Object image);
    protected abstract String getActionHint();
    protected abstract int getActionKeyModifier();

    protected void showImages() {
        JDialog dialog = new JDialog(frame, "Insert Image");
        dialog.setSize(625, 625);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel label = new JLabel("Drag and drop an image to the left canvas! ");
        panel.add(label, BorderLayout.NORTH);

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

        if ("Custom".equals(type)) {
            JButton importBtn = new JButton("Import");
            importBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Import an Image");
                    
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
                            File customFolder = new File(filePath);
                            if (!customFolder.exists()) {
                                customFolder.mkdirs();
                            }
                            
                            File destFile = new File(customFolder, selectedFile.getName());
                            
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
                            
                            java.nio.file.Files.copy(
                                selectedFile.toPath(),
                                destFile.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING
                            );
                            
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

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            JPanel imagesPanel;

            @Override
            protected Void doInBackground() {
                File folder = new File(filePath);
                File[] files = folder.listFiles();
                
                if (files != null) {
                    int fileCount = Math.max(1, files.length);
                    imagesPanel = new JPanel(new GridLayout(Math.ceilDiv(fileCount, 3), 3, 10, 10));
                    
                    if (files.length > 0) {
                        for (File file : files) {
                            if (file.isFile()) {
                                String imagePath = file.getAbsolutePath();
                                
                                if (imagePath.toLowerCase().endsWith(".jpg") || 
                                    imagePath.toLowerCase().endsWith(".jpeg") || 
                                    imagePath.toLowerCase().endsWith(".png") || 
                                    imagePath.toLowerCase().endsWith(".gif")) {
                                    
                                    JPanel imageContainer = createImageContainer(imagePath);
                                    MouseDragHandler handler = new MouseDragHandler(imageContainer, imagePath, dialog, frame);
                                    imageContainer.addMouseListener(handler);
                                    imageContainer.addMouseMotionListener(handler); 
                                    imagesPanel.add(imageContainer);
                                }
                            }
                        }
                    } 
                    else if ("Custom".equals(type)) {
                        JLabel emptyLabel = new JLabel("No custom images. Import some!", SwingConstants.CENTER);
                        emptyLabel.setPreferredSize(new Dimension(150, 150));
                        imagesPanel.add(emptyLabel);
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
    
    protected JPanel createImageContainer(String imagePath) {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        
        container.add(imageLabel, BorderLayout.CENTER);
        
        String fileName = new File(imagePath).getName();
        if (fileName.length() > 15) {
            fileName = fileName.substring(0, 12) + "...";
        }
        JLabel nameLabel = new JLabel(fileName, SwingConstants.CENTER);
        container.add(nameLabel, BorderLayout.SOUTH);
        
        return container;
    }
    
    protected class MouseDragHandler extends java.awt.event.MouseAdapter {
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
            if (!isDragging) {
                imageContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                imageContainer.revalidate();
                imageContainer.repaint();
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent evt) {
            findAndAddImageToLeftCanvas(imagePath);
            dialog.dispose();
        }
        
        @Override
        public void mousePressed(MouseEvent evt) {
            dragStart = evt.getPoint();
            imageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 86, 179), 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        }
        
        @Override
        public void mouseReleased(MouseEvent evt) {
            if (isDragging) {
                isDragging = false;
                if (dragImage != null) {
                    dragImage.dispose();
                    dragImage = null;
                }
                
                Point screenPos = evt.getLocationOnScreen();
                LeftCanvas leftCanvas = findLeftCanvas();
                if (leftCanvas != null) {
                    Point canvasPos = leftCanvas.getLocationOnScreen();
                    Rectangle canvasBounds = new Rectangle(
                        canvasPos.x, 
                        canvasPos.y, 
                        leftCanvas.getWidth(), 
                        leftCanvas.getHeight()
                    );
                    
                    if (canvasBounds.contains(screenPos)) {
                        Point relativePos = new Point(
                            screenPos.x - canvasPos.x,
                            screenPos.y - canvasPos.y
                        );
                        
                        addImageToCanvasAt(leftCanvas, imagePath, relativePos);
                        dialog.dispose();
                    }
                }
            }
            
            imageContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 123, 255), 3),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
            ));
        }
        
        @Override
        public void mouseDragged(MouseEvent evt) {
            if (dragStart != null) {
                int dragThreshold = 5;
                Point currentPoint = evt.getPoint();
                if (!isDragging && 
                    (Math.abs(currentPoint.x - dragStart.x) > dragThreshold || 
                     Math.abs(currentPoint.y - dragStart.y) > dragThreshold)) {
                    
                    isDragging = true;
                    
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
        
        protected LeftCanvas findLeftCanvas() {
            if (mainFrame instanceof MainFrame) {
                return ((MainFrame) mainFrame).getLeftCanvas();
            }
            
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
        
        protected void findAndAddImageToLeftCanvas(String path) {
            LeftCanvas leftCanvas = findLeftCanvas();
            if (leftCanvas != null) {
                leftCanvas.addImage(path, type);
            }
        }
        
        protected void addImageToCanvasAt(LeftCanvas canvas, String path, Point position) {
            if (canvas != null) {
                canvas.addImageAt(path, position.x, position.y, type);
            }
        }
    }
    
    protected static Image resizeImage(File file, int w, int h) {
        try {
            Image originalImage = new ImageIcon(file.getAbsolutePath()).getImage();
            Image scaledImage = originalImage.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
            return scaledImage;
        } catch (Exception e) {
            System.err.println("Error resizing image: " + e.getMessage());
            return null;
        }
    }
}
