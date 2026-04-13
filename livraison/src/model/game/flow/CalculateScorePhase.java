package mvc.model.game.flow;

import java.util.logging.Logger;
import mvc.model.game.GameModel;

public class CalculateScorePhase extends GamePhase {
    private static final Logger logger = Logger.getLogger(CalculateScorePhase.class.getName());

    @Override
    protected void executePhase(GameModel model) {
        logger.info("Phase: Calculating score");
        int score = model.calculateScore();
        logger.info("Score calculated: " + score);
    }
}
