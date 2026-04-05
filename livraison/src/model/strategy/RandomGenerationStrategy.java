package mvc.model.strategies;
import mvc.model.shapes.GameShape;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mvc.model.shapes.GameShape;

public class RandomGenerationStrategy implements ShapeGenerationStrategy {
    private Random random = new Random();
    
    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        List<GameShape> shapes = new ArrayList<>();
        return shapes;
    }
    
    @Override
    public String getStrategyName() {
        return "Random Generation";
    }
}