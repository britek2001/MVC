package mvc.model.strategies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;

public class AIPlayerStrategy implements ShapeGenerationStrategy {
    private final List<List<GameShape>> humanRounds = new ArrayList<>();
    private final List<List<GameShape>> aiRounds = new ArrayList<>();
    private int currentRound = 0;
    private boolean isHumanTurn = true;
    private int panelWidth, panelHeight;
    private final Random random = new Random();

    @Override
    public List<GameShape> generateShapes(int count, int panelWidth, int panelHeight) {
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        if (isHumanTurn) {
            // Human's turn: wait for user input, return empty list
            return new ArrayList<>();
        } else {
            // AI's turn: generate 4 valid shapes
            List<GameShape> aiShapes = new ArrayList<>();
            int attempts = 0;
            while (aiShapes.size() < count && attempts < 1000) {
                GameShape candidate = randomShape();
                if (isValidPlacement(candidate, aiShapes)) {
                    aiShapes.add(candidate);
                }
                attempts++;
            }
            aiRounds.add(new ArrayList<>(aiShapes));
            return aiShapes;
        }
    }

    public void submitHumanShapes(List<GameShape> shapes) {
        humanRounds.add(new ArrayList<>(shapes));
        isHumanTurn = false;
    }

    public boolean isHumanTurn() {
        return isHumanTurn;
    }

    public void switchToAITurn() {
        isHumanTurn = false;
    }

    public void switchToHumanTurn() {
        isHumanTurn = true;
    }

    public int getCurrentRound() {
        return currentRound + 1;
    }

    public boolean isGameComplete() {
        return currentRound >= 4; // 4 rounds per player
    }

    public double getHumanScore() {
        return calculateTotalScore(humanRounds);
    }

    public double getAIScore() {
        return calculateTotalScore(aiRounds);
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

    private GameShape randomShape() {
        int type = random.nextInt(2);
        int x = 50 + random.nextInt(Math.max(1, panelWidth - 150));
        int y = 50 + random.nextInt(Math.max(1, panelHeight - 150));
        if (type == 0) {
            return new Rectangle(x, y, 60, 40, Color.BLUE);
        } else {
            return new Circle(x, y, 25, Color.BLUE);
        }
    }

    private boolean isValidPlacement(GameShape candidate, List<GameShape> currentShapes) {
        for (List<GameShape> round : aiRounds) {
            for (GameShape shape : round) {
                if (candidate.intersects(shape)) {
                    return false;
                }
            }
        }
        for (GameShape shape : currentShapes) {
            if (candidate.intersects(shape)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getStrategyName() {
        return "AI Player";
    }
}
