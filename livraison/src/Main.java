import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import model.commands.Command;
import model.commands.CreateShapeCommand;
import model.commands.DeleteShapeCommand;
import model.commands.MoveShapeCommand;
import model.commands.ResizeShapeCommand;
import model.games.GameModel;
import model.games.Player;
import model.shapes.Circle;
import model.shapes.GameShape;
import model.shapes.Rectangle;
import model.strategy.RandomGenerationStrategy;
import model.strategy.ShapeGenerationStrategy;
import model.view.GameView;

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

        // ShapeGenerationStrategy strategy = new ClickPlacementStrategy();
        ShapeGenerationStrategy strategy = new RandomGenerationStrategy();
        System.out.println("The used Strategy " + strategy.getStrategyName());
        game.setGenerationStrategy(strategy);
        ControleurSouris controleurSouris = new ControleurSouris(new EtatSelection(game));

        SwingUtilities.invokeLater(() -> {
            GameView view = new GameView(game, null);
            JFrame frame = new JFrame("Rectangle and Circle Game " + strategy.getStrategyName());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.setSize(900, 890);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅Initialisation data paremeters⋅⋅⋅********************");
        game.getStatistics();
        game.generateRedShapes(4, 800, 600);

        Rectangle startupRect = new Rectangle(120, 120, 60, 60, Color.BLUE);
        Circle startupCircle = new Circle(260, 220, 35, Color.BLUE);
        executeCommand(CommandType.CREATE, game, startupRect, 0, 0);
        executeCommand(CommandType.CREATE, game, startupCircle, 0, 0);

        game.getStatistics();
        System.out.println("⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅⋅ TEST ********************");
        testCommands(game);
    }

    private static void testCommands(GameModel game) {
        Rectangle blueRect0 = new Rectangle(100, 100, 50, 50, Color.BLUE);
        Rectangle blueRect1 = new Rectangle(100, 100, 50, 50, Color.BLUE);
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
        System.out.println("Test 2 Creation of blue forms:" + (game.getBlueShapes().size() == 3));

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
                command = new MoveShapeCommand(game, shape, shape.getX(), shape.getY(), value1, value2);
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