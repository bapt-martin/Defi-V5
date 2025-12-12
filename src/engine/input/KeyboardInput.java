package engine.input;

import engine.core.Camera;
import engine.core.Engine3D;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    private Engine3D engine3D;

    public KeyboardInput(Engine3D engine3D) {
        this.engine3D = engine3D;
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
}
