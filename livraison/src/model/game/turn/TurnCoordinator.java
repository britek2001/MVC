package mvc.model.game.turn;

import java.util.Objects;

public class TurnCoordinator {
    private final Object monitor = new Object();
    private PlayerTurnState currentState = PlayerTurnState.WAITING;
    private TurnSignal lastSignal = null;
    private int turnNumber = 0;

    public void startTurn(PlayerTurnState state) {
        synchronized (monitor) {
            PlayerTurnState nextState = Objects.requireNonNull(state, "state must not be null");
            if (currentState != nextState) {
                turnNumber++;
            }
            currentState = nextState;
            lastSignal = TurnSignal.TURN_STARTED;
            monitor.notifyAll();
        }
    }

    public void requestAction() {
        synchronized (monitor) {
            lastSignal = TurnSignal.ACTION_REQUESTED;
            monitor.notifyAll();
        }
    }

    public void completeAction() {
        synchronized (monitor) {
            lastSignal = TurnSignal.ACTION_COMPLETED;
            currentState = PlayerTurnState.COMPLETED;
            monitor.notifyAll();
        }
    }

    public void completeTurn() {
        synchronized (monitor) {
            lastSignal = TurnSignal.TURN_COMPLETED;
            currentState = PlayerTurnState.WAITING;
            monitor.notifyAll();
        }
    }

    public void abortTurn() {
        synchronized (monitor) {
            lastSignal = TurnSignal.TURN_ABORTED;
            currentState = PlayerTurnState.WAITING;
            monitor.notifyAll();
        }
    }

    public void awaitTurnCompletion() throws InterruptedException {
        synchronized (monitor) {
            while (currentState != PlayerTurnState.COMPLETED) {
                monitor.wait();
            }
        }
    }

    public void awaitState(PlayerTurnState expectedState) throws InterruptedException {
        synchronized (monitor) {
            while (currentState != expectedState) {
                monitor.wait();
            }
        }
    }

    public PlayerTurnState getCurrentState() {
        synchronized (monitor) {
            return currentState;
        }
    }

    public TurnSignal getLastSignal() {
        synchronized (monitor) {
            return lastSignal;
        }
    }

    public int getTurnNumber() {
        synchronized (monitor) {
            return turnNumber;
        }
    }
}
