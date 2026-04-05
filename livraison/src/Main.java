import javax.swing.*;
import mvc.model.game.Player;
import mvc.model.game.GameModel;
import mvc.model.strategies.RandomGenerationStrategy;

public class Main {
    public static void main(String[] args) {
        
        Player player1 = new Player("Krim");
        Player player2 = new Player("Taiwen");
        GameModel game = new GameModel();
        
        game.setGenerationStrategy(new RandomGenerationStrategy());
        
        game.generateRedShapes(4, 800, 600);
        
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