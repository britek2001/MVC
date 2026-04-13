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

    private final GameModel model;
    private final ThemeStrategy theme;
    private final StyledButtonFactory buttonFactory;
    private static final int BUTTON_SIZE = 56;
    private static final int PANEL_HEIGHT = 72;

    public GameCPanel(
            GameModel model,
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 8));
        this.model = model;
        this.theme = ThemeManager.getCurrentTheme();
        this.buttonFactory = new StyledButtonFactory(theme);
        setBackground(theme.getInfoPanelBackgroundColor());
        setPreferredSize(new Dimension(0, PANEL_HEIGHT));
        initialize(onCreateRectangle, onCreateCircle, onDeleteSelected, onUndo, onRedo, onEndGame);
    }

    private void initialize(
            Runnable onCreateRectangle,
            Runnable onCreateCircle,
            Runnable onDeleteSelected,
            Runnable onUndo,
            Runnable onRedo,
            Runnable onEndGame) {

        JButton createRectangleButton = createShapeButton(ShapeType.RECTANGLE, onCreateRectangle, true);
        JButton createCircleButton = createShapeButton(ShapeType.CIRCLE, onCreateCircle, true);
        JButton eliminateSelectedButton = createSquareButton("X", onDeleteSelected, false);
        JButton undoButton = createSquareButton("<-", onUndo, false);
        JButton redoButton = createSquareButton("->", onRedo, false);
        JButton endGameButton = createSquareButton(" Finish ", onEndGame, true);

        add(createRectangleButton);
        add(createCircleButton);
        add(eliminateSelectedButton);
        add(undoButton);
        add(redoButton);
        add(endGameButton);
    }

    private JButton createSquareButton(String icon, Runnable onClick, boolean primary) {
        JButton button = primary 
            ? buttonFactory.createPrimaryButton(icon, onClick)
            : buttonFactory.createSecondaryButton(icon, onClick);
        button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMinimumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        button.setMaximumSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
        return button;
    }

    private JButton createShapeButton(ShapeType shapeType, Runnable onClick, boolean primary) {
        JButton button = primary
            ? buttonFactory.createPrimaryButton("", onClick)
            : buttonFactory.createSecondaryButton("", onClick);
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
            g2.setColor(java.awt.Color.WHITE);

            if (type == ShapeType.RECTANGLE) {
                g2.fill(new Rectangle2D.Double(x + 2, y + 5, size - 4, size - 10));
            } else {
                g2.fill(new Ellipse2D.Double(x + 3, y + 3, size - 6, size - 6));
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
