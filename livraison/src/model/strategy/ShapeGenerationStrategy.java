package mvc.model.strategies;

import java.util.List;
import mvc.model.shapes.GameShape;
public interface ShapeGenerationStrategy {
    List<GameShape> generateShapes(int count, int panelWidth, int panelHeight);
    String getStrategyName();
}