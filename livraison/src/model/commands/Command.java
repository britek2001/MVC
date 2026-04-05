package mvc.model.commands;
public interface Command {
   
   public void execut();
   public void undo();
   public void redo();

}