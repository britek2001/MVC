package model.strategy;

import java.util.List;
import model.shapes.GameShape;
public interface ShapeGenerationStrategy {
    List<GameShape> generateShapes(int count, int panelWidth, int panelHeight);
    String getStrategyName();
}