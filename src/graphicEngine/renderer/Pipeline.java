package graphicEngine.renderer;

import graphicEngine.core.EngineContext;
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
    private Matrix viewMatrix;
    private Matrix worldTransformMatrix;
    private List<Triangle> trisToProcess;
    private List<Triangle> trisToRender;
    private final Camera camera;
    private double worldRotationAngle;
    private final Scene scene;
    private final EngineContext engineContext;

    public Pipeline(Camera camera, Scene scene, EngineContext engineContext) {
        this.camera = camera;
        this.scene = scene;
        this.engineContext = engineContext;
        this.updateViewMatrix();
        this.updateWorldTransformMatrix();
    }

    public void updateViewMatrix() {
        this.viewMatrix = Matrix.createViewMatrix(this.camera.getCameraPosition(), this.camera.getCameraDirection(), this.camera.getCameraUp());
    }

    public void updateWorldTransformMatrix() {
        this.worldTransformMatrix = Matrix.createWorldTransformMatrix(this.worldRotationAngle * 0.5,this.worldRotationAngle * 1,0,0,0,14);
    }

    public void pipelineExecution(Graphics g) {
        this.updateWorldTransformMatrix();
        this.updateViewMatrix();

        this.generateRenderList();

        this.paintersAlgorithm();

        this.finalRenderPass(g);
    }

    public void generateRenderList() {
        this.scene.initiateTriangleList();
        int width = engineContext.getWindowWidth();
        int height = engineContext.getWindowHeight();


        this.trisToProcess = new ArrayList<>();
        List<Triangle> sceneTriangles = this.scene.getTriangleList();

        for (Triangle triMeshClean : sceneTriangles) {
            Triangle triTransformed = triMeshClean.transformed(this.worldTransformMatrix);

            if (triTransformed.isFacing(this.camera)) {
                triTransformed.setLighting(new Vector3D(0,0,-1));

                triTransformed.transformInPlace(this.getViewMatrix());

                Plane frontClippingPlane = new Plane(new Vertex3D(0,0, 0.1), new Vector3D(0,0,1));
                int nbClippedTris = frontClippingPlane.clipTriangleAgainstPlane(triTransformed, this.trisToProcess);

                for (int n = 0; n < nbClippedTris; n++) {
                    this.trisToProcess.get(this.trisToProcess.size() - (1+n)).projectToScreenInPlace(this.camera.getProjectionMatrix(), width, height);
                }
            }
        }
    }

    public void paintersAlgorithm() {
        this.trisToProcess.sort((t1, t2) -> {
            double dMeanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double dMeanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(dMeanZ2,dMeanZ1);
        });
    }

    public void finalRenderPass(Graphics g) {
        int width = engineContext.getWindowWidth();
        int height = engineContext.getWindowHeight();

        for (Triangle triToClip : trisToProcess) {
            this.clipAgainstScreenBounds(width, height, triToClip);

            this.rasterization(engineContext, g);
        }
    }

    public void clipAgainstScreenBounds(int iWinWidth, int iWinHeight, Triangle triToClip) {
        trisToRender = new ArrayList<>();
        trisToRender.add(triToClip);

        for (int p = 0; p < 4; p++) {
            List<Triangle> futureTestToClip = new ArrayList<>();
            for(Triangle test : trisToRender) {
                planeToClipAgainst(p, iWinWidth, iWinHeight).clipTriangleAgainstPlane(test, futureTestToClip);
            }
            trisToRender = futureTestToClip;
        }
    }

    public void rasterization(EngineContext engineContext, Graphics g) {
        for (Triangle triToDraw : trisToRender) {
            triToDraw.drawTriangle(g, false);

            engineContext.updateNbRenderedTriangle();
            System.out.println("nb tris :" + engineContext.getNbTriRenderPerFrame() + ", frame duration : " + engineContext.getLastFrameDuration());
        }
    }

    public Matrix getViewMatrix() {
        return viewMatrix;
    }

    public Matrix getWorldTransformMatrix() {
        return worldTransformMatrix;
    }

    public List<Triangle> getTrisToProcess() {
        return trisToProcess;
    }

    public double getWorldRotationAngle() {
        return worldRotationAngle;
    }

    public void setWorldRotationAngle(double worldRotationAngle) {
        this.worldRotationAngle = worldRotationAngle;
    }
}

