package mvc.model.game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;

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

    @Test
    @DisplayName("testNextLevelIncrementsLevel")
    void testNextLevelIncrementsLevel() {
        int initialLevel = model.getLevel();
        model.nextLevel();
        assertEquals(initialLevel + 1, model.getLevel());
    }

    @Test
    @DisplayName("testCantPlaceMoreThan4BlueShapesPerLevel")
    void testCantPlaceMoreThan4BlueShapesPerLevel() {
        for (int i = 0; i < 4; i++) {
            Circle circle = new Circle(100 + i * 50, 100, 20, Color.BLUE);
            assertTrue(model.addBlueShape(circle));
        }
        
        Circle fifthCircle = new Circle(400, 100, 20, Color.BLUE);
        boolean added = model.addBlueShape(fifthCircle);
        
        assertFalse(added);
        assertEquals(4, model.getBlueShapes().size());
    }

    @Test
    @DisplayName("testGetBlueShapesRemainingForLevel")
    void testGetBlueShapesRemainingForLevel() {
        assertEquals(4, model.getBlueShapesRemainingForLevel());
        
        model.addBlueShape(testCircle);
        assertEquals(3, model.getBlueShapesRemainingForLevel());
        
        model.addBlueShape(testRectangle);
        assertEquals(2, model.getBlueShapesRemainingForLevel());
    }

    @Test
    @DisplayName("testAreRedShapesVisibleInitially")
    void testAreRedShapesVisibleInitially() {
        assertTrue(model.areRedShapesVisible());
    }

    @Test
    @DisplayName("testHideRedShapes")
    void testHideRedShapes() {
        model.hideRedShapes();
        assertFalse(model.areRedShapesVisible());
    }

    @Test
    @DisplayName("testShowRedShapes")
    void testShowRedShapes() {
        model.hideRedShapes();
        assertFalse(model.areRedShapesVisible());
        
        model.showRedShapes();
        assertTrue(model.areRedShapesVisible());
    }

    @Test
    @DisplayName("testGetRemainingRedTime")
    void testGetRemainingRedTime() {
        model.setGenerationStrategy(new RandomGenerationStrategy());
        model.generateRedShapes(2, 800, 600);
        
        long remaining = model.getRemainingRedTime();
        assertTrue(remaining >= 0);
        assertTrue(remaining <= 10000); 
    }

    @Test
    @DisplayName("testRemoveNonExistentShape")
    void testRemoveNonExistentShape() {
        Circle nonExistentCircle = new Circle(999, 999, 10, Color.BLUE);
        int sizeBefore = model.getBlueShapes().size();
        
        model.removeBlueShape(nonExistentCircle);
        
        assertEquals(sizeBefore, model.getBlueShapes().size());
    }

    @Test
    @DisplayName("testRestoreBlueShapeWithInvalidIndex")
    void testRestoreBlueShapeWithInvalidIndex() {
        model.addBlueShape(testCircle);
        int sizeBefore = model.getBlueShapes().size();
        
        model.restoreBlueShape(testRectangle, 999);
        assertEquals(sizeBefore + 1, model.getBlueShapes().size());
        
        model.restoreBlueShape(new Circle(300, 300, 20, Color.BLUE), -1);
        assertEquals(sizeBefore + 2, model.getBlueShapes().size());
    }

    @Test
    @DisplayName("testPlaceShapeExactlyOnGameAreaBorder")
    void testPlaceShapeExactlyOnGameAreaBorder() {
        Rectangle borderRect = new Rectangle(0, 100, 50, 50, Color.BLUE);
        assertTrue(model.canPlaceBlueShape(borderRect));
        
        Rectangle borderRightRect = new Rectangle(750, 100, 50, 50, Color.BLUE);
        assertTrue(model.canPlaceBlueShape(borderRightRect));
        
        Circle borderCircle = new Circle(20, 100, 20, Color.BLUE);
        assertTrue(model.canPlaceBlueShape(borderCircle));
    }

    @Test
    @DisplayName("testPlaceShapeExactlyOnBorderEdge")
    void testPlaceShapeExactlyOnBorderEdge() {
        Rectangle exactRect = new Rectangle(800, 0, 0, 0, Color.BLUE);
        assertFalse(model.canPlaceBlueShape(exactRect));
    }

    @Test
    @DisplayName("testGetLevelConfig")
    void testGetLevelConfig() {
        LevelConfig config1 = model.getLevelConfig(1);
        assertEquals(2, config1.redShapeCount);
        assertEquals(10, config1.timeSeconds);
        assertEquals("Facile", config1.difficulty);
        
        LevelConfig config3 = model.getLevelConfig(3);
        assertEquals(4, config3.redShapeCount);
        assertEquals(8, config3.timeSeconds);
        assertEquals("Difficile", config3.difficulty);
        
        LevelConfig config5 = model.getLevelConfig(5);
        assertEquals(6, config5.redShapeCount);
        assertEquals(6, config5.timeSeconds);
        assertEquals("Extreme", config5.difficulty);
    }

    @Test
    @DisplayName("testGetLevelConfigWithInvalidLevel")
    void testGetLevelConfigWithInvalidLevel() {
        LevelConfig defaultConfig = model.getLevelConfig(99);
        assertNotNull(defaultConfig);
        assertEquals(2, defaultConfig.redShapeCount);
    }

    @Test
    @DisplayName("testIsGameFinished")
    void testIsGameFinished() {
        assertFalse(model.isGameFinished());
        
        for (int i = 0; i < 4; i++) {
            model.addBlueShape(new Circle(100 + i * 50, 100, 20, Color.BLUE));
        }
        
        assertTrue(model.isGameFinished());
    }

    @Test
    @DisplayName("testGetFinalCoveredArea")
    void testGetFinalCoveredArea() {
        model.addBlueShape(testCircle);
        model.addBlueShape(testRectangle);
        
        for (int i = 2; i < 4; i++) {
            model.addBlueShape(new Circle(100 + i * 50, 100, 20, Color.BLUE));
        }

        int expectedArea = (int) (3 * (Math.PI * 20 * 20) + 2000);
        assertEquals(expectedArea, model.getFinalCoveredArea());
    }

    @Test
    @DisplayName("testGetStatistics")
    void testGetStatistics() {
        model.addBlueShape(testCircle);
        model.addBlueShape(testRectangle);
        assertDoesNotThrow(() -> model.getStatistics());
    }

    @Test
    @DisplayName("testSetGameAreaSize")
    void testSetGameAreaSize() {
        model.setGameAreaSize(1024, 768);
        assertEquals(1024, model.getGameWidth());
        assertEquals(768, model.getGameHeight());
    }

    @Test
    @DisplayName("testSetGameAreaSizeWithInvalidValues")
    void testSetGameAreaSizeWithInvalidValues() {
        model.setGameAreaSize(-100, -200);
        assertEquals(1, model.getGameWidth());
        assertEquals(1, model.getGameHeight());
    }

    @Test
    @DisplayName("testGetCurrentDrawingColorInSoloMode")
    void testGetCurrentDrawingColorInSoloMode() {
        assertEquals(Color.BLUE, model.getCurrentDrawingColor());
    }

    @Test
    @DisplayName("testSetCurrentLevel")
    void testSetCurrentLevel() {
        model.setCurrentLevel(3);
        assertEquals(3, model.getLevel());
        
        model.setCurrentLevel(10);
        assertEquals(4, model.getLevel());
    }
}