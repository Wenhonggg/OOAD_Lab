import java.awt.*;
import javax.swing.*;

class CanvasPanel extends JPanel {
    public CanvasPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
    }
}