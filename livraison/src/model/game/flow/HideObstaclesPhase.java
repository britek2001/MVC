package mvc.model.game.flow;

import java.util.logging.Logger;
import mvc.model.game.GameModel;

public class HideObstaclesPhase extends GamePhase {
    private static final Logger logger = Logger.getLogger(HideObstaclesPhase.class.getName());

    @Override
    protected void executePhase(GameModel model) {
        logger.info("HIDERedShapes");
        model.hideRedShapes();
    }
}
