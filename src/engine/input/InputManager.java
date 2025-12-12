package engine.input;

import engine.core.Camera;
import engine.core.Engine3D;

import java.awt.*;
import java.awt.event.*;

public class InputManager {
    private Engine3D engine3D;
    private Camera camera;
    private KeyboardInput keyboardInput;
    private MouseInput mouseInput;
    private MouseMotionInput mouseMotionInput;

    public InputManager(Engine3D engine3D, Camera camera) {
        this.engine3D = engine3D;
        this.camera = camera;
        this.keyboardInput = new KeyboardInput(engine3D);
        this.mouseInput = new MouseInput(engine3D, this);
        this.mouseMotionInput = new MouseMotionInput(engine3D, this);
    }

    public void attachTo(Component c) {
        c.addKeyListener(this.keyboardInput);   // séparé pour plus de propreté
        c.addMouseListener(this.mouseInput);
        c.addMouseMotionListener(this.mouseMotionInput);
        c.setFocusable(true);
        c.requestFocus();
    }

    public void moveMouseWindow(Point coordinatesIn) {
        try {
            Robot robot = new Robot();
            Point coordinatesOut = new Point();

            Point winPosition = this.engine3D.getLocationOnScreen();

            coordinatesOut.x = winPosition.x + coordinatesIn.x;
            coordinatesOut.y = winPosition.y + coordinatesIn.y;

            robot.mouseMove(coordinatesOut.x, coordinatesOut.y);

            this.engine3D.getpWinLastMousePosition().setX(coordinatesIn.x);
            this.engine3D.getpWinLastMousePosition().setY(coordinatesIn.y);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void centerMouse() {
        Point winPanelCenter = new Point(this.engine3D.getWindowWidth() /2, this.engine3D.getWindowWHeight() /2);
        this.moveMouseWindow(winPanelCenter);
    }

    public void handleKeyPress() {
        double deltaFrameTime = engine3D.getDeltaTime();
        // TRANSLATION
        // Q = Left
        if (engine3D.getKeysPressed()[KeyEvent.VK_Q]) {
            camera.translateCameraInPlace(camera.getvCamRight(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }
        // D = Right
        if (engine3D.getKeysPressed()[KeyEvent.VK_D]) {
            camera.translateCameraInPlace(camera.getvCamRight(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }

        // Z = Forward
        if (engine3D.getKeysPressed()[KeyEvent.VK_Z]) {
            camera.translateCameraInPlace(camera.getvCamDirection(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }
        // S = Behind
        if (engine3D.getKeysPressed()[KeyEvent.VK_S]) {
            camera.translateCameraInPlace(camera.getvCamDirection(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (engine3D.getKeysPressed()[KeyEvent.VK_SHIFT]) {
            if (engine3D.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getvCamUp(), -1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
            }
        } else {
            if (engine3D.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.translateCameraInPlace(camera.getvCamUp(), 1, camera.getdTranslationCameraSpeed(), deltaFrameTime);
            }
        }

        // ROTATION
        // UP = Trigo X-Axis rotation Pitch
        if (engine3D.getKeysPressed()[KeyEvent.VK_UP]) {
            camera.setCamPitch(camera.rotateCameraInPlace(camera.getCamPitch(), camera.getdRotationCameraSpeed(), deltaFrameTime));
        }

        // DOWN = Horaire X-Axis rotation Pitch
        if (engine3D.getKeysPressed()[KeyEvent.VK_DOWN]) {
            camera.setCamPitch(camera.getCamPitch() - camera.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // RIGHT = Trigo Y-Axis rotation Yaw
        if (engine3D.getKeysPressed()[KeyEvent.VK_RIGHT]) {
            camera.setCamYaw(camera.getCamYaw() + camera.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // LEFT = Horaire Y-Axis rotation Yaw
        if (engine3D.getKeysPressed()[KeyEvent.VK_LEFT]) {
            camera.setCamYaw(camera.getCamYaw() - camera.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // A = Trigo Z-Axis rotation Roll
        if (engine3D.getKeysPressed()[KeyEvent.VK_A]) {
            camera.setCamRoll(camera.getCamRoll() + camera.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // E = Horaire Y-Axis rotation Roll
        if (engine3D.getKeysPressed()[KeyEvent.VK_E]) {
            camera.setCamRoll(camera.getCamRoll() - camera.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

    }

    public void handleMouseMoving(MouseEvent e) {
        Point winPanelCenter = new Point(engine3D.getWindowWidth() /2, engine3D.getWindowWHeight() /2);
        Point winMousePos = e.getPoint();

        if(engine3D.isFirstMouseMove()) {
            // Skip the first delta to avoid initial Jump
            engine3D.getpWinLastMousePosition().setX(winPanelCenter.x);
            engine3D.getpWinLastMousePosition().setY(winPanelCenter.y);
            engine3D.setFirstMouseMove(false);
            return;
        }

        int dx = winMousePos.x - (int) engine3D.getpWinLastMousePosition().getX();
        int dy = winMousePos.y - (int) engine3D.getpWinLastMousePosition().getY();

        camera.setCamPitch(camera.getCamPitch() + camera.getdRotationCameraSpeed() * -dy * engine3D.getMouseSensibility());
        camera.setCamYaw(camera.getCamYaw() + camera.getdRotationCameraSpeed() * dx * engine3D.getMouseSensibility());

        try {
            centerMouse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
