package graphicEngine.overlay;

import graphicEngine.core.GraphicEngineContext;
import graphicEngine.math.geometry.Vertex3D;
import graphicEngine.renderer.Camera;

import javax.swing.*;
import java.awt.*;

public class HeadUpDisplay extends JPanel {
    private final JLabel fpsLabel;
    private final JLabel perfLabel;
    private final JLabel positionLabel;
    private final JLabel rotationLabel;


    private final GraphicEngineContext graphicEngineContext;

    private long lastUiUpdate = 0;
    private final long UI_UPDATE_INTERVAL = 250_000_000L;
    private final int FPS_TARGET = 60;

    public HeadUpDisplay(GraphicEngineContext graphicEngineContext) {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        this.setBackground(new Color(0, 0, 0, 150));

        this.fpsLabel = createStatLabel("FPS: -");
        this.add(fpsLabel);

        this.perfLabel = createStatLabel("Perf: -");
        this.add(perfLabel);

        this.positionLabel = createStatLabel("Pos: -");
        this.add(positionLabel);

        this.rotationLabel = createStatLabel("Rot: -");
        this.add(rotationLabel);

        this.graphicEngineContext = graphicEngineContext;
    }

    public void update() {
        this.updateStats();
        this.updateTransform();
    }

    public void updateTransform() {
        this.updatePosition();
        this.updateRotation();
    }

    public void updatePosition() {
        Vertex3D cameraPosition = graphicEngineContext.getCamera().getCameraPosition();
        positionLabel.setText(String.format("Pos: [X:%.1f Y:%.1f Z:%.1f]",
                cameraPosition.getX(),
                cameraPosition.getY(),
                cameraPosition.getZ()
        ));
    }

    public void updateRotation() {
        Camera.CameraRotation cameraRotation = graphicEngineContext.getCamera().getCameraRotation();
        double degreeConversion = (180 / Math.PI)   ;
        rotationLabel.setText(String.format("Rot: [Y:%.1f P:%.1f R:%.1f]",
                cameraRotation.yaw * degreeConversion,
                cameraRotation.pitch * degreeConversion,
                cameraRotation.roll * degreeConversion
        ));
    }

    public void updateStats() {
        long now = System.nanoTime();

        if (now - lastUiUpdate < UI_UPDATE_INTERVAL) {
            graphicEngineContext.updateTimeInformation();
            return;
        }
        lastUiUpdate = now;

        double frameDuration = graphicEngineContext.getLastFrameDuration();
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
