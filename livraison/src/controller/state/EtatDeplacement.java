package controller.state;

import model.games.GameModel;
import model.shapes.GameShape;

public class EtatDeplacement implements EtatInteraction {

    private GameModel modele;
    private GameShape formeSelectionnee;
    private int derniereX;
    private int derniereY;

    public EtatDeplacement(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        formeSelectionnee = modele.findBlueShapeAt(x, y);

        if (formeSelectionnee != null) {
            derniereX = x;
            derniereY = y;
            System.out.println("Forme sélectionnée pour déplacement.");
        } else {
            System.out.println("Aucune forme sélectionnée.");
        }
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        if (formeSelectionnee != null) {
            double dx = x - derniereX;
            double dy = y - derniereY;

            formeSelectionnee.move(dx, dy);

            derniereX = x;
            derniereY = y;

            System.out.println("Déplacement en cours...");
        }
    }

    @Override
    public void sourisRelachee(int x, int y) {
        if (formeSelectionnee != null) {
            System.out.println("Fin du déplacement.");
        }
        formeSelectionnee = null;
    }
}