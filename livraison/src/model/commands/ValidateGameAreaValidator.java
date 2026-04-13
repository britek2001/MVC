package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class ValidateGameAreaValidator extends ResizeValidator {
    @Override
    protected boolean validateLocal(GameModel model, GameShape shape, double factor) {
        GameShape resizedShape = shape.copy();
        resizedShape.resize(factor);
        return model.isShapeWithinGameArea(resizedShape);
    }
}
