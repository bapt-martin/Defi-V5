package engine.renderer;

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
    private Mesh mesh;
    private Camera camera;
    private double worldRotationAngle;

    public Pipeline(Mesh mesh, Camera camera) {
        this.mesh = mesh;
        this.camera = camera;
        this.updateViewMatrix();
        this.updateWorldTransformMatrix();
    }

    public void updateViewMatrix() {
        this.viewMatrix = Matrix.createViewMatrix(this.camera.getCameraPosition(), this.camera.getCameraDirection(), this.camera.getCameraUp());
    }

    public void updateWorldTransformMatrix() {
        this.worldTransformMatrix = Matrix.createWorldTransformMatrix(this.worldRotationAngle * 0.5,this.worldRotationAngle * 1,0,0,0,14);
    }

    public void pipelineExecution(int iWinWidth, int iWinHeight, Graphics g) {
        this.updateWorldTransformMatrix();
        this.updateViewMatrix();

        this.generateRenderList(iWinWidth,iWinHeight);

        this.paintersAlgorithm();

        this.finalRenderPass(iWinWidth,iWinHeight,g);
    }

    public void generateRenderList(int iWinWidth, int iWinHeight) {
        this.trisToProcess = new ArrayList<>();

        for (Triangle triMeshClean : this.mesh.getMeshTriangle()) {
            Triangle triTransformed = triMeshClean.transformed(this.worldTransformMatrix);

            if (triTransformed.isFacing(this.camera)) {
                triTransformed.setLighting(new Vector3D(0,0,-1));

                triTransformed.transformInPlace(this.getViewMatrix());

                Plane frontClippingPlane = new Plane(new Vertex3D(0,0, 0.1), new Vector3D(0,0,1));
                int nbClippedTris = frontClippingPlane.clipTriangleAgainstPlane(triTransformed, this.trisToProcess);

                for (int n = 0; n < nbClippedTris; n++) {
                    this.trisToProcess.get(this.trisToProcess.size() - (1+n)).projectToScreenInPlace(this.camera.getProjectionMatrix(), iWinHeight, iWinWidth);
                }
            }
        }
    }

    public void backFaceCulling() {
    }

    public void setLighting() {

    }

    public void applyViewTransform() {

    }

    public void clipAgainstNearPlane() {

    }

    public void projectTriangles() {

    }

    public void paintersAlgorithm() {
        this.trisToProcess.sort((t1, t2) -> {
            double dMeanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double dMeanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(dMeanZ2,dMeanZ1);
        });
    }

    public void finalRenderPass(int iWinWidth, int iWinHeight, Graphics g) {
        for (Triangle triToClip : trisToProcess) {
            this.clipAgainstScreenBounds(iWinWidth, iWinHeight, triToClip);

            this.rasterization(g);
        }
    }

    public void clipAgainstScreenBounds(int iWinWidth, int iWinHeight, Triangle triToClip) {
        trisToRender = new ArrayList<>();
        trisToRender.add(triToClip);

        for (int p = 0; p < 4; p++) {
            int nbTrisToAdd = 1;
            List<Triangle> futureTestToClip = new ArrayList<>();
            for(Triangle test : trisToRender) {
                nbTrisToAdd = planeToClipAgainst(p, iWinWidth, iWinHeight).clipTriangleAgainstPlane(test, futureTestToClip);
            }
            trisToRender = futureTestToClip;
        }
    }

    public void rasterization(Graphics g) {
        for (Triangle triToDraw : trisToRender) {
            // Getting back the coordinate to draw the 2D triangle
            int[] xs = new int[3];
            int[] ys = new int[3];

            triToDraw.get2DCoordinates(xs, ys);

            Graphics2D g2 = (Graphics2D) g;

            g2.setColor(triToDraw.getColor()); // Setting the correct color
            g2.fillPolygon(xs, ys, 3);

            g2.setColor(Color.BLACK);
            g2.drawPolygon(xs, ys, 3);

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            //System.out.println(nbTriRender++);
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

