package mvc;
import mvc.model.game.LevelConfig;
import mvc.model.game.ai.AIPlayPhase;
import mvc.model.game.flow.GameFlowBuilder;
import mvc.model.game.flow.ShowObstaclesPhase;
import mvc.model.game.flow.PlayerPlayPhase;
import mvc.model.game.flow.CalculateScorePhase;
import mvc.model.commands.*;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatSelection;
import mvc.model.shapes.GameShape;
import mvc.model.game.GameModel;
import mvc.model.game.turn.TurnCoordinator;
import mvc.model.game.turn.PlayerTurnState;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.AIPlayerStrategy;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.view.GameView;
import mvc.model.view.MainMenuView;
import mvc.model.view.GameTutorialView;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final int WINDOW_WIDTH = 1180;
    private static final int WINDOW_HEIGHT = 890;
    private static final int INFO_PANEL_WIDTH = 400;
    private static List<Command> commandHistory = new ArrayList<>();
    private enum CommandType {
        CREATE,
        DELETE,
        MOVE,
        RESIZE
    }
    
    public static void main(String[] args) {
        GameModel game = new GameModel();
        showTutorial(game);
    }
    
    private static void showTutorial(GameModel game) {
        GameTutorialView tutorialView = new GameTutorialView();
        tutorialView.show(() -> showMenu(game));
    }
    
    private static void showMenu(GameModel game) {
        MainMenuView menuView = new MainMenuView();
        menuView.show(selection -> {
            ShapeGenerationStrategy strategy;
            if (selection.isTwoPlayers()) {
                game.enableTwoPlayerMode(selection.redPlayerName(), selection.bluePlayerName());
                game.setAIPlayerMode(false);
                strategy = new RandomGenerationStrategy();
                game.setGenerationStrategy(strategy);
                logger.info("Strategy: Two Players");
                logger.info("Jouer Red: " + game.getRedPlayerName());
                logger.info("Jouer Blue: " + game.getBluePlayerName());
            } else if (MainMenuView.STRATEGY_AI.equals(selection.strategyLabel())) {
                game.enableTwoPlayerMode("IA", "Humain");
                game.setAIPlayerMode(true);
                game.setRedPlayerTurn(false);
                strategy = new AIPlayerStrategy();
                game.setGenerationStrategy(new RandomGenerationStrategy());
                logger.info("Strategy: AI Player");
                logger.info("Mode: Alternance Humain (Bleu) vs IA (Rouge)");
            } else {
                strategy = new RandomGenerationStrategy();
                game.disableTwoPlayerMode();
                game.setAIPlayerMode(false);
                game.setGenerationStrategy(strategy);
                logger.info("Strategy: Random Generation");
                logger.info("Difficulte: " + selection.difficultyLabel());
            }

            LevelConfig config = game.getLevelConfig(selection.level());
            game.setCurrentLevel(selection.level() - 1);
            startGame(game, strategy, selection.strategyLabel(), config);
        });
    }

    private static void startGame(GameModel game, ShapeGenerationStrategy strategy, String strategyLabel, LevelConfig config) {
        SwingUtilities.invokeLater(() -> {
            ControleurSouris controller = new ControleurSouris(new EtatSelection(game));
            TurnCoordinator turnCoordinator = new TurnCoordinator();
            JFrame frame = new JFrame("Jeu Rectangle et Cercle " + strategy.getStrategyName());
            AIPlayerStrategy aiStrategy = (strategy instanceof AIPlayerStrategy) ? (AIPlayerStrategy) strategy : null;
            GameView view = new GameView(game, controller, () -> {
                frame.dispose();
                showMenu(new GameModel());
            }, aiStrategy, turnCoordinator);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(view);
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            if (MainMenuView.STRATEGY_TWO_PLAYERS.equals(strategyLabel)) {
                Thread turnWaitThread = new Thread(() -> {
                    try {
                        while (!game.isGameFinished()) {
                            turnCoordinator.awaitState(PlayerTurnState.HUMAN_GUI);
                            turnCoordinator.awaitTurnCompletion();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, "turn-wait-thread");
                turnWaitThread.setDaemon(true);
                turnWaitThread.start();
            }

            SwingUtilities.invokeLater(() -> {
                int realGameAreaWidth = Math.max(1, view.getWidth() - INFO_PANEL_WIDTH);
                int realGameAreaHeight = Math.max(1, view.getHeight());
                game.setGameAreaSize(realGameAreaWidth, realGameAreaHeight);

                if (MainMenuView.STRATEGY_RANDOM.equals(strategyLabel)) {
                    logger.info("Lancement variante simple (" + config.timeSeconds + " secondes visibles)");
                    game.generateRedShapes(config.redShapeCount, realGameAreaWidth, realGameAreaHeight);
                    GameFlowBuilder flow = GameFlowBuilder.createDefaultFlow();
                    flow.executeFlow(game);
                    logger.info("Score final: " + game.getTotalScore());
                } else if (MainMenuView.STRATEGY_TWO_PLAYERS.equals(strategyLabel)) {
                    logger.info("Lancement variante avec mémorisation (" + config.timeSeconds + " secondes)");
                    game.generateRedShapes(config.redShapeCount, realGameAreaWidth, realGameAreaHeight);
                    GameFlowBuilder flow = GameFlowBuilder.createVariantWithWait(config.timeSeconds * 1000L);
                    flow.executeFlow(game);
                    logger.info("Score final: " + game.getTotalScore());
                } else if (MainMenuView.STRATEGY_AI.equals(strategyLabel)) {
                    logger.info("Lancement variante IA (Intelligence Artificielle)");
                    game.generateRedShapes(config.redShapeCount, realGameAreaWidth, realGameAreaHeight);
                    // Provide game model context to AI strategy
                    if (aiStrategy != null) {
                        aiStrategy.setGameModel(game);
                        logger.info("AI: GameModel context provided to AI strategy");
                    }
                    GameFlowBuilder flow = new GameFlowBuilder()
                        .addPhase(new ShowObstaclesPhase())
                        .addPhase(new AIPlayPhase())
                        .addPhase(new PlayerPlayPhase())
                        .addPhase(new CalculateScorePhase());
                        
                    flow.executeFlow(game);
                    logger.info("Score final: " + game.getTotalScore());
                } else {
                    logger.info("Mode " + strategyLabel + ": pas de generation automatique de formes");
                }
            });
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