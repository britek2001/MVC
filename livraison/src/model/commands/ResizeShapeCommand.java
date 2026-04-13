package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;
import java.util.logging.Logger;

public class ResizeShapeCommand implements Command {
    private static final Logger logger = Logger.getLogger(ResizeShapeCommand.class.getName());
    private GameModel model;
    private GameShape shape;
    private double oldX, oldY;
    private double newX, newY;
    private ResizeValidator validator;

    public ResizeShapeCommand(GameModel model, GameShape shape, double newX, double newY) {
        this.model = model;
        this.shape = shape;
        this.oldX = shape.getX();
        this.oldY = shape.getY();
        this.newX = newX;
        this.newY = newY;
        this.validator = buildValidationChain();
    }

    private ResizeValidator buildValidationChain() {
        ResizeValidator first = new ValidateResizeFactorValidator();
        ResizeValidator second = first.setNext(new ValidateIntersectionValidator());
        second.setNext(new ValidateGameAreaValidator());
        return first;
    }

    @Override
    public void execut() {
        if (!validator.validate(model, shape, newX)) {
            logger.warning("BLUE_SHAPE_RESIZED ERROR: validation failed");
            return;
        }
        shape.resize(newX);
        model.modelChanged("BLUE_SHAPE_RESIZED");
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