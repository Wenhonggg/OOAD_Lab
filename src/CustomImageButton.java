import javax.swing.*;
import java.io.File;

public class CustomImageButton extends ImageButton {
    
    public CustomImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Custom";
        this.filePath = "assets/custom";
        setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/customIcon.png"), 30, 30)));
    }
}
