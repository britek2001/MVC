package mvc.model.strategies;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.List;
import mvc.model.shapes.GameShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomGenerationStrategyTest {

    @Test
    @DisplayName("generateShapesReturnsExpectedCount")
    void generateShapesReturnsExpectedCount() {
        RandomGenerationStrategy strategy = new RandomGenerationStrategy();

        List<GameShape> shapes = strategy.generateShapes(8, 800, 600);

        assertEquals(8, shapes.size());
        for (GameShape shape : shapes) {
            assertNotNull(shape);
            assertEquals(Color.RED, shape.getColor());
        }
    }

    @Test
    @DisplayName("generateShapesWithNonPositiveCount")
    void generateShapesWithNonPositiveCount() {
        RandomGenerationStrategy strategy = new RandomGenerationStrategy();

        assertTrue(strategy.generateShapes(0, 800, 600).isEmpty());
        assertTrue(strategy.generateShapes(-3, 800, 600).isEmpty());
    }

    @Test
    @DisplayName("strategyNameIsStable")
    void strategyNameIsStable() {
        RandomGenerationStrategy strategy = new RandomGenerationStrategy();
        assertEquals("Random Generation", strategy.getStrategyName());
    }
}
