package mvc.model.controller;

import java.awt.Color;
import mvc.model.game.GameModel;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.shapes.Circle;

public class EtatCreationCercle implements EtatInteraction {

    private GameModel modele;
    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private boolean isDragging = false;

    public EtatCreationCercle(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        if (!isDragging) {
            startX = x;
            startY = y;
            currentX = x;
            currentY = y;
            isDragging = true;
            modele.modelChanged("DRAG_START");
            return;
        }

        currentX = x;
        currentY = y;
        double rayon = Math.sqrt(Math.pow(currentX - startX, 2) + Math.pow(currentY - startY, 2));

        if (rayon < 10) {
            isDragging = false;
            modele.modelChanged("DRAG_END");
            return;
        }

        ShapeGenerationStrategy strategy = modele.getGenerationStrategy();
        Circle cercle;
        if (strategy instanceof ClickPlacementStrategy) {
            cercle = ((ClickPlacementStrategy) strategy)
                    .createCircleFromClicks(startX, startY, currentX, currentY, Color.BLUE);
        } else {
            cercle = new Circle(startX, startY, rayon, Color.BLUE);
        }
        modele.addBlueShape(cercle);

        isDragging = false;
        modele.modelChanged("DRAG_END");
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        if (isDragging) {
            currentX = x;
            currentY = y;
            modele.modelChanged("DRAG_UPDATE");
        }
    }

    @Override
    public void sourisRelachee(int x, int y) {
        // A faire 
    }

    public boolean isDragging() {
        return isDragging;
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
}
