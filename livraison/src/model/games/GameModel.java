package mvc.model.game;


import mvc.model.strategies.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import mvc.model.shapes.GameShape;

public class GameModel extends Observable {
    private List<GameShape> redShapes;
    private List<GameShape> blueShapes;
    private ShapeGenerationStrategy generationStrategy;
    private GameState state;
    private int currentLevel;
    private int totalScore;
    private long redShapesVisibleUntil;
    
    public GameModel() {
        redShapes = new ArrayList<>();
        blueShapes = new ArrayList<>();
        state = GameState.WAITING_FOR_RED;
        currentLevel = 0;
        totalScore = 0;
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
        state = GameState.RED_VISIBLE;
  
        List<GameShape> newRedShapes = generationStrategy.generateShapes(count, panelWidth, panelHeight);
        
        for (GameShape shape : newRedShapes) {
            redShapes.add(shape);
        }

        nextLevel();
        setChanged();
        notifyObservers();
    }
    public boolean canPlaceBlueShape(GameShape shape) {
        for (GameShape red : redShapes) {
            if (shape.intersects(red)) {
                return false;
            }
        }
        for (GameShape blue : blueShapes) {
            if (shape.intersects(blue)) {
                return false;
            }
        }
        return true;
    }
    public void addBlueShape(GameShape shape) {
        if (blueShapes.size() < 4 && canPlaceBlueShape(shape)) {
            blueShapes.add(shape);
            
            if (blueShapes.size() == 4) {
                state = GameState.LEVEL_COMPLETE;
                int score = calculateScore();
                totalScore += score;
            }
            
            setChanged();
            notifyObservers();
        }
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
        setChanged();
        notifyObservers();
    }
    public void nextLevel() {
        currentLevel++;
    }
    
    public int getLevel(){ return currentLevel;}
    public long getRedVisibleTime(){ return redShapesVisibleUntil;}
    public List<GameShape> getRedShapes() { return redShapes; }
    public List<GameShape> getBlueShapes() { return blueShapes; }
    public void setGenerationStrategy(ShapeGenerationStrategy strategy) { 
        this.generationStrategy = strategy; 
    }
    public GameState getState() { return state; }
    public int getTotalScore() { return totalScore; }
    public boolean areRedShapesVisible() {
        return true;
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