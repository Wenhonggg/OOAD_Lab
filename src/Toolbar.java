import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class Toolbar extends JToolBar {
    private JFrame parent;
    private LeftCanvas leftCanvas;
    private RightCanvas rightCanvas;
    private JSplitPane splitPane;

    public Toolbar(JFrame f) {
        super();
        parent = f;
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        add(new NewButton(this));
        add(new SaveButton(parent));
        
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

    private void setupLayout() {
        setFloatable(true);
        setRollover(true);
        createDefaultCanvases();
    }

    private void createDefaultCanvases() {
        leftCanvas = new LeftCanvas(650, 730);
        DrawingToolPanel dtp = getDrawingToolPanel();
        rightCanvas = new RightCanvas(dtp);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCanvas, rightCanvas);
        splitPane.setResizeWeight(0.5);
        parent.add(splitPane, BorderLayout.CENTER);
    }

    public void createNewLeftCanvas() {
        leftCanvas = new LeftCanvas(650, 730);
        splitPane.setLeftComponent(leftCanvas);
        splitPane.setDividerLocation(0.5);
        parent.revalidate();
    }

    public void createNewRightCanvas() {
        DrawingToolPanel dtp = getDrawingToolPanel();
        rightCanvas = new RightCanvas(dtp);
        rightCanvas.clearCanvas();
        splitPane.setRightComponent(rightCanvas);
        splitPane.setDividerLocation(0.5);
        parent.revalidate();
        parent.repaint();
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