package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class CreateShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 
    private boolean created;

    public CreateShapeCommand(GameModel model, GameShape shape){
        this.model = model;
        this.shape = shape;
        this.index = -1;
        this.created = false;
    }

    @Override
    public void execut(){
        model.addBlueShape(shape);
        index = model.getBlueShapes().indexOf(shape);
        created = index != -1;
    }
    
    @Override
    public void undo(){
        if (created) {
            model.removeBlueShape(shape);
        }
    }

    @Override
    public void redo(){
        if (!created) {
            return;
        }
        if (!model.getBlueShapes().contains(shape)) {
            model.addBlueShape(shape);
        }
    }

}