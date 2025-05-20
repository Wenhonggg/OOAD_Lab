import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.*;

public class RightCanvas extends CanvasPanel implements MouseListener, MouseMotionListener {

    private Color penColour = Color.BLACK;
    private int strokeSize = 10;
    private boolean erasing = false;

    private Point last;
    private final java.util.List<Stroke> strokes = new ArrayList<>();

    public RightCanvas() {
        super(650, 730);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    // Setter methods to be called by Toolbar or UI controls
    public void setPenMode() {
        erasing = false;
    }

    public void setEraserMode() {
        erasing = true;
    }

    public void setPenColour(Color color) {
        if (!erasing && color != null) {
            penColour = color;
        }
    }

    public void setStrokeSize(int size) {
        strokeSize = size;
    }

    public void clearAll() {
        strokes.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Stroke st : strokes) st.paint(g2);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        last = e.getPoint();
        Color drawColor = erasing ? Color.WHITE : penColour;
        strokes.add(new Stroke(drawColor, strokeSize));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (last == null) return;
        Point p = e.getPoint();
        Stroke currentStroke = strokes.get(strokes.size() - 1);
        currentStroke.add(last, p);
        last = p;
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        last = null;
    }

    /* Unused mouse events */
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    private static class Stroke {
        final Color colour;
        final int size;
        final java.util.List<Line2D> seg = new ArrayList<>();

        Stroke(Color c, int s) {
            colour = c;
            size = s;
        }

        void add(Point a, Point b) {
            seg.add(new Line2D.Float(a, b));
        }

        void paint(Graphics2D g) {
            g.setColor(colour);
            g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (Line2D l : seg) {
                g.draw(l);
            }
        }
    }
}
