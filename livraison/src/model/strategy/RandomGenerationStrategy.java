package mvc.model.strategies;
import mvc.model.shapes.GameShape;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;

public class RandomGenerationStrategy implements ShapeGenerationStrategy {
    private Random random = new Random();
    
    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        List<GameShape> shapes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            int type = 0;
            int x = random.nextInt(panelWidth - 100) + 50;
            int y = random.nextInt(panelHeight - 100) + 50;
            
            switch (type) {
                case 0:
                    shapes.add(new Rectangle(x, y, random.nextInt(80) + 20,  random.nextInt(80) + 20, Color.RED));
                    System.out.printf("Printing Shape");
                    break;
                case 1:
                    shapes.add(new Circle(x, y, random.nextInt(40) + 10, Color.RED));
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