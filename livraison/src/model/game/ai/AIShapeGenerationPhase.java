package mvc.model.game.ai;

import java.util.List;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public abstract class AIShapeGenerationPhase {
    protected AIShapeGenerationPhase next;

    public AIShapeGenerationPhase setNext(AIShapeGenerationPhase nextPhase) {
        this.next = nextPhase;
        return nextPhase;
    }

    public final List<GameShape> generate(GameModel model, int count, int panelWidth, int panelHeight) {
        List<GameShape> shapes = generateShapes(model, count, panelWidth, panelHeight);
        if (next != null) {
            return next.generate(model, count, panelWidth, panelHeight);
        }
        return shapes;
    }

    protected abstract List<GameShape> generateShapes(GameModel model, int count, int panelWidth, int panelHeight);

    public String getPhaseName() {
        return this.getClass().getSimpleName();
    }
}
