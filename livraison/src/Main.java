import javax.swing.*;

import mvc.model.game.Player;
import mvc.model.commands.*;
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
        ShapeGenerationStrategy strategy = new RandomGenerationStrategy();
        System.out.println("The used Strategy " + strategy.getStrategyName());
        game.setGenerationStrategy(strategy);
        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Initialisation data paremeters⋅⋅⋅********************");
        game.getStatistics();
        game.generateRedShapes(4, 800, 600);
        game.getStatistics();
        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Test the intersection Jugador genera Figura Azul ⋅⋅⋅********************");
        // Crear figura en la misma posicion 
        Rectangle blueRect0 = new Rectangle(100, 100, 50, 50, Color.BLUE);
        Rectangle blueRect1 = new Rectangle(100, 100, 50, 50, Color.BLUE);
        // Crear circulo en la misma poscion 
        Circle blueCircle = new Circle(300, 300, 40, Color.BLUE);
        Circle blueCircle1 = new Circle(300, 300, 40, Color.BLUE); 
        game.addBlueShape(blueRect0);
        boolean canPlace = game.canPlaceBlueShape(blueRect1);
        System.out.println("Can place blue shape: " + canPlace);
        game.addBlueShape(blueCircle);
        boolean canPlace2 = game.canPlaceBlueShape(blueCircle1);
        System.out.println("Can place blue shape: " + canPlace2);
        game.addBlueShape(blueRect1);

        System.out.println("PLAYERS STATS");
        System.out.printf("%s %n", player1.getStatistics()); 
        System.out.printf("%s %n", player2.getStatistics());

    }
}