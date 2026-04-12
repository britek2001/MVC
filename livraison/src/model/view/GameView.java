package mvc.model.view;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.awt.Window;
import mvc.model.commands.CreateShapeCommand;
import mvc.model.commands.DeleteShapeCommand;
import mvc.model.commands.Command;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import javax.swing.*;
import java.util.ArrayList;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.RandomGenerationStrategy;
import mvc.model.strategies.AIPlayerStrategy;

public class GameView extends JPanel implements Observer {

    private static final int GAME_AREA_TOP_INSET = GamePainter.CONTROL_BAR_HEIGHT;

    private final GameModel model;
    private final MouseAdapter controller;
    private final Runnable onEndGame;
    private final Random random = new Random();
    private final GamePainter painter = new GamePainter();
    private final CommandHManager historyManager = new CommandHManager();
    private final Timer hudRefreshTimer;
    private GameShape selectedShape;
    private boolean gameResultShown;
    private AIPlayerStrategy aiStrategy;
    private Timer aiMoveTimer;
    private Timer aiPlacementAnimationTimer;
    private Timer aiNextShapeTimer;
    private boolean aiTurnInProgress;
    private GameShape aiPreviewShape;
    private java.util.List<GameShape> pendingAIShapes;
    private int pendingAIShapeIndex;
    private static final int AI_ANIMATION_STEPS = 12;
    private static final int AI_ANIMATION_FRAME_MILLIS = 35;
    private static final int AI_SHAPE_DELAY_MILLIS = 140;

