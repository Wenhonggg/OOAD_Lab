import java.io.File;
import java.awt.event.InputEvent;
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
        try {
            // Example: Rotate the flower image
            if (imageObj != null) {
                // Find the rotate method
                java.lang.reflect.Method rotateMethod = 
                    imageObj.getClass().getMethod("rotate", double.class);
                
                // Call the method to rotate by 45 degrees
                rotateMethod.invoke(imageObj, Math.toRadians(45));
                
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
        return "Press ALT to rotate image 45 degrees";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.ALT_DOWN_MASK;
    }
}
