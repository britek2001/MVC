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
        int panelWidth = g2.getClipBounds() != null ? g2.getClipBounds().width : model.getGameWidth() + 400;
        int panelHeight = g2.getClipBounds() != null ? g2.getClipBounds().height : model.getGameHeight();
        int gameWidth = Math.max(1, panelWidth - 400);

        model.setGameAreaSize(gameWidth, panelHeight);

        g2.setColor(new Color(248, 250, 252));
        g2.fillRect(0, 0, gameWidth, panelHeight);

        g2.setColor(new Color(241, 245, 249));
        g2.fillRect(gameWidth, 0, 400, panelHeight);

        g2.setColor(new Color(203, 213, 225));
        g2.drawLine(gameWidth, 0, gameWidth, panelHeight);

        Graphics2D gameAreaGraphics = (Graphics2D) g2.create();
        gameAreaGraphics.setClip(0, 0, gameWidth, panelHeight);

        if (model.areRedShapesVisible() && (state == GameState.RED_VISIBLE
            || state == GameState.WAITING_FOR_RED
            || state == GameState.LEVEL_COMPLETE
            || state == GameState.PLACING_BLUE)) {
            model.getRedShapes().forEach(shape -> drawShape(gameAreaGraphics, shape, selectedShape));
        }

        model.getBlueShapes().forEach(shape -> drawShape(gameAreaGraphics, shape, selectedShape));

        if (controller instanceof ControleurSouris) {
            drawDragPreview(gameAreaGraphics, (ControleurSouris) controller);
        }

        gameAreaGraphics.dispose();
        drawHud(g2, model, state, gameWidth);
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

    private void drawHud(Graphics2D g2, GameModel model, GameState state, int gameWidth) {
        double remainingSeconds = model.getRemainingRedTime() / 1000.0;
        double limitMillis = Math.max(model.getCurrentRedTimeLimitMillis(), 1L);
        double ratio = Math.max(0.0, Math.min(1.0, model.getRemainingRedTime() / limitMillis));
        int infoX = gameWidth + 16;
        g2.setColor(Color.BLACK);
        g2.drawString("INFORMATIONS", infoX, 24);
        g2.drawString("Etat: " + state, infoX, 52);
        g2.drawString("Score: " + model.getTotalScore(), infoX, 76);
        g2.drawString("Compteur: " + model.getBlueShapesPlacedThisLevel() + "/" + model.getBlueShapesPerLevel(), infoX, 100);
        g2.drawString("Rouges: " + model.getRedShapes().size(), infoX, 124);
        g2.drawString("Niveau: " + model.getLevel(), infoX, 148);
        g2.drawString("Temps restant: " + String.format("%.1f", remainingSeconds) + " s", infoX, 172);
        g2.drawString("Objectif: " + model.getBlueShapesPerLevel() + " formes", infoX, 196);
        g2.drawString("Restantes: " + model.getBlueShapesRemainingForLevel(), infoX, 220);

        int barX = infoX;
        int barY = 236;
        int barWidth = 200;
        int barHeight = 18;
        g2.setColor(new Color(203, 213, 225));
        g2.fillRoundRect(barX, barY, barWidth, barHeight, 10, 10);
        g2.setColor(getCountdownColor(ratio));
        g2.fillRoundRect(barX, barY, (int) Math.round(barWidth * ratio), barHeight, 10, 10);
        g2.setColor(new Color(148, 163, 184));
        g2.drawRoundRect(barX, barY, barWidth, barHeight, 10, 10);

        if (model.isTwoPlayerMode()) {
            g2.drawString("Mode: 2 joueurs", infoX, 272);
            g2.drawString("Tour: " + model.getCurrentPlayerName() + " [" + model.getCurrentPlayerColorLabel() + "]", infoX, 296);
            g2.drawString("Tours: " + model.getTurnsPlayed() + "/" + (model.getMaxTurnsPerPlayer() * 2), infoX, 320);
            g2.drawString(model.getRedPlayerName() + " score: " + model.getRedPlayerScore(), infoX, 344);
            g2.drawString(model.getBluePlayerName() + " score: " + model.getBluePlayerScore(), infoX, 368);
        }
    }

    private Color getCountdownColor(double ratio) {
        if (ratio > 0.5) {
            double t = (1.0 - ratio) * 2.0;
            return blend(new Color(34, 197, 94), new Color(250, 204, 21), t);
        }

        double t = (0.5 - ratio) * 2.0;
        return blend(new Color(250, 204, 21), new Color(239, 68, 68), t);
    }

    private Color blend(Color start, Color end, double ratio) {
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        int red = (int) Math.round(start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int green = (int) Math.round(start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int blue = (int) Math.round(start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
        return new Color(red, green, blue);
    }
}
