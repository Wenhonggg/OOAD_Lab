import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

class RightCanvas extends CanvasPanel {
    private DrawingToolPanel toolPanel;
    private int prevX, prevY;
    private List<DrawingInstruction> drawingInstructions;

    public RightCanvas(DrawingToolPanel toolPanel) {
        super(650, 730);
        this.toolPanel = toolPanel;
        this.drawingInstructions = new ArrayList<>();
        setBackground(Color.WHITE); 
        setupDrawing();
        
        System.out.println("=== RIGHT CANVAS CONSTRUCTOR ===");
        System.out.println("Created RightCanvas: " + this);
        System.out.println("DrawingToolPanel: " + toolPanel);
        System.out.println("================================");
    }

    public void setToolPanel(DrawingToolPanel toolPanel) {
        this.toolPanel = toolPanel;
        System.out.println("Updated toolPanel: " + toolPanel);
    }

    private void setupDrawing() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (toolPanel == null) {
                    System.out.println("ERROR: toolPanel is null!");
                    return;
                }
                
                prevX = e.getX();
                prevY = e.getY();
                
                Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
                float size = toolPanel.getCurrentSize();
                
                System.out.println("=== MOUSE PRESSED DEBUG ===");
                System.out.println("Mouse pressed at: (" + e.getX() + "," + e.getY() + ")");
                System.out.println("Color: " + color + ", Size: " + size);
                System.out.println("Is eraser: " + toolPanel.isEraser());
                
                DrawingInstruction newInstruction = new DrawingInstruction(e.getX(), e.getY(), e.getX(), e.getY(), color, size);
                drawingInstructions.add(newInstruction);
                System.out.println("Added instruction: " + newInstruction);
                System.out.println("Total instructions: " + drawingInstructions.size());
                System.out.println("Canvas instance: " + RightCanvas.this);
                System.out.println("============================");
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (toolPanel == null) {
                    System.out.println("ERROR: toolPanel is null during drag!");
                    return;
                }
                
                Color color = toolPanel.isEraser() ? Color.WHITE : toolPanel.getCurrentColor();
                float size = toolPanel.getCurrentSize();
                
                System.out.println("=== MOUSE DRAGGED DEBUG ===");
                System.out.println("Mouse dragged from: (" + prevX + "," + prevY + ") to (" + e.getX() + "," + e.getY() + ")");
                System.out.println("Color: " + color + ", Size: " + size);
                
                DrawingInstruction newInstruction = new DrawingInstruction(prevX, prevY, e.getX(), e.getY(), color, size);
                drawingInstructions.add(newInstruction);
                System.out.println("Added instruction: " + newInstruction);
                
