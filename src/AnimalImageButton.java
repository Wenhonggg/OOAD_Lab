import javax.swing.*;
import java.io.File;

public class AnimalImageButton extends ImageButton {
    
    public AnimalImageButton(JFrame frame) {
        super(frame);
    }
    
    @Override
    protected void initialize() {
        this.type = "Animal";
        this.filePath = "assets/animals";
        setIcon(new ImageIcon(resizeImage(new File("assets/toolbarIcons/animalIcon.png"), 30, 30)));
    }
}
