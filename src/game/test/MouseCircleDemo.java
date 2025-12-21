package game.test;

import java.awt.*;

public class MouseCircleDemo {
    public static void main(String[] args) throws Exception {
        Robot robot = new Robot();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;
        int radius = 200; // rayon du cercle
        double angle = 0;

        while (true) {
            // Calcul des coordonnées x,y sur le cercle
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));

            robot.mouseMove(x, y);

            angle += 0.05; // vitesse de rotation
            if (angle > 2 * Math.PI) angle -= 2 * Math.PI;

            robot.delay(10); // contrôle la fluidité
        }
    }
}