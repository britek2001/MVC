package mvc.model.controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import mvc.model.game.GameModel;
import mvc.model.shapes.Rectangle;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;

public class EtatCreationRectangle implements EtatInteraction {
    private GameModel modele;
    private int startX, startY, currentX, currentY;
    private boolean isDragging = false;

    public EtatCreationRectangle(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(MouseEvent e, ControleurSouris controller) {
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
        if (isDragging) {
            currentX = e.getX();
            currentY = e.getY();
            modele.modelChanged("DRAG_UPDATE");
        }
    }

    @Override
    public void sourisRelachee(MouseEvent e, ControleurSouris controller) {
        if (isDragging) {
            int largeur = Math.abs(currentX - startX);
            int hauteur = Math.abs(currentY - startY);

            if (largeur < 10 || hauteur < 10) {
                isDragging = false;
                modele.modelChanged("DRAG_END");
                controller.changerEtat(new EtatSelection(modele));
                return;
            }

            int coinX = Math.min(startX, currentX);
            int coinY = Math.min(startY, currentY);

            ShapeGenerationStrategy strategy = modele.getGenerationStrategy();
            Rectangle rect;
            if (strategy instanceof ClickPlacementStrategy) {
                rect = ((ClickPlacementStrategy) strategy)
                        .createRectangleFromClicks(startX, startY, currentX, currentY, Color.BLUE);
            } else {
                rect = new Rectangle(coinX, coinY, largeur, hauteur, Color.BLUE);
            }
            modele.addBlueShape(rect);

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