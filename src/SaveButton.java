import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.io.File;

public class SaveButton extends JButton {
    JFrame frame;

    public SaveButton(JFrame f) {
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
                            System.out.println("Artwork is being saved...");
                            saveDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(saveDialog,
                                    "Please select a canvas to save and the preferred file type!", "Error",
                                    JOptionPane.ERROR_MESSAGE);
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
