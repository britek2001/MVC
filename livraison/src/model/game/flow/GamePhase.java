package mvc.model.game.flow;

import mvc.model.game.GameModel;

public abstract class GamePhase {
    protected GamePhase next;

    public GamePhase setNext(GamePhase nextPhase) {
        this.next = nextPhase;
        return nextPhase;
    }

    public final void execute(GameModel model) {
        executePhase(model);
        if (next != null) {
            next.execute(model);
        }
    }

    protected abstract void executePhase(GameModel model);

    public String getPhaseName() {
        return this.getClass().getSimpleName();
    }
}
