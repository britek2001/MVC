package mvc.model.commands;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;

import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.shapes.Rectangle;

class CommandTest {

    private GameModel model;
    private Circle testCircle;
    private Rectangle testRectangle;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        model.setGameAreaSize(800, 600);
        testCircle = new Circle(100, 100, 20, Color.BLUE);
        testRectangle = new Rectangle(200, 200, 50, 40, Color.BLUE);
    }

    @Test
    @DisplayName("testCreateShapeCommand")
    void testCreateShapeCommand() {
        CreateShapeCommand cmd = new CreateShapeCommand(model, testCircle);
        
        // Execute
        cmd.execut();
        assertTrue(model.getBlueShapes().contains(testCircle));
        assertEquals(1, model.getBlueShapes().size());
        
        // Undo
        cmd.undo();
        assertFalse(model.getBlueShapes().contains(testCircle));
        assertEquals(0, model.getBlueShapes().size());
        
        // Redo
        cmd.redo();
        assertTrue(model.getBlueShapes().contains(testCircle));
        assertEquals(1, model.getBlueShapes().size());
    }

    @Test
    @DisplayName("testDeleteShapeCommand")
    void testDeleteShapeCommand() {
        model.addBlueShape(testCircle);
        assertTrue(model.getBlueShapes().contains(testCircle));
        
        DeleteShapeCommand cmd = new DeleteShapeCommand(model, testCircle);
        
        // Execute
        cmd.execut();
        assertFalse(model.getBlueShapes().contains(testCircle));
        
        // Undo
        cmd.undo();
        assertTrue(model.getBlueShapes().contains(testCircle));
        
        // Redo
        cmd.redo();
        assertFalse(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testMoveShapeCommand")
    void testMoveShapeCommand() {
        model.addBlueShape(testCircle);
        double oldX = testCircle.getX();
        double oldY = testCircle.getY();
        double newX = oldX + 50;
        double newY = oldY + 30;
        
        MoveShapeCommand cmd = new MoveShapeCommand(model, testCircle, oldX, oldY, newX, newY);
        
        // Execute
        cmd.execut();
        assertEquals(newX, testCircle.getX());
        assertEquals(newY, testCircle.getY());
        
        // Undo
        cmd.undo();
        assertEquals(oldX, testCircle.getX());
        assertEquals(oldY, testCircle.getY());
        
        // Redo
        cmd.redo();
        assertEquals(newX, testCircle.getX());
        assertEquals(newY, testCircle.getY());
    }

    @Test
    @DisplayName("testResizeShapeCommandCircle")
    void testResizeShapeCommandCircle() {
        model.addBlueShape(testCircle);
        double originalRadius = testCircle.getRadius();
        double factor = 1.5;
        
        ResizeShapeCommand cmd = new ResizeShapeCommand(model, testCircle, factor, 0);
        
        cmd.execut();
        assertEquals(originalRadius * factor, testCircle.getRadius(), 0.01);
        
        cmd.undo();
        assertEquals(originalRadius, testCircle.getRadius(), 0.01);
        
        cmd.redo();
        assertEquals(originalRadius * factor, testCircle.getRadius(), 0.01);
    }

    @Test
    @DisplayName("testResizeShapeCommandRectangle")
    void testResizeShapeCommandRectangle() {
        model.addBlueShape(testRectangle);
        double originalWidth = testRectangle.width;
        double originalHeight = testRectangle.height;
        double factorX = 1.5;
        double factorY = 0;
        
        ResizeShapeCommand cmd = new ResizeShapeCommand(model, testRectangle, factorX, factorY);
        
        // Execute
        cmd.execut();
        assertEquals(originalWidth * factorX, testRectangle.width, 0.01);
        assertEquals(originalHeight * factorX, testRectangle.height, 0.01);
        
        // Undo
        cmd.undo();
        assertEquals(originalWidth, testRectangle.width, 0.01);
        assertEquals(originalHeight, testRectangle.height, 0.01);
        
        // Redo
        cmd.redo();
        assertEquals(originalWidth * factorX, testRectangle.width, 0.01);
        assertEquals(originalHeight * factorX, testRectangle.height, 0.01);
    }

    @Test
    @DisplayName("testResizeShapeCommandRectangleWithZeroFactor")
    void testResizeShapeCommandRectangleWithZeroFactor() {
        model.addBlueShape(testRectangle);
        double originalWidth = testRectangle.width;
        double originalHeight = testRectangle.height;
        
        ResizeShapeCommand cmd = new ResizeShapeCommand(model, testRectangle, 0, 0);
        
        cmd.execut();
        assertEquals(originalWidth, testRectangle.width, 0.01);
        assertEquals(originalHeight, testRectangle.height, 0.01);
    }

}