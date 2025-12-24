package graphicEngine.core;

import graphicEngine.math.geometry.Vertex3D;

public class EngineContext {
    private final Engine3D engine3D;

    private int windowWidth;
    private int windowHeight;
    private Vertex3D windowPosition;

    private int nbTriRenderPerFrame;

    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private double lastFrameDuration;
    private double elapsedTime = 0;


    public EngineContext(Engine3D engine3D, int windowWidth, int windowHeight) {
        this.engine3D = engine3D;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.nbTriRenderPerFrame = 0;
        this.lastFrameDuration = 0.1;
    }

    public void updateWindowInformation() {
        this.windowPosition = new Vertex3D(engine3D.getLocationOnScreen());
        this.windowWidth = engine3D.getWidth();
        this.windowHeight = engine3D.getHeight();
    }

    public void updateTimeInformation() {
        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0;
        lastFrameDuration = (now - lastFrameTime) / 1_000_000_000.0;
        lastFrameTime = now;
    }

    public void updateNbRenderedTriangle() {
        this.setNbTriRenderPerFrame(this.getNbTriRenderPerFrame() + 1);
    }

    public void resetNbRenderedTriangle() {
        this.setNbTriRenderPerFrame(0);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getNbTriRenderPerFrame() {
        return nbTriRenderPerFrame;
    }

    public void setNbTriRenderPerFrame(int nbTriRenderPerFrame) {
        this.nbTriRenderPerFrame = nbTriRenderPerFrame;
    }

    public long getStartFrameTime() {
        return startFrameTime;
    }

    public void setStartFrameTime(long startFrameTime) {
        this.startFrameTime = startFrameTime;
    }

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public void setLastFrameTime(long lastFrameTime) {
        this.lastFrameTime = lastFrameTime;
    }

    public double getLastFrameDuration() {
        return lastFrameDuration;
    }

    public void setLastFrameDuration(double lastFrameDuration) {
        this.lastFrameDuration = lastFrameDuration;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Vertex3D getWindowPosition() {
        return windowPosition;
    }

    public void setWindowPosition(Vertex3D windowPosition) {
        this.windowPosition = windowPosition;
    }
}
