import javax.swing.*;
import mvc.model.game.LevelConfig;
import mvc.model.game.Player;
import mvc.model.commands.*;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatSelection;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.game.GameModel;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.view.GameView;
import mvc.model.game.GameState;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
        SwingUtilities.invokeLater(() -> {
            JFrame menuFrame = new JFrame("ASI GAME");
            menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            menuFrame.setSize(400, 300);
            menuFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            JLabel strategyLabel = new JLabel("Selection de strategie:");
            String[] strategies = {"Random Generation", "Two Players"};
            JComboBox<String> strategyComboBox = new JComboBox<>(strategies);

            JLabel difficultyLabel = new JLabel("Selection de Difficulte:");
            String[] difficulties = {"Facile", "Moyen", "Difficile", "Tres difficile", "Extreme"};
            JComboBox<String> difficultyComboBox = new JComboBox<>(difficulties);

            JButton startButton = new JButton(" Comencer le jeu ");
            startButton.addActionListener(e -> {
                String selectedStrategy = (String) strategyComboBox.getSelectedItem();
                String selectedDifficulty = (String) difficultyComboBox.getSelectedItem();

                int level = switch (selectedDifficulty) {
                    case "Facile" -> 1;
                    case "Moyen" -> 2;
                    case "Difficile" -> 3;
                    case "Tres difficile" -> 4;
                    case "Extreme" -> 5;
                    default -> 1;
                };

                if ("Two Players".equals(selectedStrategy)) {
                    setupTwoPlayersAndStart(menuFrame, game, level);
                    return;
                }

                ShapeGenerationStrategy strategy;
                if ("Click".equals(selectedStrategy)) {
                    strategy = new ClickPlacementStrategy();
                } else {
                    strategy = new RandomGenerationStrategy();
                }

                game.disableTwoPlayerMode();
                game.setGenerationStrategy(strategy);

                LevelConfig config = game.getLevelConfig(level);
                game.setCurrentLevel(level - 1);
                game.setGameAreaSize(GAME_AREA_WIDTH, WINDOW_HEIGHT);
                game.generateRedShapes(config.redShapeCount, GAME_AREA_WIDTH, WINDOW_HEIGHT);

                System.out.println("Strategy: " + selectedStrategy);
                System.out.println("Difficulte: " + selectedDifficulty);
                System.out.println("Temps  pour definir les formes: " + config.timeSeconds + " secondes");

                menuFrame.dispose();
                startGame(game, strategy);
            });

            panel.add(strategyLabel);
            panel.add(strategyComboBox);
            panel.add(difficultyLabel);
            panel.add(difficultyComboBox);
            panel.add(startButton);

            menuFrame.add(panel);
            menuFrame.setVisible(true);
        });
    }

    private static void setupTwoPlayersAndStart(JFrame menuFrame, GameModel game, int level) {
        JTextField redPlayerField = new JTextField(15);
        JTextField bluePlayerField = new JTextField(15);

        JPanel form = new JPanel(new GridLayout(0, 1, 6, 6));
        form.add(new JLabel("Nom du joueur rouge:"));
        form.add(redPlayerField);
        form.add(new JLabel("Nom du joueur bleu:"));
        form.add(bluePlayerField);

        int result = JOptionPane.showConfirmDialog(
                menuFrame,
                form,
                "Two Players Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String redName = redPlayerField.getText();
        String blueName = bluePlayerField.getText();

        game.enableTwoPlayerMode(redName, blueName);
        game.setGenerationStrategy(new ClickPlacementStrategy());

        LevelConfig config = game.getLevelConfig(level);
        game.setCurrentLevel(level - 1);
        game.setGameAreaSize(GAME_AREA_WIDTH, WINDOW_HEIGHT);
        game.generateRedShapes(config.redShapeCount, GAME_AREA_WIDTH, WINDOW_HEIGHT);

        System.out.println("Strategy: Two Players");
        System.out.println("Jouer Red: " + game.getRedPlayerName());
        System.out.println("Jouer Blue: " + game.getBluePlayerName());
        menuFrame.dispose();
        startGame(game, new ClickPlacementStrategy());
    }

    private static void startGame(GameModel game, ShapeGenerationStrategy strategy) {
        SwingUtilities.invokeLater(() -> {
            ControleurSouris controller = new ControleurSouris(new EtatSelection(game));
            JFrame frame = new JFrame("Jeu Rectangle et Cercle " + strategy.getStrategyName());
            GameView view = new GameView(game, controller, () -> {
                frame.dispose();
                showMenu(new GameModel());
            });
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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