package mvc.model.controller;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import mvc.model.game.GameModel;
import mvc.model.game.GameState;
import mvc.model.shapes.GameShape;
import mvc.model.shapes.Rectangle;
import mvc.model.shapes.Circle;
import mvc.model.commands.ResizeShapeCommand;

public class EtatResizeShape implements EtatInteraction {
    
    private GameModel model;
    private GameShape selectedShape;
    private double startX, startY;
    private double originalX, originalY;
    private double originalWidth, originalHeight, originalRadius;
    private int resizeHandle; 
    private boolean dragging;
    private GameState previousState;
    
    public EtatResizeShape(GameModel model, GameShape selectedShape, GameState previousState) {
        this.model = model;
        this.selectedShape = selectedShape;
        this.dragging = false;
        this.previousState = previousState;
    }
    
    private int getResizeHandle(double mouseX, double mouseY) {
        if (selectedShape instanceof Rectangle) {
            Rectangle r = (Rectangle) selectedShape;
            double handleSize = 8;
            if (Math.abs(mouseX - r.x) < handleSize && Math.abs(mouseY - r.y) < handleSize) return 5; // NW
            if (Math.abs(mouseX - (r.x + r.width)) < handleSize && Math.abs(mouseY - r.y) < handleSize) return 4; // NE
            if (Math.abs(mouseX - r.x) < handleSize && Math.abs(mouseY - (r.y + r.height)) < handleSize) return 7; // SW
            if (Math.abs(mouseX - (r.x + r.width)) < handleSize && Math.abs(mouseY - (r.y + r.height)) < handleSize) return 6; // SE
            if (Math.abs(mouseX - (r.x + r.width/2)) < handleSize && Math.abs(mouseY - r.y) < handleSize) return 0; // N
            if (Math.abs(mouseX - (r.x + r.width/2)) < handleSize && Math.abs(mouseY - (r.y + r.height)) < handleSize) return 1; // S
            if (Math.abs(mouseX - r.x) < handleSize && Math.abs(mouseY - (r.y + r.height/2)) < handleSize) return 3; // W
            if (Math.abs(mouseX - (r.x + r.width)) < handleSize && Math.abs(mouseY - (r.y + r.height/2)) < handleSize) return 2; // E

            if (r.contains(new Point2D.Double(mouseX, mouseY)) && r.getZoneType(new Point2D.Double(mouseX, mouseY)) == 0) {
                double dTop = Math.abs(mouseY - r.y);
                double dBottom = Math.abs(mouseY - (r.y + r.height));
                double dLeft = Math.abs(mouseX - r.x);
                double dRight = Math.abs(mouseX - (r.x + r.width));

                double min = Math.min(Math.min(dTop, dBottom), Math.min(dLeft, dRight));
                if (min == dTop) return 0;
                if (min == dBottom) return 1;
                if (min == dRight) return 2;
                return 3;
            }
        } else if (selectedShape instanceof Circle) {
            Circle c = (Circle) selectedShape;
            double distToCenter = Math.hypot(mouseX - c.getX(), mouseY - c.getY());
            double radius = c.getRadius();
            if (Math.abs(distToCenter - radius) < 10) return 0;
            if (c.getZoneType(new Point2D.Double(mouseX, mouseY)) == 0) return 0;
        }
        return -1;
    }
    