                prevX = e.getX();
                prevY = e.getY();
                System.out.println("Total instructions: " + drawingInstructions.size());
                System.out.println("Canvas instance: " + RightCanvas.this);
                System.out.println("============================");
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        System.out.println("=== PAINT COMPONENT DEBUG ===");
        System.out.println("Canvas size: " + getWidth() + "x" + getHeight());
        System.out.println("Drawing " + drawingInstructions.size() + " instructions");
        System.out.println("Canvas instance: " + this);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int drawnCount = 0;
        for (DrawingInstruction instruction : drawingInstructions) {
            g2.setColor(instruction.color);
            g2.setStroke(new BasicStroke(instruction.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (instruction.x1 == instruction.x2 && instruction.y1 == instruction.y2) {
                int radius = (int)(instruction.size / 2);
                g2.fillOval(instruction.x1 - radius, instruction.y1 - radius, (int)instruction.size, (int)instruction.size);
                System.out.println("Drew dot at (" + instruction.x1 + "," + instruction.y1 + ") with color " + instruction.color);
            } else {
                g2.drawLine(instruction.x1, instruction.y1, instruction.x2, instruction.y2);
                System.out.println("Drew line from (" + instruction.x1 + "," + instruction.y1 + ") to (" + instruction.x2 + "," + instruction.y2 + ") with color " + instruction.color);
            }
            drawnCount++;
        }
        
        System.out.println("Actually drew " + drawnCount + " instructions");
        System.out.println("===============================");
        
        g2.dispose();
    }

    public void clearCanvas() {
        System.out.println("Clearing canvas - had " + drawingInstructions.size() + " instructions");
        drawingInstructions.clear();
        repaint();
    }

    public BufferedImage saveToImage() {
        System.out.println("=== SAVE TO IMAGE DEBUG START ===");
        System.out.println("Canvas instance being saved: " + this);
        
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        
        if (width <= 0) width = getWidth() > 0 ? getWidth() : 650;
        if (height <= 0) height = getHeight() > 0 ? getHeight() : 730;
        
        System.out.println("Image dimensions: " + width + "x" + height);
        System.out.println("Number of drawing instructions: " + drawingInstructions.size());
        
        for (int i = 0; i < drawingInstructions.size(); i++) {
            DrawingInstruction inst = drawingInstructions.get(i);
            System.out.println("Instruction " + i + ": " + inst);
        }
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        System.out.println("Created image and filled with white background");
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        int savedCount = 0;
        for (DrawingInstruction instruction : drawingInstructions) {
            g2.setColor(instruction.color);
            g2.setStroke(new BasicStroke(instruction.size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            if (instruction.x1 == instruction.x2 && instruction.y1 == instruction.y2) {
                int radius = (int)(instruction.size / 2);
                g2.fillOval(instruction.x1 - radius, instruction.y1 - radius, (int)instruction.size, (int)instruction.size);
                System.out.println("SAVED dot at (" + instruction.x1 + "," + instruction.y1 + ") with color " + instruction.color + " and size " + instruction.size);
            } else {
                g2.drawLine(instruction.x1, instruction.y1, instruction.x2, instruction.y2);
                System.out.println("SAVED line from (" + instruction.x1 + "," + instruction.y1 + ") to (" + instruction.x2 + "," + instruction.y2 + ") with color " + instruction.color + " and size " + instruction.size);
            }
            savedCount++;
        }
        
        System.out.println("Actually saved " + savedCount + " instructions to image");
        
        g2.dispose();
        
        boolean hasNonWhitePixels = false;
        for (int x = 0; x < Math.min(width, 100); x += 10) {
            for (int y = 0; y < Math.min(height, 100); y += 10) {
                int rgb = image.getRGB(x, y);
                if (rgb != Color.WHITE.getRGB()) {
                    hasNonWhitePixels = true;
                    System.out.println("Found non-white pixel at (" + x + "," + y + "): " + Integer.toHexString(rgb));
                    break;
                }
            }
            if (hasNonWhitePixels) break;
        }
        
        if (!hasNonWhitePixels) {
            System.out.println("WARNING: Image appears to be all white!");
        } else {
            System.out.println("Image has non-white content - good!");
        }
        
        System.out.println("=== SAVE TO IMAGE DEBUG END ===");
        return image;
    }

    public void printInstructions() {
        System.out.println("=== CANVAS DEBUG INFO ===");
        System.out.println("Canvas instance: " + this);
        System.out.println("Canvas size: " + getWidth() + "x" + getHeight());
        System.out.println("Preferred size: " + getPreferredSize());
        System.out.println("Number of drawing instructions: " + drawingInstructions.size());
        for (int i = 0; i < drawingInstructions.size(); i++) {
            DrawingInstruction inst = drawingInstructions.get(i);
            System.out.println("Instruction " + i + ": " + inst);
        }
        System.out.println("=========================");
    }

    private static class DrawingInstruction {
        int x1, y1, x2, y2;
        Color color;
        float size;

        public DrawingInstruction(int x1, int y1, int x2, int y2, Color color, float size) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
            this.size = size;
        }
        
        @Override
        public String toString() {
            return "DrawingInstruction{" +
                    "from=(" + x1 + "," + y1 + ") to=(" + x2 + "," + y2 + "), " +
                    "color=" + color + ", size=" + size + '}';
        }
    }
}