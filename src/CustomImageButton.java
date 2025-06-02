import java.io.File;
import java.awt.event.InputEvent;
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
        try {
            // Example: Apply a filter to the custom image
            if (imageObj != null) {
                // Find the applyFilter method
                java.lang.reflect.Method filterMethod = 
                    imageObj.getClass().getMethod("applyFilter", String.class);
                
                // Call the method to apply grayscale filter
                filterMethod.invoke(imageObj, "grayscale");
                
                // Find and call the repaint method on the canvas
                if (canvasObj != null) {
                    java.lang.reflect.Method repaintMethod = 
                        canvasObj.getClass().getMethod("repaint");
                    repaintMethod.invoke(canvasObj);
                }
            }
        } catch (Exception e) {
            System.err.println("Error performing custom image action: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected String getActionHint() {
        return "Press CTRL to apply grayscale filter";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.CTRL_DOWN_MASK;
    }
}
