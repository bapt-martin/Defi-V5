package engine.core;

import engine.math.Matrix;
import engine.math.Vector3D;
import engine.math.Vertex3D;

public class Camera {
    private Vertex3D pCamPosition;
    private Vector3D vCamDirection;
    private Vector3D vCamUp;
    private Vector3D vCamRight;

    private double camPitch;
    private double camYaw;
    private double camRoll;

    private Matrix projectionMatrix;
    private double near;
    private double far;
    private double fov;

    private double dTranslationCameraSpeed = 10;
    private double dRotationCameraSpeed = 0.5;

    public Camera() {
        this.pCamPosition = new Vertex3D(0, 0, 1);
        this.vCamDirection = new Vector3D(0, 0, 1);
        this.vCamUp        = new Vector3D(0, 1, 0);
        this.vCamRight     = new Vector3D(1, 0, 0);
        this.camPitch = 0;
        this.camYaw = 0;
        this.camRoll = 0;
        this.near = 0.1;
        this.far = 1000;
        this.fov = 90;
    }

    public Camera(double near, double far, double fov) {
        this();
        this.near = near;
        this.far = far;
        this.fov = fov;

    }

    public Camera(Vertex3D pCamPosition, Vector3D vCamDirection, Vector3D vCamUp, Vector3D vCamRight, double camPitch, double camYaw, double camRoll, Matrix projectionMatrix, double near, double far, double fov) {
        this.pCamPosition  = new Vertex3D(pCamPosition);
        this.vCamDirection = new Vector3D(vCamDirection);
        this.vCamUp        = new Vector3D(vCamUp);
        this.vCamRight     = new Vector3D(vCamRight);
        this.camPitch = camPitch;
        this.camYaw = camYaw;
        this.camRoll = camRoll;
        this.projectionMatrix = new Matrix(projectionMatrix);
        this.near = near;
        this.far = far;
        this.fov = fov;
    }

    public void updateProjectionMatrix(Engine3D engine3D) {
        if (engine3D.getWidth() != engine3D.getWindowWidth() || engine3D.getHeight() != engine3D.getWindowWHeight()) {
            engine3D.setWindowWidth(engine3D.getWidth());
            engine3D.setWindowWHeight(engine3D.getHeight());

            this.projectionMatrix = Matrix.createProjectionMatrix(this.far, this.near, this.fov, engine3D.getWindowWidth(), engine3D.getWindowWHeight());
        }
    }

    public void updateCamReferential() {
        Vector3D[] localAxes = {new Vector3D(0,0,1),
                                new Vector3D(0,1,0),
                                new Vector3D(1,0,0)};

        Matrix matCameraRotYaw = Matrix.createRotationAroundAxis(camYaw,localAxes[1]);
        matCameraRotYaw.rotateBasisInPlace(localAxes);

        camPitch = Math.max(-89.0*Math.PI/180.0, Math.min(89.0*Math.PI/180.0, camPitch));
        Matrix matCameraRotPitch = Matrix.createRotationAroundAxis(camPitch,localAxes[2]);
        matCameraRotPitch.rotateBasisInPlace(localAxes);

        Matrix matCameraRotRoll = Matrix.createRotationAroundAxis(camRoll,localAxes[0]);
        matCameraRotRoll.rotateBasisInPlace(localAxes);

        vCamDirection = localAxes[0];
        vCamUp        = localAxes[1];
        vCamRight     = localAxes[2];
    }

    public void translateCameraInPlace(Vector3D translationDirection, int sens, double translationSpeed, double deltaFrameTime) {
        translationDirection.scaleInPlace(sens * deltaFrameTime * translationSpeed);
        this.pCamPosition.translateInPlace(translationDirection);
    }

    public double rotateCameraInPlace(double currentRotationValue, double rotationSpeed, double deltaFrameTime) {
        return currentRotationValue + rotationSpeed * deltaFrameTime;
    }

    public Vertex3D getpCamPosition() {
        return pCamPosition;
    }


    public Vector3D getvCamDirection() {
        return vCamDirection;
    }

    public Vector3D getvCamUp() {
        return vCamUp;
    }

    public Vector3D getvCamRight() {
        return vCamRight;
    }

    public double getCamPitch() {
        return camPitch;
    }

    public void setCamPitch(double camPitch) {
        this.camPitch = camPitch;
    }

    public double getCamYaw() {
        return camYaw;
    }

    public void setCamYaw(double camYaw) {
        this.camYaw = camYaw;
    }

    public double getCamRoll() {
        return camRoll;
    }

    public void setCamRoll(double camRoll) {
        this.camRoll = camRoll;
    }

    public Matrix getProjectionMatrix() {
        return new Matrix(projectionMatrix);
    }

    public double getdTranslationCameraSpeed() {
        return dTranslationCameraSpeed;
    }

    public double getdRotationCameraSpeed() {
        return dRotationCameraSpeed;
    }
}


