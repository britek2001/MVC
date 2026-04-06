package mvc.model.shapes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Rectangle implements GameShape {

    private double x, y, width, height;
    private Color color;
    
    public Rectangle(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }
    

    @Override
    public double getX() { return x; }
    @Override
    public double getY() { return y; }
    
    @Override
    public boolean intersects(GameShape other) {
        Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
        if (other instanceof Rectangle) {
            Rectangle r = (Rectangle) other;
            return rect.intersects(r.x, r.y, r.width, r.height);
        } else if (other instanceof Circle) {
            Circle c = (Circle) other;
            return c.intersects(this);
        }
        return false;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
        g.setColor(Color.BLACK);
        g.drawRect((int)x, (int)y, (int)width, (int)height);
    }
    
    @Override
    public boolean contains(Point2D p) {
        return p.getX() >= x && p.getX() <= x + width &&
               p.getY() >= y && p.getY() <= y + height;
    }
    
  
    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
    
    @Override
    public void resize(double factor) {
        width *= factor;
        height *= factor;
    }
    
    @Override
    public Color getColor() { return color; }
    
    @Override
    public void setColor(Color color) { this.color = color; }
    
    @Override
    public GameShape clone() {
        return new Rectangle(x, y, width, height, color);
    }

    
}