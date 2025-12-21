package engine.input;

import engine.core.Engine3D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    private Engine3D engine3D;
    private final boolean[] keysPressed = new boolean[256];

    public KeyboardInput(Engine3D engine3D) {
        this.engine3D = engine3D;
    }

    // Action listener methods
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public Engine3D getEngine3D() {
        return engine3D;
    }

    public boolean[] getKeysPressed() {
        return keysPressed;
    }
}
