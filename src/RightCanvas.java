import java.awt.*;
import java.awt.event.*;


class RightCanvas extends CanvasPanel {
    private Graphics2D g2;
    private DrawingToolPanel toolPanel;
    private int prevX, prevY;
    

    public RightCanvas(DrawingToolPanel toolPanel) {
        super(650, 730);
        this.toolPanel = toolPanel;
        setupDrawing();
    }

    private void setupDrawing() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
                draw(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawLine(prevX, prevY, e.getX(), e.getY());
                prevX = e.getX();
                prevY = e.getY();
            }
        });
    }

    private void draw(int x, int y) {
        if (g2 == null) {
            g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
        g2.setColor(color);
        g2.setStroke(new BasicStroke(toolPanel.getCurrentSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x, y, x, y);
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        if (g2 == null) {
            g2 = (Graphics2D) getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
        g2.setColor(color);
        g2.setStroke(new BasicStroke(toolPanel.getCurrentSize(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x2, y2);
    }

     public void clearCanvas() {
        if (g2 != null) {
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());
            repaint();
        }
    }
}