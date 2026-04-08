package mvc.model.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JPanel;

import mvc.model.commands.Command;
import mvc.model.commands.CreateShapeCommand;
import mvc.model.commands.DeleteShapeCommand;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;

public class GameView  extends JPanel implements Observer, MouseListener {

    private GameModel model; 
    private MouseAdapter controller; 
    private final Random random = new Random();
    private GameShape selectedShape;
    
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public GameView(GameModel model , MouseAdapter controller) {
        this.model = model;
        this.controller = controller;
        this.model.addObserver(this);
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        initializeControls();

        addMouseListener(this);

        if (controller != null) {
            addMouseListener(controller);
            addMouseMotionListener(controller);
        }
    }

    public void update(Observable o , Object g) {
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        selectedShape = findShapeAt(e.getX(), e.getY());
        repaint();
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
        if (state == GameState.RED_VISIBLE || state == GameState.WAITING_FOR_RED || state == GameState.PLACING_BLUE) {
            model.getRedShapes().forEach(shape -> drawShape(g2, shape));
        }
        model.getBlueShapes().forEach(shape -> drawShape(g2, shape));
        g2.setColor(Color.BLACK);
        g2.drawString("État: " + state, 10, 20);
        g2.drawString("Score: " + model.getTotalScore(), 10, 40);
        g2.drawString("Bleues: " + model.getBlueShapes().size(), 10, 60);
        g2.drawString("Rouges: " + model.getRedShapes().size(), 10, 80);
        g2.drawString("Niveau: " + model.getLevel(), 10, 100);
        g2.drawString("Red Visible Time: " + model.getRedVisibleTime() + " ms", 10, 120);
    }

    private void drawShape(Graphics2D g2, GameShape shape) {
        g2.setColor(shape.getColor());

        if (shape instanceof Rectangle) {
            Rectangle rectangle = (Rectangle) shape;
            g2.fill(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Rectangle2D.Double(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
            }
        } else if (shape instanceof Circle) {
            Circle circle = (Circle) shape;
            double radius = circle.getRadius();
            g2.fill(new Ellipse2D.Double(circle.getX() - radius, circle.getY() - radius, radius * 2, radius * 2));

            if (shape == selectedShape) {
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new Ellipse2D.Double(circle.getX() - radius, circle.getY() - radius, radius * 2, radius * 2));
            }
        }
    }

    private void initializeControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton createRectangleButton = new JButton("Rectangle");
        createRectangleButton.addActionListener(e -> createRandomRectangle());

        JButton createCircleButton = new JButton("Circle");
        createCircleButton.addActionListener(e -> createRandomCircle());

        JButton eliminateSelectedButton = new JButton("Eliminate Selected Figure");
        eliminateSelectedButton.addActionListener(e -> deleteSelectedShape());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> deleteSelectedShape());

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoLastCommand());

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> redoLastCommand());

        controls.add(createRectangleButton);
        controls.add(createCircleButton);
        controls.add(eliminateSelectedButton);
        controls.add(deleteButton);
        controls.add(undoButton);
        controls.add(redoButton);

        add(controls, BorderLayout.SOUTH);
    }

    private void createRandomRectangle() {
        double width = 40 + random.nextInt(80);
        double height = 40 + random.nextInt(80);
        int panelWidth = Math.max(getWidth(), 900);
        int panelHeight = Math.max(getHeight(), 700);
        double x = 20 + random.nextInt(Math.max(1, panelWidth - 160));
        double y = 20 + random.nextInt(Math.max(1, panelHeight - 220));

        Rectangle rect = new Rectangle(x, y, width, height, Color.BLUE);
        executeAndStore(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();
    }

    private void createRandomCircle() {
        double radius = 20 + random.nextInt(40);
        int panelWidth = Math.max(getWidth(), 900);
        int panelHeight = Math.max(getHeight(), 700);
        double x = 20 + random.nextInt(Math.max(1, panelWidth - 160));
        double y = 20 + random.nextInt(Math.max(1, panelHeight - 220));

        Circle circle = new Circle(x, y, radius, Color.BLUE);
        executeAndStore(new CreateShapeCommand(model, circle));
        selectedShape = circle;
        repaint();
    }

    private void deleteSelectedShape() {
        GameShape shape = getTargetShape();
        if (shape == null) {
            return;
        }

        executeAndStore(new DeleteShapeCommand(model, shape));
        selectedShape = null;
        repaint();
    }

    private void undoLastCommand() {
        if (undoStack.isEmpty()) {
            return;
        }

        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        model.modelChanged("UNDO");
        repaint();
    }

    private void redoLastCommand() {
        if (redoStack.isEmpty()) {
            return;
        }

        Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        model.modelChanged("REDO");
        repaint();
    }

    private void executeAndStore(Command command) {
        command.execut();
        undoStack.push(command);
        redoStack.clear();
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
        executeAndStore(new CreateShapeCommand(model, rect));
        selectedShape = rect;
        repaint();
    }

    public void createCircle(Circle circle) {
        executeAndStore(new CreateShapeCommand(model, circle));
        selectedShape = circle;
        repaint();
    }

}