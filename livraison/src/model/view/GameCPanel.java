package mvc.model.view;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class GameCPanel extends JPanel {

    private final GameModel model;

    public GameCPanel(
            GameModel model,
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame) {
        super(new FlowLayout(FlowLayout.CENTER));
        this.model = model;
        initialize(onCreateRectangle, onCreateCircle, onDeleteSelected, onUndo, onRedo, onEndGame);
    }

    private void initialize(
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame) {

        JButton createRectangleButton = new JButton("Rectangle Click ");
        createRectangleButton.addActionListener(e -> onCreateRectangle.run());

        JButton createCircleButton = new JButton("Circle Click ");
        createCircleButton.addActionListener(e -> onCreateCircle.run());

        JButton eliminateSelectedButton = new JButton("Eliminate Selected Figure");
        eliminateSelectedButton.addActionListener(e -> onDeleteSelected.run());

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> onUndo.run());

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(e -> onRedo.run());

        JButton endGameButton = new JButton("Termine Jeux!");
        endGameButton.addActionListener(e -> {
            model.setGameState(GameState.GAME_OVER);
            onEndGame.run();
        });
        add(endGameButton);

        add(createRectangleButton);
        add(createCircleButton);
        add(eliminateSelectedButton);
        add(undoButton);
        add(redoButton);
    }
}
