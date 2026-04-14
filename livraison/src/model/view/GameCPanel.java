package mvc.model.view;

import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import mvc.model.game.GameModel;
import mvc.model.view.theme.StyledButtonFactory;
import mvc.model.view.theme.ThemeManager;
import mvc.model.view.theme.ThemeStrategy;

public class GameCPanel extends JPanel {

    private transient final GameModel model;
    private transient ThemeStrategy theme;
    private final StyledButtonFactory buttonFactory;
    private static final int BUTTON_SIZE = 56;
    private static final int FINISH_BUTTON_WIDTH = 72;
    private static final int PANEL_HEIGHT = 72;

    public GameCPanel(
            GameModel model,
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame) {
        this(model, onCreateRectangle, onCreateCircle, onDeleteSelected, onUndo, onRedo, onEndGame, null);
    }

    public GameCPanel(
            GameModel model,
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame,
            Runnable onValidateTurn) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 8));
        this.model = model;
        this.theme = ThemeManager.getCurrentTheme();
        this.buttonFactory = new StyledButtonFactory(theme);
        setBackground(theme.getInfoPanelBackgroundColor());
        setPreferredSize(new Dimension(0, PANEL_HEIGHT));
        initialize(onCreateRectangle, onCreateCircle, onDeleteSelected, onUndo, onRedo, onEndGame, onValidateTurn);
    }

    private void initialize(
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame,
            Runnable onValidateTurn) {

        JButton createRectangleButton = createShapeButton(ShapeType.RECTANGLE, onCreateRectangle);
        JButton createCircleButton = createShapeButton(ShapeType.CIRCLE, onCreateCircle);
        JButton eliminateSelectedButton = createSquareButton("X", onDeleteSelected);
        JButton undoButton = createSquareButton("<-", onUndo);
        JButton redoButton = createSquareButton("->", onRedo);
        JButton validateButton = onValidateTurn != null
            ? createSquareButton("OK", onValidateTurn)
            : null;
        JButton endGameButton = createSquareButton(" Finish ", onEndGame);
        endGameButton.setPreferredSize(new Dimension(FINISH_BUTTON_WIDTH, BUTTON_SIZE));
        endGameButton.setMinimumSize(new Dimension(FINISH_BUTTON_WIDTH, BUTTON_SIZE));
        endGameButton.setMaximumSize(new Dimension(FINISH_BUTTON_WIDTH, BUTTON_SIZE));

        add(createRectangleButton);
        add(createCircleButton);
        add(eliminateSelectedButton);
        add(undoButton);
        add(redoButton);
        if (validateButton != null) {
            add(validateButton);
        }
        add(endGameButton);
    }

    private JButton createSquareButton(String icon, Runnable onClick) {
        JButton button = buttonFactory.createPrimaryButton(icon, onClick);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        return button;
    }

    private JButton createShapeButton(ShapeType shapeType, Runnable onClick) {
        JButton button = buttonFactory.createPrimaryButton("", onClick);
        button.setIcon(new ShapeIcon(shapeType, 22));
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setFocusPainted(false);
        return button;
    }

    private enum ShapeType {
        RECTANGLE,
        CIRCLE
    }

    private static final class ShapeIcon implements Icon {
        private final ShapeType type;
        private final int size;

        private ShapeIcon(ShapeType type, int size) {
            this.type = type;
            this.size = size;
        }

        @Override
        public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c != null ? c.getForeground() : java.awt.Color.BLACK);

            if (type == ShapeType.RECTANGLE) {
                g2.fill(new Rectangle2D.Double(x + 2.0, y + 5.0, size - 4.0, size - 10.0));
            } else {
                g2.fill(new Ellipse2D.Double(x + 3.0, y + 3.0, size - 6.0, size - 6.0));
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
