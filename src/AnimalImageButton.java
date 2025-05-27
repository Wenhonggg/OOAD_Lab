import java.awt.event.InputEvent;
import java.io.File;
import javax.swing.JFrame;

public class AnimalImageButton extends ImageButton {
    
    public AnimalImageButton(JFrame f) {
        super(f, "Animal");
        String projectRoot = System.getProperty("user.dir");
        filePath = projectRoot + File.separator + "assets" + File.separator + "animals";
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        try {
            // Use reflection to call the flip method on the image object
            if (imageObj != null) {
                // Find the flip method
                java.lang.reflect.Method flipMethod = 
                    imageObj.getClass().getMethod("flip", boolean.class);
                
                // Call the method
                flipMethod.invoke(imageObj, true);
                
                // Find and call the repaint method on the canvas
                if (canvasObj != null) {
                    java.lang.reflect.Method repaintMethod = 
                        canvasObj.getClass().getMethod("repaint");
                    repaintMethod.invoke(canvasObj);
                }
            }
        } catch (Exception e) {
            System.err.println("Error performing animal image action: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected String getActionHint() {
        return "Press SHIFT to flip image horizontally";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.SHIFT_DOWN_MASK;
    }
}