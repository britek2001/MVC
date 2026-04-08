package mvc.model.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mvc.model.commands.Command;
import mvc.model.commands.CreateShapeCommand;
import mvc.model.commands.DeleteShapeCommand;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.controller.EtatInteraction;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;

public class GameView extends JPanel implements Observer, MouseListener {

    private GameModel model;
    private MouseAdapter mouseController;
    private GameShape selectedShape;

    private Deque<Command> undoStack;
    private Deque<Command> redoStack;

    private JLabel infoLabel;

    public GameView(GameModel model, MouseAdapter controller) {
        this.model = model;
        this.mouseController = controller;

        this.undoStack = new ArrayDeque<Command>();
        this.redoStack = new ArrayDeque<Command>();
        this.infoLabel = new JLabel();

        this.model.addObserver(this);

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        createButtons();
        createBottomInfo();
        updateInfoLabel();

        addMouseListener(this);

        if (mouseController != null) {
            addMouseListener(mouseController);
            addMouseMotionListener(mouseController);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("LEVEL_COMPLETE".equals(arg)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(
                        GameView.this,
                        "Level complete!\nCurrent total score: " + model.getTotalScore(),
                        "Next Level",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    model.startNextLevel();
                }
            });
        }

        updateInfoLabel();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        selectedShape = getShapeAt(e.getX(), e.getY());
        updateInfoLabel();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        GameState state = model.getState();

        if (state == GameState.RED_VISIBLE
                || state == GameState.WAITING_FOR_RED
                || state == GameState.PLACING_BLUE) {
            for (GameShape shape : model.getRedShapes()) {
                drawOneShape(g2, shape);
            }
        }

        for (GameShape shape : model.getBlueShapes()) {
            drawOneShape(g2, shape);
        }

        if (mouseController instanceof ControleurSouris) {
            ControleurSouris controller = (ControleurSouris) mouseController;
            drawPreview(g2, controller);
        }
    }

    private void drawOneShape(Graphics2D g2, GameShape shape) {
        g2.setColor(shape.getColor());

        if (shape instanceof Rectangle) {
            Rectangle rect = (Rectangle) shape;

            g2.fill(new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Rectangle2D.Double(rect.x, rect.y, rect.width, rect.height));
            }
        } else if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            double r = circle.getRadius();

            g2.fill(new Ellipse2D.Double(
                circle.getX() - r,
                circle.getY() - r,
                r * 2,
                r * 2
            ));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Ellipse2D.Double(
                    circle.getX() - r,
                    circle.getY() - r,
                    r * 2,
                    r * 2
                ));
            }
        }
    }

    private void drawPreview(Graphics2D g2, ControleurSouris controller) {
        EtatInteraction currentState = controller.getEtatCourant();

        if (currentState instanceof EtatCreationRectangle) {
            EtatCreationRectangle rectState = (EtatCreationRectangle) currentState;

            if (rectState.isDragging()) {
                int x = Math.min(rectState.getStartX(), rectState.getCurrentX());
                int y = Math.min(rectState.getStartY(), rectState.getCurrentY());
                int w = Math.abs(rectState.getCurrentX() - rectState.getStartX());
                int h = Math.abs(rectState.getCurrentY() - rectState.getStartY());

                g2.setColor(new Color(0, 100, 200, 100));
                g2.fillRect(x, y, w, h);
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(x, y, w, h);
            }
        }

        if (currentState instanceof EtatCreationCercle) {
            EtatCreationCercle circleState = (EtatCreationCercle) currentState;

            if (circleState.isDragging()) {
                double radius = Math.hypot(
                    circleState.getCurrentX() - circleState.getStartX(),
                    circleState.getCurrentY() - circleState.getStartY()
                );

                int x = (int) (circleState.getStartX() - radius);
                int y = (int) (circleState.getStartY() - radius);
                int diameter = (int) (radius * 2);

                g2.setColor(new Color(0, 100, 200, 100));
                g2.fillOval(x, y, diameter, diameter);
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(x, y, diameter, diameter);
            }
        }
    }

    private void createButtons() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton rectangleButton = new JButton("Rectangle (2 clics)");
        rectangleButton.addActionListener(e -> {
            if (mouseController instanceof ControleurSouris) {
                ControleurSouris controller = (ControleurSouris) mouseController;
                controller.changerEtat(new EtatCreationRectangle(model));
                setFocusable(true);
                requestFocus();
            }
        });

        JButton circleButton = new JButton("Circle (2 clics)");
        circleButton.addActionListener(e -> {
            if (mouseController instanceof ControleurSouris) {
                ControleurSouris controller = (ControleurSouris) mouseController;
                controller.changerEtat(new EtatCreationCercle(model));
                setFocusable(true);
                requestFocus();
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteCurrentShape());

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoAction());

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> redoAction());

        topPanel.add(rectangleButton);
        topPanel.add(circleButton);
        topPanel.add(deleteButton);
        topPanel.add(undoButton);
        topPanel.add(redoButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createBottomInfo() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(infoLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateInfoLabel() {
        String selectedText = "none";

        if (selectedShape != null) {
            selectedText = selectedShape.getClass().getSimpleName();
        }

        infoLabel.setText(
            "État: " + model.getState()
            + " | Score: " + model.getTotalScore()
            + " | Bleues: " + model.getBlueShapes().size()
            + " | Rouges: " + model.getRedShapes().size()
            + " | Nivel: " + model.getLevel()
            + " | Selected: " + selectedText
        );
    }

    private void executeCommand(Command command) {
        command.execut();
        undoStack.push(command);
        redoStack.clear();
        updateInfoLabel();
    }

    private void deleteCurrentShape() {
        if (selectedShape == null) {
            return;
        }

        if (!model.getBlueShapes().contains(selectedShape)) {
            selectedShape = null;
            updateInfoLabel();
            repaint();
            return;
        }

        executeCommand(new DeleteShapeCommand(model, selectedShape));
        selectedShape = null;
        updateInfoLabel();
        repaint();
    }

    private void undoAction() {
        if (undoStack.isEmpty()) {
            return;
        }

        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        model.modelChanged("UNDO");
        updateInfoLabel();
        repaint();
    }

    private void redoAction() {
        if (redoStack.isEmpty()) {
            return;
        }

        Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        model.modelChanged("REDO");
        updateInfoLabel();
        repaint();
    }

    private GameShape getShapeAt(double x, double y) {
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
        updateInfoLabel();
        repaint();
    }

    public void createCircle(Circle circle) {
        executeCommand(new CreateShapeCommand(model, circle));
        selectedShape = circle;
        updateInfoLabel();
        repaint();
    }
}