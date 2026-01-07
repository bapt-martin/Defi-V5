package graphicEngine.input;

import graphicEngine.core.GraphicEngineContext;
import graphicEngine.math.tools.Vector3D;
import graphicEngine.renderer.Camera;
import graphicEngine.core.GraphicEngine;
import graphicEngine.math.geometry.Vertex3D;

import java.awt.*;
import java.awt.event.*;

public class InputManager {
    private final GraphicEngineContext graphicEngineContext;
    private GraphicEngine graphicEngine;
    private final Camera camera;
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;
    private final MouseMotionInput mouseMotionInput;
    private Robot robot;

    public InputManager(GraphicEngine graphicEngine, Camera camera) {
        this.graphicEngineContext = graphicEngine.getEngineContext();
        this.camera = camera;
        this.graphicEngine = graphicEngine;
        this.keyboardInput = new KeyboardInput(this);
        this.mouseInput = new MouseInput(graphicEngine, this);
        this.mouseMotionInput = new MouseMotionInput(graphicEngineContext,this);

        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public void attachTo(Component c) {
        c.addKeyListener(this.keyboardInput);
        c.addMouseListener(this.mouseInput);
        c.addMouseWheelListener(this.mouseInput);
        c.addMouseMotionListener(this.mouseMotionInput);
        c.setFocusable(true);
        c.requestFocus();
    }

    public void moveMouseWindow(Vertex3D coordinatesIn) {
        Vertex3D coordinatesOut = coordinatesIn.add(graphicEngineContext.getWindowPosition());

        robot.mouseMove((int) coordinatesOut.getX(), (int) coordinatesOut.getY());

        this.mouseMotionInput.setWinLastMousePosition(coordinatesIn);
    }

    public void centerMouse() {
        Vertex3D winPanelCenter = new Vertex3D(graphicEngineContext.getWindowWidth()/2.0, graphicEngineContext.getWindowHeight()/2.0,0);
        this.moveMouseWindow(winPanelCenter);
    }

    public void handleKeyPress() {
        double deltaFrameTime = graphicEngineContext.getLastFrameDuration();
        double translationCameraSpeed = camera.getdTranslationCameraSpeed();
        double rotationCameraSpeed    = camera.getdRotationCameraSpeed();

        this.handleMovement(deltaFrameTime, translationCameraSpeed);
        this.handleRotation(deltaFrameTime, rotationCameraSpeed);
    }

    public void handleMovement(double deltaFrameTime, double translationCameraSpeed) {
        double xDir = 0; // Strafe
        double zDir = 0; // Forward
        double yDir = 0; // Fly

        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Z]) zDir += 1; // Forward
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_S]) zDir -= 1; // Backward
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Q]) xDir -= 1; // Left strafe
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_D]) xDir += 1; // Right strafe

        if (keyboardInput.getKeysPressed()[KeyEvent.VK_SPACE]) {
            if (keyboardInput.getKeysPressed()[KeyEvent.VK_SHIFT]) {
                yDir -= 1; // Down
            } else {
                yDir += 1; // Up
            }
        }

        if (xDir == 0 && yDir == 0 && zDir == 0) return;


        Vector3D moveX = new Vector3D(camera.getCameraRight());
        moveX.scaleInPlace(xDir);

        Vector3D moveZ = new Vector3D(camera.getCameraDirection());
        moveZ.scaleInPlace(zDir);

        Vector3D moveY = new Vector3D(camera.getCameraUp());
        moveY.scaleInPlace(yDir);

        Vector3D totalTranslation = moveX.add(moveZ).add(moveY);
        totalTranslation.normalizeInPlace();

        camera.translateCameraInPlace(totalTranslation, 1, translationCameraSpeed, deltaFrameTime);
    }

    public void handleRotation(double deltaFrameTime, double rotationCameraSpeed) {
        this.handlePitch(deltaFrameTime, rotationCameraSpeed);
        this.handleYaw(deltaFrameTime, rotationCameraSpeed);
        this.handleRoll(deltaFrameTime, rotationCameraSpeed);
    }

    public void handlePitch(double deltaFrameTime, double rotationCameraSpeed) {
        // UP = Anti-Clockwise X-Axis rotation Pitch
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_UP]) {
            camera.setCamPitch(camera.getCamPitch() - rotationCameraSpeed * deltaFrameTime);
        }

        // DOWN = Clockwise X-Axis rotation Pitch
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_DOWN]) {
            camera.setCamPitch(camera.getCamPitch() + rotationCameraSpeed * deltaFrameTime);
        }
    }

    public void handleYaw(double deltaFrameTime, double rotationCameraSpeed) {
        // RIGHT = Anti-Clockwise Y-Axis rotation Yaw
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_RIGHT]) {
            camera.setCamYaw(camera.getCamYaw() + rotationCameraSpeed * deltaFrameTime);
        }

        // LEFT = Clockwise Y-Axis rotation Yaw
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_LEFT]) {
            camera.setCamYaw(camera.getCamYaw() - rotationCameraSpeed * deltaFrameTime);
        }
    }

    public void handleRoll(double deltaFrameTime, double rotationCameraSpeed) {
        // A = Anti-Clockwise Z-Axis rotation Roll
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_A]) {
            camera.setCamRoll(camera.getCamRoll() + rotationCameraSpeed * deltaFrameTime);
        }

        // E = Clockwise Y-Axis rotation Roll
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_E]) {
            camera.setCamRoll(camera.getCamRoll() - rotationCameraSpeed * deltaFrameTime);
        }
    }

    public void handleTranslation(double deltaFrameTime, double translationCameraSpeed) {
        this.handleForward(deltaFrameTime, translationCameraSpeed);
        this.handleRight(deltaFrameTime, translationCameraSpeed);
        this.handleUp(deltaFrameTime, translationCameraSpeed);
    }

    public void handleRight(double deltaFrameTime, double translationCameraSpeed) {
        // Q = Left
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Q]) {
            camera.translateCameraInPlace(camera.getCameraRight(), -1, translationCameraSpeed, deltaFrameTime);
        }
        // D = Right
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_D]) {
            camera.translateCameraInPlace(camera.getCameraRight(), 1, translationCameraSpeed, deltaFrameTime);
        }
    }

    public void handleForward(double deltaFrameTime, double translationCameraSpeed) {
        // Z = Forward
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Z]) {
            camera.translateCameraInPlace(camera.getCameraDirection(), 1, translationCameraSpeed, deltaFrameTime);
        }
        // S = Behind
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_S]) {
            camera.translateCameraInPlace(camera.getCameraDirection(), -1, translationCameraSpeed, deltaFrameTime);
        }
    }

    public void handleUp(double deltaFrameTime, double translationCameraSpeed) {
        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_SHIFT]) {
            if (keyboardInput.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getCameraUp(), -1, translationCameraSpeed, deltaFrameTime);
            }
        } else {
            if (keyboardInput.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getCameraUp(), 1, translationCameraSpeed, deltaFrameTime);
            }
        }
    }

    public void handleMouseMoving(MouseEvent e) {
        Vertex3D winMousePos = new Vertex3D(e.getPoint());

        Vertex3D delta = new Vertex3D(winMousePos.sub(mouseMotionInput.getWinLastMousePosition()));

        camera.setCamPitch(camera.getCamPitch() + camera.getdRotationCameraSpeed() * delta.getY() * mouseMotionInput.getMouseSensitivity());
        camera.setCamYaw(camera.getCamYaw() + camera.getdRotationCameraSpeed() * delta.getX() * mouseMotionInput.getMouseSensitivity());

        try {
            centerMouse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void handleMouseWheelInput(MouseWheelEvent e) {
        camera.setZoom(camera.getZoom() - e.getWheelRotation() * camera.getZoomFactor());
        System.out.println(camera.getZoom());
    }
}
