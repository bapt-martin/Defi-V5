package engine.input;

import engine.core.Camera;
import engine.core.Engine3D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionInput implements MouseMotionListener {
    private Engine3D engine3D;
    private InputManager inputManager;

    public MouseMotionInput(Engine3D engine3D, InputManager inputManager) {
        this.engine3D = engine3D;
        this.inputManager = inputManager;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        inputManager.handleMouseMoving(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragggggged : " + e.getX() + ", " + e.getY());
        engine3D.requestFocusInWindow();
    }

}
