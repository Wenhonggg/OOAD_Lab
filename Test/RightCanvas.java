import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class RightCanvas extends CanvasPanel implements MouseMotionListener {
    private Color currentColor = Color.BLACK; // default pen color
    private int strokeSize = 5;                // default stroke size
    private ArrayList<StrokeShape> strokes;   // store drawn lines

    // For drawing the current stroke
    private Point lastPoint;

    public RightCanvas() {
        super(650, 730);
        strokes = new ArrayList<>();
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
    }

    // Drawing a stroke helper class to store color, stroke size, and points
    private static class StrokeShape {
        Color color;
        int size;
        ArrayList<Point> points;

        StrokeShape(Color c, int s) {
            color = c;
            size = s;
            points = new ArrayList<>();
        }

        void addPoint(Point p) {
            points.add(p);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw all previous strokes
        Graphics2D g2d = (Graphics2D) g;
        for (StrokeShape stroke : strokes) {
            g2d.setColor(stroke.color);
            g2d.setStroke(new BasicStroke(stroke.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i = 1; i < stroke.points.size(); i++) {
                Point p1 = stroke.points.get(i - 1);
                Point p2 = stroke.points.get(i);
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    // MouseMotionListener methods
    @Override
    public void mouseDragged(MouseEvent e) {
        if (lastPoint == null) {
            lastPoint = e.getPoint();
            // Start a new stroke
            StrokeShape newStroke = new StrokeShape(currentColor, strokeSize);
            newStroke.addPoint(lastPoint);
            strokes.add(newStroke);
        } else {
            Point currentPoint = e.getPoint();
            StrokeShape currentStroke = strokes.get(strokes.size() - 1);
            currentStroke.addPoint(currentPoint);
            lastPoint = currentPoint;
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        lastPoint = null; // reset when moving without dragging
    }

    // Set pen mode: color for drawing
    public void setPenColor(Color color) {
        this.currentColor = color;
    }

    // Set eraser mode: white color (background color)
    public void setEraser() {
        this.currentColor = Color.WHITE;
    }

    // Set stroke size (for both pen and eraser)
    public void setStrokeSize(int size) {
        if (size > 0)
            this.strokeSize = size;
    }

    // Clear all strokes on the canvas
    public void clearCanvas() {
        strokes.clear();
        repaint();
    }
}
