package graphicEngine.core;

import graphicEngine.math.geometry.Vertex3D;
import graphicEngine.renderer.Camera;

public class GraphicEngineContext {
    private final GraphicEngine graphicEngine;

    private int windowWidth;
    private int windowHeight;
    private Vertex3D windowPosition;

    private int nbTriRenderPerFrame;

    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private double lastFrameDuration;
    private double elapsedTime = 0;

    private Camera camera;


    public GraphicEngineContext(GraphicEngine graphicEngine, int windowWidth, int windowHeight) {
        this.graphicEngine = graphicEngine;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.nbTriRenderPerFrame = 0;
        this.lastFrameDuration = 0.1;
    }

    public void updateWindowInformation() {
        this.windowPosition = new Vertex3D(graphicEngine.getLocationOnScreen());
        this.windowWidth = graphicEngine.getWidth();
        this.windowHeight = graphicEngine.getHeight();
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

    public int getWindowHeight() {
        return windowHeight;
    }

    public int getNbTriRenderPerFrame() {
        return nbTriRenderPerFrame;
    }

    public void setNbTriRenderPerFrame(int nbTriRenderPerFrame) {
        this.nbTriRenderPerFrame = nbTriRenderPerFrame;
    }

    public double getLastFrameDuration() {
        return lastFrameDuration;
    }

    public Vertex3D getWindowPosition() {
        return windowPosition;
    }

    public GraphicEngine getGraphicEngine() {
        return graphicEngine;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
