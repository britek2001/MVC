package mvc.model.shapes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Rectangle implements GameShape {

    public double x, y, width, height;
    public Color color;
    private static final double CENTRAL_ZONE_RATIO = 0.6;
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
        if (other instanceof Rectangle) {
            return intersects((Rectangle) other);
        } else if (other instanceof Circle) {
            return intersects((Circle) other);
        }
        return false;
    }

    public boolean intersects(Circle circle) {
        double closestX =  Math.max(this.x,  Math.min(circle.getX(),  this.x + this.width));
        double closestY = Math.max(this.y,  Math.min(circle.getY(),  this.y + this.height));
        double distanceX = circle.getX() - closestX;
        double distanceY = circle.getY() - closestY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        return distance <= circle.getRadius();
    }


    public int getZoneType(Point2D p) {
       
        if (!contains(p)) {
            return -1;  
        }
        
        double centralWidth = width * CENTRAL_ZONE_RATIO;
        double centralHeight = height * CENTRAL_ZONE_RATIO;
        double centralX = x + (width - centralWidth) / 2;
        double centralY = y + (height - centralHeight) / 2;
        
        if (p.getX() >= centralX && p.getX() <= centralX + centralWidth &&
            p.getY() >= centralY && p.getY() <= centralY + centralHeight) {
            return 1;  
        }

        return 0;  
    }


    public boolean intersects(Rectangle rectangle) {
        return this.x < rectangle.x + rectangle.width &&
               this.x + this.width > rectangle.x &&
               this.y < rectangle.y + rectangle.height &&
               this.y + this.height > rectangle.y;
    }
    
    @Override
    public double getArea() {
        return width * height;
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
    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
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