package mvc.model.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.GameShape;
import mvc.model.commands.MoveShapeCommand;

public class EtatMoveShape implements EtatInteraction {
    private GameModel model;
    private GameShape selectedShape;
    private double startX, startY;
    private double originalX, originalY;
    private boolean dragging;
    private GameState previousState;
    
    public EtatMoveShape(GameModel model, GameShape selectedShape, GameState previousState) {
        this.model = model;
        this.selectedShape = selectedShape;
        this.dragging = false;
        this.previousState = previousState;
    }
    
    @Override
    public void sourisAppuyee(MouseEvent e, ControleurSouris controller) {
        if (selectedShape != null && selectedShape.contains(new Point2D.Double(e.getX(), e.getY()))) {
            startX = e.getX();
            startY = e.getY();
            originalX = selectedShape.getX();
            originalY = selectedShape.getY();
            dragging = true;
        }
    }
    
    @Override
    public void sourisDeplacee(MouseEvent e, ControleurSouris controller) {
        if (dragging && selectedShape != null) {
            double deltaX = e.getX() - startX;
            double deltaY = e.getY() - startY;
            selectedShape.move(deltaX, deltaY);
            startX = e.getX();
            startY = e.getY();
            controller.repaintView();
        }
    }
    
    @Override
    public void sourisRelachee(MouseEvent e, ControleurSouris controller) {
        if (dragging && selectedShape != null) {

            boolean intersectsRed = model.getRedShapes().stream()
                .anyMatch(redShape -> redShape.intersects(selectedShape));
            boolean intersectsOtherBlue = model.isTwoPlayerMode() && model.getBlueShapes().stream()
                .anyMatch(blueShape -> blueShape != selectedShape && blueShape.intersects(selectedShape));
            boolean outOfGameArea = !model.isShapeWithinGameArea(selectedShape);

            if (intersectsRed || intersectsOtherBlue || outOfGameArea) {
                selectedShape.setPosition(originalX, originalY);
                model.setState(outOfGameArea ? GameState.MOVE_INVALID_BOUNDS : GameState.MOVE_INVALID_INTERSECTION);
            } else {
                MoveShapeCommand cmd = new MoveShapeCommand(
                    model, selectedShape, 
                    originalX, originalY, 
                    selectedShape.getX(), selectedShape.getY()
                );
                controller.addCommand(cmd);
            }
            dragging = false;
        }
        model.setState(previousState);
        controller.changerEtat(new EtatSelection(model));
    }
    
    @Override
    public void sourisBougee(MouseEvent e, ControleurSouris controller) {}
}