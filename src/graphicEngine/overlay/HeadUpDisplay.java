package graphicEngine.overlay;

import graphicEngine.core.EngineContext;

import javax.swing.*;
import java.awt.*;

public class HeadUpDisplay extends JPanel {
    private final JLabel fpsLabel;
    private final JLabel perfLabel;

    private final EngineContext engineContext;

    private long lastUiUpdate = 0;
    private final long UI_UPDATE_INTERVAL = 250_000_000L;
    private final int FPS_TARGET = 60;

    public HeadUpDisplay(EngineContext engineContext) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        this.setBackground(new Color(0, 0, 0, 150));

        this.fpsLabel = createStatLabel("FPS: -");
        this.add(fpsLabel);

        this.perfLabel = createStatLabel("Perf: -");
        this.add(perfLabel);

        this.engineContext = engineContext;
    }

    public void updateStats() {
        long now = System.nanoTime();

        if (now - lastUiUpdate < UI_UPDATE_INTERVAL) {
            engineContext.updateTimeInformation();
            return;
        }
        lastUiUpdate = now;

        double frameDuration = engineContext.getLastFrameDuration();
        if (frameDuration <= 0) return;

        double currentFps = 1.0 / frameDuration;


        double score = ((1.0/FPS_TARGET) / frameDuration) * 100;

        fpsLabel.setText(String.format("FPS: %.0f", currentFps));

        perfLabel.setForeground(getScoreColor(score));
        perfLabel.setText(String.format("Perf: %.0f%% (%.1f ms)", score, frameDuration));

        Dimension newSize = this.getPreferredSize();

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
