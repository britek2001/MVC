package mvc.model.game.ai;

import java.util.logging.Logger;
import mvc.model.game.GameModel;
import mvc.model.game.flow.GamePhase;

public class AIPlayPhase extends GamePhase {
    private static final Logger logger = Logger.getLogger(AIPlayPhase.class.getName());

    @Override
    public void executePhase(GameModel game) {
        logger.info("AI: En attente des coups de l'IA");
    }
}
