package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class DeleteShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 

    private DeleteShapeCommand(GameModel model, GameShape shape){
        this.model = model;
        this.shape = shape;
    }

    @Override
    public void execut(){
        index = model.getBlueShapes().indexOf(shape);
        if(index != -1){
            this.model.removeBlueShape(shape);
        }
    }
    
    @Override
    public void undo(){
        model.getBlueShapes().add(index, shape);
    }

    @Override
    public void redo(){
    }

}