package mvc.model.shapes;

import java.awt.*;
import java.awt.geom.*;

public class Circle implements GameShape {
    
    private double x, y, radius;
    private Color color;
    private static final double CENTRAL_ZONE_RATIO = 0.6;
    public Circle(double x, double y, double radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public boolean intersects(GameShape other) {
        if (other instanceof Rectangle) {
            return intersects((Rectangle) other);
        } else if (other instanceof Circle) {
            return intersects((Circle) other);
        }
        return false;
    }

    public boolean intersects(Rectangle rectangle) {
        double closestX =  Math.max(rectangle.x,  Math.min(this.x,  rectangle.x + rectangle.width));
        double closestY = Math.max(rectangle.y,  Math.min(this.y,  rectangle.y + rectangle.height));
        double distanceX = this.x - closestX;
        double distanceY = this.y - closestY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= this.radius;
    }

    public boolean intersects(Circle other) {
        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (this.radius + other.getRadius());
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    public int getZoneType(Point2D p) {
        if (!contains(p)) {
            return -1;  
        }
        double dx = p.getX() - x;
        double dy = p.getY() - y;
        double distance = Math.sqrt(dx * dx + dy * dy);    
        if (distance <= radius * CENTRAL_ZONE_RATIO) {
            return 1;  
        }
        return 0;  
    }
    
    @Override
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
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
    public double getRadius() {
        return radius;
    }
}