import java.awt.event.InputEvent;
import java.io.File;
import javax.swing.JFrame;

public class FlowerImageButton extends ImageButton {
    
    public FlowerImageButton(JFrame f) {
        super(f, "Flower");
        String projectRoot = System.getProperty("user.dir");
        filePath = projectRoot + File.separator + "assets" + File.separator + "flowers";
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        try {
            // Use reflection to call the scale method on the image object
            if (imageObj != null) {
                // Find the scale method
                java.lang.reflect.Method scaleMethod = 
                    imageObj.getClass().getMethod("scale", float.class);
                
                // Call the method
                scaleMethod.invoke(imageObj, 1.1f);
                
                // Find and call the repaint method on the canvas
                if (canvasObj != null) {
                    java.lang.reflect.Method repaintMethod = 
                        canvasObj.getClass().getMethod("repaint");
                    repaintMethod.invoke(canvasObj);
                }
            }
        } catch (Exception e) {
            System.err.println("Error performing flower image action: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected String getActionHint() {
        return "Press ALT to scale image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.ALT_DOWN_MASK;
    }
}