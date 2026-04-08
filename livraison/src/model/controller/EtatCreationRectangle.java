package mvc.model.controller;

import java.awt.Color;
import mvc.model.game.GameModel;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.shapes.Rectangle;

public class EtatCreationRectangle implements EtatInteraction {

    private GameModel modele;
    private int startX;
    private int startY;
    private int currentX;
    private int currentY;
    private boolean isDragging = false;

    public EtatCreationRectangle(GameModel modele) {
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

        int largeur = Math.abs(currentX - startX);
        int hauteur = Math.abs(currentY - startY);

        if (largeur < 10 || hauteur < 10) {
            isDragging = false;
            modele.modelChanged("DRAG_END");
            return;
        }

        ShapeGenerationStrategy strategy = modele.getGenerationStrategy();
        Rectangle rectangle;
        if (strategy instanceof ClickPlacementStrategy) {
            rectangle = ((ClickPlacementStrategy) strategy)
                    .createRectangleFromClicks(startX, startY, currentX, currentY, Color.BLUE);
        } else {
            int coinX = Math.min(startX, currentX);
            int coinY = Math.min(startY, currentY);
            rectangle = new Rectangle(coinX, coinY, largeur, hauteur, Color.BLUE);
        }
        modele.addBlueShape(rectangle);

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
        // A fair 
    }

    public boolean isDragging() {
        return isDragging;
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }
}
