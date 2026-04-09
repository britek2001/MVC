package mvc.model.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import mvc.model.commands.CreateShapeCommand;
import mvc.model.commands.DeleteShapeCommand;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import javax.swing.JPanel;

public class GameView extends JPanel implements Observer {

    private final GameModel model;
    private final MouseAdapter controller;
    private final Random random = new Random();
    private final GamePainter painter = new GamePainter();
    private final CommandHistoryManager historyManager = new CommandHistoryManager();
    private GameShape selectedShape;

    public GameView(GameModel model, MouseAdapter controller) {
        this.model = model;
        this.controller = controller;
        this.model.addObserver(this);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
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
        }
    }

    @Override
    public void update(Observable o, Object g) {
        repaint();
    }

    private void handleSelection(MouseEvent e) {
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
        JPanel controls = new GameControlsPanel(
                this::activateRectangleCreation,
                this::activateCircleCreation,
                this::deleteSelectedShape,
                this::undoLastCommand,
                this::redoLastCommand);
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