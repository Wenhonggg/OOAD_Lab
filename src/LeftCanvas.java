import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

class LeftCanvas extends CanvasPanel {
    private static final int STANDARD_IMAGE_SIZE = 150;
    private List<CanvasImage> images = new ArrayList<>();
    private CanvasImage selectedImage = null;
    private CanvasImage hoveredImage = null;
    private Point lastMousePosition;
    private boolean isRotating = false;
    private boolean isPerformingSpecialAction = false;
    
    // Add canvas rotation variables
    private double canvasRotation = 0;
    private boolean isRotatingCanvas = false;
    private Point canvasRotationCenter;
    
    public LeftCanvas(int width, int height) {
        super(width, height);
        setupMouseListeners();
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint();
                
                // Determine if clicking on an image or empty space
                CanvasImage clickedImage = getImageAt(e.getX(), e.getY());
                
                if (clickedImage != null) {
                    // Clicked on an image
                    selectedImage = clickedImage;
                    
                    if (e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        isRotating = true;
                        isPerformingSpecialAction = false;
                        isRotatingCanvas = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } 
                    else if (selectedImage.getSourceType() != null) {
                        int modifiers = e.getModifiersEx();
                        
                        // Animal - SHIFT key
                        if (selectedImage.getSourceType().equals("Animal") && 
                            (modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            isRotatingCanvas = false;
                            selectedImage.flip(true); // Flip horizontally
                            repaint();
                        }
                        // Flower - ALT key
                        else if (selectedImage.getSourceType().equals("Flower") && 
                                (modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            isRotatingCanvas = false;
                            // Initial scale will be done in mouseDragged
                        }
                        // Custom - CTRL+SHIFT
                        else if (selectedImage.getSourceType().equals("Custom") && 
                                (modifiers & (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) 
                                == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) {
                            isPerformingSpecialAction = true;
                            isRotating = false;
                            isRotatingCanvas = false;
                            setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        }
                    }
                } 
                else {
                    // Clicked on empty space - check for canvas rotation
                    if (e.isControlDown()) {
                        isRotatingCanvas = true;
                        isRotating = false;
                        isPerformingSpecialAction = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        // Use center of canvas as rotation point
                        canvasRotationCenter = new Point(getWidth()/2, getHeight()/2);
                    } else {
                        // Just deselect
                        selectedImage = null;
                    }
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isRotating = false;
                isPerformingSpecialAction = false;
                isRotatingCanvas = false;
                setCursor(Cursor.getDefaultCursor());
                lastMousePosition = null;
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRotating && !isPerformingSpecialAction && !isRotatingCanvas) {
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
                hoveredImage = getImageAt(e.getX(), e.getY());
                
                if (hoveredImage != previousHover) {
                    repaint();
                }
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastMousePosition == null) {
                    return;
                }
                
                if (isRotatingCanvas) {
                    // Canvas rotation logic
                    Point center = canvasRotationCenter;
                    
                    double angle1 = Math.atan2(
                        lastMousePosition.y - center.y,
                        lastMousePosition.x - center.x
                    );
                    double angle2 = Math.atan2(
                        e.getY() - center.y,
                        e.getX() - center.x
                    );
                    
                    double deltaRotation = angle2 - angle1;
                    canvasRotation += deltaRotation;
                    
                    lastMousePosition = e.getPoint();
                    repaint();
                }
                else if (isRotating && selectedImage != null) {
                    // Rotation code for individual image - account for canvas rotation
                    Point center = new Point(
                        selectedImage.getX() + selectedImage.getWidth()/2,
                        selectedImage.getY() + selectedImage.getHeight()/2
                    );
                    
                    // Get points in canvas space
                    Point p1 = transformPoint(lastMousePosition.x, lastMousePosition.y);
                    Point p2 = transformPoint(e.getX(), e.getY());
                    
                    double angle1 = Math.atan2(
                        p1.y - center.y,
                        p1.x - center.x
                    );
                    double angle2 = Math.atan2(
                        p2.y - center.y,
                        p2.x - center.x
                    );
                    
                    double rotation = angle2 - angle1;
                    selectedImage.rotate(rotation);
                    
                    lastMousePosition = e.getPoint();
                    repaint();
                }
                else if (isPerformingSpecialAction && selectedImage != null) {
                    if (selectedImage.getSourceType().equals("Flower")) {
                        // Get the raw mouse movement in screen space
                        int rawDeltaY = e.getY() - lastMousePosition.y;  // Positive means moving down
                        
                        // Transform the vertical movement to account for canvas rotation
                        // We need the standard up/down direction in user's perspective
                        double adjustedDelta;
                        
                        if (canvasRotation == 0) {
                            // No rotation, use raw delta directly
                            adjustedDelta = rawDeltaY;
                        } else {
                            // With rotation, we need to project the movement onto the "true" vertical axis
                            // in the user's perspective (not the canvas perspective)
                            double cos = Math.cos(-canvasRotation);  // Negative rotation to convert back to screen space
                            double sin = Math.sin(-canvasRotation);
                            
                            int rawDeltaX = e.getX() - lastMousePosition.x;
                            
                            // Project the 2D movement vector onto the user's vertical axis
                            adjustedDelta = rawDeltaX * sin + rawDeltaY * cos;
                        }
                        
                        // Scale down when moving mouse up (negative delta), scale up when moving down (positive delta)
                        float scaleFactor = 1.0f;
                        if (adjustedDelta != 0) {
                            scaleFactor = 1.0f + (float)(adjustedDelta * 0.01f);
                            selectedImage.scale(scaleFactor);
                        }
                    }
                    else if (selectedImage.getSourceType().equals("Custom")) {
                        // Moving for Custom images in the user's coordinate space
                        int rawDeltaX = e.getX() - lastMousePosition.x;
                        int rawDeltaY = e.getY() - lastMousePosition.y;
                        
                        // Transform movement direction based on canvas rotation
                        double cos = Math.cos(-canvasRotation);
                        double sin = Math.sin(-canvasRotation);
                        int deltaX = (int)(rawDeltaX * cos - rawDeltaY * sin);
                        int deltaY = (int)(rawDeltaX * sin + rawDeltaY * cos);
                        
                        selectedImage.move(deltaX, deltaY, getWidth(), getHeight());
                    }
                    
                    lastMousePosition = e.getPoint();
                    repaint();
                }
            }
        });
    }

    
    // Modified to use transformations for hit detection
    private CanvasImage getImageAt(int x, int y) {
        Point transformedPoint = transformPoint(x, y);
        
        for (CanvasImage image : images) {
            if (image.containsPoint(transformedPoint.x, transformedPoint.y)) {
                return image;
            }
        }
        return null;
    }
    
