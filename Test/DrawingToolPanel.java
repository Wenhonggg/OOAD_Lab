
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicSliderUI;

import java.util.ArrayList;
import java.util.List;

public class DrawingToolPanel extends JPanel {
    private final int HORIZONTAL_SLIDER_WIDTH = 150;
    private final int HORIZONTAL_SLIDER_HEIGHT = 30;
    private final int VERTICAL_SLIDER_WIDTH = 30;
    private final int VERTICAL_SLIDER_HEIGHT = 100;
    private final Color selectedTrackColour = new Color(3, 157, 252);
    private final Color unselectedTrackColour = Color.LIGHT_GRAY;
    private final Color trackBorderColour = Color.GRAY;
    private final Color thumbColour = new Color(204, 204, 204);
    private final Color thumbBorder = Color.GRAY;

    private JFrame frame;
    private JButton brush;
    private JButton eraser;
    private JPanel sliderPanel;
    private JSlider sizeSlider;
    private JLabel sizeLabel;
    private JPanel jccPanel;
    private List<Color> savedColours;

    public DrawingToolPanel(JFrame f) {
        super();
        frame = f;
        // initialize list of saved colours
        savedColours = new ArrayList<>();
        savedColours.add(Color.BLACK);
        savedColours.add(Color.RED);
        savedColours.add(Color.ORANGE);
        savedColours.add(Color.YELLOW);
        savedColours.add(Color.GREEN);
        savedColours.add(Color.BLUE);
        savedColours.add(Color.PINK);

        setOpaque(false);

        // create brush button
        brush = new JButton();
        brush.setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/brushIcon.png"), 30, 30)));
        brush.setBackground(new Color(238, 238, 238));
        brush.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Brush selected");
            }
        });

        // create eraser button
        eraser = new JButton();
        eraser.setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/eraserIcon.png"), 30, 30)));
        eraser.setBackground(new Color(238, 238, 238));
        eraser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Eraser selected");
            }
        });

        // create slider to adjust pen/eraser size
        sliderPanel = new JPanel();
        sliderPanel.setOpaque(false);
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
        jccPanel = new JPanel();
        jccPanel.setOpaque(false);
        createColourPalettePanel(jccPanel, false);

        setLayout(new FlowLayout());
        add(brush);
        add(eraser);
        add(sliderPanel);
        add(jccPanel);
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

            jccPanel.setLayout(new FlowLayout());

            add(brush);
            add(eraser);
            add(sliderPanel);
            add(jccPanel);
        } else {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            brush.setAlignmentX(Component.CENTER_ALIGNMENT);
            eraser.setAlignmentX(Component.CENTER_ALIGNMENT);

            sliderPanel.removeAll();
            sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
            sliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

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

            createColourPalettePanel(jccPanel, true);

            add(brush);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(eraser);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(sliderPanel);
            add(Box.createRigidArea(new Dimension(0, 10)));
            add(jccPanel);
            add(Box.createRigidArea(new Dimension(0, 10)));
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
                g2.setColor(thumbColour);
                g2.fillOval(thumbBounds.x, thumbBounds.y + (thumbBounds.height - diameter) / 2, diameter, diameter);
                g2.setColor(thumbBorder);
                g2.drawOval(thumbBounds.x, thumbBounds.y + (thumbBounds.height - diameter) / 2, diameter, diameter);
                g2.dispose();
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle trackBounds = trackRect;
                int trackHeight = 4;
                int thumbPosition = thumbRect.x + thumbRect.width / 2;
                g2.setColor(selectedTrackColour);
                g2.fillRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight) / 2,
                        thumbPosition - trackBounds.x, trackHeight, 5, 5);
                g2.setColor(unselectedTrackColour);
                g2.fillRoundRect(thumbPosition, trackBounds.y + (trackBounds.height - trackHeight) / 2,
                        trackBounds.width - (thumbPosition - trackBounds.x),
                        trackHeight, 5, 5);
                g2.setColor(trackBorderColour);
                g2.drawRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight) / 2,
                        trackBounds.width,
                        trackHeight, 5, 5);
                g2.dispose();
            }
        };
    }

    private BasicSliderUI createVerticalSlider(JSlider slider) {
        return new BasicSliderUI(slider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle thumbBounds = thumbRect;
                int diameter = Math.min(thumbRect.width, thumbRect.height);
                g2.setColor(thumbColour);
                g2.fillOval(thumbBounds.x + (thumbBounds.width - diameter) / 2, thumbBounds.y, diameter, diameter);
                g2.setColor(thumbBorder);
                g2.drawOval(thumbBounds.x + (thumbBounds.width - diameter) / 2, thumbBounds.y, diameter, diameter);
                g2.dispose();
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Rectangle trackBounds = trackRect;
                int trackWidth = 4;
                int thumbPosition = thumbRect.y + thumbRect.height / 2;
                g2.setColor(selectedTrackColour);
                g2.fillRoundRect(trackBounds.x + (trackBounds.width - trackWidth) / 2, thumbPosition, trackWidth,
                        trackBounds.height - (thumbPosition - trackBounds.y), 5, 5);
                g2.setColor(unselectedTrackColour);
                g2.fillRoundRect(trackBounds.x + (trackBounds.width - trackWidth) / 2, trackBounds.y, trackWidth,
                        thumbPosition - trackBounds.y, 5, 5);
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
                g2.dispose();
            }

        });
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually change the brush colour
                System.out.println(color + " chosen");
            }
        });
        return btn;
    }

    private JButton createCustomColourBtn() {
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
                Color[] colours = {
                        Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.MAGENTA
                };
                float[] fractions = {
                        0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f
                };
                LinearGradientPaint gradient = new LinearGradientPaint(0, 0, DIAMETER, DIAMETER, fractions, colours);
                g2.setPaint(gradient);
                g2.fillOval(0, 0, DIAMETER, DIAMETER);
                g2.dispose();
            }

        });
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO actually change the brush colour
                Color selectedColor = JColorChooser.showDialog(frame, "Change brush colour", Color.BLACK);
                System.out.println(selectedColor + " chosen");
            }
        });
        return btn;
    }

    private void createColourPalettePanel(JPanel panel, boolean isVertical) {
        panel.removeAll();
        panel.setLayout(new BoxLayout(panel, isVertical ? BoxLayout.Y_AXIS : BoxLayout.X_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.setMaximumSize(new Dimension(isVertical ? 100 : Integer.MAX_VALUE, isVertical ? Integer.MAX_VALUE : 50));

        for (Color colour : savedColours) {
            JButton colourBtn = createColourPaletteBtn(colour);
            colourBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            colourBtn.setMaximumSize(new Dimension(30, 30));
            panel.add(colourBtn);
            if (isVertical)
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
            else
                panel.add(Box.createRigidArea(new Dimension(5, 0)));

        }

        JButton customColourBtn = createCustomColourBtn();
        customColourBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        customColourBtn.setMaximumSize(new Dimension(30, 30));
        panel.add(customColourBtn);
        if (isVertical)
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        else
            panel.add(Box.createRigidArea(new Dimension(5, 0)));
    }
}