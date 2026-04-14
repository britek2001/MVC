package mvc.model.game.ai;
import java.awt.Color;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;

public class AIGenerateShapesPhase extends AIShapeGenerationPhase {
    private static final Logger logger = Logger.getLogger(AIGenerateShapesPhase.class.getName());
    private static final int TOP_SAFE_MARGIN = 86;
    private final SecureRandom random = new SecureRandom();

    @Override
    protected List<GameShape> generateShapes(GameModel model, int count, int panelWidth, int panelHeight) {
        logger.info("AI: Generating " + count + " figure base dans le jeux de model");

        List<GameShape> generatedShapes = new ArrayList<>();
        int attempts = 0;
        double adjustedSize = calculateAdjustedSize(panelWidth, panelHeight, Math.max(1, count));

        while (generatedShapes.size() < count && attempts < 1000) {
            GameShape candidate = createRandomShape(attempts, count, generatedShapes.size(), panelWidth, panelHeight, adjustedSize);

            if (isValidAgainstRedShapes(candidate, model)
                    && isValidAgainstOtherShapes(candidate, generatedShapes)
                    && isValidAgainstBlueShapes(candidate, model)) {
                generatedShapes.add(candidate);
                logger.info("AI: Figure " + (generatedShapes.size()) + " added - " + candidate.getClass().getSimpleName());
            }
            attempts++;
        }

        logger.info("AI: Generer " + generatedShapes.size() + " figures in " + attempts + " attempts");
        return generatedShapes;
    }

    private double calculateAdjustedSize(int panelWidth, int panelHeight, int count) {
        int availableWidth = Math.max(1, panelWidth - 50);
        int availableHeight = Math.max(1, panelHeight - TOP_SAFE_MARGIN - 50);
        double spacePressure = calculateSpacePressure(availableWidth, availableHeight);
        double area = (double) availableWidth * availableHeight;
        double baseSize = Math.sqrt(area / count);
        return Math.max(10.0, baseSize * (1.0 - spacePressure * 0.5));
    }

    private double calculateSpacePressure(int width, int height) {
        return Math.max(0.0, 1.0 - ((width * height) / 500000.0));
    }

    private boolean isValidAgainstRedShapes(GameShape candidate, GameModel model) {
        for (GameShape redShape : model.getRedShapes()) {
            if (candidate.intersects(redShape)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidAgainstBlueShapes(GameShape candidate, GameModel model) {
        for (GameShape blueShape : model.getBlueShapes()) {
            if (candidate.intersects(blueShape)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidAgainstOtherShapes(GameShape candidate, List<GameShape> shapes) {
        for (GameShape shape : shapes) {
            if (candidate.intersects(shape)) {
                return false;
            }
        }
        return true;
    }

    private GameShape createRandomShape(int attempts, int targetCount, int placedCount, int panelWidth, int panelHeight, double adjustedSize) {
        int minY = Math.min(TOP_SAFE_MARGIN, Math.max(0, panelHeight - 1));
        int usableWidth = Math.max(1, panelWidth);
        int usableHeight = Math.max(1, panelHeight - minY);

        boolean tightSpace = usableWidth < 360 || usableHeight < 260 || random.nextDouble() < 0.35;
        double attemptPressure = Math.min(1.0, attempts / 1000.0);
        double progress = targetCount <= 0 ? 0.0 : (placedCount / (double) targetCount);
        boolean aggressive = attemptPressure > 0.35 || progress > 0.5;
        boolean ultraAggressive = attemptPressure > 0.7;
        double sizeFactor = adjustedSize / 40.0;

        int type = random.nextInt(2);

        if (type == 0) {
            return createRectangle(ultraAggressive, aggressive, tightSpace, minY, panelWidth, panelHeight, sizeFactor);
        } else {
            return createCircle(ultraAggressive, aggressive, tightSpace, minY, panelWidth, panelHeight, sizeFactor);
        }
    }

    private Rectangle createRectangle(boolean ultraAggressive, boolean aggressive, boolean tightSpace, int minY, int panelWidth, int panelHeight, double sizeFactor) {
        int rectW;
        int rectH;
        if (ultraAggressive) {
            rectW = scaledDimension(14, 13, sizeFactor);
            rectH = scaledDimension(10, 11, sizeFactor);
        } else if (aggressive) {
            rectW = scaledDimension(18, 20, sizeFactor);
            rectH = scaledDimension(12, 16, sizeFactor);
        } else if (tightSpace) {
            rectW = scaledDimension(24, 33, sizeFactor);
            rectH = scaledDimension(18, 25, sizeFactor);
        } else {
            rectW = scaledDimension(44, 33, sizeFactor);
            rectH = scaledDimension(30, 21, sizeFactor);
        }

        int maxX = Math.max(1, panelWidth - rectW);
        int maxY = Math.max(minY + 1, panelHeight - rectH);
        int x = random.nextInt(maxX);
        int y = minY + random.nextInt(Math.max(1, maxY - minY));
        return new Rectangle(x, y, rectW, rectH, Color.BLUE);
    }

    private Circle createCircle(boolean ultraAggressive, boolean aggressive, boolean tightSpace, int minY, int panelWidth, int panelHeight, double sizeFactor) {
        int radius;
        if (ultraAggressive) {
            radius = scaledDimension(6, 6, sizeFactor);
        } else if (aggressive) {
            radius = scaledDimension(8, 8, sizeFactor);
        } else if (tightSpace) {
            radius = scaledDimension(10, 11, sizeFactor);
        } else {
            radius = scaledDimension(16, 13, sizeFactor);
        }

        int minX = radius;
        int maxX = Math.max(minX + 1, panelWidth - radius);
        int minCY = Math.max(minY + radius, radius);
        int maxCY = Math.max(minCY + 1, panelHeight - radius);

        int x = minX + random.nextInt(Math.max(1, maxX - minX));
        int y = minCY + random.nextInt(Math.max(1, maxCY - minCY));

        return new Circle(x, y, radius, Color.BLUE);
    }

    private int scaledDimension(int base, int variation, double sizeFactor) {
        double scaled = (base + random.nextInt(Math.max(1, variation))) * sizeFactor;
        return Math.max(5, (int) Math.round(scaled));
    }
}
