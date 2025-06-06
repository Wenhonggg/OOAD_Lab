import java.awt.event.InputEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class AnimalImageButton extends ImageButton {
    
    public AnimalImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Animal";
        String projectRoot = System.getProperty("user.dir");
        this.filePath = projectRoot + File.separator + "assets" + File.separator + "animals";
        
        File iconFile = new File(projectRoot + File.separator + "assets" + File.separator + 
                               "toolbarIcons" + File.separator + "animalIcon.png");
        
        if (iconFile.exists()) {
            setIcon(new ImageIcon(resizeImage(iconFile, 30, 30)));
        } else {
            setText("Animal");
        }
    }
    
    @Override
    protected void performSpecialAction(Object canvasObj, Object imageObj) {
        // Handle SHIFT key for horizontal flip
        if (imageObj instanceof LeftCanvas.CanvasImage) {
            LeftCanvas.CanvasImage selectedImage = (LeftCanvas.CanvasImage) imageObj;
            selectedImage.flip(true); // Flip horizontally
            
            if (canvasObj instanceof LeftCanvas) {
                ((LeftCanvas) canvasObj).repaint();
            }
        }
    }
    
    @Override
    protected String getActionHint() {
        return "Press SHIFT to flip horizontally (Right-click to flip vertically)";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.SHIFT_DOWN_MASK;
    }
    
    // Handle right-click for vertical flip
    public void handleRightClick(Object canvasObj, Object imageObj) {
        if (imageObj instanceof LeftCanvas.CanvasImage) {
            LeftCanvas.CanvasImage selectedImage = (LeftCanvas.CanvasImage) imageObj;
            selectedImage.flip(false); // Flip vertically on right click
            
            if (canvasObj instanceof LeftCanvas) {
                ((LeftCanvas) canvasObj).repaint();
            }
        }
    }
}
