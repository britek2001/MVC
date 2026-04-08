package mvc.model.game;

import java.util.Timer;
import java.util.TimerTask;
import mvc.model.strategies.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import mvc.model.shapes.GameShape;
import java.util.Observable; 
import mvc.model.game.GameState;
import mvc.model.view.GameView;
import mvc.model.game.LevelConfig;
import java.util.Map;

public class GameModel extends Observable {
    private boolean redShapesVisible = true;
    private final Timer timer = new Timer(true); 
    private List<GameShape> redShapes;
    private List<GameShape> blueShapes;
    private ShapeGenerationStrategy generationStrategy;
    private GameState state;
    private int currentLevel;
    private int totalScore;
    private int width;
    private int height;
    
    private static final int MAX_LEVEL = 5;
    private long redShapesVisibleUntil;
    private static final LevelConfig DEFAULT_LEVEL_CONFIG = new  LevelConfig(2, 10, "Facile", "2 petits rectangles");
    public GameModel() {
        redShapes = new ArrayList<>();
        blueShapes = new ArrayList<>();
        state = GameState.WAITING_FOR_RED;
        currentLevel = 0;
        totalScore = 0;
        width = 900;
        height = 900;
    }

    public boolean areRedShapesVisible() {
        return redShapesVisible;
    }

    public void hideRedShapes() {
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
            3, new LevelConfig(4, 8, "Difficile", "4 formes mélangées"),
            4, new LevelConfig(5, 8, "Très difficile", "5 polygones complexes"),
            5, new LevelConfig(6, 6, "Extrême", "6 formes qui se chevauchent")
    );

    private LevelConfig getLevelConfig(int level) {
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
        if(panelWidth < this.width  || panelHeight < this.height){
            System.out.println("Panel must be at least this size: " + this.width + "x" + this.height);
            return;
        }
        
        int levelToGenerate = Math.min(currentLevel + 1, MAX_LEVEL);
        LevelConfig cfg = getLevelConfig(levelToGenerate);
        count = Math.min(count, cfg.redShapeCount); // Ensure count does not exceed the configured limit
        
        redShapes.clear();
        showRedShapes();
        redShapesVisibleUntil = System.currentTimeMillis() + (cfg.timeSeconds * 1000L);
        modelChanged("RED_SHAPES_GENERATED");

        List<GameShape> newRedShapes = generationStrategy.generateShapes(count, panelWidth, panelHeight);
        redShapes.addAll(newRedShapes);
        modelChanged("RED_SHAPES_GENERATED");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                hideRedShapes(); // contient déjà modelChanged(...)
            }
        }, cfg.timeSeconds * 1000L);
        nextLevel();
    }

    public boolean canPlaceBlueShape(GameShape shape) {
        for (GameShape red : redShapes) {
            if (shape.intersects(red)) {
                System.out.println("Cannot place blue shape, it intersects with a red shape.");
                return false;
            }
        }
        for (GameShape blue : blueShapes) {
            if (shape.intersects(blue)) {
                System.out.println("Cannot place blue shape, it intersects with another blue shape.");
                return false;
            }
        }
        return true;
    }

    public boolean addBlueShape(GameShape shape) {
        if ( canPlaceBlueShape(shape)) {
            blueShapes.add(shape);
            System.out.println("Blue shape added. Total blue shapes: " + blueShapes.size());
            if (blueShapes.size() == 40) {
                System.out.println("Maximum number of blue shapes reached. Ending game.");
                System.out.println("Level " + currentLevel + " complete! Score: " + calculateScore());
                state = GameState.LEVEL_COMPLETE;
                int score = calculateScore();
                totalScore += score;
            }
            System.out.println("Current total score: " + totalScore);
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
        blueShapes.remove(shape);
        modelChanged("BLUE_SHAPE_REMOVED");
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
    

    public int getLevel(){ return currentLevel;}
    public long getRedVisibleTime(){ return redShapesVisibleUntil;}
    public List<GameShape> getRedShapes() { return redShapes; }
    public List<GameShape> getBlueShapes() { return blueShapes; }
    public ShapeGenerationStrategy getGenerationStrategy() { return generationStrategy; }
    public void setGenerationStrategy(ShapeGenerationStrategy strategy) { 
        this.generationStrategy = strategy; 
    }
    public GameState getState() { return state; }
    public int getTotalScore() { return totalScore; }


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
        System.out.println();
    }
}