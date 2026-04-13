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

        while (generatedShapes.size() < count && attempts < 1000) {
            GameShape candidate = createRandomShape(attempts, count, generatedShapes.size(), panelWidth, panelHeight);

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

    private GameShape createRandomShape(int attempts, int targetCount, int placedCount, int panelWidth, int panelHeight) {
        int minY = Math.min(TOP_SAFE_MARGIN, Math.max(0, panelHeight - 1));
        int usableWidth = Math.max(1, panelWidth);
        int usableHeight = Math.max(1, panelHeight - minY);

        boolean tightSpace = usableWidth < 360 || usableHeight < 260 || random.nextDouble() < 0.35;
        double attemptPressure = Math.min(1.0, attempts / 1000.0);
        double progress = targetCount <= 0 ? 0.0 : (placedCount / (double) targetCount);
        boolean aggressive = attemptPressure > 0.35 || progress > 0.5;
        boolean ultraAggressive = attemptPressure > 0.7;

        int type = random.nextInt(2);

        if (type == 0) {
            return createRectangle(ultraAggressive, aggressive, tightSpace, minY, panelWidth, panelHeight);
        } else {
            return createCircle(ultraAggressive, aggressive, tightSpace, minY, panelWidth, panelHeight);
        }
    }

    private Rectangle createRectangle(boolean ultraAggressive, boolean aggressive, boolean tightSpace, int minY, int panelWidth, int panelHeight) {
        int rectW;
        int rectH;
        if (ultraAggressive) {
            rectW = 14 + random.nextInt(13);
            rectH = 10 + random.nextInt(11);
        } else if (aggressive) {
            rectW = 18 + random.nextInt(20);
            rectH = 12 + random.nextInt(16);
        } else if (tightSpace) {
            rectW = 24 + random.nextInt(33);
            rectH = 18 + random.nextInt(25);
        } else {
            rectW = 44 + random.nextInt(33);
            rectH = 30 + random.nextInt(21);
        }

        int maxX = Math.max(1, panelWidth - rectW);
        int maxY = Math.max(minY + 1, panelHeight - rectH);
        int x = random.nextInt(maxX);
        int y = minY + random.nextInt(Math.max(1, maxY - minY));
        return new Rectangle(x, y, rectW, rectH, Color.BLUE);
    }

    private Circle createCircle(boolean ultraAggressive, boolean aggressive, boolean tightSpace, int minY, int panelWidth, int panelHeight) {
        int radius;
        if (ultraAggressive) {
            radius = 6 + random.nextInt(6);
        } else if (aggressive) {
            radius = 8 + random.nextInt(8);
        } else if (tightSpace) {
            radius = 10 + random.nextInt(11);
        } else {
            radius = 16 + random.nextInt(13);
        }

        int minX = radius;
        int maxX = Math.max(minX + 1, panelWidth - radius);
        int minCY = Math.max(minY + radius, radius);
        int maxCY = Math.max(minCY + 1, panelHeight - radius);

        int x = minX + random.nextInt(Math.max(1, maxX - minX));
        int y = minCY + random.nextInt(Math.max(1, maxCY - minCY));

        return new Circle(x, y, radius, Color.BLUE);
    }
}
