package mvc.model.controller.state;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class EtatRedimensionnement implements EtatInteraction {

    private GameModel modele;
    private GameShape formeSelectionnee;
    private int startX;
    private int startY;

    public EtatRedimensionnement(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        formeSelectionnee = modele.findBlueShapeAt(x, y);

        if (formeSelectionnee != null) {
            startX = x;
            startY = y;
            System.out.println("Début redimensionnement.");
        } else {
            System.out.println("Aucune forme sélectionnée pour redimensionnement.");
        }
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        if (formeSelectionnee != null) {
            double dx = x - startX;
            double dy = y - startY;

            double distance = Math.sqrt(dx * dx + dy * dy);

            double facteur = 1.0 + distance / 100.0;

            formeSelectionnee.resize(facteur);

            startX = x;
            startY = y;

            System.out.println("Redimensionnement en cours, facteur = " + facteur);
        }
    }

    @Override
    public void sourisRelachee(int x, int y) {
        if (formeSelectionnee != null) {
            System.out.println("Fin du redimensionnement.");
        }
        formeSelectionnee = null;
    }
} 