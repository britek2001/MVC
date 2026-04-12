package mvc.model.strategies;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;

public class AIPlayerStrategy implements ShapeGenerationStrategy {
    private static final int TOP_SAFE_MARGIN = 86;
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
            return new ArrayList<>();
        } else {
            List<GameShape> aiShapes = new ArrayList<>();
            int attempts = 0;
            while (aiShapes.size() < count && attempts < 1000) {
                GameShape candidate = randomShape(attempts, count, aiShapes.size());
                if (isValidPlacement(candidate, aiShapes)) {
                    aiShapes.add(candidate);
                }
                attempts++;
            }

            if (!aiShapes.isEmpty()) {
                aiRounds.add(new ArrayList<>(aiShapes));
            }
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

    private GameShape randomShape(int attempts, int targetCount, int placedCount) {
        int minY = Math.min(TOP_SAFE_MARGIN, Math.max(0, panelHeight - 1));
        int usableWidth = Math.max(1, panelWidth);
        int usableHeight = Math.max(1, panelHeight - minY);

        boolean tightSpace = usableWidth < 360 || usableHeight < 260 || random.nextDouble() < 0.35;
        double attemptPressure = Math.min(1.0, attempts / 1000.0);
        double progress = targetCount <= 0 ? 0.0 : (placedCount / (double) targetCount);
        boolean aggressive = attemptPressure > 0.35 || progress > 0.5;
        boolean ultraAggressive = attemptPressure > 0.7;

        int type = random.nextInt(2);

        if (type == 0) {
            int rectW;
            int rectH;
            if (ultraAggressive) {
                rectW = 14 + random.nextInt(13);
                rectH = 10 + random.nextInt(11);
            } else if (aggressive) {
                rectW = 18 + random.nextInt(20);
                rectH = 12 + random.nextInt(16);
            } else if (tightSpace) {
                rectW = 24 + random.nextInt(33);
                rectH = 18 + random.nextInt(25);
            } else {
                rectW = 44 + random.nextInt(33);
                rectH = 30 + random.nextInt(21);
            }

            int maxX = Math.max(1, panelWidth - rectW);
            int maxY = Math.max(minY + 1, panelHeight - rectH);
            int x = random.nextInt(maxX);
            int y = minY + random.nextInt(Math.max(1, maxY - minY));

            return new Rectangle(x, y, rectW, rectH, Color.BLUE);
        } else {
            int radius;
            if (ultraAggressive) {
                radius = 6 + random.nextInt(6);
            } else if (aggressive) {
                radius = 8 + random.nextInt(8);
            } else if (tightSpace) {
                radius = 10 + random.nextInt(11);
            } else {
                radius = 16 + random.nextInt(13);
            }
            int minX = radius;
            int maxX = Math.max(minX + 1, panelWidth - radius);
            int minCY = Math.max(minY + radius, radius);
            int maxCY = Math.max(minCY + 1, panelHeight - radius);

            int x = minX + random.nextInt(Math.max(1, maxX - minX));
            int y = minCY + random.nextInt(Math.max(1, maxCY - minCY));

            return new Circle(x, y, radius, Color.BLUE);
        }
    }

    private boolean isValidPlacement(GameShape candidate, List<GameShape> currentShapes) {
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
