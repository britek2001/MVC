package mvc.model.shapes;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public interface GameShape extends Serializable {
    boolean intersects(GameShape other);
    double getArea();
    public double getX();
    public double getY();
    int getZoneType(Point2D p);
    boolean contains(Point2D p);
    void move(double dx, double dy);
    void resize(double factor);
    Color getColor();
    void setColor(Color color);
    GameShape copy();
    void setPosition(double newX, double newY);
}