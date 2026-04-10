package mvc.model.game;

import java.util.Timer;
import java.util.TimerTask;
import mvc.model.strategies.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import java.util.Observable; 
import mvc.model.game.GameState;
import mvc.model.view.GameView;
import mvc.model.game.LevelConfig;
import java.util.Map;


public class GameModel extends Observable {

    private static final int BLUE_SHAPES_PER_LEVEL = 4;
    private static final long TOTAL_GAME_DURATION_MILLIS = 60_000L;
    private boolean redShapesVisible = true;
    private final Timer timer = new Timer(true); 
    private List<GameShape> redShapes;
    private List<GameShape> blueShapes;
    private int blueShapesPlacedThisLevel;
    private ShapeGenerationStrategy generationStrategy;
    private GameState state;
    private int currentLevel;
    private int totalScore;
    private int width;
    private int height;
    private boolean twoPlayerMode;
    private String redPlayerName;
    private String bluePlayerName;
    private boolean redPlayerTurn;
    private int turnsPlayed;
    private int redPlayerScore;
    private int bluePlayerScore;
    private boolean gameFinished;
    private boolean gameWon;
    private boolean magistralWin;
    private int finalCoveredArea;
    private long currentRedTimeLimitMillis;
    private long gameEndsAtMillis;
    private long currentGameTimeLimitMillis;
    private boolean globalTimerStarted;
    
    private static final int MAX_LEVEL = 5;
    private static final int MAX_TURNS_PER_PLAYER = 4;
    private long redShapesVisibleUntil;
    private static final LevelConfig DEFAULT_LEVEL_CONFIG = new  LevelConfig(2, 10, "Facile", "2 petits rectangles");
    public GameModel() {
        redShapes = new ArrayList<>();
        blueShapes = new ArrayList<>();
        state = GameState.WAITING_FOR_RED;
        currentLevel = 0;
        totalScore = 0;
        width = 800;
        height = 600;
        blueShapesPlacedThisLevel = 0;
        twoPlayerMode = false;
        redPlayerName = "Joueur Rouge";
        bluePlayerName = "Joueur Bleu";
        redPlayerTurn = true;
        turnsPlayed = 0;
        redPlayerScore = 0;
        bluePlayerScore = 0;
        gameFinished = false;
        gameWon = false;
        magistralWin = false;
        finalCoveredArea = 0;
        currentRedTimeLimitMillis = 0;
        gameEndsAtMillis = 0;
        currentGameTimeLimitMillis = TOTAL_GAME_DURATION_MILLIS;
        globalTimerStarted = false;
    }

    public boolean areRedShapesVisible() {
        return redShapesVisible;
    }

    public void hideRedShapes() {
        if (gameFinished) {
            return;
        }

        if (System.currentTimeMillis() < redShapesVisibleUntil) {
            return;
        }

        redShapesVisible = false;
        state = GameState.PLACING_BLUE;
        modelChanged("RED_NOT_VISIBLE"); 
    }

    public void showRedShapes() {
        redShapesVisible = true;
        state = GameState.RED_VISIBLE;
        modelChanged("RED_VISIBLE");
    }

    private static final Map<Integer, LevelConfig> LEVEL_CONFIGS = Map.of(
            1, new LevelConfig(2, 10, "Facile", "2 petits rectangles"),
            2, new LevelConfig(3, 10, "Moyen", "3 cercles moyens"),
            3, new LevelConfig(4, 8, "Difficile", "4 formes melangées"),
            4, new LevelConfig(5, 8, "Tres difficile", "5 polygones complexes"),
            5, new LevelConfig(6, 6, "Extreme", "6 formes qui se chevauchent")
    );

    public LevelConfig getLevelConfig(int level) {
        return LEVEL_CONFIGS.getOrDefault(level, DEFAULT_LEVEL_CONFIG);
    }


    public boolean validateMove(double newX, double newY) {
        for (GameShape red : redShapes) {
            if (red.contains(new Point((int)newX, (int)newY))) {
                return false;
            }
        }
        for (GameShape blue : blueShapes) {
            if (blue.contains(new Point((int)newX, (int)newY))) {
                return false;
            }
        }
        return true;
    }

