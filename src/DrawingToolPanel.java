
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSliderUI;

public class DrawingToolPanel extends JPanel {
    private final int HORIZONTAL_SLIDER_WIDTH = 150;
    private final int HORIZONTAL_SLIDER_HEIGHT = 30;
    private final int VERTICAL_SLIDER_WIDTH = 30;
    private final int VERTICAL_SLIDER_HEIGHT = 100;

    private JFrame frame;
    private JButton brush;
    private JButton eraser;
    private JPanel sliderPanel;
    private JSlider sizeSlider;
    private JLabel sizeLabel;
    private JButton jccBtn;

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
        sizeSlider.setOrientation(JSlider.HORIZONTAL);
        sizeSlider.setUI(createHorizontalSlider(sizeSlider));
        sizeSlider.setPreferredSize(new Dimension(HORIZONTAL_SLIDER_WIDTH, HORIZONTAL_SLIDER_HEIGHT));
        sliderPanel.add(sizeSlider);

        sizeLabel = new JLabel(String.valueOf(sizeSlider.getValue()));
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
        JPanel jccPanel = new JPanel();
        jccPanel.add(createColourPaletteBtn(Color.black));
        jccPanel.add(createColourPaletteBtn(Color.red));
        jccPanel.add(createColourPaletteBtn(Color.magenta));

        jccBtn = new JButton("jcc");
        jccBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO replace Color.BLACK with current brush colour, and change brush colour
                // according to colour chosen by user
                Color selectedColor = JColorChooser.showDialog(frame, "Change brush colour", Color.BLACK);
                System.out.println(selectedColor + " chosen");
            }
        });

        setLayout(new FlowLayout());
        add(brush);
        add(eraser);
        add(sliderPanel);
        add(jccPanel);
        add(jccBtn);
    }

    public void updateOrientation(int tbOrientation) {
        removeAll();
        sliderPanel.removeAll();

        if (tbOrientation == JToolBar.HORIZONTAL) {
            setLayout(new FlowLayout());
            sliderPanel.setLayout(new FlowLayout());
            sliderPanel.setBorder(null);

            sizeSlider.setOrientation(JSlider.HORIZONTAL);
            sizeSlider.setUI(createHorizontalSlider(sizeSlider));
            sizeSlider.setPreferredSize(new Dimension(HORIZONTAL_SLIDER_WIDTH, HORIZONTAL_SLIDER_HEIGHT));

            sliderPanel.add(sizeSlider);
            sliderPanel.add(sizeLabel);

            add(brush);
            add(eraser);
            add(sliderPanel);
            add(jccBtn);
        } else {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            brush.setAlignmentX(Component.CENTER_ALIGNMENT);
            eraser.setAlignmentX(Component.CENTER_ALIGNMENT);
            jccBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            sliderPanel.removeAll();
            sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
            sliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sliderPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JPanel sliderContainer = new JPanel();
            sliderContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
            sliderContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            sliderContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
            sliderContainer.setOpaque(false);

            sizeSlider.setOrientation(JSlider.VERTICAL);
            sizeSlider.setUI(createVerticalSlider(sizeSlider));
            sizeSlider.setPreferredSize(new Dimension(VERTICAL_SLIDER_WIDTH, VERTICAL_SLIDER_HEIGHT));
            sliderContainer.add(sizeSlider);

            sliderPanel.add(sliderContainer);

            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            labelPanel.add(sizeLabel);
            labelPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            labelPanel.setOpaque(false);
            sliderPanel.add(labelPanel);
            sliderPanel.add(Box.createRigidArea(new Dimension(0, 5)));

            add(brush);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(eraser);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(sliderPanel);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(jccBtn);
        }

        revalidate();
        repaint();
    }

    private BasicSliderUI createHorizontalSlider(JSlider slider) {
        return new BasicSliderUI(slider) {
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
        };
    }

    private BasicSliderUI createVerticalSlider(JSlider slider) {
        return new BasicSliderUI(sizeSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle thumbBounds = thumbRect;
                int diameter = Math.min(thumbRect.width, thumbRect.height);
                g2.setColor(Color.red);
                g2.fillOval(thumbBounds.x + (thumbBounds.width - diameter) / 2, thumbBounds.y, diameter, diameter);
                g2.setColor(Color.BLACK);
                g2.drawOval(thumbBounds.x + (thumbBounds.width - diameter) / 2, thumbBounds.y, diameter, diameter);
                g2.dispose();
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle trackBounds = trackRect;
                int trackWidth = 4;
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRoundRect(trackBounds.x + (trackBounds.width - trackWidth) / 2, trackBounds.y,
                        trackWidth, trackBounds.height, 5, 5);
                g2.setColor(Color.GRAY);
                g2.drawRoundRect(trackBounds.x + (trackBounds.width - trackWidth) / 2, trackBounds.y,
                        trackWidth, trackBounds.height, 5, 5);
                g2.dispose();
            }
        };
    }

    private JButton createColourPaletteBtn(Color color) {
        final int DIAMETER = 24;
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(DIAMETER, DIAMETER));
        btn.setContentAreaFilled(false); // disable default rectangular background
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, DIAMETER, DIAMETER);
            }

        });
        return btn;
    }
}