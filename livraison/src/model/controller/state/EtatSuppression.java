package mvc.model.controller.state;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class EtatSuppression implements EtatInteraction {

    private GameModel modele;

    public EtatSuppression(GameModel modele) {
        this.modele = modele;
    }

    @Override
    public void sourisAppuyee(int x, int y) {
        GameShape forme = modele.findBlueShapeAt(x, y);

        if (forme != null) {
            modele.removeBlueShape(forme);
            System.out.println("Forme supprimée.");
        } else {
            System.out.println("Aucune forme trouvée à cet endroit.");
        }
        
    }

    @Override
    public void sourisDeplacee(int x, int y) {
        // rien à faire ici
    }

    @Override
    public void sourisRelachee(int x, int y) {
        // rien à faire ici
    }
}