import java.awt.event.InputEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class CustomImageButton extends ImageButton {
    
    public CustomImageButton(JFrame f) {
        super(f, "Custom");
        setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/customIcon.png"), 30, 30)));
        String projectRoot = System.getProperty("user.dir");
        filePath = projectRoot + File.separator + "assets" + File.separator + "custom";
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        // The actual movement happens in the mouse dragged event in LeftCanvas
        // This method is more for single-click actions
    }
    
    @Override
    protected String getActionHint() {
        return "Press CTRL+SHIFT to move image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
    }
}