    public void generateRedShapes(int count, int panelWidth, int panelHeight) {
        if (panelWidth < this.width || panelHeight < this.height) {
            System.out.println("Panel size petit : " + this.width + "x" + this.height);
            return;
        }

        if (generationStrategy == null) {
            System.out.println("Pas de strategy !");
            return;
        }

        int levelToGenerate = Math.min(currentLevel + 1, MAX_LEVEL);
        LevelConfig cfg = getLevelConfig(levelToGenerate);
        count = Math.min(count, cfg.redShapeCount);
        startGlobalGameTimerIfNeeded();

        System.out.println("Level: " + levelToGenerate + ", Red shapes pour generer: " + count);

        if (!twoPlayerMode) {
            redShapes.clear();
            blueShapes.clear();
        }
        blueShapesPlacedThisLevel = 0;
        showRedShapes();
        redShapesVisibleUntil = System.currentTimeMillis() + (cfg.timeSeconds * 1000L);
        currentRedTimeLimitMillis = cfg.timeSeconds * 1000L;

        List<GameShape> newRedShapes = generateValidRedShapes(count, panelWidth, panelHeight);
        if (newRedShapes == null || newRedShapes.isEmpty()) {
            System.out.println("ERROR: No shapes generated!");
            return;
        }

        redShapes.addAll(newRedShapes);
        System.out.println("Generer " + redShapes.size() + " red shapes");
        modelChanged("RED_SHAPES_GENERATED");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hideRedShapes();
            }
        }, cfg.timeSeconds * 1000L);

        nextLevel();
    }

    public boolean canPlaceBlueShape(GameShape shape) {
        if (!isShapeWithinGameArea(shape)) {
            System.out.println("Pas possible out of arrea ");
            return false;
        }

        for (GameShape red : redShapes) {
            if (shape.intersects(red)) {
                System.out.println("Pas possible intercection de figure de couleur diferente");
                return false;
            }
        }

        if (twoPlayerMode) {
            for (GameShape existing : blueShapes) {
                if (shape.intersects(existing)) {
                    System.out.println("Pas possible place shape interseccion not allowed ");
                    return false;
                }
            }
            return true;
        }
        for (GameShape blue : blueShapes) {
            if (shape.intersects(blue)) {
                System.out.println("Pas possible place shape intersection avec un blueshape");
                return false;
            }
        }
        return true;
    }

    public boolean addBlueShape(GameShape shape) {
        if (gameFinished) {
            return false;
        }

        if (twoPlayerMode) {
            shape.setColor(getCurrentDrawingColor());
        }
        
        if (getBlueShapesPlacedThisLevel() >= BLUE_SHAPES_PER_LEVEL) {
            modelChanged("BLUE_LIMIT_REACHED");
            return false;
        }

        if ( canPlaceBlueShape(shape)) {
            shape.setColor(getCurrentDrawingColor());
            blueShapes.add(shape);
            blueShapesPlacedThisLevel++;
            System.out.println("Numero de Blue Shapes: " + getBlueShapesPlacedThisLevel() + "/" + BLUE_SHAPES_PER_LEVEL);
            if (getBlueShapesPlacedThisLevel() == BLUE_SHAPES_PER_LEVEL) {
                System.out.println("Level " + currentLevel + " complet! Score: " + calculateScore());
                if (twoPlayerMode) {
                    state = GameState.LEVEL_COMPLETE;
                    int score = calculateScore();
                    totalScore += score;
                    if (redPlayerTurn) {
                        redPlayerScore += score;
                    } else {
                        bluePlayerScore += score;
                    }
                    completeTwoPlayerTurn();
                    modelChanged("LEVEL_COMPLETE");
                } else {
                    if (!globalTimerStarted || generationStrategy == null) {
                        finishGame(true);
                    } else {
                        int nextLevel = Math.min(currentLevel + 1, MAX_LEVEL);
                        LevelConfig cfg = getLevelConfig(nextLevel);
                        generateRedShapes(cfg.redShapeCount, width, height);
                    }
                }
            }
            System.out.println("Score Actuel: " + totalScore);
            modelChanged("BLUE_SHAPE_ADDED");
            return true;
        }
        return false;
    }

    public int calculateScore() {
        double totalArea = 0;
        for (GameShape shape : blueShapes) {
            totalArea += shape.getArea();
        }
        return (int)totalArea;
    }

    public void removeBlueShape(GameShape shape) {
        if (blueShapes.remove(shape)) {
            if (blueShapesPlacedThisLevel > 0) {
                blueShapesPlacedThisLevel--;
            }
            modelChanged("BLUE_SHAPE_REMOVED");
        }
    }

    public void restoreBlueShape(GameShape shape, int index) {
        int safeIndex = Math.max(0, Math.min(index, blueShapes.size()));
        shape.setColor(getCurrentDrawingColor());
        blueShapes.add(safeIndex, shape);
        blueShapesPlacedThisLevel++;
        modelChanged("BLUE_SHAPE_RESTORED");
    }

    public GameShape findBlueShapeAt(int x, int y) {
        Point point = new Point(x, y);
        for (int i = blueShapes.size() - 1; i >= 0; i--) {
            GameShape shape = blueShapes.get(i);
            if (shape.contains(point)) {
                return shape;
            }
        }
        return null;
    }

    public void nextLevel() {
        currentLevel++;
    }

    public void enableTwoPlayerMode(String redName, String blueName) {
        twoPlayerMode = true;
        redPlayerName = (redName == null || redName.trim().isEmpty()) ? "Joueur Rouge" : redName.trim();
        bluePlayerName = (blueName == null || blueName.trim().isEmpty()) ? "Joueur Bleu" : blueName.trim();
        redPlayerTurn = true;
        turnsPlayed = 0;
        redPlayerScore = 0;
        bluePlayerScore = 0;
        redShapes.clear();
        blueShapes.clear();
        blueShapesPlacedThisLevel = 0;
        gameFinished = false;
        gameWon = false;
        magistralWin = false;
        finalCoveredArea = 0;
        gameEndsAtMillis = 0;
        currentGameTimeLimitMillis = TOTAL_GAME_DURATION_MILLIS;
        globalTimerStarted = false;
        modelChanged("TWO_PLAYER_ENABLED");
    }

    public void disableTwoPlayerMode() {
        twoPlayerMode = false;
        gameFinished = false;
        gameWon = false;
        magistralWin = false;
        finalCoveredArea = 0;
        gameEndsAtMillis = 0;
        currentGameTimeLimitMillis = TOTAL_GAME_DURATION_MILLIS;
        globalTimerStarted = false;
        modelChanged("TWO_PLAYER_DISABLED");
    }

    private void finishGame(boolean won) {
        gameFinished = true;
        gameWon = won;
        magistralWin = won && !twoPlayerMode && currentLevel >= MAX_LEVEL;
        finalCoveredArea = calculateScore();
        totalScore = finalCoveredArea;
        state = GameState.GAME_OVER;
        modelChanged("GAME_OVER");
    }

    public boolean canStartNextLevel() {
        return !twoPlayerMode && gameFinished && gameWon && currentLevel < MAX_LEVEL;
    }

    public boolean startNextLevel() {
        if (!canStartNextLevel()) {
            return false;
        }

        gameFinished = false;
        gameWon = false;
        magistralWin = false;
        finalCoveredArea = 0;

        int nextLevel = Math.min(currentLevel + 1, MAX_LEVEL);
        LevelConfig cfg = getLevelConfig(nextLevel);
        generateRedShapes(cfg.redShapeCount, width, height);
        return true;
    }

    private void completeTwoPlayerTurn() {
        turnsPlayed++;
        if (turnsPlayed >= MAX_TURNS_PER_PLAYER * 2) {
            setState(GameState.GAME_OVER);
            System.out.println("Two player TERMINER");
            return;
        }

        redPlayerTurn = !redPlayerTurn;
        int nextLevel = Math.min(currentLevel + 1, MAX_LEVEL);
        LevelConfig cfg = getLevelConfig(nextLevel);
        generateRedShapes(cfg.redShapeCount, width, height);
    }
    

    public int getLevel(){ return currentLevel;}
    public void setCurrentLevel(int level) {
        currentLevel = Math.max(0, Math.min(level, MAX_LEVEL - 1));
        modelChanged("LEVEL_CHANGED");
    }
    public long getRedVisibleTime(){ return redShapesVisibleUntil;}
    public int getBlueShapesPerLevel() { return BLUE_SHAPES_PER_LEVEL; }
    public int getBlueShapesPlacedThisLevel() { return blueShapesPlacedThisLevel; }
    public int getBlueShapesRemainingForLevel() { return Math.max(0, BLUE_SHAPES_PER_LEVEL - blueShapesPlacedThisLevel); }
    public List<GameShape> getRedShapes() { return redShapes; }
    public List<GameShape> getBlueShapes() { return blueShapes; }
    public ShapeGenerationStrategy getGenerationStrategy() { return generationStrategy; }
    public void setGenerationStrategy(ShapeGenerationStrategy strategy) { 
        this.generationStrategy = strategy; 
    }
    public GameState getState() { return state; }
    public void setState(GameState state) {
        this.state = state;
        modelChanged("STATE_CHANGED");
    }
    public void setGameState(GameState newState) {
        this.state = newState;
        modelChanged("GAME_STATE_CHANGED");
    }
    public int getTotalScore() { return totalScore; }
    public boolean isTwoPlayerMode() { return twoPlayerMode; }
    public String getRedPlayerName() { return redPlayerName; }
    public String getBluePlayerName() { return bluePlayerName; }
    public boolean isRedPlayerTurn() { return redPlayerTurn; }
    public String getCurrentPlayerName() { return redPlayerTurn ? redPlayerName : bluePlayerName; }
    public String getCurrentPlayerColorLabel() { return redPlayerTurn ? "ROUGE" : "BLEU"; }
    public int getTurnsPlayed() { return turnsPlayed; }
    public int getMaxTurnsPerPlayer() { return MAX_TURNS_PER_PLAYER; }
    public int getRedPlayerScore() { return redPlayerScore; }
    public int getBluePlayerScore() { return bluePlayerScore; }
    public boolean isGameFinished() { return gameFinished; }
    public boolean isGameWon() { return gameWon; }
    public boolean isMagistralWin() { return magistralWin; }
    public int getFinalCoveredArea() { return finalCoveredArea; }
    public int getGameWidth() { return width; }
    public int getGameHeight() { return height; }
    public void setGameAreaSize(int newWidth, int newHeight) {
        width = Math.max(1, newWidth);
        height = Math.max(1, newHeight);
    }

    public boolean isPointInsideGameArea(double x, double y) {
        return x >= 0 && y >= 0 && x <= width && y <= height;
    }

    public boolean isShapeWithinGameArea(GameShape shape) {
        if (shape instanceof Rectangle) {
            Rectangle r = (Rectangle) shape;
            if (r.width <= 0 || r.height <= 0) {
                return false;
            }
            return r.x >= 0 && r.y >= 0 && (r.x + r.width) <= width && (r.y + r.height) <= height;
        }

        if (shape instanceof Circle) {
            Circle c = (Circle) shape;
            double radius = c.getRadius();
            if (radius <= 0) {
                return false;
            }
            return (c.getX() - radius) >= 0
                    && (c.getY() - radius) >= 0
                    && (c.getX() + radius) <= width
                    && (c.getY() + radius) <= height;
        }

        return isPointInsideGameArea(shape.getX(), shape.getY());
    }
    public Color getCurrentDrawingColor() {
        if (twoPlayerMode) {
            return redPlayerTurn ? Color.RED : Color.BLUE;
        }
        return Color.BLUE;
    }
    public long getRemainingRedTime() {
        long remainingTime = redShapesVisibleUntil - System.currentTimeMillis();
        return Math.max(remainingTime, 0);
    }

    public long getCurrentRedTimeLimitMillis() {
        return currentRedTimeLimitMillis;
    }

    public long getRemainingGameTime() {
        if (!globalTimerStarted || gameEndsAtMillis <= 0) {
            return currentGameTimeLimitMillis;
        }
        long remainingTime = gameEndsAtMillis - System.currentTimeMillis();
        return Math.max(remainingTime, 0);
    }

    public long getCurrentGameTimeLimitMillis() {
        return currentGameTimeLimitMillis;
    }

    private void startGlobalGameTimerIfNeeded() {
        if (globalTimerStarted || gameFinished) {
            return;
        }

        globalTimerStarted = true;
        currentGameTimeLimitMillis = TOTAL_GAME_DURATION_MILLIS;
        gameEndsAtMillis = System.currentTimeMillis() + TOTAL_GAME_DURATION_MILLIS;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!gameFinished) {
                    finishGame(false);
                }
            }
        }, TOTAL_GAME_DURATION_MILLIS);
    }

    private List<GameShape> generateValidRedShapes(int count, int panelWidth, int panelHeight) {
        List<GameShape> validShapes = new ArrayList<>();
        int attempts = 0;
        final int maxAttempts = 40;

        while (validShapes.size() < count && attempts < maxAttempts) {
            List<GameShape> candidates = generationStrategy.generateShapes(count, panelWidth, panelHeight);
            if (candidates == null || candidates.isEmpty()) {
                attempts++;
                continue;
            }

            for (GameShape candidate : candidates) {
                if (!isShapeWithinGameArea(candidate)) {
                    continue;
                }

                boolean intersectsBlue = blueShapes.stream().anyMatch(blue -> blue.intersects(candidate));
                if (intersectsBlue) {
                    continue;
                }

                boolean intersectsRedCandidate = validShapes.stream().anyMatch(red -> red.intersects(candidate));
                if (intersectsRedCandidate) {
                    continue;
                }

                validShapes.add(candidate);
                if (validShapes.size() >= count) {
                    break;
                }
            }

            attempts++;
        }

        if (validShapes.size() < count) {
            System.out.println("ATTENTION : " + validShapes.size() + "/" + count + " formes rouges générées.");
        }

        return validShapes;
    }

    public void modelChanged(String event) {
        setChanged();
        notifyObservers(event);
    }
    
    public void getStatistics () {
        System.out.printf("========== GAME STATISTICS  %s ==========%n", currentLevel);
        System.out.println("Total Score: " + totalScore);
        System.out.println("Game State: " + state);
        System.out.println("Red Shapes Visible: " + areRedShapesVisible());
        System.out.println("Red Visible Time: " + redShapesVisibleUntil);
        System.out.println("Reds: " + getRedShapes().size());
        System.out.println("Blues: " + getBlueShapes().size());
        System.out.println("Blue placed this level: " + getBlueShapesPlacedThisLevel() + "/" + getBlueShapesPerLevel());
        System.out.println();
    }
}