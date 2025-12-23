package engine.overlay;

import engine.core.Engine3D;

import javax.swing.*;
import java.awt.*;

public class HeadUpDisplay extends JPanel {
    private final JLabel fpsLabel;
    private final JLabel perfLabel;

    private long lastUiUpdate = 0;
    private final long UI_UPDATE_INTERVAL = 250_000_000L;
    private final double TARGET_MS = 1000.0 / 60.0;

    public HeadUpDisplay() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        this.setBackground(new Color(0, 0, 0, 150));

        this.fpsLabel = createStatLabel("FPS: -");
        this.add(fpsLabel);

        this.perfLabel = createStatLabel("Perf: -");
        this.add(perfLabel);
    }

    public void updateStats(Engine3D engine3D) {
        long now = System.nanoTime();
        if (now - lastUiUpdate < UI_UPDATE_INTERVAL) {
            return;
        }
        lastUiUpdate = now;

        double frameDuration = engine3D.getLastFrameDuration();
        if (frameDuration <= 0) return;

        double currentFps = 1.0 / frameDuration;

        double currentFrameMs = frameDuration * 1000;
        double score = (TARGET_MS / currentFrameMs) * 100;

        fpsLabel.setText(String.format("FPS: %.0f", currentFps));

        perfLabel.setForeground(getScoreColor(score));

        perfLabel.setText(String.format("Perf: %.0f%% (%.1f ms)", score, currentFrameMs));

        // 1. Demande au Layout (FlowLayout) de recalculer la taille idéale avec le nouveau texte
        Dimension newSize = this.getPreferredSize();

        // 2. Applique cette nouvelle taille au panneau
        this.setSize(newSize);

        this.revalidate();
    }

    public static Color getScoreColor(double score) {
        double clampedScore = Math.max(0, Math.min(100, score));

        float hue = (float) (clampedScore / 100.0 * 0.33); // Chromatic circle hue

        return Color.getHSBColor(hue, 1.0f, 1.0f);
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Consolas", Font.BOLD, 14));
        return label;
    }
}
