import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class LeftCanvas extends CanvasPanel {
    private static final int STANDARD_IMAGE_SIZE = 150;
    private List<CanvasImage> images = new ArrayList<>();
    
    public LeftCanvas(int width, int height) {
        super(width, height);
    }
    
    public void addImage(String imagePath) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        // Calculate center position
        int x = (getWidth() - STANDARD_IMAGE_SIZE) / 2;
        int y = (getHeight() - STANDARD_IMAGE_SIZE) / 2;
        
        addImageAt(imagePath, x, y);
    }
    
    // New method to add image at a specific position
    public void addImageAt(String imagePath, int x, int y) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        // Ensure image stays within canvas bounds
        x = Math.max(0, Math.min(x, getWidth() - STANDARD_IMAGE_SIZE));
        y = Math.max(0, Math.min(y, getHeight() - STANDARD_IMAGE_SIZE));
        
        CanvasImage canvasImage = new CanvasImage(scaledImage, x, y);
        images.add(canvasImage);
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (CanvasImage image : images) {
            g.drawImage(image.getImage(), image.getX(), image.getY(), this);
        }
    }
    
    // Inner class to represent an image on the canvas
    private static class CanvasImage {
        private Image image;
        private int x;
        private int y;
        
        public CanvasImage(Image image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }
        
        public Image getImage() {
            return image;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
    }
}