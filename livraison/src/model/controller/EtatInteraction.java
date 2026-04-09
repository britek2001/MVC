package mvc.model.controller;

import java.awt.event.MouseEvent;

public interface EtatInteraction {
    void sourisAppuyee(MouseEvent e, ControleurSouris controller);
    void sourisDeplacee(MouseEvent e, ControleurSouris controller);
    void sourisRelachee(MouseEvent e, ControleurSouris controller);
    void sourisBougee(MouseEvent e, ControleurSouris controller);
}