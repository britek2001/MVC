package mvc.model.strategies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import mvc.model.shapes.GameShape;

public class TwoPlayerStrategy implements ShapeGenerationStrategy {
    
    private List<List<GameShape>> player1Shapes;
    private List<List<GameShape>> player2Shapes;
    private int currentRound;
    private int currentPlayer; 
    private boolean isPlayer1TurnToDraw;
    
    public TwoPlayerStrategy() {
        player1Shapes = new ArrayList<>();
        player2Shapes = new ArrayList<>();
        currentRound = 0;
        currentPlayer = 0;
        isPlayer1TurnToDraw = true;
    }
    
    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        if (isPlayer1TurnToDraw) {
            return new ArrayList<>();
        } else {
            if (currentRound < player1Shapes.size()) {
                return cloneShapes(player1Shapes.get(currentRound));
            }
            return new ArrayList<>();
        }
    }
    
    public void submitPlayerShapes(List<GameShape> shapes) {
        if (isPlayer1TurnToDraw) {
            player1Shapes.add(shapes);
            isPlayer1TurnToDraw = false;
        } else {
            player2Shapes.add(shapes);
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
            clones.add(shape.clone());
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
        if (isPlayer1TurnToDraw) {
            return "Joueur 1 ";
        } else {
            return "Joueur 2 ";
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Deux Joueurs " + getCurrentRound();
    }
}