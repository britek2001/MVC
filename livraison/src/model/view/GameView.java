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
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import javax.swing.*;
import java.util.ArrayList;
import mvc.model.strategies.ShapeGenerationStrategy;
import mvc.model.strategies.ClickPlacementStrategy;
import mvc.model.strategies.RandomGenerationStrategy;

public class GameView extends JPanel implements Observer {

    private final GameModel model;
    private final MouseAdapter controller;
    private final Runnable onEndGame;
    private final Random random = new Random();
    private final GamePainter painter = new GamePainter();
    private final CommandHManager historyManager = new CommandHManager();
    private final Timer hudRefreshTimer;
    private GameShape selectedShape;
    private boolean gameResultShown;

    public GameView(GameModel model, MouseAdapter controller, Runnable onEndGame) {
        
        this.model = model;
        this.controller = controller;
        this.onEndGame = onEndGame;
        gameResultShown = false;
        hudRefreshTimer = new Timer(100, e -> repaint());
        
        this.model.addObserver(this);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        hudRefreshTimer.start();
        initializeControls();
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleSelection(e);
            }
        });

        if (controller != null) {
            addMouseListener(controller);
            addMouseMotionListener(controller);
        } else {
            System.out.println("WARNING mouse controller not set");
        }
    }

    @Override
    public void removeNotify() {
        hudRefreshTimer.stop();
        model.deleteObserver(this);
        super.removeNotify();
    }

    @Override
    public void update(Observable o, Object g) {
        repaint();
        if (model.isGameFinished()) {
            showGameResultPopup();
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
        painter.paint(g2, model, selectedShape, controller);
    }

    private void initializeControls() {
        JPanel controls = new GameCPanel(
                model,
                this::activateRectangleCreation,
                this::activateCircleCreation,
                this::deleteSelectedShape,
                this::undoLastCommand,
            this::redoLastCommand,
            onEndGame);
        add(controls, BorderLayout.SOUTH);
    }

    private void activateRectangleCreation() {
        if (controller instanceof ControleurSouris) {
            ((ControleurSouris) controller).changerEtat(new EtatCreationRectangle(model));
            setFocusable(true);
            requestFocus();
        }
    }

    private void activateCircleCreation() {
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
        historyManager.executeAndStore(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();

    }

    private void deleteSelectedShape() {
        GameShape shape = getTargetShape();
        if (shape == null) {
            return;
        }

        historyManager.executeAndStore(new DeleteShapeCommand(model, shape));
        selectedShape = null;
        repaint();
    }

    private void undoLastCommand() {

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
        historyManager.executeAndStore(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();
    }

    public void createCircle(Circle circle) {
        historyManager.executeAndStore(new CreateShapeCommand(model, circle));
        selectedShape = circle;
        repaint();
    }


}