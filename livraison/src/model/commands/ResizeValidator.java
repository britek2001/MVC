package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public abstract class ResizeValidator {
    protected ResizeValidator next;

    public ResizeValidator setNext(ResizeValidator next) {
        this.next = next;
        return next;
    }

    public final boolean validate(GameModel model, GameShape shape, double factor) {
        if (!validateLocal(model, shape, factor)) {
            return false;
        }
        if (next != null) {
            return next.validate(model, shape, factor);
        }
        return true;
    }

    protected abstract boolean validateLocal(GameModel model, GameShape shape, double factor);
}
