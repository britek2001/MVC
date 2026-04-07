package controller.state;

import java.awt.*;
import model.games.GameModel;
import model.shapes.Circle;

public class EtatCreationCercle implements EtatInteraction {

    private GameModel modele;
    private int startX;
    private int startY;

    public EtatCreationCercle(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        startX = x;
        startY = y;
        System.out.println("Début création cercle : (" + x + ", " + y + ")");
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        System.out.println("Aperçu du cercle jusqu'à (" + x + ", " + y + ")");
    }

    @Override
    public void sourisRelachee(int x, int y) {
        double rayon = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));

        Circle cercle = new Circle(startX, startY, rayon, Color.BLUE);
        modele.addBlueShape(cercle);

        System.out.println("Cercle créé avec rayon = " + rayon);
    }
}