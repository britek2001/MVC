package mvc.model.game;

public enum GameState {
    WAITING_FOR_RED,
    RED_VISIBLE,
    RED_NOT_VISIBLE,    
    PLACING_BLUE,
    LEVEL_COMPLETE,
    GAME_OVER, 
    MOVING_SHAPE,
    RESIZING_SHAPE,
    MOVE_INVALID_INTERSECTION
}