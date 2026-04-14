package mvc.model.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.strategies.ClickPlacementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EtatCreationCercleTest {

    private GameModel model;
    private JPanel source;

    @BeforeEach
    void setUp() {
        model = new GameModel();
        model.setGameAreaSize(800, 600);
        source = new JPanel();
    }

    @Test
    @DisplayName("constructorRejectsNullModel")
    void constructorRejectsNullModel() {
        assertThrows(NullPointerException.class, () -> new EtatCreationCercle(null));
    }

    @Test
    @DisplayName("initialDraggingStateIsFalse")
    void initialDraggingStateIsFalse() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        assertFalse(state.isDragging());
    }

    @Test
    @DisplayName("sourisAppuyeeIgnoredWhenGameFinished")
    void sourisAppuyeeIgnoredWhenGameFinished() {
        model.endGame();
        EtatCreationCercle state = new EtatCreationCercle(model);

        state.sourisAppuyee(new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 0, 0, 100, 100, 1, false), null);

        assertFalse(state.isDragging());
    }

    @Test
    @DisplayName("sourisBougeeDoesNothing")
    void sourisBougeeDoesNothing() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        state.sourisBougee(new MouseEvent(source, MouseEvent.MOUSE_MOVED, 0, 0, 210, 220, 0, false), null);

        assertFalse(state.isDragging());
        assertTrue(model.getBlueShapes().isEmpty());
    }

    @Test
    @DisplayName("smallDragEndsWithoutCreatingShape")
    void smallDragEndsWithoutCreatingShape() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        ControleurSouris controller = new ControleurSouris(state);

        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 100, 100), controller);
        state.sourisDeplacee(mouse(MouseEvent.MOUSE_DRAGGED, 105, 105), controller);
        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 105, 105), controller);

        assertFalse(state.isDragging());
        assertTrue(model.getBlueShapes().isEmpty());
        assertTrue(controller.getEtatCourant() instanceof EtatSelection);
    }

    @Test
    @DisplayName("sourisAppuyeeSetsInitialAndCurrentCoordinates")
    void sourisAppuyeeSetsInitialAndCurrentCoordinates() {
        EtatCreationCercle state = new EtatCreationCercle(model);

        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 140, 150), null);

        assertTrue(state.isDragging());
        assertEquals(140, state.getStartX());
        assertEquals(150, state.getStartY());
        assertEquals(140, state.getCurrentX());
        assertEquals(150, state.getCurrentY());
    }

    @Test
    @DisplayName("secondSourisAppuyeeWhileDraggingDoesNotOverrideStart")
    void secondSourisAppuyeeWhileDraggingDoesNotOverrideStart() {
        EtatCreationCercle state = new EtatCreationCercle(model);

        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 100, 120), null);
        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 300, 350), null);

        assertEquals(100, state.getStartX());
        assertEquals(120, state.getStartY());
        assertEquals(100, state.getCurrentX());
        assertEquals(120, state.getCurrentY());
    }

    @Test
    @DisplayName("sourisDeplaceeUpdatesCurrentCoordinatesWhenDragging")
    void sourisDeplaceeUpdatesCurrentCoordinatesWhenDragging() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 90, 95), null);

        state.sourisDeplacee(mouse(MouseEvent.MOUSE_DRAGGED, 160, 170), null);

        assertEquals(90, state.getStartX());
        assertEquals(95, state.getStartY());
        assertEquals(160, state.getCurrentX());
        assertEquals(170, state.getCurrentY());
    }

    @Test
    @DisplayName("sourisRelacheeWhenNotDraggingDoesNothing")
    void sourisRelacheeWhenNotDraggingDoesNothing() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        ControleurSouris controller = new ControleurSouris(state);

        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 150, 150), controller);

        assertFalse(state.isDragging());
        assertTrue(model.getBlueShapes().isEmpty());
        assertTrue(controller.getEtatCourant() instanceof EtatCreationCercle);
    }

    @Test
    @DisplayName("sourisRelacheeSmallRadiusWithNullControllerEndsDrag")
    void sourisRelacheeSmallRadiusWithNullControllerEndsDrag() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 100, 100), null);
        state.sourisDeplacee(mouse(MouseEvent.MOUSE_DRAGGED, 106, 106), null);

        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 106, 106), null);

        assertFalse(state.isDragging());
        assertTrue(model.getBlueShapes().isEmpty());
    }

    @Test
    @DisplayName("sourisRelacheeLargeRadiusWithClickStrategyCreatesShapeAndEndsDrag")
    void sourisRelacheeLargeRadiusWithClickStrategyCreatesShapeAndEndsDrag() {
        model.setGenerationStrategy(new ClickPlacementStrategy());
        EtatCreationCercle state = new EtatCreationCercle(model);
        ControleurSouris controller = new ControleurSouris(state);

        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 120, 130), controller);
        state.sourisDeplacee(mouse(MouseEvent.MOUSE_DRAGGED, 170, 130), controller);
        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 170, 130), controller);

        assertFalse(state.isDragging());
        assertEquals(1, model.getBlueShapes().size());
        assertTrue(model.getBlueShapes().get(0) instanceof Circle);
        assertTrue(controller.getEtatCourant() instanceof EtatSelection);

        Circle created = (Circle) model.getBlueShapes().get(0);
        assertEquals(120.0, created.getX(), 0.01);
        assertEquals(130.0, created.getY(), 0.01);
        assertEquals(50.0, created.getRadius(), 0.01);
        assertEquals(Color.BLUE, created.getColor());
    }

    @Test
    @DisplayName("sourisRelacheeLargeRadiusWithDefaultStrategyCreatesShape")
    void sourisRelacheeLargeRadiusWithDefaultStrategyCreatesShape() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        ControleurSouris controller = new ControleurSouris(state);

        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 100, 100), controller);
        state.sourisDeplacee(mouse(MouseEvent.MOUSE_DRAGGED, 140, 100), controller);
        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 140, 100), controller);

        assertFalse(state.isDragging());
        assertEquals(1, model.getBlueShapes().size());
        Circle created = (Circle) model.getBlueShapes().get(0);
        assertEquals(40.0, created.getRadius(), 0.01);
        assertEquals(Color.BLUE, created.getColor());
    }

    @Test
    @DisplayName("sourisRelacheeIgnoredWhenGameFinished")
    void sourisRelacheeIgnoredWhenGameFinished() {
        EtatCreationCercle state = new EtatCreationCercle(model);
        state.sourisAppuyee(mouse(MouseEvent.MOUSE_PRESSED, 100, 100), null);
        model.endGame();

        state.sourisRelachee(mouse(MouseEvent.MOUSE_RELEASED, 180, 180), null);

        assertTrue(state.isDragging());
        assertTrue(model.getBlueShapes().isEmpty());
    }

    private MouseEvent mouse(int eventType, int x, int y) {
        return new MouseEvent(source, eventType, 0L, 0, x, y, 1, false);
    }

}
