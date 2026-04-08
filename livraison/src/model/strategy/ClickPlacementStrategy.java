package mvc.model.strategies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;

public class ClickPlacementStrategy implements ShapeGenerationStrategy {

    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        // Non aléatoire: distribution déterministe sur une grille simple
        List<GameShape> shapes = new ArrayList<>();
        if (count <= 0) {
            return shapes;
        }

        int cols = Math.max(1, (int) Math.ceil(Math.sqrt(count)));
        int rows = Math.max(1, (int) Math.ceil((double) count / cols));

        double stepX = Math.max(80, panelWidth / (double) (cols + 1));
        double stepY = Math.max(80, panelHeight / (double) (rows + 1));

        int created = 0;
        for (int r = 1; r <= rows && created < count; r++) {
            for (int c = 1; c <= cols && created < count; c++) {
                double x = c * stepX;
                double y = r * stepY;

                if (created % 2 == 0) {
                    shapes.add(new Rectangle(x - 25, y - 20, 50, 40, Color.RED));
                } else {
                    shapes.add(new Circle(x, y, 22, Color.RED));
                }
                created++;
            }
        }

        return shapes;
    }

    public Rectangle createRectangleFromClicks(int startX, int startY, int endX, int endY, Color color) {
        int width = Math.abs(endX - startX);
        int height = Math.abs(endY - startY);
        int x = Math.min(startX, endX);
        int y = Math.min(startY, endY);
        return new Rectangle(x, y, width, height, color);
    }

    public Circle createCircleFromClicks(int centerX, int centerY, int edgeX, int edgeY, Color color) {
        double radius = Math.sqrt(Math.pow(edgeX - centerX, 2) + Math.pow(edgeY - centerY, 2));
        return new Circle(centerX, centerY, radius, color);
    }

    @Override
    public String getStrategyName() {
        return "Click Placement Strategy";
    }
}