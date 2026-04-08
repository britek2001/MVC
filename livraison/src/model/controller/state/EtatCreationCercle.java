package mvc.model.controller.state;
import java.awt.*;
import mvc.model.controller.state.EtatInteraction;
import mvc.model.game.GameModel;
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
        startX = x;
        startY = y;
        currentX = x;
        currentY = y;
        isDragging = true;
        System.out.println("Début création cercle : (" + x + ", " + y + ")");
        modele.modelChanged("DRAG_START");
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        if (isDragging) {
            currentX = x;
            currentY = y;
            System.out.println("Aperçu du cercle jusqu'à (" + x + ", " + y + ")");
            modele.modelChanged("DRAG_UPDATE");
        }
    }

    @Override
    public void sourisRelachee(int x, int y) {
        if (!isDragging) return;
        
        isDragging = false;
        double rayon = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));

        if (rayon < 10) {
            System.out.println("Cercle trop petit, annulé.");
            modele.modelChanged("DRAG_END");
            return;
        }

        Circle cercle = new Circle(startX, startY, rayon, Color.BLUE);
        modele.addBlueShape(cercle);

        System.out.println("Cercle créé avec rayon = " + rayon);
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