package mvc.model.view;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GameCPanel extends JPanel {

    public GameCPanel(
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo) {
        super(new FlowLayout(FlowLayout.CENTER));
        initialize(onCreateRectangle, onCreateCircle, onDeleteSelected, onUndo, onRedo);
    }

    private void initialize(
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo) {

        JButton createRectangleButton = new JButton("Rectangle (2 clics)");
        createRectangleButton.addActionListener(e -> onCreateRectangle.run());

        JButton createCircleButton = new JButton("Circle (2 clics)");
        createCircleButton.addActionListener(e -> onCreateCircle.run());

        JButton eliminateSelectedButton = new JButton("Eliminate Selected Figure");
        eliminateSelectedButton.addActionListener(e -> onDeleteSelected.run());

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> onDeleteSelected.run());

        JButton moveButton = new JButton("Move");
        moveButton.addActionListener(e -> onDeleteSelected.run());

        JButton resizeButton = new JButton("Resize");
        resizeButton.addActionListener(e -> onDeleteSelected.run());

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> onUndo.run());

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> onRedo.run());

        add(createRectangleButton);
        add(createCircleButton);
        add(eliminateSelectedButton);
        add(deleteButton);
        add(undoButton);
        add(redoButton);
    }
}
