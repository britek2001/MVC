package mvc.model.game.turn;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TurnCoordinatorTest {

    @Test
    @DisplayName("startTurn sets state, signal and turn number")
    void startTurnSetsStateSignalAndTurnNumber() {
        TurnCoordinator coordinator = new TurnCoordinator();

        coordinator.startTurn(PlayerTurnState.HUMAN_GUI);

        assertEquals(PlayerTurnState.HUMAN_GUI, coordinator.getCurrentState());
        assertEquals(TurnSignal.TURN_STARTED, coordinator.getLastSignal());
        assertEquals(1, coordinator.getTurnNumber());

        coordinator.startTurn(PlayerTurnState.HUMAN_GUI);
        assertEquals(1, coordinator.getTurnNumber());

        coordinator.startTurn(PlayerTurnState.AI);
        assertEquals(2, coordinator.getTurnNumber());
    }

    @Test
    @DisplayName("completeAction and completeTurn transition correctly")
    void completeActionAndCompleteTurnTransitionCorrectly() {
        TurnCoordinator coordinator = new TurnCoordinator();

        coordinator.startTurn(PlayerTurnState.AI);
        coordinator.requestAction();
        assertEquals(TurnSignal.ACTION_REQUESTED, coordinator.getLastSignal());

        coordinator.completeAction();
        assertEquals(PlayerTurnState.COMPLETED, coordinator.getCurrentState());
        assertEquals(TurnSignal.ACTION_COMPLETED, coordinator.getLastSignal());

        coordinator.completeTurn();
        assertEquals(PlayerTurnState.WAITING, coordinator.getCurrentState());
        assertEquals(TurnSignal.TURN_COMPLETED, coordinator.getLastSignal());
    }

    @Test
    @DisplayName("awaitState unblocks when expected state is reached")
    void awaitStateUnblocksWhenExpectedStateIsReached() throws Exception {
        TurnCoordinator coordinator = new TurnCoordinator();
        CountDownLatch waiting = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);

        Thread waiter = new Thread(() -> {
            waiting.countDown();
            try {
                coordinator.awaitState(PlayerTurnState.AI);
                finished.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        waiter.start();

        assertTrue(waiting.await(500, TimeUnit.MILLISECONDS));
        assertFalse(finished.await(120, TimeUnit.MILLISECONDS));

        coordinator.startTurn(PlayerTurnState.AI);

        assertTrue(finished.await(1, TimeUnit.SECONDS));
        waiter.join(1000);
    }

    @Test
    @DisplayName("awaitTurnCompletion unblocks after completeAction")
    void awaitTurnCompletionUnblocksAfterCompleteAction() throws Exception {
        TurnCoordinator coordinator = new TurnCoordinator();
        CountDownLatch waiting = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);

        Thread waiter = new Thread(() -> {
            waiting.countDown();
            try {
                coordinator.awaitTurnCompletion();
                finished.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        waiter.start();

        assertTrue(waiting.await(500, TimeUnit.MILLISECONDS));
        assertFalse(finished.await(120, TimeUnit.MILLISECONDS));

        coordinator.completeAction();

        assertTrue(finished.await(1, TimeUnit.SECONDS));
        waiter.join(1000);
    }

    @Test
    @DisplayName("abortTurn resets to waiting and emits TURN_ABORTED")
    void abortTurnResetsStateAndSignal() {
        TurnCoordinator coordinator = new TurnCoordinator();
        coordinator.startTurn(PlayerTurnState.HUMAN_GUI);

        coordinator.abortTurn();

        assertEquals(PlayerTurnState.WAITING, coordinator.getCurrentState());
        assertEquals(TurnSignal.TURN_ABORTED, coordinator.getLastSignal());
    }
}
