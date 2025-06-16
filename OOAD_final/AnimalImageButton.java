import java.awt.event.InputEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.event.MouseEvent;

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
    public void handleMousePressed(LeftCanvas canvas, LeftCanvas.CanvasImage image, MouseEvent e) {
        if (e != null && image != null && e.isShiftDown()) {
            image.flip(true);
            if (canvas != null) {
                canvas.repaint();
            }
        }
    }

    @Override
    public void handleRightClick(LeftCanvas canvas, LeftCanvas.CanvasImage image) {
        image.flip(false);
        canvas.repaint();
    }
    
    @Override
    protected void performSpecialAction(LeftCanvas canvas, LeftCanvas.CanvasImage image, 
                                     MouseEvent currentEvent, MouseEvent lastEvent, 
                                     double canvasRotation) {
        // No drag action for AnimalImageButton
    }
    
    @Override
    protected String getActionHint() {
        return "Press SHIFT to flip horizontally (Right-click to flip vertically)";
    }
    
    @Override
    protected int getActionKeyModifier() {
        return InputEvent.SHIFT_DOWN_MASK;
    }
}
