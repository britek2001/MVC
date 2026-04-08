package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class MoveShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private double oldX, oldY;
    private double newX, newY;
    
    public MoveShapeCommand(GameModel model, GameShape shape, double oldX, double oldY, double newX, double newY){
        this.model = model;
        this.shape = shape;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX; 
        this.newY = newY; 
    }

    @Override
    public void execut(){
         shape.move(newX - oldX, newY - oldY);
         model.modelChanged("BLUE_SHAPE_MOVED");
    }

    @Override
    public void undo(){
        shape.move(oldX - newX, oldY - newY);
        model.modelChanged("BLUE_SHAPE_MOVE_UNDO");
    }

    @Override
    public void redo(){
        shape.move(newX - oldX, newY - oldY);
        model.modelChanged("BLUE_SHAPE_MOVE_REDO");
    }
    

}