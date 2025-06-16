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
    public void handleMousePressed(LeftCanvas canvas, LeftCanvas.CanvasImage image, MouseEvent e) {
        // No special press action for FlowerImageButton
    }

    @Override
    public void handleRightClick(LeftCanvas canvas, LeftCanvas.CanvasImage image) {
        // No right click action for FlowerImageButton
    }
    
    @Override
    protected String getActionHint() {
        return "Press ALT and drag up/down to scale image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.ALT_DOWN_MASK;
    }
    
    @Override
    protected void performSpecialAction(LeftCanvas canvas, LeftCanvas.CanvasImage image, 
                                     MouseEvent currentEvent, MouseEvent lastEvent, 
                                     double canvasRotation) {
        int rawDeltaY = currentEvent.getY() - lastEvent.getY();  
        double adjustedDelta;
        
        if (canvasRotation == 0) {
            adjustedDelta = rawDeltaY;
        } else {
            double cos = Math.cos(-canvasRotation);  
            double sin = Math.sin(-canvasRotation);
            int rawDeltaX = currentEvent.getX() - lastEvent.getX();
            adjustedDelta = rawDeltaX * sin + rawDeltaY * cos;
        }
        
        float scaleFactor = 1.0f;
        if (adjustedDelta != 0) {
            scaleFactor = 1.0f + (float)(adjustedDelta * 0.01f);
            image.scale(scaleFactor);
        }
        canvas.repaint();
    }
}