    // Transform a point from screen space to canvas space (accounting for canvas rotation)
    private Point transformPoint(int x, int y) {
        if (canvasRotation == 0) {
            return new Point(x, y);
        }
        
        // Center of rotation
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Translate point to origin
        double translatedX = x - centerX;
        double translatedY = y - centerY;
        
        // Apply inverse rotation
        double cos = Math.cos(-canvasRotation);
        double sin = Math.sin(-canvasRotation);
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;
        
        // Translate back
        int finalX = (int)(rotatedX + centerX);
        int finalY = (int)(rotatedY + centerY);
        
        return new Point(finalX, finalY);
    }
    
    public void selectImageAt(int x, int y) {
        selectedImage = getImageAt(x, y);
        repaint();
    }
    
    public void addImage(String imagePath, String sourceType) {
        int x = (getWidth() - STANDARD_IMAGE_SIZE) / 2;
        int y = (getHeight() - STANDARD_IMAGE_SIZE) / 2;
        
        addImageAt(imagePath, x, y, sourceType);
    }
    
    public void addImageAt(String imagePath, int x, int y, String sourceType) {
        // Transform the drop coordinates from screen space to canvas space
        Point transformedPoint = transformPoint(x, y);
        
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        
        // Center the image more precisely on the drop point
        int centeredX = transformedPoint.x - (STANDARD_IMAGE_SIZE / 2);
        int centeredY = transformedPoint.y - (STANDARD_IMAGE_SIZE / 2);
        
        // Use centered coordinates for bounds checking
        int finalX = Math.max(0, Math.min(centeredX, getWidth() - STANDARD_IMAGE_SIZE));
        int finalY = Math.max(0, Math.min(centeredY, getHeight() - STANDARD_IMAGE_SIZE));
        
        CanvasImage canvasImage = new CanvasImage(scaledImage, finalX, finalY, sourceType);
        // Initialize with the inverse of current canvas rotation to face north
        if (canvasRotation != 0) {
            canvasImage.setRotationAngle(-canvasRotation);
        }
        images.add(canvasImage);
        selectedImage = canvasImage;
        repaint();
    }
    
