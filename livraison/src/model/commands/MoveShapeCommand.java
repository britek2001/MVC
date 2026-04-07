package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class MoveShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 
    private double oldX, oldY;
    private double newX, newY;
    
    public MoveShapeCommand(GameModel model, GameShape shape, double newX, double newY){
        this.model = model;
        this.shape = shape;
        this.oldX = shape.getX();
        this.oldY = shape.getY();
        // newX and newY 
        this.newX = newX; 
        this.newY = newY; 
    }

    @Override
    public void execut(){
        if(model.validateMove(newX, newY)){
             shape.move(newX - oldX, newY - oldY);
             model.modelChanged("BLUE_SHAPE_MOVED");
        }
    }

    @Override
    public void undo(){
        if(model.validateMove(oldX, oldY)){
            shape.move(oldX - newX, oldY - newY);
            model.modelChanged("BLUE_SHAPE_MOVE_UNDO");
        }
    }

    @Override
    public void redo(){
        if(model.validateMove(newX, newY)){
            shape.move(newX - oldX, newY - oldY);
            model.modelChanged("BLUE_SHAPE_MOVE_REDO");
        }
    }

}