import java.io.File;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class FlowerImageButton extends ImageButton {
    
    public FlowerImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Flower";
        String projectRoot = System.getProperty("user.dir");
        this.filePath = projectRoot + File.separator + "assets" + File.separator + "flowers";
        
        File iconFile = new File(projectRoot + File.separator + "assets" + File.separator + 
                               "toolbarIcons" + File.separator + "flowerIcon.png");
        
        if (iconFile.exists()) {
            setIcon(new ImageIcon(resizeImage(iconFile, 30, 30)));
        } else {
            setText("Flower");
        }
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        // This method will be called from mouse drag event
        // The actual scaling logic is handled in handleMouseDrag method
    }
    
    @Override
    protected String getActionHint() {
        return "Press ALT and drag up/down to scale image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.ALT_DOWN_MASK;
    }
    
    // Handle mouse drag for flower scaling
    public void handleMouseDrag(Object canvasObj, Object imageObj, MouseEvent currentEvent, MouseEvent lastEvent, double canvasRotation) {
        if (imageObj instanceof LeftCanvas.CanvasImage) {
            LeftCanvas.CanvasImage selectedImage = (LeftCanvas.CanvasImage) imageObj;
            
            // Get the raw mouse movement in screen space
            int rawDeltaY = currentEvent.getY() - lastEvent.getY();  // Positive means moving down
            
            // Transform the vertical movement to account for canvas rotation
            // We need the standard up/down direction in user's perspective
            double adjustedDelta;
            
            if (canvasRotation == 0) {
                // No rotation, use raw delta directly
                adjustedDelta = rawDeltaY;
            } else {
                // With rotation, we need to project the movement onto the "true" vertical axis
                // in the user's perspective (not the canvas perspective)
                double cos = Math.cos(-canvasRotation);  // Negative rotation to convert back to screen space
                double sin = Math.sin(-canvasRotation);
                
                int rawDeltaX = currentEvent.getX() - lastEvent.getX();
                
                // Project the 2D movement vector onto the user's vertical axis
                adjustedDelta = rawDeltaX * sin + rawDeltaY * cos;
            }
            
            // Scale down when moving mouse up (negative delta), scale up when moving down (positive delta)
            float scaleFactor = 1.0f;
            if (adjustedDelta != 0) {
                scaleFactor = 1.0f + (float)(adjustedDelta * 0.01f);
                selectedImage.scale(scaleFactor);
            }
            
            if (canvasObj instanceof LeftCanvas) {
                ((LeftCanvas) canvasObj).repaint();
            }
        }
    }
}
