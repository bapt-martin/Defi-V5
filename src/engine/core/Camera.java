package engine.core;

import engine.math.Matrix;
import engine.math.Vertex3D;

import java.awt.*;

import static java.lang.Math.tan;

public class Camera {
    private Vertex3D pCamPosition;
    private Vertex3D vCamDirection;
    private Vertex3D vCamUp;
    private Vertex3D vCamRight;

    private double dCamPitch;
    private double dCamYaw;
    private double dCamRoll;

    private Matrix matProjection;
    private double dNear;
    private double dFar;
    private double dFov;

    public Camera() {
        this.pCamPosition = new Vertex3D(0, 0, 1);
        this.vCamDirection = new Vertex3D(0, 0, 1,0);
        this.vCamUp = new Vertex3D(0, 1, 0,0);
        this.vCamRight = new Vertex3D(1, 0, 0,0);
        this.dCamPitch = 0;
        this.dCamYaw = 0;
        this.dCamRoll = 0;
        this.dNear = 0.1;
        this.dFar = 1000;
        this.dFov = 90;

    }

    public Camera(double dNear, double dFar, double dFov) {
        this.pCamPosition = new Vertex3D(0, 0, 1,0);
        this.vCamDirection = new Vertex3D(0, 0, 1,0);
        this.vCamUp = new Vertex3D(0, 1, 0,0);
        this.vCamRight = new Vertex3D(1, 0, 0,0);
        this.dCamPitch = 0;
        this.dCamYaw = 0;
        this.dCamRoll = 0;
        this.dNear = dNear;
        this.dFar = dFar;
        this.dFov = dFov;

    }

    public Camera(Vertex3D pCamPosition, Vertex3D vCamDirection, Vertex3D vCamUp, Vertex3D vCamRight, double dCamPitch, double dCamYaw, double dCamRoll, Matrix matProjection, double dNear, double dFar, double dFov) {
        this.pCamPosition = pCamPosition;
        this.vCamDirection = vCamDirection;
        this.vCamUp = vCamUp;
        this.vCamRight = vCamRight;
        this.dCamPitch = dCamPitch;
        this.dCamYaw = dCamYaw;
        this.dCamRoll = dCamRoll;
        this.matProjection = matProjection;
        this.dNear = dNear;
        this.dFar = dFar;
        this.dFov = dFov;
    }

    public void matCreateCamProjection(int width, int height) {
        double q = this.dFar / (dFar - dNear);
        double aspectRatio = (double) width / height;
        double scalingFactorRad = 1 / tan(dFov * 0.5 / 180 * Math.PI);

        Matrix matProj = new Matrix();
        matProj.getMatrix()[0][0] = aspectRatio * scalingFactorRad;
        matProj.getMatrix()[1][1] = scalingFactorRad;
        matProj.getMatrix()[2][2] = q;
        matProj.getMatrix()[3][2] = - dNear * q;
        matProj.getMatrix()[2][3] = 1;
        matProj.getMatrix()[3][3] = 0;

        this.setMatProjection(matProj);
    }

    public void matProjectionActualisation(Graphics g, Engine3D engine3D) {
        if (engine3D.getWidth() != engine3D.getiWinWidth() || engine3D.getHeight() != engine3D.getiWinHeight()) {
            engine3D.setiWinWidth(engine3D.getWidth());
            engine3D.setiWinHeight(engine3D.getHeight());

            engine3D.getCamera().matCreateCamProjection(engine3D.getiWinWidth(), engine3D.getiWinHeight());
        }
    }

