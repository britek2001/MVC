package mvc.model.game;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.awt.geom.Point2D;
import mvc.model.shapes.GameShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TwoPlayersTest {

    private static final class DummyShape implements GameShape {
        private double x, y;
        private Color color = Color.BLACK;

        DummyShape(double x, double y) { this.x = x; this.y = y; }
        @Override
        public boolean intersects(GameShape other) { return false; }

        @Override
        public double getArea() { return 1.0; }

        @Override
        public double getX() { return x; }

        @Override
        public double getY() { return y; }

        @Override
        public int getZoneType(Point2D p) { return 0; }

        @Override
        public boolean contains(Point2D p) { return false; }

        @Override
        public void move(double dx, double dy) { x += dx; y += dy; }

        @Override
        public void resize(double factor) { }

        @Override
        public Color getColor() { return color; }

        @Override
        public void setColor(Color color) { this.color = color; }

        @Override
        public GameShape copy() { return new DummyShape(x, y); }

        @Override
        public void setPosition(double newX, double newY) { x = newX; y = newY; }
    }

    @Test
    @DisplayName("twoPlayerAddBlueShapeAndBlockAfterEnd")
    void twoPlayerAddBlueShapeAndBlockAfterEnd() {
        GameModel model = new GameModel();

        model.enableTwoPlayerMode("R", "B");
        assertTrue(model.isTwoPlayerMode());

        model.setRedPlayerTurn(false);

        DummyShape s = new DummyShape(10, 10);
        boolean added = model.addBlueShape(s);

        assertTrue(added, "Ajout de figure bleu doit réussir");
        assertEquals(1, model.getBlueShapesPlacedThisLevel());

        model.endGame();
        DummyShape s2 = new DummyShape(20, 20);
        boolean addedAfterEnd = model.addBlueShape(s2);
        assertFalse(addedAfterEnd, "Pas de possibilité d'ajouter une figure après la fin de la partie");
        assertTrue(model.isGameFinished());
    }
}
