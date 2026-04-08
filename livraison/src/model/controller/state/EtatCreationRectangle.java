package mvc.model.controller.state;
import java.awt.*;
import mvc.model.game.GameModel;
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
        startX = x;
        startY = y;
        currentX = x;
        currentY = y;
        isDragging = true;
        System.out.println("Début création rectangle : (" + x + ", " + y + ")");
        modele.modelChanged("DRAG_START");
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        if (isDragging) {
            currentX = x;
            currentY = y;
            System.out.println("Aperçu rectangle jusqu'à (" + x + ", " + y + ")");
            modele.modelChanged("DRAG_UPDATE");
        }
    }

    @Override
    public void sourisRelachee(int x, int y) {
        
        if (!isDragging) return;
        
        isDragging = false;
        int largeur = Math.abs(x - startX);
        int hauteur = Math.abs(y - startY);

        if (largeur < 10 || hauteur < 10) {
            System.out.println("Rectangle trop petit, annulé.");
            modele.modelChanged("DRAG_END");
            return;
        }

        int coinX = Math.min(startX, x);
        int coinY = Math.min(startY, y);

        Rectangle rectangle = new Rectangle(coinX, coinY, largeur, hauteur, Color.BLUE);
        modele.addBlueShape(rectangle);

        System.out.println("Rectangle créé : x=" + coinX + ", y=" + coinY
                + ", largeur=" + largeur + ", hauteur=" + hauteur);
        modele.modelChanged("DRAG_END");
    }

    public boolean isDragging() {
        return isDragging;
    }

    public int getStartX() { return startX; }
    public int getStartY() { return startY; }
    public int getCurrentX() { return currentX; }
    public int getCurrentY() { return currentY; }

}