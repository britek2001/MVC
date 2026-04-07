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
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Command> commandHistory = new ArrayList<>();
    
    public static void main(String[] args) {
        
        Player player1 = new Player("Krim  jag ");
        Player player2 = new Player("Taiwen");
        GameModel game = new GameModel();
        
        // Add Observer to listen for model changes
        game.addObserver((obs, arg) -> System.out.println("  [OBSERVER] " + arg));
        
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

        System.out.println("\n⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Testing Commands Pattern⋅⋅⋅********************");
        testCommands(game);

        System.out.println("\nPLAYERS STATS");
        System.out.printf("%s %n", player1.getStatistics()); 
        System.out.printf("%s %n", player2.getStatistics());

    }
    
    private static void testCommands(GameModel game) {
        Rectangle rect = new Rectangle(200, 200, 60, 60, Color.BLUE);
        CreateShapeCommand create = new CreateShapeCommand(game, rect);
        create.execut();
        System.out.println("Create: " + game.getBlueShapes().size());
        
        DeleteShapeCommand delete = new DeleteShapeCommand(game, rect);
        delete.execut();
        System.out.println("Delete: " + game.getBlueShapes().size());
        delete.undo();
        System.out.println("Undo delete: " + game.getBlueShapes().size());
        
        Circle circle = new Circle(150, 150, 30, Color.BLUE);
        game.addBlueShape(circle);
        MoveShapeCommand move = new MoveShapeCommand(game, circle, 500, 500);
        move.execut();
        System.out.println("Move: (" + circle.getX() + ", " + circle.getY() + ")");
        move.undo();
        System.out.println("Undo move: (" + circle.getX() + ", " + circle.getY() + ")");
        
        Rectangle rect2 = new Rectangle(400, 400, 50, 50, Color.BLUE);
        game.addBlueShape(rect2);
        ResizeShapeCommand resize = new ResizeShapeCommand(game, rect2, 1.5, 1.5);
        resize.execut();
        System.out.println("Resize: " + rect2.width);
        resize.undo();
        System.out.println("Undo resize: " + rect2.width);
    }
}