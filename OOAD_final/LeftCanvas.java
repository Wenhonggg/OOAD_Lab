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
    private double canvasRotation = 0;
    private boolean isRotatingCanvas = false;
    private Point canvasRotationCenter;
    private Toolbar toolbar;
    
    public LeftCanvas(int width, int height, Toolbar toolbar) {
        super(width, height);
        this.toolbar = toolbar;
        setupMouseListeners();
    }
    
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePosition = e.getPoint();
                CanvasImage clickedImage = getImageAt(e.getX(), e.getY());
                
                if (clickedImage != null) {
                    selectedImage = clickedImage;
                    
                    if (e.isControlDown() && !e.isShiftDown() && !e.isAltDown()) {
                        isRotating = true;
                        isPerformingSpecialAction = false;
                        isRotatingCanvas = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } 
                    else if (selectedImage.getSourceType() != null) {
                        ImageButton button = getButtonForType(selectedImage.getSourceType());
                        if (button != null) {
                            button.handleMousePressed(LeftCanvas.this, selectedImage, e);
                        }
                    }
                } 
                else {
                    if (e.isControlDown()) {
                        isRotatingCanvas = true;
                        isRotating = false;
                        isPerformingSpecialAction = false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        canvasRotationCenter = new Point(getWidth()/2, getHeight()/2);
                    } else {
                        selectedImage = null;
                    }
                    repaint();
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isRotating && !isPerformingSpecialAction && !isRotatingCanvas) {
                    selectImageAt(e.getX(), e.getY());
                }
                
                if (e.getButton() == MouseEvent.BUTTON3 && selectedImage != null && 
                    selectedImage.getSourceType() != null) {
                    ImageButton button = getButtonForType(selectedImage.getSourceType());
                    if (button != null) {
                        button.handleRightClick(LeftCanvas.this, selectedImage);
                    }
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
                    Point center = new Point(
                        selectedImage.getX() + selectedImage.getWidth()/2,
                        selectedImage.getY() + selectedImage.getHeight()/2
                    );

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
                else if (isPerformingSpecialAction && selectedImage != null && 
                         selectedImage.getSourceType() != null) {
                    ImageButton button = getButtonForType(selectedImage.getSourceType());
                    if (button != null) {
                        button.performSpecialAction(
                            LeftCanvas.this, 
                            selectedImage, 
                            e, 
                            new MouseEvent(
                                e.getComponent(), 
                                e.getID(), 
                                e.getWhen(), 
                                e.getModifiersEx(), 
                                lastMousePosition.x, 
                                lastMousePosition.y, 
                                e.getClickCount(), 
                                e.isPopupTrigger()
                            ),
                            canvasRotation
                        );
                    }
                }
            }
        });
    }

    private ImageButton getButtonForType(String type) {
        if (toolbar == null || type == null) return null;
        
        for (Component comp : toolbar.getComponents()) {
            if (comp instanceof ImageButton) {
                ImageButton button = (ImageButton) comp;
                if (type.equals(button.getType())) {
                    return button;
                }
            }
        }
        return null;
    }

    private CanvasImage getImageAt(int x, int y) {
        Point transformedPoint = transformPoint(x, y);
        
        for (CanvasImage image : images) {
            if (image.containsPoint(transformedPoint.x, transformedPoint.y)) {
                return image;
            }
        }
        return null;
    }
    
    private Point transformPoint(int x, int y) {
        if (canvasRotation == 0) {
            return new Point(x, y);
        }

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        double translatedX = x - centerX;
        double translatedY = y - centerY;

        double cos = Math.cos(-canvasRotation);
        double sin = Math.sin(-canvasRotation);
        double rotatedX = translatedX * cos - translatedY * sin;
        double rotatedY = translatedX * sin + translatedY * cos;

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
        Point transformedPoint = transformPoint(x, y);
        
        Image originalImage = new ImageIcon(imagePath).getImage();
        Image scaledImage = originalImage.getScaledInstance(
            STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, Image.SCALE_SMOOTH);
        int centeredX = transformedPoint.x - (STANDARD_IMAGE_SIZE / 2);
        int centeredY = transformedPoint.y - (STANDARD_IMAGE_SIZE / 2);
        int finalX = Math.max(0, Math.min(centeredX, getWidth() - STANDARD_IMAGE_SIZE));
        int finalY = Math.max(0, Math.min(centeredY, getHeight() - STANDARD_IMAGE_SIZE));
        
        CanvasImage canvasImage = new CanvasImage(scaledImage, finalX, finalY, sourceType);
        if (canvasRotation != 0) {
            canvasImage.setRotationAngle(-canvasRotation);
        }
        images.add(canvasImage);
        selectedImage = canvasImage;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        List<HintInfo> hoverHints = new ArrayList<>();
        
        if (canvasRotation != 0) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            AffineTransform canvasTransform = new AffineTransform();
            canvasTransform.translate(centerX, centerY);
            canvasTransform.rotate(canvasRotation);
            canvasTransform.translate(-centerX, -centerY);
            g2d.transform(canvasTransform);
            
            if (isRotatingCanvas) {
                g2d.setColor(new Color(255, 0, 0, 100));
                g2d.fillOval(centerX - 10, centerY - 10, 20, 20);
                g2d.setColor(Color.RED);
                g2d.drawLine(centerX, centerY, centerX + 30, centerY);
            }
        }
        
        for (CanvasImage canvasImage : images) {
            canvasImage.draw(g2d, canvasImage == selectedImage, canvasImage == hoveredImage, isRotating);
            
            if (canvasImage == hoveredImage && canvasImage.getSourceType() != null) {
                ImageButton button = getButtonForType(canvasImage.getSourceType());
                if (button != null) {
                    String hint = button.getActionHint();
                    
                    if (hint != null && !hint.isEmpty()) {
                        int centerX = canvasImage.getX() + canvasImage.getWidth()/2;
                        int centerY = canvasImage.getY() - 25;

                        Point screenPoint;
                        if (canvasRotation != 0) {
                            int cx = getWidth() / 2;
                            int cy = getHeight() / 2;

                            double tx = centerX - cx;
                            double ty = centerY - cy;

                            double cos = Math.cos(canvasRotation);
                            double sin = Math.sin(canvasRotation);
                            double rx = tx * cos - ty * sin;
                            double ry = tx * sin + ty * cos;

                            screenPoint = new Point((int)(rx + cx), (int)(ry + cy));
                        } else {
                            screenPoint = new Point(centerX, centerY);
                        }
                        
                        hoverHints.add(new HintInfo(hint, screenPoint.x, screenPoint.y));
                    }
                }
            }
        }

        g2d.setTransform(new AffineTransform());

        for (HintInfo hintInfo : hoverHints) {
            Font hintFont = new Font("Arial", Font.BOLD, 14);
            g2d.setFont(hintFont);
            FontMetrics fm = g2d.getFontMetrics(hintFont);
            int textWidth = fm.stringWidth(hintInfo.text);
            int padding = 12;

            g2d.setColor(new Color(0, 0, 0, 220)); 
            g2d.fillRoundRect(
                hintInfo.x - textWidth/2 - padding, 
                hintInfo.y - fm.getHeight() + 5, 
                textWidth + 2*padding, 
                fm.getHeight() + 12, 
                12,
                12
            );

            g2d.setColor(Color.WHITE);
            g2d.drawString(
                hintInfo.text, 
                hintInfo.x - textWidth/2, 
                hintInfo.y
            );
        }

        String rotationHint = canvasRotation != 0 ? "Canvas is rotated" : "Hold CTRL to rotate canvas and images";

        Font rotationFont = new Font("Arial", Font.BOLD, 16);
        g2d.setFont(rotationFont);
        FontMetrics fm = g2d.getFontMetrics(rotationFont);
        int textWidth1 = fm.stringWidth(rotationHint);
        int padding = 8;

        g2d.setColor(new Color(0, 0, 0, 220));
        g2d.fillRoundRect(
            10, 
            10, 
            textWidth1 + 2*padding, 
            fm.getHeight() + padding*2, 
            10, 
            10
        );

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(rotationHint, 11 + padding, 11 + fm.getAscent() + padding/2);
        g2d.setColor(Color.WHITE);
        g2d.drawString(rotationHint, 10 + padding, 10 + fm.getAscent() + padding/2);
        
        g2d.dispose();
    }
    
    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
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
        private String sourceType; 
        
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
            float newScaleX = scaleX * factor;
            float newScaleY = scaleY * factor;

            if (newScaleX >= 0.2f && newScaleX <= 3.0f && 
                newScaleY >= 0.2f && newScaleY <= 3.0f) {
                scaleX = newScaleX;
                scaleY = newScaleY;
                width = (int)(STANDARD_IMAGE_SIZE * scaleX);
                height = (int)(STANDARD_IMAGE_SIZE * scaleY);
            }
        }
        
        public void move(int deltaX, int deltaY, int canvasWidth, int canvasHeight) {
            int newX = x + deltaX;
            int newY = y + deltaY;
            x = Math.max(0, Math.min(newX, canvasWidth - width));
            y = Math.max(0, Math.min(newY, canvasHeight - height));
        }
        
        public boolean containsPoint(int px, int py) {
            int hitboxPadding = 10;
            int hitboxX = x - hitboxPadding;
            int hitboxY = y - hitboxPadding;
            int hitboxWidth = width + 2 * hitboxPadding;
            int hitboxHeight = height + 2 * hitboxPadding;
            return px >= hitboxX && px <= hitboxX + hitboxWidth &&
                   py >= hitboxY && py <= hitboxY + hitboxHeight;
        }
        
        public void draw(Graphics2D g2d, boolean isSelected, boolean isHovered, boolean isRotating) {
            AffineTransform oldTransform = g2d.getTransform();
            
            int centerX = x + width / 2;
            int centerY = y + height / 2;

            g2d.translate(centerX, centerY);
            g2d.rotate(rotationAngle);
            g2d.scale(flipX ? -1 : 1, flipY ? -1 : 1);
            g2d.scale(scaleX, scaleY);

            g2d.drawImage(image, -STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE, STANDARD_IMAGE_SIZE, null);

            g2d.setTransform(oldTransform);

            if (isSelected) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2));

                if (rotationAngle == 0 && scaleX == 1.0f && scaleY == 1.0f && !flipX && !flipY) {
                    g2d.drawRect(x-2, y-2, width+4, height+4);
                } else {
                    int[] corners = {
                        -STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2,  
                        STANDARD_IMAGE_SIZE/2, -STANDARD_IMAGE_SIZE/2,   
                        STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE/2,    
                        -STANDARD_IMAGE_SIZE/2, STANDARD_IMAGE_SIZE/2    
                    };
                    int[] xPoints = new int[4];
                    int[] yPoints = new int[4];
                    
                    for (int i = 0; i < 4; i++) {
                        double px = corners[i*2];
                        double py = corners[i*2+1];

                        px *= scaleX;
                        py *= scaleY;

                        if (flipX) px = -px;
                        if (flipY) py = -py;

                        double rotatedX = px * Math.cos(rotationAngle) - py * Math.sin(rotationAngle);
                        double rotatedY = px * Math.sin(rotationAngle) + py * Math.cos(rotationAngle);

                        xPoints[i] = (int)(centerX + rotatedX);
                        yPoints[i] = (int)(centerY + rotatedY);
                    }

                    g2d.drawPolygon(xPoints, yPoints, 4);
                }
                
                if (isRotating) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(centerX - 5, centerY - 5, 10, 10);
                }
            }
        }
    }

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