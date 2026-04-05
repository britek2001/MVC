package mvc.model.shapes;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;

public interface GameShape extends Serializable {
    boolean intersects(GameShape other);
    double getArea();
    void draw(Graphics2D g);
    boolean contains(Point2D p);
    void translate(double dx, double dy);
    void resize(double factor);
    Color getColor();
    void setColor(Color color);
    GameShape clone();
}