    // Add a method to realign images with user perspective
    public void realignSelectedImage() {
        if (selectedImage != null) {
            // Reset the image rotation to compensate for canvas rotation
            selectedImage.setRotationAngle(-canvasRotation);
            repaint();
        }
    }
    
    public void resetCanvasRotation() {
        canvasRotation = 0;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        // Store all hover hints to render later in screen space
        List<HintInfo> hoverHints = new ArrayList<>();
        
        // Apply canvas rotation
        if (canvasRotation != 0) {
            // Rotate around the center of the canvas
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            AffineTransform canvasTransform = new AffineTransform();
            canvasTransform.translate(centerX, centerY);
            canvasTransform.rotate(canvasRotation);
            canvasTransform.translate(-centerX, -centerY);
            g2d.transform(canvasTransform);
            
            // Draw rotation indicator
            if (isRotatingCanvas) {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillOval(centerX - 10, centerY - 10, 20, 20);
                g2d.setColor(Color.RED);
                g2d.drawLine(centerX, centerY, centerX + 30, centerY);
            }
        }
        
        for (CanvasImage canvasImage : images) {
            canvasImage.draw(g2d, canvasImage == selectedImage, canvasImage == hoveredImage, isRotating);
            
            // Collect hover hint info but don't draw it yet
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
                    // Calculate screen position for the hint
                    int centerX = canvasImage.getX() + canvasImage.getWidth()/2;
                    int centerY = canvasImage.getY() - 25;
                    
                    // Transform this point to screen space if canvas is rotated
                    Point screenPoint;
                    if (canvasRotation != 0) {
                        // Inverse transform - canvas space to screen space
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        
                        // Translate to origin
                        double tx = centerX - cx;
                        double ty = centerY - cy;
                        
                        // Rotate
                        double cos = Math.cos(canvasRotation);
                        double sin = Math.sin(canvasRotation);
                        double rx = tx * cos - ty * sin;
                        double ry = tx * sin + ty * cos;
                        
                        // Translate back
                        screenPoint = new Point((int)(rx + cx), (int)(ry + cy));
                    } else {
                        screenPoint = new Point(centerX, centerY);
                    }
                    
                    hoverHints.add(new HintInfo(hint, screenPoint.x, screenPoint.y));
                }
            }
        }
        
        // Reset transform for screen space elements
        g2d.setTransform(new AffineTransform());
        
        // Draw all hover hints in screen space
        for (HintInfo hintInfo : hoverHints) {
            // Use larger, bold font for better visibility
            Font hintFont = new Font("Arial", Font.BOLD, 14);
            g2d.setFont(hintFont);
            FontMetrics fm = g2d.getFontMetrics(hintFont);
            int textWidth = fm.stringWidth(hintInfo.text);
            int padding = 12;
            
            // Darker background with higher opacity for better contrast
            g2d.setColor(new Color(0, 0, 0, 220)); // More opaque black
            g2d.fillRoundRect(
                hintInfo.x - textWidth/2 - padding, 
                hintInfo.y - fm.getHeight() + 5, 
                textWidth + 2*padding, 
                fm.getHeight() + 12, // Slightly larger height
                12, // Rounder corners
                12
            );
            
            // Brighter white text
            g2d.setColor(Color.WHITE);
            g2d.drawString(
                hintInfo.text, 
                hintInfo.x - textWidth/2, 
                hintInfo.y
            );
        }
        
        // Always show rotation hint with larger text
        String rotationHint = canvasRotation != 0 ? "Canvas is rotated" : "Hold CTRL to rotate canvas and images";
        
        // Create larger, bold font for better visibility
        Font rotationFont = new Font("Arial", Font.BOLD, 16);
        g2d.setFont(rotationFont);
        FontMetrics fm = g2d.getFontMetrics(rotationFont);
        int textWidth1 = fm.stringWidth(rotationHint);
        int padding = 8;
        
        // Better contrast background with higher opacity
        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRoundRect(
            10, 
            10, 
            textWidth1 + 2*padding, 
            fm.getHeight() + padding*2, 
            10, 
            10
        );
        
        // Draw text with a subtle drop shadow for even better readability
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(rotationHint, 11 + padding, 11 + fm.getAscent() + padding/2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(rotationHint, 10 + padding, 10 + fm.getAscent() + padding/2);
        
        g2d.dispose();
    }
    
    public static class CanvasImage {
        private Image image;
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
    
    // Helper class to store hint information
    private static class HintInfo {
        String text;
        int x;
        int y;
        
        public HintInfo(String text, int x, int y) {
            this.text = text;
            this.x = x;
            this.y = y;
        }
    }
}