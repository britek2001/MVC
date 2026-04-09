package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class DeleteShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 
    private boolean deleted;

    public DeleteShapeCommand(GameModel model, GameShape shape) {
        this.model = model;
        this.shape = shape;
        this.index = -1;
        this.deleted = false;
    }

    @Override
    public void execut(){
        index = model.getBlueShapes().indexOf(shape);
        if(index != -1){
            this.model.removeBlueShape(shape);
            deleted = true;
        } else {
            deleted = false;
        }
    }
    
    @Override
    public void undo(){
        if (!deleted || index < 0 || index > model.getBlueShapes().size()) {
            return;
        }
        model.restoreBlueShape(shape, index);
    }

    @Override
    public void redo(){
        if (deleted && model.getBlueShapes().contains(shape)) {
            model.removeBlueShape(shape);
        }
    }

}