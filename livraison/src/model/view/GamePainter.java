package mvc.model.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatInteraction;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;

public class GamePainter {

    public void paint(Graphics2D g2, GameModel model, GameShape selectedShape, MouseAdapter controller) {
        GameState state = model.getState();

        if (state == GameState.RED_VISIBLE
                || state == GameState.WAITING_FOR_RED
                || state == GameState.LEVEL_COMPLETE
                || state == GameState.PLACING_BLUE) {
            model.getRedShapes().forEach(shape -> drawShape(g2, shape, selectedShape));
        }

        model.getBlueShapes().forEach(shape -> drawShape(g2, shape, selectedShape));

        if (controller instanceof ControleurSouris) {
            drawDragPreview(g2, (ControleurSouris) controller);
        }

        drawHud(g2, model, state);
    }

    private void drawShape(Graphics2D g2, GameShape shape, GameShape selectedShape) {
        g2.setColor(shape.getColor());

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            g2.fill(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
            }
        } else if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            double radius = circle.getRadius();
            g2.fill(new Ellipse2D.Double(circle.getX() - radius, circle.getY() - radius, radius * 2, radius * 2));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Ellipse2D.Double(circle.getX() - radius, circle.getY() - radius, radius * 2, radius * 2));
            }
        }
    }

    private void drawDragPreview(Graphics2D g2, ControleurSouris cs) {
        EtatInteraction estado = cs.getEtatCourant();

        if (estado instanceof EtatCreationRectangle) {
            EtatCreationRectangle est = (EtatCreationRectangle) estado;
            if (est.isDragging()) {
                int x = Math.min(est.getStartX(), est.getCurrentX());
                int y = Math.min(est.getStartY(), est.getCurrentY());
                int w = Math.abs(est.getCurrentX() - est.getStartX());
                int h = Math.abs(est.getCurrentY() - est.getStartY());

                g2.setColor(new Color(0, 100, 200, 100));
                g2.fillRect(x, y, w, h);
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(x, y, w, h);
            }
        } else if (estado instanceof EtatCreationCercle) {
            EtatCreationCercle est = (EtatCreationCercle) estado;
            if (est.isDragging()) {
                double rayon = Math.sqrt(Math.pow(est.getCurrentX() - est.getStartX(), 2)
                        + Math.pow(est.getCurrentY() - est.getStartY(), 2));

                g2.setColor(new Color(0, 100, 200, 100));
                g2.fillOval((int) (est.getStartX() - rayon), (int) (est.getStartY() - rayon),
                        (int) (rayon * 2), (int) (rayon * 2));
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval((int) (est.getStartX() - rayon), (int) (est.getStartY() - rayon),
                        (int) (rayon * 2), (int) (rayon * 2));
            }
        }
    }

    private void drawHud(Graphics2D g2, GameModel model, GameState state) {
        g2.setColor(Color.BLACK);
        g2.drawString("État: " + state, 10, 20);
        g2.drawString("Score: " + model.getTotalScore(), 10, 40);
        g2.drawString("Bleues posées (niveau): " + model.getBlueShapesPlacedThisLevel() + "/" + model.getBlueShapesPerLevel(), 10, 60);
        g2.drawString("Rouges: " + model.getRedShapes().size(), 10, 80);
        g2.drawString("Nivel: " + model.getLevel(), 10, 100);
        g2.drawString("Red Visible Time: " + model.getRedVisibleTime() + " ms", 10, 120);
        g2.drawString("Objectif: poser exactement " + model.getBlueShapesPerLevel() + " formes bleues", 10, 140);
        g2.drawString("Restantes: " + model.getBlueShapesRemainingForLevel(), 10, 160);
    }
}
