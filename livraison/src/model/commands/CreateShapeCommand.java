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
        boolean added = model.addBlueShape(shape);
        if(added){
                System.out.println("Shape added");
                index = model.getBlueShapes().indexOf(shape);
                System.out.println("Index of created shape: " + index); 
                created = index != -1;
            } else {
                System.out.println("Failed");
                return; 
        }   
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