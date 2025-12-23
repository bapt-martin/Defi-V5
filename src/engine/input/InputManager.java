package engine.input;

import engine.renderer.Camera;
import engine.core.Engine3D;
import engine.math.geometry.Vertex3D;

import java.awt.*;
import java.awt.event.*;

public class InputManager {
    private final Engine3D engine3D;
    private final Camera camera;
    private final KeyboardInput keyboardInput;
    private final MouseInput mouseInput;
    private final MouseMotionInput mouseMotionInput;
    private Robot robot;

    public InputManager(Engine3D engine3D, Camera camera) {
        this.engine3D = engine3D;
        this.camera = camera;
        this.keyboardInput = new KeyboardInput(engine3D);
        this.mouseInput = new MouseInput(engine3D, this);
        this.mouseMotionInput = new MouseMotionInput(engine3D, this);

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
        Vertex3D winPosition = new Vertex3D(this.engine3D.getLocationOnScreen());

        Vertex3D coordinatesOut = coordinatesIn.add(winPosition);

        robot.mouseMove((int) coordinatesOut.getX(), (int) coordinatesOut.getY());

        this.mouseMotionInput.setWinLastMousePosition(coordinatesIn);
    }

    public void centerMouse() {
        Vertex3D winPanelCenter = new Vertex3D(this.engine3D.getWindowWidth() /2, this.engine3D.getWindowWHeight() /2,0);
        this.moveMouseWindow(winPanelCenter);
    }

    public void handleKeyPress() {
        double deltaFrameTime = engine3D.getLastFrameDuration();
        // TRANSLATION
        // Q = Left
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Q]) {
            camera.translateCameraInPlace(camera.getCameraRight(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }
        // D = Right
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_D]) {
            camera.translateCameraInPlace(camera.getCameraRight(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }

        // Z = Forward
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_Z]) {
            camera.translateCameraInPlace(camera.getCameraDirection(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }
        // S = Behind
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_S]) {
            camera.translateCameraInPlace(camera.getCameraDirection(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_SHIFT]) {
            if (keyboardInput.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getCameraUp(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
            }
        } else {
            if (keyboardInput.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getCameraUp(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
            }
        }

        // ROTATION
        // UP = Anti-Clockwise X-Axis rotation Pitch
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_UP]) {
            camera.setCamPitch(camera.rotateCameraInPlace(camera.getCamPitch(), camera.getdRotationCameraSpeed(), deltaFrameTime));
        }

        // DOWN = Clockwise X-Axis rotation Pitch
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_DOWN]) {
            camera.setCamPitch(camera.getCamPitch() - camera.getdRotationCameraSpeed() * engine3D.getLastFrameDuration());
        }

        // RIGHT = Anti-Clockwise Y-Axis rotation Yaw
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_RIGHT]) {
            camera.setCamYaw(camera.getCamYaw() + camera.getdRotationCameraSpeed() * engine3D.getLastFrameDuration());
        }

        // LEFT = Clockwise Y-Axis rotation Yaw
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_LEFT]) {
            camera.setCamYaw(camera.getCamYaw() - camera.getdRotationCameraSpeed() * engine3D.getLastFrameDuration());
        }

        // A = Anti-Clockwise Z-Axis rotation Roll
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_A]) {
            camera.setCamRoll(camera.getCamRoll() + camera.getdRotationCameraSpeed() * engine3D.getLastFrameDuration());
        }

        // E = Clockwise Y-Axis rotation Roll
        if (keyboardInput.getKeysPressed()[KeyEvent.VK_E]) {
            camera.setCamRoll(camera.getCamRoll() - camera.getdRotationCameraSpeed() * engine3D.getLastFrameDuration());
        }
    }

    public void handleMouseMoving(MouseEvent e) {
        Vertex3D winMousePos = new Vertex3D(e.getPoint());

        Vertex3D delta = new Vertex3D(winMousePos.sub(mouseMotionInput.getWinLastMousePosition()));

        camera.setCamPitch(camera.getCamPitch() + camera.getdRotationCameraSpeed() * -delta.getY() * mouseMotionInput.getMouseSensitivity());
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
