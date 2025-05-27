import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

class LeftCanvas extends CanvasPanel {
    private static final int STANDARD_IMAGE_SIZE = 150;
    private List<CanvasImage> images = new ArrayList<>();
    private CanvasImage selectedImage = null;
    private CanvasImage hoveredImage = null;
    private Point lastMousePosition;
    private boolean isRotating = false;
    private boolean isPerformingSpecialAction = false;
    
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
                
                if (selectedImage != null) {
                    // Check for rotation (original functionality)
                    if (e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        isRotating = true;
                        isPerformingSpecialAction = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } 
                    // Check for special actions based on image type
                    else if (selectedImage.getSourceType() != null) {
                        int modifiers = e.getModifiersEx();
                        
                        // Animal - SHIFT key
                        if (selectedImage.getSourceType().equals("Animal") && 
                            (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            selectedImage.flip(true); // Flip horizontally
                            repaint();
                        }
                        // Flower - ALT key
                        else if (selectedImage.getSourceType().equals("Flower") && 
                                (modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            // Initial scale will be done in mouseDragged
                        }
                        // Custom - CTRL+SHIFT
                        else if (selectedImage.getSourceType().equals("Custom") && 
                                (modifiers & (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) 
                                == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        }
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isRotating = false;
                isPerformingSpecialAction = false;
                setCursor(Cursor.getDefaultCursor());
                lastMousePosition = null;
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRotating && !isPerformingSpecialAction) {
                    selectImageAt(e.getX(), e.getY());
                }
                
                // Toggle flip direction for Animal images on right-click
                if (e.getButton() == MouseEvent.BUTTON3 && selectedImage != null && 
                    selectedImage.getSourceType() != null && 
                    selectedImage.getSourceType().equals("Animal")) {
                    selectedImage.flip(false); // Flip vertically on right click
                    repaint();
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
                    if (image.containsPoint(e.getX(), e.getY())) {
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
                if (selectedImage == null || lastMousePosition == null) {
                    return;
                }
                
                if (isRotating) {
                    // Original rotation code
                    Point center = new Point(
                        selectedImage.getX() + selectedImage.getWidth()/2,
                        selectedImage.getY() + selectedImage.getHeight()/2
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
                    
                    lastMousePosition = e.getPoint();
                    repaint();
                }
                else if (isPerformingSpecialAction) {
                    if (selectedImage.getSourceType().equals("Flower")) {
                        // Scaling for Flower images
                        int deltaY = e.getY() - lastMousePosition.y;
                        float scaleFactor = 1.0f;
                        
                        // Scale down when moving mouse up, scale up when moving down
                        if (deltaY != 0) {
                            scaleFactor = 1.0f + (deltaY * 0.01f);
                            selectedImage.scale(scaleFactor);
                        }
                    }
                    else if (selectedImage.getSourceType().equals("Custom")) {
                        // Moving for Custom images
                        int deltaX = e.getX() - lastMousePosition.x;
                        int deltaY = e.getY() - lastMousePosition.y;
                        
                        selectedImage.move(deltaX, deltaY, getWidth(), getHeight());
                    }
                    
                    lastMousePosition = e.getPoint();
                    repaint();
                }
            }
        });
    }
    
    public void selectImageAt(int x, int y) {
        for (CanvasImage image : images) {
            if (image.containsPoint(x, y)) {
                selectedImage = image;
                repaint();
                return;
            }
        }
        selectedImage = null;
        repaint();
    }
    
    public void addImage(String imagePath, String sourceType) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        int x = (getWidth() - STANDARD_IMAGE_SIZE) / 2;
        int y = (getHeight() - STANDARD_IMAGE_SIZE) / 2;
        
        addImageAt(imagePath, x, y, sourceType);
    }
    
    public void addImageAt(String imagePath, int x, int y, String sourceType) {
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        x = Math.max(0, Math.min(x, getWidth() - STANDARD_IMAGE_SIZE));
        y = Math.max(0, Math.min(y, getHeight() - STANDARD_IMAGE_SIZE));
        
        CanvasImage canvasImage = new CanvasImage(scaledImage, x, y, sourceType);
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
            canvasImage.draw(g2d, canvasImage == selectedImage, canvasImage == hoveredImage, isRotating);
            
            // Draw action hint if hovered
            if (canvasImage == hoveredImage) {
                String hint = "";
                
                // Default rotation hint
                if (canvasImage.getSourceType() == null) {
                    hint = "Press and hold CTRL to rotate";
                } 
                // Special action hints based on image type
                else if (canvasImage.getSourceType().equals("Animal")) {
                    hint = "Press SHIFT to flip horizontally (Right-click to flip vertically)";
                }
                else if (canvasImage.getSourceType().equals("Flower")) {
                    hint = "Press ALT and drag up/down to scale image";
                }
                else if (canvasImage.getSourceType().equals("Custom")) {
                    hint = "Press CTRL+SHIFT to move image";
                }
                
                if (!hint.isEmpty()) {
                    g2d.setColor(new Color(0, 0, 0, 180)); // Semi-transparent black
                    
                    int x = canvasImage.getX() + canvasImage.getWidth()/2;
                    int y = canvasImage.getY() - 25;
                    
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(hint);
                    int padding = 10;
                    
                    g2d.fillRoundRect(
                        x - textWidth/2 - padding, 
                        y - fm.getHeight() + 5, 
                        textWidth + 2*padding, 
                        fm.getHeight() + 10, 
                        10, 
                        10
                    );
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                    g2d.drawString(
                        hint, 
                        x - textWidth/2, 
                        y
                    );
                }
            }
        }
        
        g2d.dispose();
    }
    
    public static class CanvasImage {
        private Image image;
        private Image originalImage;
        private int x;
        private int y;
        private double rotationAngle = 0;
        private int width = STANDARD_IMAGE_SIZE;
        private int height = STANDARD_IMAGE_SIZE;
        private float scaleX = 1.0f;
        private float scaleY = 1.0f;
        private boolean flipX = false;
        private boolean flipY = false;
        private String sourceType; // "Animal", "Flower", or "Custom"
        
        public CanvasImage(Image image, int x, int y, String sourceType) {
            this.image = image;
            this.originalImage = image;
            this.x = x;
            this.y = y;
            this.sourceType = sourceType;
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
        
        public int getWidth() {
            return width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public double getRotationAngle() {
            return rotationAngle;
        }
        
        public String getSourceType() {
            return sourceType;
        }
        
        public void setRotationAngle(double angle) {
            this.rotationAngle = angle;
        }
        
        public void rotate(double angle) {
            this.rotationAngle += angle;
        }
        
        public void flip(boolean horizontal) {
            if (horizontal) {
                flipX = !flipX;
            } else {
                flipY = !flipY;
            }
        }
        
        public void scale(float factor) {
            // Apply scaling with bounds
            float newScaleX = scaleX * factor;
            float newScaleY = scaleY * factor;
            
            // Limit scaling (0.2x to 3.0x)
            if (newScaleX >= 0.2f && newScaleX <= 3.0f && 
                newScaleY >= 0.2f && newScaleY <= 3.0f) {
                scaleX = newScaleX;
                scaleY = newScaleY;
                
                // Update width and height
                width = (int)(STANDARD_IMAGE_SIZE * scaleX);
                height = (int)(STANDARD_IMAGE_SIZE * scaleY);
            }
        }
        
        public void move(int deltaX, int deltaY, int canvasWidth, int canvasHeight) {
            int newX = x + deltaX;
            int newY = y + deltaY;
            
            // Keep image within canvas bounds
            x = Math.max(0, Math.min(newX, canvasWidth - width));
            y = Math.max(0, Math.min(newY, canvasHeight - height));
        }
        
        public boolean containsPoint(int px, int py) {
            // Create a larger hitbox around the image to make selection easier
            int hitboxPadding = 10;
            int hitboxX = x - hitboxPadding;
            int hitboxY = y - hitboxPadding;
            int hitboxWidth = width + 2 * hitboxPadding;
            int hitboxHeight = height + 2 * hitboxPadding;
            
            // Basic bounding box check
            return px >= hitboxX && px <= hitboxX + hitboxWidth &&
                   py >= hitboxY && py <= hitboxY + hitboxHeight;
        }
        
        public void draw(Graphics2D g2d, boolean isSelected, boolean isHovered, boolean isRotating) {
            // Save the original transform
            AffineTransform oldTransform = g2d.getTransform();
            
            // Calculate center point for transformations
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            
            // Apply transformations
            g2d.translate(centerX, centerY);
            g2d.rotate(rotationAngle);
            g2d.scale(flipX ? -1 : 1, flipY ? -1 : 1);
            g2d.scale(scaleX, scaleY);
            
            // Draw the image
            g2d.drawImage(image, -STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, null);
            
            // Reset transform for UI elements
            g2d.setTransform(oldTransform);
            
            // Draw selection border if selected
            if (isSelected) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2));
                
                // Draw the border correctly accounting for transformations
                if (rotationAngle == 0 && scaleX == 1.0f && scaleY == 1.0f && !flipX && !flipY) {
                    // Simple case - no transformations
                    g2d.drawRect(x-2, y-2, width+4, height+4);
                } else {
                    // Complex case - draw corner points
                    int[] corners = {
                        -STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2,  // top-left
                        STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2,   // top-right
                        STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE/2,    // bottom-right
                        -STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE/2    // bottom-left
                    };
                    
                    // Transform and draw lines between corners
                    int[] xPoints = new int[4];
                    int[] yPoints = new int[4];
                    
                    for (int i = 0; i < 4; i++) {
                        // Apply transformations to each corner
                        double px = corners[i*2];
                        double py = corners[i*2+1];
                        
                        // Apply scaling
                        px *= scaleX;
                        py *= scaleY;
                        
                        // Apply flipping
                        if (flipX) px = -px;
                        if (flipY) py = -py;
                        
                        // Apply rotation
                        double rotatedX = px * Math.cos(rotationAngle) - py * Math.sin(rotationAngle);
                        double rotatedY = px * Math.sin(rotationAngle) + py * Math.cos(rotationAngle);
                        
                        // Translate back to world coordinates
                        xPoints[i] = (int)(centerX + rotatedX);
                        yPoints[i] = (int)(centerY + rotatedY);
                    }
                    
                    // Draw the selection polygon
                    g2d.drawPolygon(xPoints, yPoints, 4);
                }
                
                if (isRotating) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(centerX - 5, centerY - 5, 10, 10);
                }
            }
        }
    }
}