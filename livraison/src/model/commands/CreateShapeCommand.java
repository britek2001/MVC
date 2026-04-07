package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class CreateShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 

    public CreateShapeCommand(GameModel model, GameShape shape){
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void execut(){
        model.addBlueShape(shape);
        index = model.getBlueShapes().indexOf(shape);;
    }
    
    @Override
    public void undo(){
        model.removeBlueShape(shape);
    }

    @Override
    public void redo(){
        model.getBlueShapes().add(index, shape);
    }

}