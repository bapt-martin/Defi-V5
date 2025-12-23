package engine.renderer;

import engine.core.Engine3D;
import engine.core.Scene;
import engine.math.geometry.Mesh;
import engine.math.geometry.Plane;
import engine.math.geometry.Triangle;
import engine.math.geometry.Vertex3D;
import engine.math.tools.Matrix;
import engine.math.tools.Vector3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static engine.math.geometry.Plane.planeToClipAgainst;

public class Pipeline {
    private Matrix viewMatrix;
    private Matrix worldTransformMatrix;
    private List<Triangle> trisToProcess;
    private List<Triangle> trisToRender;
    private Camera camera;
    private double worldRotationAngle;
    private Scene scene;

    public Pipeline(Camera camera, Scene scene) {
        this.camera = camera;
        this.scene = scene;
        this.updateViewMatrix();
        this.updateWorldTransformMatrix();
    }

    public void updateViewMatrix() {
        this.viewMatrix = Matrix.createViewMatrix(this.camera.getCameraPosition(), this.camera.getCameraDirection(), this.camera.getCameraUp());
    }

    public void updateWorldTransformMatrix() {
        this.worldTransformMatrix = Matrix.createWorldTransformMatrix(this.worldRotationAngle * 0.5,this.worldRotationAngle * 1,0,0,0,14);
    }

    public void pipelineExecution(int iWinWidth, int iWinHeight, Graphics g, Engine3D engine3D) {
        this.updateWorldTransformMatrix();
        this.updateViewMatrix();

        this.generateRenderList(iWinWidth, iWinHeight);

        this.paintersAlgorithm();

        this.finalRenderPass(iWinWidth, iWinHeight, g, engine3D);
    }

    public void generateRenderList(int iWinWidth, int iWinHeight) {
        this.scene.initiateTriangleList();

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
                    this.trisToProcess.get(this.trisToProcess.size() - (1+n)).projectToScreenInPlace(this.camera.getProjectionMatrix(), iWinWidth, iWinHeight);
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

    public void finalRenderPass(int iWinWidth, int iWinHeight, Graphics g, Engine3D engine3D) {
        for (Triangle triToClip : trisToProcess) {
            this.clipAgainstScreenBounds(iWinWidth, iWinHeight, triToClip);

            this.rasterization(g, engine3D);
        }
    }

    public void clipAgainstScreenBounds(int iWinWidth, int iWinHeight, Triangle triToClip) {
        trisToRender = new ArrayList<>();
        trisToRender.add(triToClip);

        for (int p = 0; p < 4; p++) {
            List<Triangle> futureTestToClip = new ArrayList<>();
            for(Triangle test : trisToRender) {
                int nbTrisToAdd = planeToClipAgainst(p, iWinWidth, iWinHeight).clipTriangleAgainstPlane(test, futureTestToClip);
            }
            trisToRender = futureTestToClip;
        }
    }

    public void rasterization(Graphics g, Engine3D engine3D) {
        for (Triangle triToDraw : trisToRender) {
            // Getting back the coordinate to draw the 2D triangle
            int[] xs = new int[3];
            int[] ys = new int[3];
            triToDraw.get2DCoordinates(xs, ys);

            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(triToDraw.getColor()); // Setting the correct color
            g2.fillPolygon(xs, ys, 3);

//            g2.setColor(Color.BLACK); // Drawing the outline
//            g2.drawPolygon(xs, ys, 3);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            engine3D.setNbTriRender(engine3D.getNbTriRender() + 1);
            System.out.println("nb tris :" + engine3D.getNbTriRender() + ", frame duration : " + engine3D.getLastFrameDuration());
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

