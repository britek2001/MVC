package controller;

import controller.state.EtatInteraction;

public class ControleurSouris {

    private EtatInteraction etatCourant;

    public ControleurSouris(EtatInteraction etatInitial) {
        this.etatCourant = etatInitial;
    }

    public void changerEtat(EtatInteraction nouvelEtat) {
        this.etatCourant = nouvelEtat;
    }

    public void sourisAppuyee(int x, int y) {
        etatCourant.sourisAppuyee(x, y);
    }

    public void sourisDeplacee(int x, int y) {
        etatCourant.sourisDeplacee(x, y);
    }

    public void sourisRelachee(int x, int y) {
        etatCourant.sourisRelachee(x, y);
    }
}