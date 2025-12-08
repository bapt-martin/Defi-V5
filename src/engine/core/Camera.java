package engine.core;

import engine.math.Matrix;
import engine.math.Vector3D;
import engine.math.Vertex3D;

import static java.lang.Math.tan;

public class Camera {
    private Vertex3D pCamPosition;
    private Vector3D vCamDirection;
    private Vector3D vCamUp;
    private Vector3D vCamRight;

    private double dCamPitch;
    private double dCamYaw;
    private double dCamRoll;

    private Matrix matProjection;
    private double dNear;
    private double dFar;
    private double dFov;

    public Camera() {
        this.pCamPosition = new Vertex3D(0, 0, 1);
        this.vCamDirection = new Vector3D(0, 0, 1);
        this.vCamUp        = new Vector3D(0, 1, 0);
        this.vCamRight     = new Vector3D(1, 0, 0);
        this.dCamPitch = 0;
        this.dCamYaw = 0;
        this.dCamRoll = 0;
        this.dNear = 0.1;
        this.dFar = 1000;
        this.dFov = 90;
    }

    public Camera(double dNear, double dFar, double dFov) {
        this();
        this.dNear = dNear;
        this.dFar = dFar;
        this.dFov = dFov;

    }

    public Camera(Vertex3D pCamPosition, Vector3D vCamDirection, Vector3D vCamUp, Vector3D vCamRight, double dCamPitch, double dCamYaw, double dCamRoll, Matrix matProjection, double dNear, double dFar, double dFov) {
        this.pCamPosition  = new Vertex3D(pCamPosition);
        this.vCamDirection = new Vector3D(vCamDirection);
        this.vCamUp        = new Vector3D(vCamUp);
        this.vCamRight     = new Vector3D(vCamRight);
        this.dCamPitch = dCamPitch;
        this.dCamYaw   = dCamYaw;
        this.dCamRoll  = dCamRoll;
        this.matProjection = new Matrix(matProjection);
        this.dNear = dNear;
        this.dFar  = dFar;
        this.dFov  = dFov;
    }

    public void matCreateCamProjection(int width, int height) {
        double q = dFar / (dFar - dNear);
        double aspectRatio = (double) width / height;
        double scalingFactorRad = 1 / tan(dFov * 0.5 / 180 * Math.PI);

        double[][] matProj = new double[4][4];
        matProj[0][0] = aspectRatio * scalingFactorRad;
        matProj[1][1] = scalingFactorRad;
        matProj[2][2] = q;
        matProj[3][3] = 0;

        matProj[3][2] = - dNear * q;
        matProj[2][3] = 1;

        this.setMatProjection(new Matrix(matProj));
    }

    public void matProjectionActualisation(Engine3D engine3D) {
        if (engine3D.getWidth() != engine3D.getiWinWidth() || engine3D.getHeight() != engine3D.getiWinHeight()) {
            engine3D.setiWinWidth(engine3D.getWidth());
            engine3D.setiWinHeight(engine3D.getHeight());

            engine3D.getCamera().matCreateCamProjection(engine3D.getiWinWidth(), engine3D.getiWinHeight());
        }
    }

    public void camUpdate() {
        Vector3D vTarget = new Vector3D(0,0,1);
        Vector3D vUp     = new Vector3D(0,1,0);
        Vector3D vRight  = new Vector3D(1,0,0);

        // Rotation around local Y-axis
        Matrix matCameraRotYaw = Matrix.matCreateRotationAroundAxis4x4(dCamYaw,vUp);

        Vector3D vTargetY = vTarget.transform(matCameraRotYaw);
        Vector3D vRightY  = vRight.transform(matCameraRotYaw);
        Vector3D vUpY     = vUp.transform(matCameraRotYaw);

        vTargetY.normalized();
        vRightY.normalized();
        vUpY.normalized();

        // Rotation around local X-axis
        dCamPitch = Math.max(-89.0*Math.PI/180.0, Math.min(89.0*Math.PI/180.0, dCamPitch));

        Matrix matCameraRotPitch = Matrix.matCreateRotationAroundAxis4x4(dCamPitch,vRightY);

        Vector3D vTargetYP = vTargetY.transform(matCameraRotPitch);
        Vector3D vRightYP  = vRightY.transform(matCameraRotPitch);
        Vector3D vUpYP     = vUpY.transform(matCameraRotPitch);

        vTargetYP.normalized();
        vRightYP.normalized();
        vUpYP.normalized();

        // Rotation around local Z-axis
        Matrix matCameraRotRoll = Matrix.matCreateRotationAroundAxis4x4(dCamRoll,vTargetYP);

        Vector3D vTargetYPR = vTargetYP.transform(matCameraRotRoll);
        Vector3D vRightYPR  = vRightYP.transform(matCameraRotRoll);
        Vector3D vUpYPR     = vUpYP.transform(matCameraRotRoll);

        vTargetYPR.normalized();
        vRightYPR.normalized();
        vUpYPR.normalized();

        vCamDirection = new Vector3D(vTargetYPR);
        vCamUp        = new Vector3D(vUpYPR);
        vCamRight     = new Vector3D(vRightYPR);
    }

    public Vertex3D getpCamPosition() {
        return pCamPosition;
    }

    public void setpCamPosition(Vertex3D pCamPosition) {
        this.pCamPosition = new Vertex3D(pCamPosition);
    }

    public Vector3D getvCamDirection() {
        return vCamDirection;
    }

    public void setvCamDirection(Vector3D vCamDirection) {
        this.vCamDirection = new Vector3D(vCamDirection);
    }

    public Vector3D getvCamUp() {
        return vCamUp;
    }

    public void setvCamUp(Vector3D vCamUp) {
        this.vCamUp = new Vector3D(vCamUp);
    }

    public Vector3D getvCamRight() {
        return vCamRight;
    }

    public void setvCamRight(Vector3D vCamRight) {
        this.vCamRight = new Vector3D(vCamRight);
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
        return new Matrix(matProjection);
    }

    public void setMatProjection(Matrix matProjection) {
        this.matProjection = new Matrix(matProjection);
    }
}


