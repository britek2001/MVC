package mvc.model.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;

public class EtatCreationCercle implements EtatInteraction {
    private GameModel modele;
    private int startX, startY, currentX, currentY;
    private boolean isDragging = false;

    public EtatCreationCercle(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(MouseEvent e, ControleurSouris controller) {
        if (modele.isGameFinished()) {
            return;
        }
        if (!isDragging) {
            startX = e.getX();
            startY = e.getY();
            currentX = startX;
            currentY = startY;
            isDragging = true;
            modele.modelChanged("DRAG_START");
        }
    }

    @Override
    public void sourisDeplacee(MouseEvent e, ControleurSouris controller) {
        if (modele.isGameFinished()) {
            return;
        }
        if (isDragging) {
            currentX = e.getX();
            currentY = e.getY();
            modele.modelChanged("DRAG_UPDATE");
        }
    }

    @Override
    public void sourisRelachee(MouseEvent e, ControleurSouris controller) {
        if (modele.isGameFinished()) {
            return;
        }
        if (isDragging) {
            double rayon = Math.sqrt(Math.pow(currentX - startX, 2) + Math.pow(currentY - startY, 2));

            if (rayon < 10) {
                isDragging = false;
                modele.modelChanged("DRAG_END");
                controller.changerEtat(new EtatSelection(modele));
                return;
            }

            ShapeGenerationStrategy strategy = modele.getGenerationStrategy();
            Circle cercle;
            if (strategy instanceof ClickPlacementStrategy) {
                cercle = ((ClickPlacementStrategy) strategy)
                        .createCircleFromClicks(startX, startY, currentX, currentY, modele.getCurrentDrawingColor());
            } else {
                cercle = new Circle(startX, startY, rayon, modele.getCurrentDrawingColor());
            }
            modele.addBlueShape(cercle);

            isDragging = false;
            modele.modelChanged("DRAG_END");
            controller.changerEtat(new EtatSelection(modele));
        }
    }

    @Override
    public void sourisBougee(MouseEvent e, ControleurSouris controller) {}

    public boolean isDragging() { return isDragging; }
    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
}