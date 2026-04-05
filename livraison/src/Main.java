import javax.swing.*;

import mvc.model.game.Player;
import mvc.commands.*;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import mvc.model.game.GameModel;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.strategies.ShapeGenerationStrategy;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Main {
    public static void main(String[] args) {
        
        Player player1 = new Player("Krim  jag ");
        Player player2 = new Player("Taiwen");
        GameModel game = new GameModel();
        Rectangle rect1 = new Rectangle(10, 10, 100, 100, Color.RED);
        ShapeGenerationStrategy strategy = new RandomGenerationStrategy();
        System.out.println("The used Strategy " + strategy.getStrategyName());
        game.setGenerationStrategy(strategy);

        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Initialisation data paremeters⋅⋅⋅********************");
        game.getStatistics();
        game.generateRedShapes(4, 800, 600);
        game.getStatistics();

        System.out.println("PLAYERS STATS");
        System.out.printf("%s %n", player1.getStatistics()); 
        System.out.printf("%s %n", player2.getStatistics()); 
    }
}