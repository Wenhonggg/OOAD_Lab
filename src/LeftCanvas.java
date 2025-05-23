import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

class LeftCanvas extends CanvasPanel {
    private static final int STANDARD_IMAGE_SIZE = 150;
    private List<CanvasImage> images = new ArrayList<>();
    private CanvasImage selectedImage = null;
    private CanvasImage hoveredImage = null;
    private Point lastMousePosition;
    private boolean isRotating = false;
    
    public LeftCanvas(int width, int height) {
        super(width, height);
        setupMouseListeners();
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectImageAt(e.getX(), e.getY());
                lastMousePosition = e.getPoint();
                
                if (selectedImage != null && e.isControlDown()) {
                    isRotating = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isRotating = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRotating) {
                    selectImageAt(e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredImage = null;
                repaint();
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // Check if mouse is over any image
                CanvasImage previousHover = hoveredImage;
                hoveredImage = null;
                for (CanvasImage image : images) {
                    if (e.getX() >= image.getX() && e.getX() <= image.getX() + STANDARD_IMAGE_SIZE &&
                        e.getY() >= image.getY() && e.getY() <= image.getY() + STANDARD_IMAGE_SIZE) {
                        hoveredImage = image;
                        break;
                    }
                }
                
                if (hoveredImage != previousHover) {
                    repaint();
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isRotating && selectedImage != null) {
                    Point center = new Point(
                        selectedImage.getX() + STANDARD_IMAGE_SIZE/2,
                        selectedImage.getY() + STANDARD_IMAGE_SIZE/2
                    );
                    
                    double angle1 = Math.atan2(
                        lastMousePosition.y - center.y,
                        lastMousePosition.x - center.x
                    );
                    double angle2 = Math.atan2(
                        e.getY() - center.y,
                        e.getX() - center.x
                    );
                    
                    double rotation = angle2 - angle1;
                    selectedImage.rotate(rotation);
                    repaint();
                    
                    lastMousePosition = e.getPoint();
                }
            }
        });
    }
    
    public void selectImageAt(int x, int y) {
        for (CanvasImage image : images) {
            if (x >= image.getX() && x <= image.getX() + STANDARD_IMAGE_SIZE &&
                y >= image.getY() && y <= image.getY() + STANDARD_IMAGE_SIZE) {
                selectedImage = image;
                repaint();
                return;
            }
        }
        selectedImage = null;
        repaint();
    }
    
    public void addImage(String imagePath) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        int x = (getWidth() - STANDARD_IMAGE_SIZE) / 2;
        int y = (getHeight() - STANDARD_IMAGE_SIZE) / 2;
        
        addImageAt(imagePath, x, y);
    }
    
    public void addImageAt(String imagePath, int x, int y) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        x = Math.max(0, Math.min(x, getWidth() - STANDARD_IMAGE_SIZE));
        y = Math.max(0, Math.min(y, getHeight() - STANDARD_IMAGE_SIZE));
        
        CanvasImage canvasImage = new CanvasImage(scaledImage, x, y);
        images.add(canvasImage);
        selectedImage = canvasImage;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        for (CanvasImage canvasImage : images) {
            Image img = canvasImage.getImage();
            int x = canvasImage.getX();
            int y = canvasImage.getY();
            double rotation = canvasImage.getRotationAngle();
            
            // Draw selection border if selected
            if (canvasImage == selectedImage) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(x-2, y-2, STANDARD_IMAGE_SIZE+4, STANDARD_IMAGE_SIZE+4);
                
                if (isRotating) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(
                        x + STANDARD_IMAGE_SIZE/2 - 5,
                        y + STANDARD_IMAGE_SIZE/2 - 5,
                        10, 10
                    );
                }
            }
            
            // Draw rotation hint if hovered
            if (canvasImage == hoveredImage && !isRotating) {
                g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
                g2d.fillRoundRect(
                    x + STANDARD_IMAGE_SIZE/2 - 100, 
                    y - 25, 
                    200, 
                    20, 
                    10, 
                    10
                );
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                String hint = "Press and hold CTRL to rotate";
                int textWidth = g2d.getFontMetrics().stringWidth(hint);
                g2d.drawString(
                    hint, 
                    x + STANDARD_IMAGE_SIZE/2 - textWidth/2, 
                    y - 10
                );
            }
            
            // Draw the image with rotation
            if (rotation != 0) {
                int centerX = x + STANDARD_IMAGE_SIZE / 2;
                int centerY = y + STANDARD_IMAGE_SIZE / 2;
                g2d.rotate(rotation, centerX, centerY);
                g2d.drawImage(img, x, y, this);
                g2d.rotate(-rotation, centerX, centerY);
            } else {
                g2d.drawImage(img, x, y, this);
            }
        }
        g2d.dispose();
    }
    
    private static class CanvasImage {
        private Image image;
        private int x;
        private int y;
        private double rotationAngle = 0;
        
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
        
        public double getRotationAngle() {
            return rotationAngle;
        }
        
        public void setRotationAngle(double angle) {
            this.rotationAngle = angle;
        }
        
        public void rotate(double angle) {
            this.rotationAngle += angle;
        }
    }
}