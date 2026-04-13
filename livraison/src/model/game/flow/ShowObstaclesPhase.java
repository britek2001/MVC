package mvc.model.game.flow;

import java.util.logging.Logger;
import mvc.model.game.GameModel;

public class ShowObstaclesPhase extends GamePhase {
    private static final Logger logger = Logger.getLogger(ShowObstaclesPhase.class.getName());

    @Override
    protected void executePhase(GameModel model) {
        logger.info("SHOWRedShapes");
        model.showRedShapes();
    }
}