    public GameView(GameModel model, MouseAdapter controller, Runnable onEndGame, AIPlayerStrategy aiStrategy) {
        
        this.model = model;
        this.controller = controller;
        this.onEndGame = onEndGame;
        this.aiStrategy = aiStrategy;
        this.model.setGameAreaTopInset(GAME_AREA_TOP_INSET);
        gameResultShown = false;
        aiTurnInProgress = false;
        hudRefreshTimer = new Timer(100, e -> repaint());
        
        this.model.addObserver(this);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        hudRefreshTimer.start();
        initializeControls();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isInputBlocked()) {
                    return;
                }
                handleSelection(e);
            }
        });

        if (controller != null) {
            MouseAdapter gatedController = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!isInputBlocked()) {
                        controller.mousePressed(e);
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!isInputBlocked()) {
                        controller.mouseDragged(e);
                    }
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!isInputBlocked()) {
                        controller.mouseMoved(e);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!isInputBlocked()) {
                        controller.mouseReleased(e);
                    }
                }
            };
            addMouseListener(gatedController);
            addMouseMotionListener(gatedController);
        } else {
            System.out.println("WARNING mouse controller not set");
        }
    }

    @Override
    public void removeNotify() {
        hudRefreshTimer.stop();
        stopAITimers();
        model.deleteObserver(this);
        super.removeNotify();
    }

    @Override
    public void update(Observable o, Object g) {
        if (gameResultShown) {
            return;
        }
        repaint();
        if (model.isGameFinished()) {
            model.showRedShapes();
            showGameResultPopup();
            return;
        }
        if (isAIMatch()) {
            if (model.isRedPlayerTurn()) {
                aiStrategy.switchToAITurn();
                if (!aiTurnInProgress) {
                    scheduleAIMove();
                }
            } else {
                aiStrategy.switchToHumanTurn();
            }
        }
    }

    private void scheduleAIMove() {
        if (aiTurnInProgress) {
            return;
        }
        if (aiMoveTimer != null && aiMoveTimer.isRunning()) {
            return;
        }
        if (aiMoveTimer != null) {
            aiMoveTimer.stop();
        }
        aiMoveTimer = new Timer(500, e -> executeAITurn());
        aiMoveTimer.setRepeats(false);
        aiMoveTimer.start();
    }

    private void executeAITurn() {
        if (aiStrategy == null || aiStrategy.isHumanTurn() || !model.isRedPlayerTurn()) {
            return;
        }
        aiTurnInProgress = true;
        int remainingShapes = model.getBlueShapesPerLevel() - model.getBlueShapesPlacedThisLevel();
        if (remainingShapes <= 0) {
            aiTurnInProgress = false;
            return;
        }

        java.util.List<GameShape> plannedShapes = buildAIPlacementPlan(remainingShapes);
        if (plannedShapes.isEmpty()) {
            aiTurnInProgress = false;
            if (model.isRedPlayerTurn() && model.getBlueShapesPlacedThisLevel() < model.getBlueShapesPerLevel()) {
                scheduleAIMove();
            }
            return;
        }

        pendingAIShapes = plannedShapes;
        pendingAIShapeIndex = 0;
        animateNextAIShape();
    }

    private java.util.List<GameShape> buildAIPlacementPlan(int remainingShapes) {
        java.util.List<GameShape> plan = new ArrayList<>();
        int attempts = 0;
        while (plan.size() < remainingShapes && attempts < 8) {
            java.util.List<GameShape> candidates = aiStrategy.generateShapes(remainingShapes, model.getGameWidth(), model.getGameHeight());
            if (candidates != null) {
                for (GameShape candidate : candidates) {
                    if (plan.size() >= remainingShapes) {
                        break;
                    }
                    if (canPlaceAIShapeSilently(candidate, plan)) {
                        plan.add(candidate);
                    }
                }
            }
            attempts++;
        }
        return plan;
    }

    private boolean canPlaceAIShapeSilently(GameShape shape, java.util.List<GameShape> plannedShapes) {
        if (!model.isShapeWithinGameArea(shape)) {
            return false;
        }

        boolean intersectsRed = model.getRedShapes().stream().anyMatch(red -> shape.intersects(red));
        if (intersectsRed) {
            return false;
        }

        boolean intersectsBlue = model.getBlueShapes().stream().anyMatch(blue -> shape.intersects(blue));
        if (intersectsBlue) {
            return false;
        }

        return plannedShapes.stream().noneMatch(planned -> shape.intersects(planned));
    }

    private void animateNextAIShape() {
        if (!model.isRedPlayerTurn() || pendingAIShapes == null || pendingAIShapeIndex >= pendingAIShapes.size()) {
            finishAITurnAnimation();
            return;
        }

        GameShape targetShape = pendingAIShapes.get(pendingAIShapeIndex);
        GameShape preview = targetShape.copy();
        aiPreviewShape = preview;

        double startX = 18;
        double startY = Math.max(24, model.getGameHeight() - 64);
        preview.setPosition(startX, startY);
        repaint();

        final int[] step = {0};
        aiPlacementAnimationTimer = new Timer(AI_ANIMATION_FRAME_MILLIS, e -> {
            step[0]++;
            double t = Math.min(1.0, step[0] / (double) AI_ANIMATION_STEPS);
            double x = startX + (targetShape.getX() - startX) * t;
            double y = startY + (targetShape.getY() - startY) * t;
            preview.setPosition(x, y);
            repaint();

            if (t >= 1.0) {
                ((Timer) e.getSource()).stop();
                aiPreviewShape = null;
                model.addBlueShapeFromAI(targetShape);
                pendingAIShapeIndex++;
                aiNextShapeTimer = new Timer(AI_SHAPE_DELAY_MILLIS, evt -> {
                    ((Timer) evt.getSource()).stop();
                    animateNextAIShape();
                });
                aiNextShapeTimer.setRepeats(false);
                aiNextShapeTimer.start();
            }
        });
        aiPlacementAnimationTimer.start();
    }

    private void finishAITurnAnimation() {
        aiPreviewShape = null;
        pendingAIShapes = null;
        pendingAIShapeIndex = 0;
        aiTurnInProgress = false;

        if (!model.isRedPlayerTurn()) {
            aiStrategy.switchToHumanTurn();
        } else if (model.getBlueShapesPlacedThisLevel() < model.getBlueShapesPerLevel()) {
            scheduleAIMove();
        }
        repaint();
    }

    private boolean isAIMatch() {
        return aiStrategy != null && model.isTwoPlayerMode() && model.isAIPlayerMode();
    }

    private boolean isHumanInputLocked() {
        return isAIMatch() && model.isRedPlayerTurn();
    }

    private boolean isInputBlocked() {
        return model.isGameFinished() || model.getState() == GameState.GAME_OVER || isHumanInputLocked();
    }

    private void stopAITimers() {
        if (aiMoveTimer != null) {
            aiMoveTimer.stop();
        }
        if (aiPlacementAnimationTimer != null) {
            aiPlacementAnimationTimer.stop();
        }
        if (aiNextShapeTimer != null) {
            aiNextShapeTimer.stop();
        }
    }

    private void showGameResultPopup() {
        if (gameResultShown) {
            return;
        }
        gameResultShown = true;

        SwingUtilities.invokeLater(() -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            JDialog dialog = new JDialog(owner, "Resultat de la partie", Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.setLayout(new BorderLayout(12, 12));

            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

            if (model.isTwoPlayerMode()) {
                String winnerName = model.isGameWon() ? model.getRedPlayerName() : model.getBluePlayerName();
                JLabel winnerLabel = new JLabel("Gagnant: " + winnerName);
                winnerLabel.setAlignmentX(LEFT_ALIGNMENT);
                
                JLabel redScoreLabel = new JLabel(model.getRedPlayerName() + " score: " + model.getRedPlayerScore());
                redScoreLabel.setAlignmentX(LEFT_ALIGNMENT);
                
                JLabel blueScoreLabel = new JLabel(model.getBluePlayerName() + " score: " + model.getBluePlayerScore());
                blueScoreLabel.setAlignmentX(LEFT_ALIGNMENT);

                content.add(winnerLabel);
                content.add(Box.createVerticalStrut(8));
                content.add(redScoreLabel);
                content.add(Box.createVerticalStrut(4));
                content.add(blueScoreLabel);
            } else {
                JLabel resultLabel = new JLabel(model.isGameWon() ? "Victoire !" : "Defaite !");
                resultLabel.setAlignmentX(LEFT_ALIGNMENT);
                JLabel scoreLabel = new JLabel("Score = espace couvert: " + model.getFinalCoveredArea());
                scoreLabel.setAlignmentX(LEFT_ALIGNMENT);
                JLabel shapesLabel = new JLabel("Formes posées: " + model.getBlueShapesPlacedThisLevel() + "/" + model.getBlueShapesPerLevel());
                shapesLabel.setAlignmentX(LEFT_ALIGNMENT);
                JLabel magistralLabel = new JLabel("VICTOIRE MAGISTRAL");
                magistralLabel.setAlignmentX(LEFT_ALIGNMENT);

                content.add(resultLabel);
                content.add(Box.createVerticalStrut(8));
                if (model.isMagistralWin()) {
                    content.add(magistralLabel);
                    content.add(Box.createVerticalStrut(4));
                } else {
                    content.add(scoreLabel);
                    content.add(Box.createVerticalStrut(4));
                }
                content.add(shapesLabel);
            }

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            if (model.canStartNextLevel()) {
                JButton nextLevelButton = new JButton("Niveau supérieur");
                nextLevelButton.addActionListener(evt -> {
                    dialog.dispose();
                    boolean started = model.startNextLevel();
                    if (started) {
                        gameResultShown = false;
                        selectedShape = null;
                        repaint();
                    }
                });
                buttons.add(nextLevelButton);
            }

            JButton returnButton = new JButton("Retourner");
            returnButton.addActionListener(evt -> {
                dialog.dispose();
                onEndGame.run();
            });

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(evt -> dialog.dispose());

            buttons.add(returnButton);
            buttons.add(exitButton);

            dialog.add(content, BorderLayout.CENTER);
            dialog.add(buttons, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        });
    }

    private void handleSelection(MouseEvent e) {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            return;
        }
        if (!model.isPointInsideGameArea(e.getX(), e.getY())) {
            selectedShape = null;
            repaint();
            return;
        }

        selectedShape = findShapeAt(e.getX(), e.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        painter.paint(g2, model, selectedShape, aiPreviewShape, controller);
    }

    private void initializeControls() {
        JPanel controls = new GameCPanel(
                model,
                this::activateRectangleCreation,
                this::activateCircleCreation,
                this::deleteSelectedShape,
                this::undoLastCommand,
            this::redoLastCommand,
            this::completeGameAndExit);
        add(controls, BorderLayout.NORTH);
    }

    private void completeGameAndExit() {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            return;
        }
        model.endGame();
        repaint();
    }

    private void activateRectangleCreation() {
        if (isInputBlocked()) {
            return;
        }
        if (controller instanceof ControleurSouris) {
            ((ControleurSouris) controller).changerEtat(new EtatCreationRectangle(model));
            setFocusable(true);
            requestFocus();
        }
    }

    private void activateCircleCreation() {
        if (isInputBlocked()) {
            return;
        }
        if (controller instanceof ControleurSouris) {
            ((ControleurSouris) controller).changerEtat(new EtatCreationCercle(model));
            setFocusable(true);
            requestFocus();
        }
    }

    private void createRandomRectangle() {

        double width = 40 + random.nextInt(80);
        double height = 40 + random.nextInt(80);

        int panelWidth = Math.max(getWidth(), 900);
        int panelHeight = Math.max(getHeight(), 700);

        double x = 20 + random.nextInt(Math.max(1, panelWidth - 160));
        double y = 20 + random.nextInt(Math.max(1, panelHeight - 220));

        Rectangle rect = new Rectangle(x, y, width, height, Color.BLUE);
        executeCommand(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();

    }

    private void deleteSelectedShape() {
        if (isInputBlocked()) {
            return;
        }
        GameShape shape = getTargetShape();
        if (shape == null) {
            return;
        }

        executeCommand(new DeleteShapeCommand(model, shape));
        selectedShape = null;
        repaint();
    }

    private void executeCommand(Command command) {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            return;
        }
        if (controller instanceof ControleurSouris) {
            command.execut();
            ((ControleurSouris) controller).addCommand(command);
            return;
        }

        historyManager.executeAndStore(command);
    }

    private void undoLastCommand() {
        if (isInputBlocked()) {
            return;
        }

        if (controller instanceof ControleurSouris) {
            boolean undone = ((ControleurSouris) controller).undoLastCommand();
            
            if (undone) {
                model.modelChanged("UNDO");
                repaint();
                return;
            }
        }

        if (!historyManager.undo()) {
            return;
        }

        model.modelChanged("UNDO");
        repaint();

    }

    private void redoLastCommand() {
        if (isInputBlocked()) {
            return;
        }
        
        if (controller instanceof ControleurSouris) {
            boolean redone = ((ControleurSouris) controller).redoLastCommand();
        
            if (redone) {
                model.modelChanged("REDO");
                repaint();
                return;
            }
        
        }

        if (!historyManager.redo()) {
            return;
        }

        model.modelChanged("REDO");
        repaint();
    }

    private GameShape getTargetShape() {
        
        if (selectedShape != null && model.getBlueShapes().contains(selectedShape)) {
            return selectedShape;
        }

        if (!model.getBlueShapes().isEmpty()) {
            selectedShape = model.getBlueShapes().get(model.getBlueShapes().size() - 1);
            return selectedShape;
        }

        return null;

    }

    private GameShape findShapeAt(double x, double y) {
        
        for (int i = model.getBlueShapes().size() - 1; i >= 0; i--) {
            GameShape shape = model.getBlueShapes().get(i);
            if (shape.contains(new Point2D.Double(x, y))) {
                return shape;
            }
        }
        return null;
    
    }

    public void createRectangle(Rectangle rect) {
        executeCommand(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();
    }

    public void createCircle(Circle circle) {
        executeCommand(new CreateShapeCommand(model, circle));
        selectedShape = circle;
        repaint();
    }


}