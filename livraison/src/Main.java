package mvc;
import mvc.model.game.LevelConfig;
import mvc.model.commands.*;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatSelection;
import mvc.model.shapes.GameShape;
import mvc.model.game.GameModel;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.view.GameView;
import mvc.model.view.MainMenuView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int WINDOW_WIDTH = 1180;
    private static final int WINDOW_HEIGHT = 890;
    private static final int INFO_PANEL_WIDTH = 400;
    private static final int GAME_AREA_WIDTH = WINDOW_WIDTH - INFO_PANEL_WIDTH;
    private static List<Command> commandHistory = new ArrayList<>();
    private enum CommandType {
        CREATE,
        DELETE,
        MOVE,
        RESIZE
    }
    
    public static void main(String[] args) {
        GameModel game = new GameModel();
        showMenu(game);
    }
    
    private static void showMenu(GameModel game) {
        MainMenuView menuView = new MainMenuView();
        menuView.show(selection -> {
            ShapeGenerationStrategy strategy;
            if (selection.isTwoPlayers()) {
                game.enableTwoPlayerMode(selection.redPlayerName(), selection.bluePlayerName());
                strategy = new ClickPlacementStrategy();
                game.setGenerationStrategy(strategy);
                System.out.println("Strategy: Two Players");
                System.out.println("Jouer Red: " + game.getRedPlayerName());
                System.out.println("Jouer Blue: " + game.getBluePlayerName());
            } else {
                strategy = new RandomGenerationStrategy();
                game.disableTwoPlayerMode();
                game.setGenerationStrategy(strategy);
                System.out.println("Strategy: Random Generation");
                System.out.println("Difficulte: " + selection.difficultyLabel());
            }

            LevelConfig config = game.getLevelConfig(selection.level());
            game.setCurrentLevel(selection.level() - 1);
            game.setGameAreaSize(GAME_AREA_WIDTH, WINDOW_HEIGHT);
            game.generateRedShapes(config.redShapeCount, GAME_AREA_WIDTH, WINDOW_HEIGHT);

            System.out.println("Temps  pour definir les formes: " + config.timeSeconds + " secondes");
            startGame(game, strategy);
        });
    }

    private static void startGame(GameModel game, ShapeGenerationStrategy strategy) {
        SwingUtilities.invokeLater(() -> {
            ControleurSouris controller = new ControleurSouris(new EtatSelection(game));
            JFrame frame = new JFrame("Jeu Rectangle et Cercle " + strategy.getStrategyName());
            GameView view = new GameView(game, controller, () -> {
                frame.dispose();
                showMenu(new GameModel());
            });
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
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
                throw new IllegalArgumentException("Pas suporter: " + type);
        }
        command.execut();
        commandHistory.add(command);
        return command;
    }
}