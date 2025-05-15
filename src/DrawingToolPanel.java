import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

public class DrawingToolPanel extends JPanel {
    private JFrame frame;
    private JButton brush;
    private JButton eraser;
    private JPanel sliderPanel;
    private JSlider sizeSlider;

    public DrawingToolPanel(JFrame f) {
        super();
        frame = f;
        // create brush button
        brush = new JButton("brush");
        brush.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Brush selected");
            }
        });

        // create eraser button
        eraser = new JButton("eraser");
        eraser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Eraser selected");
            }
        });

        // create slider to adjust pen/eraser size
        sliderPanel = new JPanel();
        // TODO replace 10 with current brush/eraser size
        sizeSlider = new JSlider(0, 100, 10);
        sizeSlider.setPaintTrack(true);
        sizeSlider.setUI(new BasicSliderUI(sizeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle thumbBounds = thumbRect;
                int diameter = Math.min(thumbRect.width, thumbRect.height);
                g2.setColor(Color.red);
                g2.fillOval(thumbBounds.x, thumbBounds.y + (thumbBounds.height - diameter) / 2, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.drawOval(thumbBounds.x, thumbBounds.y + (thumbBounds.height - diameter) / 2, diameter, diameter);
                g2.dispose();
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle trackBounds = trackRect;
                int trackHeight = 4;
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight) / 2,
                        trackBounds.width,
                        trackHeight, 5, 5);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight) / 2,
                        trackBounds.width,
                        trackHeight, 5, 5);
                g2.dispose();
            }
        });
        sliderPanel.add(sizeSlider);

        JLabel sizeLabel = new JLabel(String.valueOf(sizeSlider.getValue()));
        sizeLabel.setPreferredSize(new Dimension(20, 20));
        // automatically update label
        sizeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sizeLabel.setText(String.valueOf(sizeSlider.getValue()));
                // TODO update brush/eraser size
                if (!sizeSlider.getValueIsAdjusting())
                    System.out.println("Brush/Eraser size changed to " + sizeSlider.getValue());
                repaint();
            }
        });
        sliderPanel.add(sizeLabel);

        // create colour chooser
        JButton jccBtn = new JButton("jcc");
        jccBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO replace Color.BLACK with current brush colour, and change brush colour
                // according to colour chosen by user
                Color selectedColor = JColorChooser.showDialog(frame, "Change brush colour", Color.BLACK);
                System.out.println(selectedColor + " chosen");
            }
        });

        add(brush);
        add(eraser);
        add(sliderPanel);
        add(jccBtn);
    }

    public void updateOrientation(int tbOrientation) {
        if (tbOrientation == JToolBar.HORIZONTAL) {
            setLayout(new FlowLayout());
            sliderPanel.setLayout(new FlowLayout());
            sizeSlider.setOrientation(SwingConstants.HORIZONTAL);
        } else {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
            sizeSlider.setOrientation(SwingConstants.VERTICAL);
        }
    }
}
