package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;

public class DeleteShapeCommand implements Command{
    private GameModel model;
    private GameShape shape;
    private int index; 
    private boolean deleted;
    private boolean currentlyDeleted;

    public DeleteShapeCommand(GameModel model, GameShape shape) {
        this.model = model;
        this.shape = shape;
        this.index = -1;
        this.deleted = false;
        this.currentlyDeleted = false;
    }

    @Override
    public void execut(){
        index = model.getBlueShapes().indexOf(shape);
        if(index != -1){
            this.model.removeBlueShape(shape);
            deleted = true;
            currentlyDeleted = true;
        } else {
            deleted = false;
            currentlyDeleted = false;
        }
    }
    
    @Override
    public void undo(){
        if (!deleted || !currentlyDeleted || index < 0) {
            return;
        }
        model.restoreBlueShape(shape, index);
        currentlyDeleted = false;
    }

    @Override
    public void redo(){
        if (!deleted || currentlyDeleted) {
            return;
        }

        int currentIndex = model.getBlueShapes().indexOf(shape);
        if (currentIndex == -1) {
            return;
        }

        index = currentIndex;
        model.removeBlueShape(shape);
        currentlyDeleted = true;
    }

}