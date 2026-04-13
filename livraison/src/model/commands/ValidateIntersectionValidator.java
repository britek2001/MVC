package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class ValidateIntersectionValidator extends ResizeValidator {
    @Override
    protected boolean validateLocal(GameModel model, GameShape shape, double factor) {
        GameShape resizedShape = shape.copy();
        resizedShape.resize(factor);
        return !model.getRedShapes().stream().anyMatch(red -> red.intersects(resizedShape));
    }
}
