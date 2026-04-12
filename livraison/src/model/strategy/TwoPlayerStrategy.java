package mvc.model.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mvc.model.shapes.GameShape;

public class TwoPlayerStrategy implements ShapeGenerationStrategy {
    
    private List<List<GameShape>> player1Shapes;
    private List<List<GameShape>> player2Shapes;
    private int currentRound;
    private boolean isPlayer1TurnToDraw;
    
    public TwoPlayerStrategy() {
        player1Shapes = new ArrayList<>();
        player2Shapes = new ArrayList<>();
        currentRound = 0;
        isPlayer1TurnToDraw = true;
    }
    
    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        return Collections.emptyList();
    }
    
    public void submitPlayerShapes(List<GameShape> shapes) {
        List<GameShape> safeShapes = cloneShapes(shapes);
        if (isPlayer1TurnToDraw) {
            player1Shapes.add(safeShapes);
            isPlayer1TurnToDraw = false;
        } else {
            player2Shapes.add(safeShapes);
            isPlayer1TurnToDraw = true;
            currentRound++;
        }
    }
    
    public double calculatePlayer1Score() {
        return calculateTotalScore(player1Shapes);
    }
    
    public double calculatePlayer2Score() {
        return calculateTotalScore(player2Shapes);
    }
    
    private double calculateTotalScore(List<List<GameShape>> allRounds) {
        double total = 0;
        for (List<GameShape> round : allRounds) {
            for (GameShape shape : round) {
                total += shape.getArea();
            }
        }
        return total;
    }
    
    private List<GameShape> cloneShapes(List<GameShape> originals) {
        List<GameShape> clones = new ArrayList<>();
        for (GameShape shape : originals) {
            clones.add(shape.copy());
        }
        return clones;
    }
    
    public boolean isGameComplete() {
        return currentRound >= 10;
    }
    
    public int getCurrentRound() {
        return currentRound + 1;
    }
    
    public String getCurrentPlayerMessage() {
        return isPlayer1TurnToDraw ? "Joueur 1" : "Joueur 2";
    }
    
    @Override
    public String getStrategyName() {
        return "Deux Joueurs " + getCurrentRound();
    }
}