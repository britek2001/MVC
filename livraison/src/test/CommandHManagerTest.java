package mvc.model.view;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.Color;

import mvc.model.game.GameModel;
import mvc.model.commands.CreateShapeCommand;
import mvc.model.commands.Command;
import mvc.model.shapes.Circle;

class CommandHManagerTest {

    private CommandHManager manager;
    private GameModel model;
    private Circle testCircle;

    @BeforeEach
    void setUp() {
        manager = new CommandHManager();
        model = new GameModel();
        model.setGameAreaSize(800, 600);
        testCircle = new Circle(100, 100, 20, Color.BLUE);
    }

    @Test
    @DisplayName("testExecuteAndStore")
    void testExecuteAndStore() {
        CreateShapeCommand cmd = new CreateShapeCommand(model, testCircle);
        manager.executeAndStore(cmd);
        assertTrue(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testUndo")
    void testUndo() {
        CreateShapeCommand cmd = new CreateShapeCommand(model, testCircle);
        manager.executeAndStore(cmd);
        assertTrue(model.getBlueShapes().contains(testCircle));
        
        boolean undone = manager.undo();
        
        assertTrue(undone);
        assertFalse(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testRedo")
    void testRedo() {
        CreateShapeCommand cmd = new CreateShapeCommand(model, testCircle);
        manager.executeAndStore(cmd);
        manager.undo();
        assertFalse(model.getBlueShapes().contains(testCircle));
        
        boolean redone = manager.redo();
        
        assertTrue(redone);
        assertTrue(model.getBlueShapes().contains(testCircle));
    }

    @Test
    @DisplayName("testClearRedoStack")
    void testClearRedoStack() {
        CreateShapeCommand cmd1 = new CreateShapeCommand(model, testCircle);
        manager.executeAndStore(cmd1);
        manager.undo();
        
        assertTrue(manager.redo());
        
        Circle anotherCircle = new Circle(200, 200, 15, Color.BLUE);
        CreateShapeCommand cmd2 = new CreateShapeCommand(model, anotherCircle);
        manager.executeAndStore(cmd2);
        assertFalse(manager.redo());
    }

    @Test
    @DisplayName("testUndoEmpty")
    void testUndoEmpty() {
        assertFalse(manager.undo());
    }

    @Test
    @DisplayName("testRedoEmpty")
    void testRedoEmpty() {
        assertFalse(manager.redo());
    }
}