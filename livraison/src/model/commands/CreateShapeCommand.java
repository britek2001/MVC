package mvc.model.commands;
import mvc.model.game.GameModel;
import mvc.model.shapes.GameShape;
import java.util.logging.Logger;

public class CreateShapeCommand implements Command{
    private static final Logger logger = Logger.getLogger(CreateShapeCommand.class.getName());
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
                logger.info("Shape added: " + shape);
                index = model.getBlueShapes().indexOf(shape);
                logger.info("Index of created shape: " + index);
                created = index != -1;
            } else {
                logger.warning("Failed to add shape: " + shape);
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