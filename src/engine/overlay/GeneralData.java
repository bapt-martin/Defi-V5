package engine.overlay;

import engine.core.Engine3D;

import javax.swing.*;
import java.awt.*;

public class GeneralData extends JPanel {
    private final JLabel fpsLabel;

    public GeneralData() {
        this.fpsLabel = new JLabel("FPS: 0");
        fpsLabel.setForeground(Color.WHITE); // couleur du texte
        fpsLabel.setBackground(Color.BLACK); // couleur de fond si tu veux
        fpsLabel.setOpaque(true);             // nécessaire pour voir le fond
        fpsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        this.add(fpsLabel);
    }

    public void CalcFpsPerSec(Engine3D engine3D) {
        engine3D.setNbFrames(engine3D.getNbFrames() + 1);
        long now = System.nanoTime();

        if (now - engine3D.getLastFPSTime() >= 1_000_000_000L) { // 1 seconde écoulée
            double currentFps = engine3D.getNbFrames(); // nombre de frames pendant 1 sec
            fpsLabel.setText(String.format("FPS: %.1f", currentFps));

            engine3D.setNbFrames(0);
            engine3D.setLastFPSTime(now);
        }
    }

    public void CalcFpsPerFrame(Engine3D engine3D) {
        double deltaTime = engine3D.getDeltaTime(); // temps écoulé depuis la dernière frame
        if (deltaTime > 0) {
            double currentFps = 1.0 / deltaTime;
            fpsLabel.setText(String.format("FPS: %.1f", currentFps));
        }
    }
}
