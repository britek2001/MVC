package mvc.model.game.ai;

import java.util.List;
import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class AIShapeGenerationBuilder {
    private static final Logger logger = Logger.getLogger(AIShapeGenerationBuilder.class.getName());
    private AIShapeGenerationPhase firstPhase;

    public AIShapeGenerationBuilder addPhase(AIShapeGenerationPhase phase) {
        if (firstPhase == null) {
            firstPhase = phase;
        } else {
            AIShapeGenerationPhase current = firstPhase;
            while (current.next != null) {
                current = current.next;
            }
            current.setNext(phase);
        }
        return this;
    }

    public List<GameShape> generate(GameModel model, int count, int panelWidth, int panelHeight) {
        if (firstPhase == null) {
            return createDefaultFlow().generate(model, count, panelWidth, panelHeight);
        }
        logger.info("AI: Width " + countPhases() );
        return firstPhase.generate(model, count, panelWidth, panelHeight);
    }

    public static AIShapeGenerationBuilder createDefaultFlow() {
        logger.info("Creation de par default AI figures");
        return new AIShapeGenerationBuilder()
                .addPhase(new AIAnalyzeGameStatePhase())
                .addPhase(new AIDecideShapeSizePhase())
                .addPhase(new AIGenerateShapesPhase());
    }

    public static AIShapeGenerationBuilder createAggressiveFlow() {
        logger.info("Creation de aggressive AI figures");
        return new AIShapeGenerationBuilder()
                .addPhase(new AIAnalyzeGameStatePhase())
                .addPhase(new AIDecideShapeSizePhase())
                .addPhase(new AIGenerateShapesPhase());
    }

    public static AIShapeGenerationBuilder createConservativeFlow() {
        logger.info("Creation de conservative AI figures");
        return new AIShapeGenerationBuilder()
                .addPhase(new AIAnalyzeGameStatePhase())
                .addPhase(new AIDecideShapeSizePhase())
                .addPhase(new AIGenerateShapesPhase());
    }

    private int countPhases() {
        int count = 0;
        AIShapeGenerationPhase current = firstPhase;
        while (current != null) {
            count++;
            current = current.next;
        }
        return count;
    }
}
