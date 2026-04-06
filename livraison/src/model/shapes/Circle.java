package mvc.model.shapes;

import java.awt.*;
import java.awt.geom.*;

public class Circle implements GameShape {
    
    private double x, y, radius;
    private Color color;
    
    public Circle(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }
    
    @Override
    public boolean intersects(GameShape other) {
        return false;
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillOval((int)(x - radius), (int)(y - radius), (int)(2*radius), (int)(2*radius));
        g.setColor(Color.BLACK);
        g.drawOval((int)(x - radius), (int)(y - radius), (int)(2*radius), (int)(2*radius));
    }
    
    @Override
    public boolean contains(Point2D p) {
        return p.distance(x, y) <= radius;
    }

    @Override
    public double getX() { return x; }

    @Override
    public double getY() { return y; }
    
    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
    
    @Override
    public void resize(double factor) {
        radius *= factor;
    }
    
    @Override
    public Color getColor() { return color; }
    
    @Override
    public void setColor(Color color) { this.color = color; }
    
    @Override
    public GameShape clone() {
        return new Circle(x, y, radius, color);
    }
}