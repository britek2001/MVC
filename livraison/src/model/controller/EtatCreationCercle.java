package mvc.model.controller;

import java.awt.event.MouseEvent;
import java.util.Objects;
import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;

public class EtatCreationCercle implements EtatInteraction {
    private final GameModel modele;
    private int startX, startY, currentX, currentY;
    private boolean isDragging = false;

    public EtatCreationCercle(GameModel modele) {
        this.modele = Objects.requireNonNull(modele, "modele must not be null");
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
            double rayon = Math.hypot((double)currentX - (double)startX, (double)currentY - (double)startY);

            if (rayon < 10) {
                endDrag(controller);
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

            endDrag(controller);
        }
    }

    private void endDrag(ControleurSouris controller) {
        isDragging = false;
        modele.modelChanged("DRAG_END");
        if (controller != null) {
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