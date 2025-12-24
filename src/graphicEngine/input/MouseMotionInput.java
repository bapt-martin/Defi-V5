package graphicEngine.input;

import graphicEngine.core.Engine3D;
import graphicEngine.core.EngineContext;
import graphicEngine.math.geometry.Vertex3D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMotionInput implements MouseMotionListener {
    private Engine3D engine3D;
    private EngineContext engineContext;
    private InputManager inputManager;
    private final double mouseSensitivity = 0.01;
    private Vertex3D winLastMousePosition;
    private boolean firstMouseMove = true;// To input manager?

    public MouseMotionInput(Engine3D engine3D, EngineContext engineContext, InputManager inputManager) {
        this.engine3D = engine3D;
        this.inputManager = inputManager;
        this.winLastMousePosition = new Vertex3D(0,0,0);
        this.engineContext = engineContext;
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

    public void manageFirstInput() {
        if(this.isFirstMouseMove()) {
            this.setWinLastMousePosition(new Vertex3D(engineContext.getWindowWidth()/2.0, engineContext.getWindowHeight()/2.0,0));
            this.setFirstMouseMove(false);
        }
    }

    public double getMouseSensitivity() {
        return mouseSensitivity;
    }

    public Vertex3D getWinLastMousePosition() {
        return winLastMousePosition;
    }

    public void setWinLastMousePosition(Vertex3D winLastMousePosition) {
        this.winLastMousePosition = winLastMousePosition;
    }

    public boolean isFirstMouseMove() {
        return firstMouseMove;
    }

    public void setFirstMouseMove(boolean firstMouseMove) {
        this.firstMouseMove = firstMouseMove;
    }
}
