import javax.swing.*;
import mvc.model.game.Player;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import mvc.model.game.GameModel;
import mvc.model.strategies.RandomGenerationStrategy;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Main {
    public static void main(String[] args) {
        
        Player player1 = new Player("Krim");
        Player player2 = new Player("Taiwen");
        GameModel game = new GameModel();
        Rectangle rect1 = new Rectangle(10, 10, 100, 100, Color.RED);
        game.setGenerationStrategy(new RandomGenerationStrategy());
        
        game.generateRedShapes(4, 800, 600);
        
        System.out.printf("Get Red Shape ",game.getRedShapes());

        System.out.println("PLAYERS STATS");
        System.out.printf("%s %n", player1.getName(), player1.getStatistics()); 
        System.out.println(player2.getStatistics());
        
        System.out.println("Game State: " + game.getState());
        System.out.println("Red Shape Visible: " + game.areRedShapesVisible());
        System.out.println("Reds: " + game.getRedShapes().size());
        System.out.println("Blues: " + game.getBlueShapes().size());
        System.out.println("Finish");
    }
}