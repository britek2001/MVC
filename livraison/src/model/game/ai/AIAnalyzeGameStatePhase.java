package mvc.model.game.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class AIAnalyzeGameStatePhase extends AIShapeGenerationPhase {
    private static final Logger logger = Logger.getLogger(AIAnalyzeGameStatePhase.class.getName());

    @Override
    protected List<GameShape> generateShapes(GameModel model, int count, int panelWidth, int panelHeight) {
        logger.info("AI: IA analise de figure Bleu: " + model.getBlueShapes().size());

        int blueCoverage = calculateBlueCoverage(model);
        logger.info("AI: Figure Bleu coverage calculated: " + blueCoverage);

        if (blueCoverage > 50) {
            logger.info("AI: Utilise une strategie defensive");
        } else {
            logger.info("AI: utilisation une strategie normale");
        }

        return new ArrayList<>();
    }

    private int calculateBlueCoverage(GameModel model) {
        double totalArea = 0;
        for (GameShape shape : model.getBlueShapes()) {
            totalArea += shape.getArea();
        }
        return (int) totalArea;
    }
}
