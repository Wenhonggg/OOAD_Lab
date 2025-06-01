import javax.swing.*;
import java.io.File;

public class FlowerImageButton extends ImageButton {
    
    public FlowerImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Flower";
        this.filePath = "assets/flowers";
        setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/flowerIcon.png"), 30, 30)));
    }
}
