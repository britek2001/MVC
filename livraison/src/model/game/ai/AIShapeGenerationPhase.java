package mvc.model.game.ai;

import java.util.ArrayList;
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
        if (model == null || model.isGameFinished()) {
            return List.of();
        }
        List<GameShape> shapes = generateShapes(model, count, panelWidth, panelHeight);
        if (next != null) {
            List<GameShape> nextShapes = next.generate(model, count, panelWidth, panelHeight);
            if (nextShapes == null) {
                return shapes == null ? List.of() : shapes;
            }
            List<GameShape> merged = new ArrayList<>();
            if (shapes != null) merged.addAll(shapes);
            merged.addAll(nextShapes);
            return merged;
        }
        return shapes == null ? List.of() : shapes;
    }

    protected abstract List<GameShape> generateShapes(GameModel model, int count, int panelWidth, int panelHeight);

    public String getPhaseName() {
        return this.getClass().getSimpleName();
    }
}
