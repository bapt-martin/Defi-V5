package graphicEngine.input;

import graphicEngine.core.GraphicEngineContext;
import graphicEngine.math.tools.Vector3D;
import graphicEngine.renderer.Camera;
import graphicEngine.core.GraphicEngine;
import graphicEngine.math.geometry.Vertex3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InputManager {
    private final GraphicEngineContext graphicEngineContext;
    private final Camera camera;
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;
    private final MouseMotionInput mouseMotionInput;
    private Robot robot;
    private Point lastRobotPos = new Point(0, 0);
    private boolean isFirstMove = true;

    public InputManager(GraphicEngine graphicEngine, Camera camera) {
        this.graphicEngineContext = graphicEngine.getEngineContext();
        this.camera = camera;
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

    public void handleKeyPress() {
        double translationCameraSpeed = camera.getdTranslationCameraSpeed();
        double rotationCameraSpeed    = camera.getdRotationCameraSpeed();

        this.handleTranslation(1.0/graphicEngineContext.getUPS_TARGET(), translationCameraSpeed);
        this.handleRotation(1.0/graphicEngineContext.getUPS_TARGET(), rotationCameraSpeed);
    }

    public void handleTranslation(double deltaFrameTime, double translationCameraSpeed) {
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

        Vector3D moveY = new Vector3D(camera.getCameraUp());
        moveY.scaleInPlace(yDir);

        Vector3D moveZ = new Vector3D(camera.getCameraDirection());
        moveZ.scaleInPlace(zDir);
        
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

    public void handleMouseMoving(MouseEvent e) {
        Point currentPosGlobal = e.getLocationOnScreen();

        if (isFirstMove) {
            lastRobotPos.setLocation(currentPosGlobal);
            centerMouse();
            isFirstMove = false;
            return;
        }

        if (currentPosGlobal.x == lastRobotPos.x && currentPosGlobal.y == lastRobotPos.y) {
            return;
        }

        double deltaX = currentPosGlobal.x - lastRobotPos.x;
        double deltaY = currentPosGlobal.y - lastRobotPos.y;

        camera.setCamPitch(camera.getCamPitch() + camera.getdRotationCameraSpeed() * deltaY * mouseMotionInput.getMouseSensitivity());
        camera.setCamYaw(camera.getCamYaw()     + camera.getdRotationCameraSpeed() * deltaX * mouseMotionInput.getMouseSensitivity());

        centerMouse();
    }

    public void centerMouse() {
        Vertex3D globalCenter = graphicEngineContext.getCanvasCenter();
        if (globalCenter == null) return;

        int targetX = (int) globalCenter.getX();
        int targetY = (int) globalCenter.getY();

        robot.mouseMove(targetX, targetY);

        lastRobotPos.setLocation(targetX, targetY);
    }

    public void handleMouseWheelInput(MouseWheelEvent e) {
        camera.setZoom(camera.getZoom() - e.getWheelRotation() * camera.getZoomFactor());
        System.out.println(camera.getZoom());
    }
}
