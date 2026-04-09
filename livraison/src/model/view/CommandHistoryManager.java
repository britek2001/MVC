package mvc.model.view;

import java.util.ArrayDeque;
import java.util.Deque;
import mvc.model.commands.Command;

public class CommandHistoryManager {
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    public void executeAndStore(Command command) {
        command.execut();
        undoStack.push(command);
        redoStack.clear();
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    public boolean redo() {
        if (redoStack.isEmpty()) {
            return false;
        }

        Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        return true;
    }
}
