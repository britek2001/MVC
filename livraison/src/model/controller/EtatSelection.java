package mvc.model.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.GameShape;

public class EtatSelection implements EtatInteraction {
    private GameModel model;
    
    public EtatSelection(GameModel model) {
        this.model = model;
    }
    
    @Override
    public void sourisAppuyee(MouseEvent e, ControleurSouris controller) {
        GameShape shape = model.findBlueShapeAt(e.getX(), e.getY());
        if (shape == null) return;

        int zone = shape.getZoneType(new Point2D.Double(e.getX(), e.getY()));

        if (zone == 1) {
            GameState previousState = model.getState();
            model.setState(GameState.MOVING_SHAPE);
            controller.changerEtat(new EtatMoveShape(model, shape, previousState));
        } else if (zone == 0) {
            GameState previousState = model.getState();
            model.setState(GameState.RESIZING_SHAPE);
            controller.changerEtat(new EtatResizeShape(model, shape, previousState));
        } else {
            return;
        }

        controller.getEtatCourant().sourisAppuyee(e, controller);
    }

    @Override public void sourisDeplacee(MouseEvent e, ControleurSouris controller) {}
    @Override public void sourisRelachee(MouseEvent e, ControleurSouris controller) {}
    @Override public void sourisBougee(MouseEvent e, ControleurSouris controller) {}
}