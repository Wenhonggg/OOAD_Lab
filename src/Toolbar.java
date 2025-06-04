import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class Toolbar extends JToolBar {
    private JFrame parent;
    private LeftCanvas leftCanvas;
    private RightCanvas rightCanvas;

    public Toolbar(JFrame f, LeftCanvas leftCanvas, RightCanvas rightCanvas) {
        super();
        parent = f;
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
        initializeComponents();
    }

    private void initializeComponents() {
        add(new NewButton(this));
        add(new SaveButton(parent, leftCanvas, rightCanvas));
        
        // Factory method approach
        for (int i = 0; i < 3; i++) {
            add(createImageButton(i));
        }
        
        DrawingToolPanel dtp = new DrawingToolPanel(parent);
        add(dtp);

        addPropertyChangeListener("orientation", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int newOrientation = (int) evt.getNewValue();
                dtp.updateOrientation(newOrientation);
            }
        });
    }
    
    private ImageButton createImageButton(int type) {
        switch (type) {
            case 0:
                return new AnimalImageButton(parent);
            case 1:
                return new FlowerImageButton(parent);
            case 2:
                return new CustomImageButton(parent);
            default:
                throw new IllegalArgumentException("Invalid image button type: " + type);
        }
    }

    public void createNewLeftCanvas() {
    if (parent instanceof MainFrame) {
        MainFrame mf = (MainFrame) parent;
        mf.showDimensionDialog(); // show popup

        int width = mf.getCanvasWidth();
        int height = mf.getCanvasHeight();

        Container container = leftCanvas.getParent();
        if (container != null) {
            leftCanvas = new LeftCanvas(width, height);
            mf.setLeftCanvas(leftCanvas); // update reference

            container.removeAll();
            container.add(leftCanvas);
            container.revalidate();
            container.repaint();
        }
    }
}


    public void createNewRightCanvas() {
        // Find the container holding the right canvas
        Container parent = rightCanvas.getParent();
        if (parent != null) {
            DrawingToolPanel dtp = getDrawingToolPanel();
            rightCanvas = new RightCanvas(dtp);
            parent.removeAll();
            parent.add(rightCanvas);
            parent.revalidate();
            parent.repaint();
        }
    }

    public RightCanvas getRightCanvas() {
        return rightCanvas;
    }

    protected String getToolbarPosition() {
        boolean isVertical = getOrientation() == JToolBar.VERTICAL;
        Rectangle toolbarBounds = getBounds();

        if (isVertical) {
            if (toolbarBounds.x == 0)
                return "WEST";
            else
                return "EAST";
        } else {
            if (toolbarBounds.y == 0)
                return "NORTH";
            else
                return "SOUTH";
        }
    }

    // Helper method to get the DrawingToolPanel from the toolbar
    private DrawingToolPanel getDrawingToolPanel() {
        for (Component comp : getComponents()) {
            if (comp instanceof DrawingToolPanel) {
                return (DrawingToolPanel) comp;
            }
        }
        return null;
    }
}