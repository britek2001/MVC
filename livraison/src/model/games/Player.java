package mvc.model.game;

import mvc.model.shapes.GameShape;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int totalScore;
    private int levelsCompleted;
    private List<Integer> levelScores;
    private List<Double> areasPerLevel;
    private int totalBlueShapesPlaced;
    private long totalTimeSpent;
    private long levelStartTime;
    
    public Player(String name) {
        this.name = name;
        this.totalScore = 0;
        this.levelsCompleted = 0;
        this.levelScores = new ArrayList<>();
        this.areasPerLevel = new ArrayList<>();
        this.totalBlueShapesPlaced = 0;
        this.totalTimeSpent = 0;
        this.levelStartTime = 0;
    }
    

    public String getName() { return name; }
    public int getTotalScore() { return totalScore; }
    public int getLevelsCompleted() { return levelsCompleted; }
    public List<Integer> getLevelScores() { return new ArrayList<>(levelScores); }
    public List<Double> getAreasPerLevel() { return new ArrayList<>(areasPerLevel); }
    public int getTotalBlueShapesPlaced() { return totalBlueShapesPlaced; }
    public long getTotalTimeSpent() { return totalTimeSpent; }
    
    public double getAverageScorePerLevel() {
        if (levelsCompleted == 0) return 0;
        return (double) totalScore / levelsCompleted;
    }
    
    public double getAverageAreaPerShape() {
        if (totalBlueShapesPlaced == 0) return 0;
        double totalArea = 0;
        for (Double area : areasPerLevel) {
            totalArea += area;
        }
        return totalArea / totalBlueShapesPlaced;
    }
    
    public int getBestLevelScore() {
        if (levelScores.isEmpty()) return 0;
        int best = levelScores.get(0);
        for (int score : levelScores) {
            if (score > best) best = score;
        }
        return best;
    }
    
    public int getWorstLevelScore() {
        if (levelScores.isEmpty()) return 0;
        int worst = levelScores.get(0);
        for (int score : levelScores) {
            if (score < worst) worst = score;
        }
        return worst;
    }
    
    public void startLevel() {
        levelStartTime = System.currentTimeMillis();
    }
    
    public void completeLevel(int score, double totalArea) {
        totalScore += score;
        levelsCompleted++;
        levelScores.add(score);
        areasPerLevel.add(totalArea);
        totalBlueShapesPlaced += 4;
        
        if (levelStartTime > 0) {
            long levelTime = System.currentTimeMillis() - levelStartTime;
            totalTimeSpent += levelTime;
        }
    }
    
    public void reset() {
        totalScore = 0;
        levelsCompleted = 0;
        levelScores.clear();
        areasPerLevel.clear();
        totalBlueShapesPlaced = 0;
        totalTimeSpent = 0;
        levelStartTime = 0;
    }
    
    public String  getStatistics() {
        StringBuilder sb = new StringBuilder();
        sb.append(" Statistics  ").append(name).append(" ===\n");
        sb.append(" SCORE : ").append(totalScore).append("\n");
        sb.append(" LEVEL ").append(levelsCompleted).append("\n");
        sb.append(" LEVEL: ").append(String.format("%.2f", getAverageScorePerLevel())).append("\n");
        sb.append(" BEST LEVEL: ").append(getBestLevelScore()).append("\n");
        sb.append(" WORST LEVEL: ").append(getWorstLevelScore()).append("\n");
        sb.append(" TOTAL AREA : ").append(String.format("%.2f", getAverageAreaPerShape())).append("\n");
        sb.append(" TIME : ").append(formatTime(totalTimeSpent)).append("\n");
        return sb.toString();
    }
    
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d min %d s", minutes, seconds);
    }
    
    @Override
    public String toString() {
        return name + " - Puntuación: " + totalScore;
    }
}