package model.commands;
import model.games.GameModel;
import model.shapes.GameShape;


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
        if (model.getRedShapes().stream().anyMatch(red -> red.intersects(shape))) {
            undo(); // Undo the resize if it intersects with a red shape
            System.out.println("Resize undone due to intersection with a red shape.");
        } else {
            model.modelChanged("BLUE_SHAPE_RESIZED");
        }
    }

    @Override
    public void undo() {
        if (newX == 0) return;
        shape.resize(1 / newX);
        model.modelChanged("BLUE_SHAPE_RESIZE_UNDO");
    }

    @Override
    public void redo() {
        if (newX == 0) return;
        shape.resize(newX);
        model.modelChanged("BLUE_SHAPE_RESIZE_REDO");
    }
}