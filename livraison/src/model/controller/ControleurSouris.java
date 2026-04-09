package mvc.model.controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import mvc.model.commands.Command;

public class ControleurSouris extends MouseAdapter {
    private EtatInteraction etatCourant;
    private List<Command> commandHistory = new ArrayList<>();
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    
    public ControleurSouris(EtatInteraction etatInitial) {
        this.etatCourant = etatInitial;
    }

    public void changerEtat(EtatInteraction nouvelEtat) {
        this.etatCourant = nouvelEtat;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        etatCourant.sourisAppuyee(e, this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        etatCourant.sourisDeplacee(e, this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        etatCourant.sourisBougee(e, this);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        etatCourant.sourisRelachee(e, this);
    }

    public EtatInteraction getEtatCourant() { return etatCourant; }
    
    public void addCommand(Command cmd) {
        commandHistory.add(cmd);
        undoStack.push(cmd);
        redoStack.clear();
    }

    public boolean undoLastCommand() {
        if (undoStack.isEmpty()) return false;
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        return true;
    }

    public boolean redoLastCommand() {
        if (redoStack.isEmpty()) return false;
        Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        return true;
    }

    public void repaintView() { }
}