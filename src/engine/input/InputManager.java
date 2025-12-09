package engine.input;

import engine.core.Camera;
import engine.core.Engine3D;

import java.awt.*;
import java.awt.event.*;


public class InputManager implements KeyListener, MouseListener, MouseMotionListener {
    private Engine3D engine3D;
    private Camera camera;

    public InputManager(Engine3D engine3D, Camera camera) {
        this.engine3D = engine3D;
        this.camera = camera;
    }

    public void centerMouse(Engine3D engine3D) {
        try {
            Robot robot = new Robot();

            Point winPosition = engine3D.getLocationOnScreen();
            Point winPanelCenter = new Point(engine3D.getiWinWidth() /2, engine3D.getiWinHeight() /2);

            int winPanelCenterX = winPosition.x + winPanelCenter.x;
            int winPanelCenterY = winPosition.y + winPanelCenter.y;

            robot.mouseMove(winPanelCenterX, winPanelCenterY); // Robot move relatively of the whole screen
            // Update last position
            engine3D.getpWinLastMousePosition().setX(winPanelCenter.x);
            engine3D.getpWinLastMousePosition().setY(winPanelCenter.y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleKeyPress() {
        // TRANSLATION
        // Q = Left
        if (engine3D.getKeysPressed()[KeyEvent.VK_Q]) {
            camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamRight().scaled(+ engine3D.getdTranslationCameraSpeed())));
        }

        // D = Right
        if (engine3D.getKeysPressed()[KeyEvent.VK_D]) {
            camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamRight().scaled(- engine3D.getdTranslationCameraSpeed())));
            //camera.setpCamPosition(camera.getpCamPosition().tupleSubtraction(camera.getvCamRight().scalarMultiplication(engine3D.getdTranslationCameraSpeed())));
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (engine3D.getKeysPressed()[KeyEvent.VK_SHIFT]) {
            if (engine3D.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamUp().scaled(- engine3D.getdTranslationCameraSpeed())));
                //camera.setpCamPosition(camera.getpCamPosition().tupleSubtraction(camera.getvCamUp().scalarMultiplication(engine3D.getdTranslationCameraSpeed())));
            }
        } else {
            if (engine3D.getKeysPressed()[KeyEvent.VK_SPACE]) {
                camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamUp().scaled(+ engine3D.getdTranslationCameraSpeed())));
                //camera.setpCamPosition(camera.getpCamPosition().tupleAddition(camera.getvCamUp().scalarMultiplication(engine3D.getdTranslationCameraSpeed())));
            }
        }

        // Z = Forward
        if (engine3D.getKeysPressed()[KeyEvent.VK_Z]) {
            camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamDirection().scaled(+ engine3D.getdTranslationCameraSpeed())));
//            camera.setpCamPosition(camera.getpCamPosition().tupleAddition(camera.getvCamDirection().scalarMultiplication(engine3D.getdTranslationCameraSpeed())));
        }
        // S = Behind
        if (engine3D.getKeysPressed()[KeyEvent.VK_S]) {
            camera.setpCamPosition(camera.getpCamPosition().translated(camera.getvCamDirection().scaled(- engine3D.getdTranslationCameraSpeed())));
//            camera.setpCamPosition(camera.getpCamPosition().tupleSubtraction(camera.getvCamDirection().scalarMultiplication(engine3D.getdTranslationCameraSpeed())));
        }

        // ROTATION
        // UP = Trigo X-Axis rotation Pitch
        if (engine3D.getKeysPressed()[KeyEvent.VK_UP]) {
            camera.setdCamPitch(camera.getdCamPitch() + engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // DOWN = Horaire X-Axis rotation Pitch
        if (engine3D.getKeysPressed()[KeyEvent.VK_DOWN]) {
            camera.setdCamPitch(camera.getdCamPitch() - engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // RIGHT = Trigo Y-Axis rotation Yaw
        if (engine3D.getKeysPressed()[KeyEvent.VK_RIGHT]) {
            camera.setdCamYaw(camera.getdCamYaw() + engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // LEFT = Horaire Y-Axis rotation Yaw
        if (engine3D.getKeysPressed()[KeyEvent.VK_LEFT]) {
            camera.setdCamYaw(camera.getdCamYaw() - engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // A = Trigo Z-Axis rotation Roll
        if (engine3D.getKeysPressed()[KeyEvent.VK_A]) {
            camera.setdCamRoll(camera.getdCamRoll() + engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

        // E = Horaire Y-Axis rotation Roll
        if (engine3D.getKeysPressed()[KeyEvent.VK_E]) {
            camera.setdCamRoll(camera.getdCamRoll() - engine3D.getdRotationCameraSpeed() * engine3D.getDeltaTime());
        }

    }

    private void handleMouseMoving(MouseEvent e) {
        Point winPanelCenter = new Point(engine3D.getiWinWidth() /2, engine3D.getiWinHeight() /2);
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

        camera.setdCamPitch(camera.getdCamPitch() + engine3D.getdRotationCameraSpeed() * -dy * engine3D.getMouseSensibility());
        camera.setdCamYaw(camera.getdCamYaw() + engine3D.getdRotationCameraSpeed() * dx * engine3D.getMouseSensibility());

        try {
            centerMouse(engine3D);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    // Action mouse motion listener methods
    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseMoving(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragggggged : " + e.getX() + ", " + e.getY());
        engine3D.requestFocusInWindow();
    }


    // Action listener methods
    @Override
    public void keyPressed(KeyEvent e) {
        engine3D.getKeysPressed()[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        engine3D.getKeysPressed()[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }


    // Action mouse listener methods
    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clic souris : " + e.getX() + ", " + e.getY());
        engine3D.requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("PRESSEEEED  " +e.getX() +" : "+ e.getY());
        engine3D.requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("REALEASSSSEED  " +e.getX() +" : "+ e.getY());
        engine3D.requestFocusInWindow();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("zrreg" + e.getX() +" : "+ e.getY());
        engine3D.requestFocusInWindow();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println(e.getX() +" : "+ e.getY());
        engine3D.requestFocusInWindow();
    }
}
