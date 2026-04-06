package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class ResizeShapeCommand implements Command {
    private GameModel model;
    private GameShape shape;
    private double oldX, oldY;
    private double newX, newY;

    public ResizeShapeCommand(GameModel model, GameShape shape, double newX, double newY) {
        this.model = model;
        this.shape = shape;
        this.oldX = shape.getX();
        this.oldY = shape.getY();
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void execut() {
        if (newX == 0) return;
        shape.resize(newX);
    }

    @Override
    public void undo() {
        if (newX == 0) return;
        shape.resize(1 / newX);
    }

    @Override
    public void redo() {
        if (newX == 0) return;
        shape.resize(newX);
    }
}