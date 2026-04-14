package mvc.model.strategies;
import mvc.model.shapes.GameShape;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;

public class RandomGenerationStrategy implements ShapeGenerationStrategy {
    private final SecureRandom random = new SecureRandom();
    
    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        List<GameShape> shapes = new ArrayList<>();
        int maxX = panelWidth - 150;
        int maxY = panelHeight - 150;
        if (count <= 0 || maxX <= 0 || maxY <= 0) {
            return shapes;
        }
        for (int i = 0; i < count; i++) {
            int type = random.nextInt(2);
            int x = 50 + random.nextInt(maxX);
            int y = 50 + random.nextInt(maxY);
            switch (type) {
                case 0:
                    shapes.add(new Rectangle(x, y, random.nextInt(80) + 20,  random.nextInt(80) + 20, Color.RED));
                    break;
                case 1:
                    shapes.add(new Circle(x, y, random.nextInt(40) + 10, Color.RED));
                    break;
                default:
                    break;
            }
        }
        return shapes;
    }
    
    @Override
    public String getStrategyName() {
        return "Random Generation";
    }
}