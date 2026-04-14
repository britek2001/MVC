package mvc.model.shapes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;
import java.awt.geom.Point2D;

class ShapeTest {

    private Circle circle;
    private Rectangle rectangle;

    @BeforeEach
    void setUp() {
        circle = new Circle(100, 100, 30, Color.RED);
        rectangle = new Rectangle(150, 150, 60, 50, Color.BLUE);
    }

    @Test
    @DisplayName("testCircleContains")
    void testCircleContains() {
        assertTrue(circle.contains(new Point2D.Double(100, 100)));  // Center
        assertTrue(circle.contains(new Point2D.Double(115, 100))); // Inside
        assertFalse(circle.contains(new Point2D.Double(140, 100))); // Outside
    }

    @Test
    @DisplayName("testRectangleContains")
    void testRectangleContains() {
        assertTrue(rectangle.contains(new Point2D.Double(180, 175))); // Inside
        assertFalse(rectangle.contains(new Point2D.Double(100, 100))); // Outside
    }

    @Test
    @DisplayName("testCircleRectangleIntersection")
    void testCircleRectangleIntersection() {
        Rectangle overlappingRect = new Rectangle(85, 85, 30, 30, Color.GREEN);
        Rectangle farRect = new Rectangle(300, 300, 30, 30, Color.GREEN);
        
        assertTrue(circle.intersects(overlappingRect));
        assertFalse(circle.intersects(farRect));
    }

    @Test
    @DisplayName("testCircleCircleIntersection")
    void testCircleCircleIntersection() {
        Circle overlappingCircle = new Circle(115, 100, 30, Color.GREEN);
        Circle farCircle = new Circle(300, 300, 30, Color.GREEN);
        
        assertTrue(circle.intersects(overlappingCircle));
        assertFalse(circle.intersects(farCircle));
    }

    @Test
    @DisplayName("testRectangleRectangleIntersection")
    void testRectangleRectangleIntersection() {
        Rectangle overlappingRect = new Rectangle(140, 140, 40, 40, Color.GREEN);
        Rectangle farRect = new Rectangle(300, 300, 40, 40, Color.GREEN);
        
        assertTrue(rectangle.intersects(overlappingRect));
        assertFalse(rectangle.intersects(farRect));
    }

    @Test
    @DisplayName("testCircleZoneType")
    void testCircleZoneType() {
        assertEquals(1, circle.getZoneType(new Point2D.Double(100, 100))); // Center
        assertEquals(1, circle.getZoneType(new Point2D.Double(110, 100))); // Inside center zone
        assertEquals(0, circle.getZoneType(new Point2D.Double(118, 100))); // Edge zone
        assertEquals(-1, circle.getZoneType(new Point2D.Double(130, 100))); // Outside
    }

    @Test
    @DisplayName("testRectangleZoneType")
    void testRectangleZoneType() {
        double centerX = 150 + 30; 
        double centerY = 150 + 25; 
        
        assertEquals(1, rectangle.getZoneType(new Point2D.Double(centerX, centerY)));
        assertEquals(0, rectangle.getZoneType(new Point2D.Double(155, 155)));
        assertEquals(-1, rectangle.getZoneType(new Point2D.Double(100, 100)));
    }

    @Test
    @DisplayName("testCircleArea")
    void testCircleArea() {
        double expectedArea = Math.PI * 30 * 30;
        assertEquals(expectedArea, circle.getArea(), 0.01);
    }

    @Test
    @DisplayName("testRectangleArea")
    void testRectangleArea() {
        double expectedArea = 60 * 50;
        assertEquals(expectedArea, rectangle.getArea(), 0.01);
    }

    @Test
    @DisplayName("testCircleMove")
    void testCircleMove() {
        double oldX = circle.getX();
        double oldY = circle.getY();
        
        circle.move(20, -10);
        
        assertEquals(oldX + 20, circle.getX());
        assertEquals(oldY - 10, circle.getY());
    }

    @Test
    @DisplayName("testRectangleResize")
    void testRectangleResize() {
        double originalWidth = rectangle.width;
        double originalHeight = rectangle.height;
        
        rectangle.resize(2.0);
        
        assertEquals(originalWidth * 2, rectangle.width);
        assertEquals(originalHeight * 2, rectangle.height);
    }

    @Test
    @DisplayName("testCircleResize")
    void testCircleResize() {
        double originalRadius = circle.getRadius();
        
        circle.resize(0.5);
        
        assertEquals(originalRadius * 0.5, circle.getRadius(), 0.01);
    }

}