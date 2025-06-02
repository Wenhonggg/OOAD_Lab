import java.awt.event.*;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class NewButton extends JButton {
    Toolbar parent;

    public NewButton(Toolbar tb) {
        super();
        // Fixed the missing parenthesis in this line only
        setIcon(new ImageIcon(ImageButton.resizeImage(new File("assets/toolbarIcons/newIcon.png"), 30, 30)));
        parent = tb;
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int x = getX(), y = getY();
                String position = parent.getToolbarPosition();
                JPopupMenu chooseCanvas = createPopupMenu();

                if ("NORTH".equals(position))
                    y += getHeight();
                else if ("SOUTH".equals(position))
                    y -= chooseCanvas.getPreferredSize().height;
                else if ("WEST".equals(position))
                    x += getWidth();
                else
                    x -= chooseCanvas.getPreferredSize().width + 5;

                chooseCanvas.show(parent, x, y);
            }
        });
    }

    private JPopupMenu createPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem leftCanvas = new JMenuItem("Left canvas");
        leftCanvas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.createNewLeftCanvas();
            }
        });
        JMenuItem rightCanvas = new JMenuItem("Right canvas");
        rightCanvas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.createNewRightCanvas();
            }
        });
        popupMenu.add(leftCanvas);
        popupMenu.add(rightCanvas);

        return popupMenu;
    }
}