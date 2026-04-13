package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class ValidateResizeFactorValidator extends ResizeValidator {
    @Override
    protected boolean validateLocal(GameModel model, GameShape shape, double factor) {
        return factor != 0;
    }
}
