package graphicEngine.renderer;

import graphicEngine.core.EngineContext;
import graphicEngine.scene.GameObject;
import graphicEngine.scene.Scene;
import graphicEngine.math.geometry.Plane;
import graphicEngine.math.geometry.Triangle;
import graphicEngine.math.geometry.Vertex3D;
import graphicEngine.math.tools.Matrix;
import graphicEngine.math.tools.Vector3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static graphicEngine.math.geometry.Plane.planeToClipAgainst;

public class Pipeline {
    private final Camera camera;
    private final Scene scene;
    private final EngineContext engineContext;

    private Matrix viewMatrix;
    private List<Triangle> processedTriangle;
    private List<Triangle> trisToRender;

    public Pipeline(Camera camera, Scene scene, EngineContext engineContext) {
        this.camera = camera;
        this.scene = scene;
        this.engineContext = engineContext;
        this.updateViewMatrix();
        this.processedTriangle = new ArrayList<>();
        this.trisToRender = new ArrayList<>();

    }

    public void updateViewMatrix() {
        this.viewMatrix = Matrix.createViewMatrix(this.camera.getCameraPosition(), this.camera.getCameraDirection(), this.camera.getCameraUp());
    }

    public void pipelineExecution(Graphics g) {
        this.updateViewMatrix();

        this.processGeometry();

        this.paintersAlgorithm();

        this.rasterizePass(g);

    }

    public void processGeometry() {
        int width = engineContext.getWindowWidth();
        int height = engineContext.getWindowHeight();

        Vector3D lightDirection = new Vector3D(0, 0, -1);
        Plane frontClippingPlane = new Plane(new Vertex3D(0, 0, 0.1), new Vector3D(0, 0, 1));

        this.processedTriangle.clear();

        List<GameObject> renderQueue = scene.getRenderQueue();
        for (GameObject obj : renderQueue) {
            List<Triangle> triList = obj.getMesh().getMeshTriangle();

            for (Triangle triMeshClean : triList) {
                Triangle triTransformed = triMeshClean.transformed(obj.getWorldTransformMatrix());

                if (triTransformed.isFacing(this.camera)) {
                    triTransformed.setLighting(lightDirection);

                    triTransformed.transformInPlace(this.getViewMatrix());

                    int startIndex = this.processedTriangle.size();
                    frontClippingPlane.clipTriangleAgainstPlane(triTransformed, this.processedTriangle);
                    int endIndex = this.processedTriangle.size();

                    for (int i = startIndex; i < endIndex; i++) {
                        this.processedTriangle.get(i).projectToScreenInPlace(this.camera.getProjectionMatrix(), width, height);
                    }
                }
            }
        }
    }

    public void paintersAlgorithm() {
        this.processedTriangle.sort((t1, t2) -> {
            double dMeanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double dMeanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(dMeanZ2, dMeanZ1);
        });
    }

    public void rasterizePass(Graphics g) {
        int width = engineContext.getWindowWidth();
        int height = engineContext.getWindowHeight();
        for (Triangle triToClip : processedTriangle) {
            this.clipToScreen(width, height, triToClip);

            this.drawBatch(engineContext, g);
        }

        System.out.println("nb tris :" + engineContext.getNbTriRenderPerFrame() + ", frame duration : " + engineContext.getLastFrameDuration());
    }

    public void clipToScreen(int iWinWidth, int iWinHeight, Triangle triToClip) {
        trisToRender.clear();
        trisToRender.add(triToClip);

        for (int p = 0; p < 4; p++) {
            List<Triangle> futureTestToClip = new ArrayList<>();
            for (Triangle test : trisToRender) {
                planeToClipAgainst(p, iWinWidth, iWinHeight).clipTriangleAgainstPlane(test, futureTestToClip);
            }
            trisToRender = futureTestToClip;
        }
    }

    public void drawBatch(EngineContext engineContext, Graphics g) {
        for (Triangle triToDraw : trisToRender) {
            triToDraw.drawTriangle(g, false);

            engineContext.updateNbRenderedTriangle();

        }

    }

    public Matrix getViewMatrix() {
        return viewMatrix;
    }


    public List<Triangle> getProcessedTriangle() {
        return processedTriangle;
    }
}
