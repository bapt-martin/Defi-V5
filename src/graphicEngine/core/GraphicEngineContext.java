package graphicEngine.core;

import graphicEngine.math.geometry.Vertex3D;
import graphicEngine.renderer.Camera;

public class GraphicEngineContext {
    private final GraphicEngine graphicEngine;

    private int windowWidth;
    private int windowHeight;
    private Vertex3D windowPosition;

    private int nbTriRenderPerFrame;

    private double deltaTime;
    private double elapsedTime = 0;

    private final int UPS_TARGET = 60;
    private final int FPS_TARGET = 60;

    private Camera camera;


    public GraphicEngineContext(GraphicEngine graphicEngine, int windowWidth, int windowHeight) {
        this.graphicEngine = graphicEngine;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowPosition = new Vertex3D(0, 0, 0);
        this.nbTriRenderPerFrame = 0;
        this.deltaTime = 0.1;
    }

    public void updateWindowInformation() {
        if (graphicEngine.isShowing()) {
            Vertex3D location = new Vertex3D(graphicEngine.getLocationOnScreen().x, graphicEngine.getLocationOnScreen().y, 0);
            this.windowPosition = new Vertex3D(location.x, location.y, 0);
        }
        this.windowWidth = graphicEngine.getWidth();
        this.windowHeight = graphicEngine.getHeight();
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

    public double getDeltaTime() {
        return deltaTime;
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

    public void setDeltaTime(double deltaTime) {
        this.deltaTime = deltaTime;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getUPS_TARGET() {
        return UPS_TARGET;
    }

    public int getFPS_TARGET() {
        return FPS_TARGET;
    }
}
