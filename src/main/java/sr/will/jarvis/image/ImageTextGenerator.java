package sr.will.jarvis.image;

import java.awt.*;

public class ImageTextGenerator {
    private Graphics2D g;
    private String s;
    private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
    private Color color = Color.BLACK;
    private int x = 0;
    private int y = 0;
    private int w = 0;
    private int h = 0;

    public ImageTextGenerator(Graphics2D g, String s) {
        this.g = g;
        this.s = s;
    }

    public ImageTextGenerator setFont(Font font) {
        this.font = font;
        calculateSize();
        return this;
    }

    public ImageTextGenerator setFontSize(int size) {
        return setFont(new Font(font.getName(), font.getStyle(), size));
    }

    public ImageTextGenerator setColor(Color color) {
        this.color = color;
        return this;
    }

    public ImageTextGenerator setColor(String color) {
        return setColor(Color.decode(color));
    }

    public ImageTextGenerator setPos(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public ImageTextGenerator setPos(double x, double y) {
        this.x = (int) x;
        this.y = (int) y;
        return this;
    }

    public ImageTextGenerator draw() {
        g.setFont(font);
        g.setColor(color);
        g.drawString(s, x, y);
        return this;
    }

    public ImageTextGenerator drawOutline() {
        g.setColor(Color.MAGENTA);
        g.drawRect(x, y - height(), width(), height());
        return this;
    }

    public int X() {
        return x;
    }

    public int Y() {
        return y;
    }

    public int width() {
        return w;
    }

    public int height() {
        return h;
    }

    private void calculateSize() {
        g.setFont(font);
        w = g.getFontMetrics().stringWidth(s);
        h = (int) g.getFont().createGlyphVector(g.getFontRenderContext(), s).getVisualBounds().getHeight() + 1;
    }
}
