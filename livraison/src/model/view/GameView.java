import java.util.Observable;
import java.util.Observer;

public class GameView implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        String event = (String) arg; 

        if ("RED_SHAPES_GENERATED".equals(event)) {
            System.out.println("RED_SHAPES_GENERATED");
        } else if ("BLUE_SHAPE_ADDED".equals(event)) {
            System.out.println("Blue ajoutée");
        } else if ("BLUE_SHAPE_REMOVED".equals(event)) {
            System.out.println("Blue supprimée");
        }
    }
}