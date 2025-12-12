package engine.input;

import engine.core.Engine3D;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {
    private Engine3D engine3D;
    private InputManager inputManager;

    public MouseInput(Engine3D engine3D, InputManager inputManager) {
        this.engine3D = engine3D;
        this.inputManager = inputManager;
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
