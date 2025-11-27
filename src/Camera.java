import java.awt.*;

public class Camera {
    private Vertex3D pointCamPosition;
    private Vertex3D vertCamDirection;
    private Vertex3D vertCamUp;
    private Vertex3D vertCamRight;

    private double camPitch;
    private double camYaw;
    private double camRoll;


    public Camera() {
        this.pointCamPosition = new Vertex3D(0, 0, 1,0);
        this.vertCamDirection = new Vertex3D(0, 0, 1,0);
        this.vertCamUp = new Vertex3D(0, 1, 0,0);
        this.vertCamRight = new Vertex3D(1, 0, 0,0);
        this.camPitch = 0;
        this.camYaw = 0;
        this.camRoll = 0;

    }

    public Camera(Vertex3D vertCamPosition, double camPitch, double camYaw, double camRoll, Vertex3D vertNormCamDirection) {
        this.pointCamPosition = vertCamPosition;
        this.camPitch = camPitch;
        this.camYaw = camYaw;
        this.camRoll = camRoll;
        this.vertCamDirection = vertNormCamDirection;
    }

    public void camUpdate() {
        // Rotation arround local Y-axis
        Matrix matCameraRotYaw = Matrix.matCreateRotationAroundAxis4x4(camYaw,vertCamUp);

        Vertex3D vertTargetY = Vertex3D.vertexMatrixMultiplication(vertCamDirection,matCameraRotYaw);
        Vertex3D vertRightY  = Vertex3D.vertexMatrixMultiplication(vertCamRight,matCameraRotYaw);
        Vertex3D vertUpY     = Vertex3D.vertexMatrixMultiplication(vertCamUp,matCameraRotYaw);

        vertTargetY.vertexNormalisation();
        vertRightY.vertexNormalisation();
        vertUpY.vertexNormalisation();

        // Rotation arround local X-axis
        Matrix matCameraRotPitch = Matrix.matCreateRotationAroundAxis4x4(camPitch,vertRightY);

        Vertex3D vertTargetYP = Vertex3D.vertexMatrixMultiplication(vertTargetY,matCameraRotPitch);
        Vertex3D vertRightYP  = Vertex3D.vertexMatrixMultiplication(vertRightY,matCameraRotPitch);
        Vertex3D vertUpYP     = Vertex3D.vertexMatrixMultiplication(vertUpY,matCameraRotPitch);

        vertTargetYP.vertexNormalisation();
        vertRightYP.vertexNormalisation();
        vertUpYP.vertexNormalisation();

        // Rotation arround local Z-axis
        Matrix matCameraRotRoll  = Matrix.matCreateRotationAroundAxis4x4(camRoll,vertTargetYP);

        Vertex3D vertTargetYPR = Vertex3D.vertexMatrixMultiplication(vertTargetYP,matCameraRotRoll);
        Vertex3D vertRightYPR  = Vertex3D.vertexMatrixMultiplication(vertRightYP,matCameraRotRoll);
        Vertex3D vertUpYPR     = Vertex3D.vertexMatrixMultiplication(vertUpYP,matCameraRotRoll);

        vertTargetYPR.vertexNormalisation();
        vertRightYPR.vertexNormalisation();
        vertUpYPR.vertexNormalisation();

        vertCamDirection = vertTargetYPR;
        vertCamUp = vertUpYPR;
        vertCamRight = vertRightYPR;
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
}


