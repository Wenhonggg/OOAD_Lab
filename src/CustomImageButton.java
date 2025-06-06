import java.io.File;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class CustomImageButton extends ImageButton {
    
    public CustomImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Custom";
        String projectRoot = System.getProperty("user.dir");
        this.filePath = projectRoot + File.separator + "assets" + File.separator + "custom";
        
        File iconFile = new File(projectRoot + File.separator + "assets" + File.separator + 
                               "toolbarIcons" + File.separator + "customIcon.png");
        
        if (iconFile.exists()) {
            setIcon(new ImageIcon(resizeImage(iconFile, 30, 30)));
        } else {
            setText("Custom");
        }
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        // This method will be called from mouse drag event
        // The actual moving logic is handled in handleMouseDrag method
    }
    
    @Override
    protected String getActionHint() {
        return "Press CTRL+SHIFT to move image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
    }
    
    // Handle mouse drag for custom image moving
    public void handleMouseDrag(Object canvasObj, Object imageObj, MouseEvent currentEvent, MouseEvent lastEvent, double canvasRotation) {
        if (imageObj instanceof LeftCanvas.CanvasImage) {
            LeftCanvas.CanvasImage selectedImage = (LeftCanvas.CanvasImage) imageObj;
            
            // Moving for Custom images in the user's coordinate space
            int rawDeltaX = currentEvent.getX() - lastEvent.getX();
            int rawDeltaY = currentEvent.getY() - lastEvent.getY();
            
            // Transform movement direction based on canvas rotation
            double cos = Math.cos(-canvasRotation);
            double sin = Math.sin(-canvasRotation);
            int deltaX = (int)(rawDeltaX * cos - rawDeltaY * sin);
            int deltaY = (int)(rawDeltaX * sin + rawDeltaY * cos);
            
            if (canvasObj instanceof LeftCanvas) {
                LeftCanvas canvas = (LeftCanvas) canvasObj;
                selectedImage.move(deltaX, deltaY, canvas.getWidth(), canvas.getHeight());
                canvas.repaint();
            }
        }
    }
}
