package controller.state;

import java.awt.*;
import model.games.GameModel;
import model.shapes.Rectangle;

public class EtatCreationRectangle implements EtatInteraction {

    private GameModel modele;
    private int startX;
    private int startY;

    public EtatCreationRectangle(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        startX = x;
        startY = y;
        System.out.println("Début création rectangle : (" + x + ", " + y + ")");
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        System.out.println("Aperçu rectangle jusqu'à (" + x + ", " + y + ")");
    }

    @Override
    public void sourisRelachee(int x, int y) {
        int largeur = Math.abs(x - startX);
        int hauteur = Math.abs(y - startY);

        int coinX = Math.min(startX, x);
        int coinY = Math.min(startY, y);

        Rectangle rectangle = new Rectangle(coinX, coinY, largeur, hauteur, Color.BLUE);
        modele.addBlueShape(rectangle);

        System.out.println("Rectangle créé : x=" + coinX + ", y=" + coinY
                + ", largeur=" + largeur + ", hauteur=" + hauteur);
    }
}