package mvc.model.view;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import mvc.model.controller.ControleurSouris;
import mvc.model.controller.EtatCreationCercle;
import mvc.model.controller.EtatCreationRectangle;
import mvc.model.game.GameModel;
import mvc.model.shapes.Circle;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.view.theme.DarkThemeStrategy;
import mvc.model.view.theme.LightThemeStrategy;
import mvc.model.view.theme.StyledButtonFactory;
import mvc.model.view.theme.ThemeManager;
import mvc.model.view.theme.ThemeStrategy;
import mvc.model.view.theme.ThemeStrategyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewTest {

	@Test
	@DisplayName("gameCPanelButtonsAndCallbackss")
	void gameCPanelButtonsAndCallbacks() {
		GameModel model = new GameModel();
		AtomicInteger rect = new AtomicInteger();
		AtomicInteger circle = new AtomicInteger();
		AtomicInteger del = new AtomicInteger();
		AtomicInteger undo = new AtomicInteger();
		AtomicInteger redo = new AtomicInteger();
		AtomicInteger finish = new AtomicInteger();

		GameCPanel panel = new GameCPanel(
				model,
				rect::incrementAndGet,
				circle::incrementAndGet,
				del::incrementAndGet,
				undo::incrementAndGet,
				redo::incrementAndGet,
				finish::incrementAndGet);

		assertEquals(6, panel.getComponentCount());

		JButton rectangleButton = (JButton) panel.getComponent(0);
		JButton circleButton = (JButton) panel.getComponent(1);
		JButton deleteButton = (JButton) panel.getComponent(2);
		JButton undoButton = (JButton) panel.getComponent(3);
		JButton redoButton = (JButton) panel.getComponent(4);
		JButton finishButton = (JButton) panel.getComponent(5);

		assertNotNull(rectangleButton.getIcon());
		assertNotNull(circleButton.getIcon());
		rectangleButton.doClick();
		circleButton.doClick();
		deleteButton.doClick();
		undoButton.doClick();
		redoButton.doClick();
		finishButton.doClick();

		assertEquals(1, rect.get());
		assertEquals(1, circle.get());
		assertEquals(1, del.get());
		assertEquals(1, undo.get());
		assertEquals(1, redo.get());
		assertEquals(1, finish.get());
	}

	@Test
	@DisplayName("themeFactoryAndButtonFactory")
	void themeFactoryAndButtonFactory() {
		ThemeStrategy light = ThemeStrategyFactory.fromName("unknown");
		ThemeStrategy dark = ThemeStrategyFactory.fromName("Dark");

		assertEquals("Light", light.getName());
		assertEquals("Dark", dark.getName());

		ThemeManager.setCurrentTheme(new DarkThemeStrategy());
		assertEquals("Dark", ThemeManager.getCurrentTheme().getName());
		ThemeManager.setCurrentTheme(null);
		assertEquals("Dark", ThemeManager.getCurrentTheme().getName());

		AtomicInteger clicks = new AtomicInteger();
		StyledButtonFactory factory = new StyledButtonFactory(new LightThemeStrategy());
		JButton primary = factory.createPrimaryButton("Go", clicks::incrementAndGet);

		assertTrue(primary.isBorderPainted());
		primary.doClick();
		assertEquals(1, clicks.get());
	}

	@Test
	@DisplayName("mainMenuDifficultyMapping")
	void mainMenuDifficultyMapping() throws Exception {
		MainMenuView menu = new MainMenuView();
		Method map = MainMenuView.class.getDeclaredMethod("mapDifficultyToLevel", String.class);
		map.setAccessible(true);

		assertEquals(1, map.invoke(menu, "Facile"));
		assertEquals(2, map.invoke(menu, "Moyen"));
		assertEquals(3, map.invoke(menu, "Difficile"));
		assertEquals(4, map.invoke(menu, "Tres difficile"));
		assertEquals(5, map.invoke(menu, "Extreme"));
		assertEquals(1, map.invoke(menu, "autre"));
	}

	@Test
	@DisplayName("GameTutorialView: helpers privés")
	void gameTutorialPrivateHelpers() throws Exception {
		GameTutorialView view = new GameTutorialView();
		JTextPane textPane = new JTextPane() {
			@Override
			public java.awt.Dimension getPreferredSize() {
				return new java.awt.Dimension(560, 320);
			}
		};
		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setSize(600, 300);

		Method center = GameTutorialView.class.getDeclaredMethod("setCenteredSlideText", JTextPane.class, String.class);
		center.setAccessible(true);
		center.invoke(view, textPane, "hello");
		assertEquals("hello", textPane.getText());

		StyledDocument doc = textPane.getStyledDocument();
		int alignment = StyleConstants.getAlignment(doc.getParagraphElement(0).getAttributes());
		assertEquals(StyleConstants.ALIGN_CENTER, alignment);

		Method adjust = GameTutorialView.class.getDeclaredMethod("adjustTextHeight", JTextPane.class, JScrollPane.class);
		adjust.setAccessible(true);
		adjust.invoke(view, textPane, scrollPane);
		int h = scrollPane.getPreferredSize().height;
		assertTrue(h >= 260 && h <= 480);
	}

	@Test
	@DisplayName("gamePainterPaintNominal")
	void gamePainterPaintNominal() {
		GameModel model = new GameModel();
		model.setGameAreaSize(900, 700);
		model.addBlueShape(new Rectangle(100, 120, 80, 40, java.awt.Color.BLUE));
		model.getRedShapes().add(new Circle(250, 220, 25, java.awt.Color.RED));

		GamePainter painter = new GamePainter();
		BufferedImage image = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setClip(0, 0, 1200, 700);

		painter.paint(g2, model, null, new Circle(180, 180, 15, java.awt.Color.BLUE), null);
		g2.dispose();

		assertEquals(GamePainter.CONTROL_BAR_HEIGHT, model.getGameAreaTopInset());
		assertEquals(800, model.getGameWidth());
	}

	@Test
	@DisplayName("gamePainterDragPreviewBranchese")
	void gamePainterDragPreviewBranches() {
		GameModel model = new GameModel();
		model.setGameAreaSize(900, 700);
		BufferedImage image = new BufferedImage(1200, 700, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setClip(0, 0, 1200, 700);

		JPanel source = new JPanel();

		EtatCreationRectangle etatRect = new EtatCreationRectangle(model);
		ControleurSouris ctrlRect = new ControleurSouris(etatRect);
		etatRect.sourisAppuyee(new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 0, 0, 20, 20, 1, false), ctrlRect);
		etatRect.sourisDeplacee(new MouseEvent(source, MouseEvent.MOUSE_DRAGGED, 0, 0, 80, 70, 1, false), ctrlRect);

		new GamePainter().paint(g2, model, null, null, ctrlRect);

		EtatCreationCercle etatCircle = new EtatCreationCercle(model);
		ControleurSouris ctrlCircle = new ControleurSouris(etatCircle);
		etatCircle.sourisAppuyee(new MouseEvent(source, MouseEvent.MOUSE_PRESSED, 0, 0, 120, 120, 1, false), ctrlCircle);
		etatCircle.sourisDeplacee(new MouseEvent(source, MouseEvent.MOUSE_DRAGGED, 0, 0, 170, 155, 1, false), ctrlCircle);

		new GamePainter().paint(g2, model, null, null, ctrlCircle);
		g2.dispose();

		assertTrue(etatRect.isDragging());
		assertTrue(etatCircle.isDragging());
	}

	@Test
	@DisplayName("gameViewControlButtonsFlow")
	void gameViewControlButtonsFlow() {
		GameModel model = new GameModel();
		AtomicInteger onEnd = new AtomicInteger();
		GameView view = new GameView(model, null, onEnd::incrementAndGet, null);

		view.createCircle(new Circle(120, 120, 20, java.awt.Color.BLUE));
		assertEquals(1, model.getBlueShapes().size());

		JPanel controls = (JPanel) view.getComponent(0);
		JButton deleteButton = (JButton) controls.getComponent(2);
		JButton undoButton = (JButton) controls.getComponent(3);
		JButton redoButton = (JButton) controls.getComponent(4);
		JButton finishButton = (JButton) controls.getComponent(5);

		deleteButton.doClick();
		assertEquals(0, model.getBlueShapes().size());

		undoButton.doClick();
		assertEquals(1, model.getBlueShapes().size());

		redoButton.doClick();
		assertEquals(0, model.getBlueShapes().size());

		finishButton.doClick();
		assertTrue(model.isGameFinished());

		view.removeNotify();
	}

	@Test
	@DisplayName("gameViewPrivateHelpers")
	void gameViewPrivateHelpers() throws Exception {
		GameModel model = new GameModel();
		GameView view = new GameView(model, null, () -> { }, null);
		Circle c1 = new Circle(150, 150, 25, java.awt.Color.BLUE);
		Rectangle r1 = new Rectangle(200, 200, 60, 30, java.awt.Color.BLUE);
		view.createCircle(c1);
		view.createRectangle(r1);

		Method findShapeAt = GameView.class.getDeclaredMethod("findShapeAt", double.class, double.class);
		findShapeAt.setAccessible(true);
		GameShape found = (GameShape) findShapeAt.invoke(view, 205.0, 205.0);
		assertNotNull(found);

		Field selected = GameView.class.getDeclaredField("selectedShape");
		selected.setAccessible(true);
		selected.set(view, null);

		Method getTargetShape = GameView.class.getDeclaredMethod("getTargetShape");
		getTargetShape.setAccessible(true);
		GameShape target = (GameShape) getTargetShape.invoke(view);
		assertNotNull(target);

		view.removeNotify();
	}
}