    public void camUpdate() {

        Vertex3D vTarget = new Vertex3D(0,0,1,0);
        Vertex3D vUp     = new Vertex3D(0,1,0,0);
        Vertex3D vRight  = new Vertex3D(1,0,0,0);

        // Rotation around local Y-axis
        Matrix matCameraRotYaw = Matrix.matCreateRotationAroundAxis4x4(dCamYaw,vUp);

        Vertex3D vTargetY = Vertex3D.vertexMatrixMultiplication(vTarget,matCameraRotYaw);
        Vertex3D vRightY  = Vertex3D.vertexMatrixMultiplication(vRight,matCameraRotYaw);
        Vertex3D vUpY     = Vertex3D.vertexMatrixMultiplication(vUp,matCameraRotYaw);

        vTargetY.vertNormalisation();
        vRightY.vertNormalisation();
        vUpY.vertNormalisation();

        // Rotation around local X-axis
        dCamPitch = Math.max(-89.0*Math.PI/180.0, Math.min(89.0*Math.PI/180.0, dCamPitch));
        Matrix matCameraRotPitch = Matrix.matCreateRotationAroundAxis4x4(dCamPitch,vRightY);

        Vertex3D vTargetYP = Vertex3D.vertexMatrixMultiplication(vTargetY,matCameraRotPitch);
        Vertex3D vRightYP  = Vertex3D.vertexMatrixMultiplication(vRightY,matCameraRotPitch);
        Vertex3D vUpYP     = Vertex3D.vertexMatrixMultiplication(vUpY,matCameraRotPitch);

        vTargetYP.vertNormalisation();
        vRightYP.vertNormalisation();
        vUpYP.vertNormalisation();

        // Rotation around local Z-axis
        Matrix matCameraRotRoll  = Matrix.matCreateRotationAroundAxis4x4(dCamRoll,vTargetYP);

        Vertex3D vTargetYPR = Vertex3D.vertexMatrixMultiplication(vTargetYP,matCameraRotRoll);
        Vertex3D vRightYPR  = Vertex3D.vertexMatrixMultiplication(vRightYP,matCameraRotRoll);
        Vertex3D vUpYPR     = Vertex3D.vertexMatrixMultiplication(vUpYP,matCameraRotRoll);

        vTargetYPR.vertNormalisation();
        vRightYPR.vertNormalisation();
        vUpYPR.vertNormalisation();

        vCamDirection = vTargetYPR;
        vCamUp = vUpYPR;
        vCamRight = vRightYPR;
    }


    public double getdNear() {
        return dNear;
    }

    public void setdNear(double dNear) {
        this.dNear = dNear;
    }

    public double getdFar() {
        return dFar;
    }

    public void setdFar(double dFar) {
        this.dFar = dFar;
    }

    public double getdFov() {
        return dFov;
    }

    public void setdFov(double dFov) {
        this.dFov = dFov;
    }

    public Vertex3D getpCamPosition() {
        return pCamPosition;
    }

    public void setpCamPosition(Vertex3D pCamPosition) {
        this.pCamPosition = pCamPosition;
    }

    public Vertex3D getvCamDirection() {
        return vCamDirection;
    }

    public void setvCamDirection(Vertex3D vCamDirection) {
        this.vCamDirection = vCamDirection;
    }

    public Vertex3D getvCamUp() {
        return vCamUp;
    }

    public void setvCamUp(Vertex3D vCamUp) {
        this.vCamUp = vCamUp;
    }

    public Vertex3D getvCamRight() {
        return vCamRight;
    }

    public void setvCamRight(Vertex3D vCamRight) {
        this.vCamRight = vCamRight;
    }

    public double getdCamPitch() {
        return dCamPitch;
    }

    public void setdCamPitch(double dCamPitch) {
        this.dCamPitch = dCamPitch;
    }

    public double getdCamYaw() {
        return dCamYaw;
    }

    public void setdCamYaw(double dCamYaw) {
        this.dCamYaw = dCamYaw;
    }

    public double getdCamRoll() {
        return dCamRoll;
    }

    public void setdCamRoll(double dCamRoll) {
        this.dCamRoll = dCamRoll;
    }

    public Matrix getMatProjection() {
        return matProjection;
    }

    public void setMatProjection(Matrix matProjection) {
        this.matProjection = matProjection;
    }
}


