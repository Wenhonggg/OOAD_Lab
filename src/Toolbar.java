import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class Toolbar extends JToolBar {
    private JFrame parent;

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
    
    private void setupLayout() {
        setFloatable(true);
        setRollover(true);
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
}