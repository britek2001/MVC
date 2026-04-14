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

    @Test
    @DisplayName("testUndoRedoOrderWithMultipleCommands")
    void testUndoRedoOrderWithMultipleCommands() {
        Circle c1 = new Circle(100, 100, 20, Color.BLUE);
        Circle c2 = new Circle(200, 200, 20, Color.BLUE);

        CreateShapeCommand cmd1 = new CreateShapeCommand(model, c1);
        CreateShapeCommand cmd2 = new CreateShapeCommand(model, c2);

        manager.executeAndStore(cmd1);
        manager.executeAndStore(cmd2);
        assertEquals(2, model.getBlueShapes().size());

        assertTrue(manager.undo());
        assertFalse(model.getBlueShapes().contains(c2));
        assertTrue(model.getBlueShapes().contains(c1));

        assertTrue(manager.undo());
        assertFalse(model.getBlueShapes().contains(c1));
        assertEquals(0, model.getBlueShapes().size());

        assertTrue(manager.redo());
        assertTrue(model.getBlueShapes().contains(c1));
        assertFalse(model.getBlueShapes().contains(c2));

        assertTrue(manager.redo());
        assertTrue(model.getBlueShapes().contains(c1));
        assertTrue(model.getBlueShapes().contains(c2));
        assertEquals(2, model.getBlueShapes().size());
    }

    @Test
    @DisplayName("testNewCommandAfterUndoClearsRedoStack")
    void testNewCommandAfterUndoClearsRedoStack() {
        Circle c1 = new Circle(100, 100, 20, Color.BLUE);
        Circle c2 = new Circle(200, 200, 20, Color.BLUE);
        Circle c3 = new Circle(300, 300, 20, Color.BLUE);

        manager.executeAndStore(new CreateShapeCommand(model, c1));
        manager.executeAndStore(new CreateShapeCommand(model, c2));
        assertEquals(2, model.getBlueShapes().size());

        assertTrue(manager.undo());
        assertFalse(model.getBlueShapes().contains(c2));

        manager.executeAndStore(new CreateShapeCommand(model, c3));
        assertTrue(model.getBlueShapes().contains(c1));
        assertTrue(model.getBlueShapes().contains(c3));
        assertFalse(model.getBlueShapes().contains(c2));

        assertFalse(manager.redo());
    }
}