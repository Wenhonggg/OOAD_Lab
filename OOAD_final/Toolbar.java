import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class Toolbar extends JToolBar {
    private JFrame parent;
    private LeftCanvas leftCanvas;
    private RightCanvas rightCanvas;
    private SaveButton saveButton;

    private enum ButtonType {
        ANIMAL, FLOWER, CUSTOM
    }

    public Toolbar(JFrame f, LeftCanvas leftCanvas, RightCanvas rightCanvas) {
        super();
        parent = f;
        this.leftCanvas = leftCanvas;
        this.rightCanvas = rightCanvas;
        initializeComponents();
    }

    private void initializeComponents() {
        add(new NewButton(this));

        saveButton = new SaveButton(parent, leftCanvas, rightCanvas);
        add(saveButton);

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
        ButtonType buttonType = ButtonType.values()[type];
        switch (buttonType) {
            case ANIMAL:
                return new AnimalImageButton(parent);
            case FLOWER:
                return new FlowerImageButton(parent);
            case CUSTOM:
                return new CustomImageButton(parent);
            default:
                throw new IllegalArgumentException("Invalid image button type: " + type);
        }
    }

    public void createNewLeftCanvas() {
        if (parent instanceof MainFrame) {
            MainFrame mf = (MainFrame) parent;
            mf.showDimensionDialog();

            int width = mf.getCanvasWidth();
            int height = mf.getCanvasHeight();

            Container container = leftCanvas.getParent();
            if (container != null) {
                leftCanvas = new LeftCanvas(width, height, this);
                mf.setLeftCanvas(leftCanvas);

                container.removeAll();
                container.add(leftCanvas);
                container.revalidate();
                container.repaint();
                
                saveButton.updateCanvases(leftCanvas, rightCanvas);
            }
        }
    }

    public void createNewRightCanvas() {
        Container container = rightCanvas.getParent();
        if (container != null) {
            DrawingToolPanel dtp = getDrawingToolPanel();
            rightCanvas = new RightCanvas(dtp);
            container.removeAll();
            container.add(rightCanvas);
            container.revalidate();
            container.repaint();
            saveButton.updateCanvases(leftCanvas, rightCanvas);
        }
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

    private DrawingToolPanel getDrawingToolPanel() {
        for (Component comp : getComponents()) {
            if (comp instanceof DrawingToolPanel) {
                return (DrawingToolPanel) comp;
            }
        }
        return null;
    }
}