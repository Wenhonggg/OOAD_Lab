
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
        switch (type) {
            case 0:
                this.type = "Animal";
                filePath = "assets/animals";
                setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/animalIcon.png"), 30, 30)));
                break;
            case 1:
                this.type = "Flower";
                filePath = "assets/flowers";
                setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/flowerIcon.png"), 30, 30)));
                break;
            case 2:
                this.type = "Custom";
                filePath = "assets/custom";
                setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/customIcon.png"), 30, 30)));
                break;
            default:
                break;
        }
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
        JLabel label = new JLabel("Drag an image to the left canvas to start working on your art!");
        panel.add(label, BorderLayout.NORTH);
        // TODO implement drag and drop to insert image into canvas

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

                    imagesPanel = new JPanel(new GridLayout(Math.ceilDiv(fileCount, 3), 3, 0, 10));
                    for (File file : files) {
                        Image scaledImage = resizeImage(file, 132, 132);
                        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        imagesPanel.add(imageLabel);
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

    public static Image resizeImage(File file, int w, int h) {
        Image originalImage = new ImageIcon(file.getAbsolutePath()).getImage();
        Image scaledImage = originalImage.getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH);
        return scaledImage;
    }
}