    @Override
    public void sourisAppuyee(MouseEvent e, ControleurSouris controller) {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            return;
        }
        resizeHandle = getResizeHandle(e.getX(), e.getY());
        if (resizeHandle != -1) {
            startX = e.getX();
            startY = e.getY();
            if (selectedShape instanceof Rectangle) {
                Rectangle r = (Rectangle) selectedShape;
                originalX = r.x;
                originalY = r.y;
                originalWidth = r.width;
                originalHeight = r.height;
            } else if (selectedShape instanceof Circle) {
                Circle c = (Circle) selectedShape;
                originalX = c.getX();
                originalY = c.getY();
                originalRadius = c.getRadius();
            }
            dragging = true;
        }
    }
    
    @Override
    public void sourisDeplacee(MouseEvent e, ControleurSouris controller) {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            return;
        }
        if (dragging && selectedShape != null) {
            double deltaX = e.getX() - startX;
            double deltaY = e.getY() - startY;
            
            if (selectedShape instanceof Rectangle) {
                Rectangle r = (Rectangle) selectedShape;
                double newWidth = originalWidth;
                double newHeight = originalHeight;
                double newX = originalX;
                double newY = originalY;
                
                switch(resizeHandle) {
                    case 0: newHeight = originalHeight - deltaY; newY = originalY + deltaY; break;
                    case 1: newHeight = originalHeight + deltaY; break;
                    case 2: newWidth = originalWidth + deltaX; break;
                    case 3: newWidth = originalWidth - deltaX; newX = originalX + deltaX; break;
                    case 4: newWidth = originalWidth + deltaX; newHeight = originalHeight - deltaY; newY = originalY + deltaY; break;
                    case 5: newWidth = originalWidth - deltaX; newHeight = originalHeight - deltaY; newX = originalX + deltaX; newY = originalY + deltaY; break;
                    case 6: newWidth = originalWidth + deltaX; newHeight = originalHeight + deltaY; break;
                    case 7: newWidth = originalWidth - deltaX; newHeight = originalHeight + deltaY; newX = originalX + deltaX; break;
                }
                
                if (newWidth > 10 && newHeight > 10) {
                    r.x = newX;
                    r.y = newY;
                    r.width = newWidth;
                    r.height = newHeight;
                    model.modelChanged("SHAPE_RESIZING");
                }
            } else if (selectedShape instanceof Circle) {
                Circle c = (Circle) selectedShape;
                double delta = Math.max(deltaX, deltaY);
                double newRadius = originalRadius + delta;
                if (newRadius > 5) {
                    double currentRadius = c.getRadius();
                    if (currentRadius > 0) {
                        c.resize(newRadius / currentRadius);
                        model.modelChanged("SHAPE_RESIZING");
                    }
                }
            }
        }
    }
    
    @Override
    public void sourisRelachee(MouseEvent e, ControleurSouris controller) {
        if (model.isGameFinished() || model.getState() == GameState.GAME_OVER) {
            dragging = false;
            controller.changerEtat(new EtatSelection(model));
            return;
        }
        if (dragging) {
            boolean intersectsRed = model.getRedShapes().stream()
                    .anyMatch(redShape -> redShape.intersects(selectedShape));
            boolean intersectsOtherBlue = model.isTwoPlayerMode() && model.getBlueShapes().stream()
                    .anyMatch(blueShape -> blueShape != selectedShape && blueShape.intersects(selectedShape));
            boolean outOfGameArea = !model.isShapeWithinGameArea(selectedShape);

            if (intersectsRed || intersectsOtherBlue || outOfGameArea) {
                if (selectedShape instanceof Rectangle) {
                    Rectangle r = (Rectangle) selectedShape;
                    r.x = originalX;
                    r.y = originalY;
                    r.width = originalWidth;
                    r.height = originalHeight;
                } else if (selectedShape instanceof Circle) {
                    Circle c = (Circle) selectedShape;
                    c.setPosition(originalX, originalY);
                    double currentRadius = c.getRadius();
                    if (currentRadius > 0) {
                        c.resize(originalRadius / currentRadius);
                    }
                }
                model.setState(outOfGameArea ? GameState.RESIZE_INVALID_BOUNDS : GameState.RESIZE_INVALID_INTERSECTION);
            } else if (selectedShape instanceof Rectangle) {
                Rectangle r = (Rectangle) selectedShape;
                double factor = (originalWidth > 0) ? (r.width / originalWidth) : 1.0;
                ResizeShapeCommand cmd = new ResizeShapeCommand(model, selectedShape, factor, 0);
                controller.addCommand(cmd);
            } else if (selectedShape instanceof Circle) {
                Circle c = (Circle) selectedShape;
                double factor = (originalRadius > 0) ? (c.getRadius() / originalRadius) : 1.0;
                ResizeShapeCommand cmd = new ResizeShapeCommand(model, selectedShape, factor, 0);
                controller.addCommand(cmd);
            }
            dragging = false;
        }
        model.setState(previousState);
        controller.changerEtat(new EtatSelection(model));
    }
    

}