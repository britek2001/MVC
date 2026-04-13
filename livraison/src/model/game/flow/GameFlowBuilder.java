package mvc.model.game.flow;

import java.util.logging.Logger;
import mvc.model.game.GameModel;

public class GameFlowBuilder {
    private static final Logger logger = Logger.getLogger(GameFlowBuilder.class.getName());
    private GamePhase firstPhase;

    public GameFlowBuilder addPhase(GamePhase phase) {
        if (firstPhase == null) {
            firstPhase = phase;
        } else {
            GamePhase current = firstPhase;
            while (current.next != null) {
                current = current.next;
            }
            current.setNext(phase);
        }
        return this;
    }

    public GamePhase build() {
        if (firstPhase == null) {
            return createDefaultFlow().firstPhase;
        }
        return firstPhase;
    }

    public static GameFlowBuilder createDefaultFlow() {
        logger.info("CreatingDefaultGameFlow");
        return new GameFlowBuilder()
                .addPhase(new ShowObstaclesPhase())
                .addPhase(new PlayerPlayPhase())
                .addPhase(new CalculateScorePhase());
    }

    public static GameFlowBuilder createVariantWithWait(long waitTimeMillis) {
        logger.info("CreatVariantWithWait");
        return new GameFlowBuilder()
                .addPhase(new ShowObstaclesPhase())
                .addPhase(new HideObstaclesPhase())
                .addPhase(new PlayerPlayPhase())
                .addPhase(new CalculateScorePhase());
    }

    public static GameFlowBuilder createVariantWithoutWait() {
        logger.info("CreatingVariantWithoutWait");
        return new GameFlowBuilder()
                .addPhase(new ShowObstaclesPhase())
                .addPhase(new PlayerPlayPhase())
                .addPhase(new CalculateScorePhase());
    }

    public void executeFlow(GameModel model) {
        if (firstPhase == null) {
            logger.warning("NOFASE IN FLOW");
            return;
        }
        logger.info("EXECUTE FLOW");
        firstPhase.execute(model);
        logger.info("FLOW COMPLETED");
    }
}
