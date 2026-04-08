package mvc.model.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ControleurSouris extends MouseAdapter {

    private EtatInteraction etatCourant;

    public ControleurSouris(EtatInteraction etatInitial) {
        this.etatCourant = etatInitial;
    }

    public void changerEtat(EtatInteraction nouvelEtat) {
        this.etatCourant = nouvelEtat;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        etatCourant.sourisAppuyee(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        etatCourant.sourisDeplacee(e.getX(), e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        etatCourant.sourisDeplacee(e.getX(), e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        etatCourant.sourisRelachee(e.getX(), e.getY());
    }

    public EtatInteraction getEtatCourant() {
        return etatCourant;
    }
    
}