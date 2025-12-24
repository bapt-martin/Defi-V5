package graphicEngine.renderer;

import graphicEngine.core.EngineContext;
import graphicEngine.math.tools.Matrix;
import graphicEngine.math.tools.Vector3D;
import graphicEngine.math.geometry.Vertex3D;

public class Camera {
    private EngineContext engineContext;

    private Vertex3D cameraPosition;
    private Vector3D cameraDirection;
    private Vector3D cameraUp;
    private Vector3D cameraRight;

    private double camPitch;
    private double camYaw;
    private double camRoll;

    private Matrix projectionMatrix;
    private double near;
    private double far;
    private double fov;

    private double dTranslationCameraSpeed = 10;
    private double dRotationCameraSpeed = 0.5;

    private double zoom;
    private double zoomFactor;

    public Camera(EngineContext engineContext) {
        this.cameraPosition = new Vertex3D(0, 0, 1);
        this.cameraDirection = new Vector3D(0, 0, 1);
        this.cameraUp = new Vector3D(0, 1, 0);
        this.cameraRight = new Vector3D(1, 0, 0);
        this.camPitch = 0;
        this.camYaw = 0;
        this.camRoll = 0;
        this.near = 0.1;
        this.far = 1000;
        this.fov = 90;
        this.zoom = 1;
        this.zoomFactor = 0.1;
        this.engineContext = engineContext;
    }

    public Camera(double near, double far, double fov, EngineContext engineContext) {
        this(engineContext);
        this.near = near;
        this.far = far;
        this.fov = fov;

    }

    public void updateWindowProjectionMatrix() {
        int currentWindowWidth = engineContext.getWindowWidth();
        int currentWindowHeight = engineContext.getWindowHeight();

        engineContext.updateWindowInformation();

        int pastWindowWidth = engineContext.getWindowWidth();
        int pastWindowHeight = engineContext.getWindowHeight();

        if (currentWindowWidth != pastWindowWidth || currentWindowHeight != pastWindowHeight) {
            if (currentWindowWidth != pastWindowWidth) {
                engineContext.setWindowWidth(currentWindowWidth);
            }

            if (currentWindowHeight != pastWindowHeight) {
                engineContext.setWindowHeight(currentWindowHeight);
            }

            this.updateProjectionMatrix();
        }
    }

    public void updateProjectionMatrix() {
        this.projectionMatrix = Matrix.createProjectionMatrix(this.far, this.near, this.fov, engineContext.getWindowWidth(), engineContext.getWindowHeight(), zoom);
    }

    public void updateCamReferentialMatrix() {
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

        cameraDirection = localAxes[0];
        cameraUp = localAxes[1];
        cameraRight = localAxes[2];
    }

    public void translateCameraInPlace(Vector3D translationDirection, int sens, double translationSpeed, double deltaFrameTime) {
        translationDirection.scaleInPlace(sens * deltaFrameTime * translationSpeed);
        this.cameraPosition.translateInPlace(translationDirection);
    }

    public double rotateCameraInPlace(double currentRotationValue, double rotationSpeed, double deltaFrameTime) {
        return currentRotationValue + rotationSpeed * deltaFrameTime;
    }

    public Vertex3D getCameraPosition() {
        return cameraPosition;
    }


    public Vector3D getCameraDirection() {
        return cameraDirection;
    }

    public Vector3D getCameraUp() {
        return cameraUp;
    }

    public Vector3D getCameraRight() {
        return cameraRight;
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

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double zoomFactor) {
        this.zoomFactor = zoomFactor;
    }
}


