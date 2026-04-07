package controller.state;
public interface EtatInteraction {
    void sourisAppuyee(int x, int y);
    void sourisDeplacee(int x, int y);
    void sourisRelachee(int x, int y);
}