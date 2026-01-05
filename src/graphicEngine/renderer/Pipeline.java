package graphicEngine.renderer;

import graphicEngine.core.GraphicEngineContext;
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
    private final GraphicEngineContext graphicEngineContext;

    private Matrix viewMatrix;
    private List<Triangle> processedTriangle;
    private List<Triangle> trisToRender;

    public Pipeline(Camera camera, Scene scene, GraphicEngineContext graphicEngineContext) {
        this.camera = camera;
        this.scene = scene;
        this.graphicEngineContext = graphicEngineContext;
        this.updateViewMatrix();
        this.processedTriangle = new ArrayList<>();
        this.trisToRender = new ArrayList<>();

    }

    public void updateViewMatrix() {
        this.viewMatrix = Matrix.createViewMatrix(this.camera.getCameraPosition(), this.camera.getCameraDirection(), this.camera.getCameraUp());
    }

    public void execution(Graphics g) {
        this.updateViewMatrix();

        this.processAllGeometry();

        this.paintersAlgorithm();

        this.rasterizePass(g);

    }

    public void processAllGeometry() {
        int width = graphicEngineContext.getWindowWidth();
        int height = graphicEngineContext.getWindowHeight();

        Matrix projectionMatrix = this.camera.getProjectionMatrix();

        Vector3D lightDirection = new Vector3D(0, 0, 1);
        Plane frontClippingPlane = new Plane(new Vertex3D(0, 0, 0.1), new Vector3D(0, 0, 1));

        this.processedTriangle.clear();

        List<GameObject> renderQueue = scene.getRenderQueue();
        for (GameObject gameObject : renderQueue) {
            this.processGameObject(width, height, projectionMatrix, frontClippingPlane, lightDirection, gameObject);
        }
    }

    public void processGameObject(int width, int height, Matrix projectionMatrix, Plane frontClippingPlane, Vector3D lightDirection, GameObject gameObject) {
        Matrix worldTransformMatrix = gameObject.getWorldTransformMatrix();
        List<Triangle> triList = gameObject.getMesh().getMeshTriangle();

        for (Triangle triMeshClean : triList) {
            this.processTriangle(width, height, projectionMatrix, frontClippingPlane, lightDirection, worldTransformMatrix, triMeshClean);
        }
    }

    public void processTriangle(int width, int height, Matrix projectionMatrix, Plane frontClippingPlane, Vector3D lightDirection, Matrix worldTransformMatrix, Triangle triMeshClean) { //Backface Culling
        Triangle triTransformed = triMeshClean.transformed(worldTransformMatrix);


        boolean isFlipped = worldTransformMatrix.getDeterminant() < 0;

        if (!triTransformed.isFacing(this.camera,isFlipped)) {
            return;
        }

        triTransformed.setLighting(lightDirection, isFlipped);

        triTransformed.transformInPlace(viewMatrix);

        this.clipAndProject(width, height, projectionMatrix, frontClippingPlane, triTransformed);
    }

    public void clipAndProject(int width, int height, Matrix projectionMatrix, Plane frontClippingPlane, Triangle triTransformed) {
        int startIndex = this.processedTriangle.size();
        frontClippingPlane.clipTriangleAgainstPlane(triTransformed, this.processedTriangle);
        int endIndex = this.processedTriangle.size();

        for (int i = startIndex; i < endIndex; i++) {
            this.processedTriangle.get(i).projectToScreenInPlace(projectionMatrix, width, height);
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
        int width = graphicEngineContext.getWindowWidth();
        int height = graphicEngineContext.getWindowHeight();
        for (Triangle triToClip : processedTriangle) {
            this.clipToScreen(width, height, triToClip);

            this.drawBatch(graphicEngineContext, g);
        }

        System.out.println("nb tris :" + graphicEngineContext.getNbTriRenderPerFrame() + ", frame duration : " + graphicEngineContext.getLastFrameDuration());
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

    public void drawBatch(GraphicEngineContext graphicEngineContext, Graphics g) {
        for (Triangle triToDraw : trisToRender) {
            triToDraw.drawTriangle(g, false);

            graphicEngineContext.updateNbRenderedTriangle();

        }

    }

    public Matrix getViewMatrix() {
        return viewMatrix;
    }

    public List<Triangle> getProcessedTriangle() {
        return processedTriangle;
    }
}
