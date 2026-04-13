package mvc.model.game.flow;

import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;

public class PlayerPlayPhase extends GamePhase {
    private static final Logger logger = Logger.getLogger(PlayerPlayPhase.class.getName());

    @Override
    protected void executePhase(GameModel model) {
        logger.info("WAITING_FOR_PLAYER");
        model.setState(GameState.PLACING_BLUE);
    }
}
