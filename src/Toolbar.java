
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;

public class Toolbar extends JToolBar {
    private JFrame parent;

    public Toolbar(JFrame f) {
        super();
        parent = f;
        add(new NewButton(this));
        add(new SaveButton(parent));
        for (int i = 0; i < 3; i++)
            add(new ImageButton(parent, i));
        DrawingToolPanel dtp = new DrawingToolPanel(parent);
        add(dtp);

        addPropertyChangeListener("orientation", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                int newOrientation = (int) evt.getNewValue();
                dtp.updateOrientation(newOrientation);
            }
        });

        setFloatable(true);
        setRollover(true);
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