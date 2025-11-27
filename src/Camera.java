import java.awt.*;

public class Camera {
    private Vertex3D pointCamPosition;
    private Vertex3D vertCamDirection;
    private Vertex3D vertCamUp;
    private Vertex3D vertCamRight;

    private double cameraPitch;
    private double cameraYaw;
    private double cameraRoll;


    public Camera() {
        this.pointCamPosition = new Vertex3D(0, 0, 1);
        this.vertCamDirection = new Vertex3D(0, 0, 1);
        this.vertCamUp = new Vertex3D(0, 1, 0);
        this.vertCamRight = new Vertex3D(1, 0, 0);
        this.cameraPitch = 0;
        this.cameraYaw = 0;
        this.cameraRoll = 0;

    }

    public Camera(Vertex3D vertCamPosition, double cameraPitch, double cameraYaw, double cameraRoll, Vertex3D vertNormCamDirection) {
        this.pointCamPosition = vertCamPosition;
        this.cameraPitch = cameraPitch;
        this.cameraYaw = cameraYaw;
        this.cameraRoll = cameraRoll;
        this.vertCamDirection = vertNormCamDirection;
    }

    public static void matProjectionActualisation(Graphics g, Engine3D engine3D) {
        if (engine3D.getWidth() != engine3D.getWinWidth() || engine3D.getHeight() != engine3D.getWinHeight()) {
            engine3D.setWinWidth(engine3D.getWidth());
            engine3D.setWinHeight(engine3D.getHeight());

            double fNear = 0.1;
            double fFar = 1000;
            double fFov = 90;

            engine3D.getMesh().setMatProj(Matrix.matCreateProjection4x4(fNear, fFar, fFov, engine3D.getWinWidth(), engine3D.getWinHeight()));
        }
    }

    public Vertex3D getPointCamPosition() {
        return pointCamPosition;
    }

    public void setPointCamPosition(Vertex3D pointCamPosition) {
        this.pointCamPosition = pointCamPosition;
    }

    public Vertex3D getVertCamDirection() {
        return vertCamDirection;
    }

    public void setVertCamDirection(Vertex3D vertCamDirection) {
        this.vertCamDirection = vertCamDirection;
    }

    public Vertex3D getVertCamUp() {
        return vertCamUp;
    }

    public void setVertCamUp(Vertex3D vertCamUp) {
        this.vertCamUp = vertCamUp;
    }

    public Vertex3D getVertCamRight() {
        return vertCamRight;
    }

    public void setVertCamRight(Vertex3D vertCamRight) {
        this.vertCamRight = vertCamRight;
    }

    public double getCameraPitch() {
        return cameraPitch;
    }

    public void setCameraPitch(double cameraPitch) {
        this.cameraPitch = cameraPitch;
    }

    public double getCameraYaw() {
        return cameraYaw;
    }

    public void setCameraYaw(double cameraYaw) {
        this.cameraYaw = cameraYaw;
    }

    public double getCameraRoll() {
        return cameraRoll;
    }

    public void setCameraRoll(double cameraRoll) {
        this.cameraRoll = cameraRoll;
    }
}


