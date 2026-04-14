package mvc.model.strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.game.ai.AIShapeGenerationBuilder;
import mvc.model.shapes.GameShape;

public class AIPlayerStrategy implements ShapeGenerationStrategy {
    private static final Logger logger = Logger.getLogger(AIPlayerStrategy.class.getName());
    private final List<List<GameShape>> humanRounds = new ArrayList<>();
    private final List<List<GameShape>> aiRounds = new ArrayList<>();
    private int currentRound = 0;
    private boolean isHumanTurn = true;
    private int panelWidth, panelHeight;
    private GameModel gameModel;
    private final AIShapeGenerationBuilder shapeGenerator;

    public AIPlayerStrategy() {
        this.shapeGenerator = AIShapeGenerationBuilder.createDefaultFlow();
    }

    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        if (gameModel != null && gameModel.isGameFinished()) {
            logger.info("AI: game finished — skipping generation");
            return new ArrayList<>();
        }

        if (isHumanTurn) {
            return new ArrayList<>();
        } else {
            if (gameModel == null) {
                logger.warning("AI: GAME MODEL NOT SET ");
                return new ArrayList<>();
            }
            
            logger.info("AI: GENERATING SHAPES USING GAMEFLOWBUILDER");
            List<GameShape> aiShapes = shapeGenerator.generate(gameModel, count, panelWidth, panelHeight);

            if (!aiShapes.isEmpty()) {
                aiRounds.add(new ArrayList<>(aiShapes));
            }
            return aiShapes;
        }
    }


    public void setGameModel(GameModel model) {
        this.gameModel = model;
        logger.info("AI: GAME MODEL SET");
    }

    public void submitHumanShapes(List<GameShape> shapes) {
        humanRounds.add(new ArrayList<>(shapes));
        isHumanTurn = false;
    }

    public boolean isHumanTurn() {
        return isHumanTurn;
    }

    public void switchToAITurn() {
        isHumanTurn = false;
    }

    public void switchToHumanTurn() {
        isHumanTurn = true;
    }

    public int getCurrentRound() {
        return currentRound + 1;
    }

    public boolean isGameComplete() {
        return currentRound >= 4; 
    }

    public double getHumanScore() {
        return calculateTotalScore(humanRounds);
    }

    public double getAIScore() {
        return calculateTotalScore(aiRounds);
    }

    private double calculateTotalScore(List<List<GameShape>> allRounds) {
        double total = 0;
        for (List<GameShape> round : allRounds) {
            for (GameShape shape : round) {
                total += shape.getArea();
            }
        }
        return total;
    }

    @Override
    public String getStrategyName() {
        return "AI Player";
    }
}
