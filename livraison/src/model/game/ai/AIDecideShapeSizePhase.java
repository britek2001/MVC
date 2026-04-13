package mvc.model.game.ai;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class AIDecideShapeSizePhase extends AIShapeGenerationPhase {

    private static final Logger logger = Logger.getLogger(AIDecideShapeSizePhase.class.getName());
    private static final int TOP_SAFE_MARGIN = 86;
    private final SecureRandom random = new SecureRandom();

    @Override
    protected List<GameShape> generateShapes(GameModel model, int count, int panelWidth, int panelHeight) {

        boolean redVisible = model.areRedShapesVisible();
        logger.info("AI: Figure Rouge visible = " + redVisible);

        int availableWidth = calculateAvailableWidth(model, panelWidth);
        int availableHeight = calculateAvailableHeight(model, panelHeight);

        logger.info("AI: Espace disponible = " + availableWidth + "x" + availableHeight);

        double spacePressure = calculateSpacePressure(availableWidth, availableHeight);
        logger.info("AI: Espace de pression = " + String.format("%.2f", spacePressure));

        return new ArrayList<>();
    }

    private int calculateAvailableWidth(GameModel model, int panelWidth) {
        return Math.max(1, panelWidth - 50);
    }

    private int calculateAvailableHeight(GameModel model, int panelHeight) {
        return Math.max(1, panelHeight - TOP_SAFE_MARGIN - 50);
    }

    private double calculateSpacePressure(int width, int height) {
        return Math.max(0.0, 1.0 - ((width * height) / 500000.0));
    }
}
