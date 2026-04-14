package mvc.model.strategies;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AITest {

    @Test
    @DisplayName("aiDoesNotGenerateAfterGameFinished")
    void aiDoesNotGenerateAfterGameFinished() {
        GameModel model = new GameModel();
        AIPlayerStrategy ai = new AIPlayerStrategy();
        ai.setGameModel(model);
        ai.switchToAITurn();
        model.endGame();
        List<GameShape> shapes = ai.generateShapes(3, model.getGameWidth(), model.getGameHeight());
        assertNotNull(shapes, "Generation doit etre null");
        assertTrue(shapes.isEmpty(), "AI doit pas genere de figure si la partie et finie");
    }
}
