package mvc.model.game;

public class LevelConfig {
    public final int redShapeCount;
    public final int timeSeconds;
    public final String difficulty;
    public final String description;

    public LevelConfig(int redShapeCount, int timeSeconds, String difficulty, String description) {
        this.redShapeCount = redShapeCount;
        this.timeSeconds = timeSeconds;
        this.difficulty = difficulty;
        this.description = description;
    }
}