package engine.overlay;

import engine.core.Engine3D;

import javax.swing.*;
import java.awt.*;

public class GeneralData extends JPanel {
    private final JLabel fpsLabel;

    public GeneralData() {
        this.fpsLabel = new JLabel("FPS: 0");
        fpsLabel.setForeground(Color.WHITE);
        fpsLabel.setBackground(Color.BLACK);
        fpsLabel.setOpaque(true);
        fpsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        this.add(fpsLabel);
    }

    public void CalcFpsPerSec(Engine3D engine3D) {
        engine3D.setNbFrames(engine3D.getNbFrames() + 1);
        long now = System.nanoTime();

        if (now - engine3D.getLastFPSTime() >= 1_000_000_000L) { // 1 sec elapsed
            double currentFps = engine3D.getNbFrames(); // frames in 1 sec
            fpsLabel.setText(String.format("FPS: %.1f", currentFps));

            engine3D.setNbFrames(0);
            engine3D.setLastFPSTime(now);
        }
    }

    public void CalcFpsPerFrame(Engine3D engine3D) {
        double deltaTime = engine3D.getDeltaTime(); // Time elapsed since last frame
        if (deltaTime > 0) {
            double currentFps = 1.0 / deltaTime;
            fpsLabel.setText(String.format("FPS: %.1f", currentFps));
        }
    }
}
