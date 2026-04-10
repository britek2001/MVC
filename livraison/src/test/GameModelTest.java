package mvc.model.game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import mvc.model.commands.*;
import mvc.model.shapes.*;
import mvc.model.strategies.*;

class GameModelTest {

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
    @DisplayName("testAddBlueShape")
    void testAddBlueShape() {
        boolean added = model.addBlueShape(testCircle);
        
        assertTrue(added);
        assertEquals(1, model.getBlueShapes().size());
        assertTrue(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testAddBlueShapeWhenGameFinished")
    void testAddBlueShapeWhenGameFinished() {
        for (int i = 0; i < 4; i++) {
            model.addBlueShape(new Circle(50 + i*50, 50, 20, Color.BLUE));
        }
        
        boolean added = model.addBlueShape(testCircle);
        
        assertFalse(added);
    }

    @Test
    @DisplayName("testAddBlueShapeIntersectsRed")
    void testAddBlueShapeIntersectsRed() {
        model.setGenerationStrategy(new RandomGenerationStrategy());
        model.generateRedShapes(1, 800, 600);
        
        if (!model.getRedShapes().isEmpty()) {
            GameShape red = model.getRedShapes().get(0);
            Circle intersectingCircle = new Circle(red.getX(), red.getY(), 30, Color.BLUE);
            
            boolean added = model.addBlueShape(intersectingCircle);
            
            assertFalse(added);
        }
    }

    @Test
    @DisplayName("testAddBlueShapeOutsideGameArea")
    void testAddBlueShapeOutsideGameArea() {
        Circle outsideCircle = new Circle(-50, -50, 20, Color.BLUE);
        
        boolean added = model.addBlueShape(outsideCircle);
        
        assertFalse(added);
        assertEquals(0, model.getBlueShapes().size());
    }


    @Test
    @DisplayName("testRemoveBlueShape")
    void testRemoveBlueShape() {
        model.addBlueShape(testCircle);
        assertEquals(1, model.getBlueShapes().size());
        
        model.removeBlueShape(testCircle);
        
        assertEquals(0, model.getBlueShapes().size());
        assertFalse(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testRemoveBlueShapeDecrementsCounter")
    void testRemoveBlueShapeDecrementsCounter() {
        model.addBlueShape(testCircle);
        int counterBefore = model.getBlueShapesPlacedThisLevel();
        
        model.removeBlueShape(testCircle);
        
        assertEquals(counterBefore - 1, model.getBlueShapesPlacedThisLevel());
    }


    @Test
    @DisplayName("testFindBlueShapeAt")
    void testFindBlueShapeAt() {
        model.addBlueShape(testCircle);
        
        GameShape found = model.findBlueShapeAt(105, 105);
        
        assertNotNull(found);
        assertEquals(testCircle, found);
    }

    @Test
    @DisplayName("testFindBlueShapeAtNotFound")
    void testFindBlueShapeAtNotFound() {
        model.addBlueShape(testCircle);
        
        GameShape found = model.findBlueShapeAt(500, 500);
        
        assertNull(found);
    }


    @Test
    @DisplayName("testCanPlaceBlueShapeIntersection")
    void testCanPlaceBlueShapeIntersection() {
        model.addBlueShape(testCircle);
        Rectangle overlappingRect = new Rectangle(90, 90, 30, 30, Color.BLUE);
        
        boolean canPlace = model.canPlaceBlueShape(overlappingRect);
        
        assertFalse(canPlace);
    }

    @Test
    @DisplayName("testCanPlaceBlueShapeNoIntersection")
    void testCanPlaceBlueShapeNoIntersection() {
        model.addBlueShape(testCircle);
        Rectangle farRect = new Rectangle(300, 300, 30, 30, Color.BLUE);
        
        boolean canPlace = model.canPlaceBlueShape(farRect);
        
        assertTrue(canPlace);
    }


    @Test
    @DisplayName("testCalculateScore")
    void testCalculateScore() {
        model.addBlueShape(testCircle);  
        model.addBlueShape(testRectangle); 
        
        int score = model.calculateScore();
        
        int expectedScore = (int)(Math.PI * 20 * 20) + 2000;
        assertEquals(expectedScore, score);
    }


    @Test
    @DisplayName("testIsPointInsideGameArea")
    void testIsPointInsideGameArea() {
        assertTrue(model.isPointInsideGameArea(100, 100));
        assertFalse(model.isPointInsideGameArea(-10, 100));
        assertFalse(model.isPointInsideGameArea(100, -10));
        assertFalse(model.isPointInsideGameArea(900, 100));
        assertFalse(model.isPointInsideGameArea(100, 700));
    }

    @Test
    @DisplayName("testIsShapeWithinGameArea")
    void testIsShapeWithinGameArea() {
        Circle insideCircle = new Circle(100, 100, 20, Color.BLUE);
        Circle outsideCircle = new Circle(790, 100, 20, Color.BLUE);
        
        assertTrue(model.isShapeWithinGameArea(insideCircle));
        assertFalse(model.isShapeWithinGameArea(outsideCircle));
    }


    @Test
    @DisplayName("testSetGameState")
    void testSetGameState() {
        model.setState(GameState.MOVING_SHAPE);
        assertEquals(GameState.MOVING_SHAPE, model.getState());
        
        model.setState(GameState.GAME_OVER);
        assertEquals(GameState.GAME_OVER, model.getState());
    }


    @Test
    @DisplayName("testEnableTwoPlayerMode")
    void testEnableTwoPlayerMode() {
        model.enableTwoPlayerMode("Joueur1", "Joueur2");
        
        assertTrue(model.isTwoPlayerMode());
        assertEquals("Joueur1", model.getRedPlayerName());
        assertEquals("Joueur2", model.getBluePlayerName());
        assertTrue(model.isRedPlayerTurn());
    }

    @Test
    @DisplayName("testGetCurrentDrawingColor")
    void testGetCurrentDrawingColor() {
        model.enableTwoPlayerMode("Red", "Blue");
        
        assertEquals(Color.RED, model.getCurrentDrawingColor());

    }
}