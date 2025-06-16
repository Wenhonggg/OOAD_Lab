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
    public void handleMousePressed(LeftCanvas canvas, LeftCanvas.CanvasImage image, MouseEvent e) {
        // No special press action for CustomImageButton
    }

    @Override
    public void handleRightClick(LeftCanvas canvas, LeftCanvas.CanvasImage image) {
        // No right click action for CustomImageButton
    }
    
    @Override
    protected String getActionHint() {
        return "Press CTRL+SHIFT to move image";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK;
    }
    
    @Override
    protected void performSpecialAction(LeftCanvas canvas, LeftCanvas.CanvasImage image, 
                                     MouseEvent currentEvent, MouseEvent lastEvent, 
                                     double canvasRotation) {
        int rawDeltaX = currentEvent.getX() - lastEvent.getX();
        int rawDeltaY = currentEvent.getY() - lastEvent.getY();
        
        double cos = Math.cos(-canvasRotation);
        double sin = Math.sin(-canvasRotation);
        int deltaX = (int)(rawDeltaX * cos - rawDeltaY * sin);
        int deltaY = (int)(rawDeltaX * sin + rawDeltaY * cos);
        
        image.move(deltaX, deltaY, canvas.getWidth(), canvas.getHeight());
        canvas.repaint();
    }
}
