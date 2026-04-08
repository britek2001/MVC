import javax.swing.*;

import mvc.model.game.Player;
import mvc.model.commands.*;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.game.GameModel;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.view.GameView;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static List<Command> commandHistory = new ArrayList<>();
    private enum CommandType {
        CREATE,
        DELETE,
        MOVE,
        RESIZE
    }
    
    public static void main(String[] args) {
        
        Player player1 = new Player("Krim  jag ");
        Player player2 = new Player("Taiwen");
        GameModel game = new GameModel();

        ShapeGenerationStrategy strategy = new RandomGenerationStrategy();
        System.out.println("The used Strategy " + strategy.getStrategyName());
        game.setGenerationStrategy(strategy);

        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView(game, null);
            JFrame frame = new JFrame("Rectangle and Circle Game "+ strategy.getStrategyName());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.setSize(900, 890);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Initialisation data paremeters⋅⋅⋅********************");
        game.getStatistics();
        game.generateRedShapes(4, 800, 600);

        // Default startup shapes created through Command pattern
        Rectangle startupRect = new Rectangle(120, 120, 60, 60, Color.BLUE);
        Circle startupCircle = new Circle(260, 220, 35, Color.BLUE);
        executeCommand(CommandType.CREATE, game, startupRect, 0, 0);
        executeCommand(CommandType.CREATE, game, startupCircle, 0, 0);

        game.getStatistics();

        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅ TEST ********************");
        //System.out.println("\n⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Testing Commands Pattern⋅⋅⋅********************");
        //testCommands(game);
        //System.out.println("\n⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅PLAYERS STATS⋅⋅⋅********************");
        //System.out.printf("%s %n", player1.getStatistics()); 
        //System.out.printf("%s %n", player2.getStatistics());
    }
    
    private static void testCommands(GameModel game) {
        // Crear figura en la misma posicion 
        Rectangle blueRect0 = new Rectangle(100, 100, 50, 50, Color.BLUE);
        Rectangle blueRect1 = new Rectangle(100, 100, 50, 50, Color.BLUE);
        // Crear circulo en la misma poscion 
        Circle blueCircle = new Circle(300, 300, 40, Color.BLUE);
        Circle blueCircle1 = new Circle(300, 300, 40, Color.BLUE); 
        executeCommand(CommandType.CREATE, game, blueRect0, 0, 0);
        boolean canPlace = game.canPlaceBlueShape(blueRect1);
        System.out.println("Test 1 Same position: " + (canPlace == false));
        executeCommand(CommandType.CREATE, game, blueCircle, 0, 0);
        boolean canPlace2 = game.canPlaceBlueShape(blueCircle1);
        System.out.println("Test 1 Same position: " + (canPlace2 == false));
        executeCommand(CommandType.CREATE, game, blueRect1, 0, 0);
        
        Rectangle rect = new Rectangle(200, 200, 60, 60, Color.BLUE);
        Command create = executeCommand(CommandType.CREATE, game, rect, 0, 0);
        System.out.println("Test 2 Creation of blue forms:" + (game.getBlueShapes().size() == 3))   ;
        
        Command delete = executeCommand(CommandType.DELETE, game, rect, 0, 0);
        System.out.println("Test 2 Deletion of blue forms:" + (game.getBlueShapes().size() == 3));
        delete.undo();

        System.out.println("Test 2 Deletion of blue forms: " + (game.getBlueShapes().size() == 3));
        
        Circle circle = new Circle(150, 150, 30, Color.BLUE);
        executeCommand(CommandType.CREATE, game, circle, 0, 0);
        Command move = executeCommand(CommandType.MOVE, game, circle, 500, 500);
        System.out.println("Test 3 Made move " + (circle.getX() == 500 && circle.getY() == 500));
        move.undo();
        System.out.println("Test 3 Made move " + (circle.getX() == 150 && circle.getY() == 150));
        
        Rectangle rect2 = new Rectangle(400, 400, 50, 50, Color.BLUE);
        executeCommand(CommandType.CREATE, game, rect2, 0, 0);
        Command resize = executeCommand(CommandType.RESIZE, game, rect2, 1.5, 1.5);
        System.out.println("Test 4 Made resize : " + (rect2.width == 75 && rect2.height == 75));
        resize.undo();
        System.out.println("Test 4 Made resize :" + (rect2.width == 50 && rect2.height == 50));
    }

    private static Command executeCommand(CommandType type, GameModel game, GameShape shape, double value1, double value2) {
        Command command;

        switch (type) {
            case CREATE:
                command = new CreateShapeCommand(game, shape);
                break;
            case DELETE:
                command = new DeleteShapeCommand(game, shape);
                break;
            case MOVE:
                command = new MoveShapeCommand(game, shape, value1, value2);
                break;
            case RESIZE:
                command = new ResizeShapeCommand(game, shape, value1, value2);
                break;
            default:
                throw new IllegalArgumentException("Unsupported command type: " + type);
        }
        command.execut();
        commandHistory.add(command);
        return command;
    }
}