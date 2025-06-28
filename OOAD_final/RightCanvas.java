import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class RightCanvas extends CanvasPanel {
    private DrawingToolPanel toolPanel;
    private int prevX, prevY;
    private List<DrawingInstruction> drawingInstructions;

    public RightCanvas(DrawingToolPanel toolPanel) {
        super(650, 730);
        this.toolPanel = toolPanel;
        this.drawingInstructions = new ArrayList<>();
        setBackground(Color.WHITE);
        setupDrawing();
    }

    public void setToolPanel(DrawingToolPanel toolPanel) {
        this.toolPanel = toolPanel;
    }

    private void setupDrawing() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (toolPanel == null) {
                    return;
                }
                prevX = e.getX();
                prevY = e.getY();
                Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
                float size = toolPanel.getCurrentSize();
                DrawingInstruction newInstruction = new DrawingInstruction(e.getX(), e.getY(), e.getX(), e.getY(), color, size);
                drawingInstructions.add(newInstruction);
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (toolPanel == null) {
                    return;
                }
                Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
                float size = toolPanel.getCurrentSize();
                DrawingInstruction newInstruction = new DrawingInstruction(prevX, prevY, e.getX(), e.getY(), color, size);
                drawingInstructions.add(newInstruction);
                prevX = e.getX();
                prevY = e.getY();
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (DrawingInstruction instruction : drawingInstructions) {
            g2.setColor(instruction.color);
            g2.setStroke(new BasicStroke(instruction.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (instruction.x1 == instruction.x2 && instruction.y1 == instruction.y2) {
                int radius = (int)(instruction.size / 2);
                g2.fillOval(instruction.x1 - radius, instruction.y1 - radius, (int)instruction.size, (int)instruction.size);
            } else {
                g2.drawLine(instruction.x1, instruction.y1, instruction.x2, instruction.y2);
            }
        }
        g2.dispose();
    }

    public void clearCanvas() {
        drawingInstructions.clear();
        repaint();
    }

    public BufferedImage saveToImage() {
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        if (width <= 0) width = getWidth() > 0 ? getWidth() : 650;
        if (height <= 0) height = getHeight() > 0 ? getHeight() : 730;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        for (DrawingInstruction instruction : drawingInstructions) {
            g2.setColor(instruction.color);
            g2.setStroke(new BasicStroke(instruction.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (instruction.x1 == instruction.x2 && instruction.y1 == instruction.y2) {
                int radius = (int)(instruction.size / 2);
                g2.fillOval(instruction.x1 - radius, instruction.y1 - radius, (int)instruction.size, (int)instruction.size);
            } else {
                g2.drawLine(instruction.x1, instruction.y1, instruction.x2, instruction.y2);
            }
        }
        g2.dispose();
        return image;
    }

    private static class DrawingInstruction {
        int x1, y1, x2, y2;
        Color color;
        float size;

        public DrawingInstruction(int x1, int y1, int x2, int y2, Color color, float size) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.size = size;
        }

        @Override
        public String toString() {
            return "DrawingInstruction{" +
                    "from=(" + x1 + "," + y1 + ") to=(" + x2 + "," + y2 + "), " +
                    "color=" + color + ", size=" + size + '}';
        }
    }
}