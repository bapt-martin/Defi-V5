import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel implements KeyListener {
    private final Mesh mesh;

    private int windowWidth;
    private int windowHeight;

    private double theta;

    private Vertex3D vertCamPosition;
    private Vertex3D vertNormCamDirection;
    private Vertex3D vertNormCamUp;
    private Vertex3D vertNormCamRight;

    private double cameraPitch;
    private double cameraYaw;
    private double cameraRoll;

    private final boolean[] keysPressed = new boolean[256];
    private final double translationCameraSpeed = 1;
    private final double rotationCameraSpeed = 0.05
            ;


    private Timer timeLoop;
    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private double deltaTime = System.nanoTime();
    private double elapsedTime;


    public Engine3D(int widthInit, int heightInit) {
        this.windowWidth = widthInit;
        this.windowHeight = heightInit;
        this.theta = 0;
        this.vertCamPosition = new Vertex3D(0,0,1);
        this.vertNormCamDirection = new Vertex3D();

        this.mesh = new Mesh();
        setBackground(Color.BLACK);

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        // .OBJ file reading + construction of the 3D triangle to render
        Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\obj model\\axis.obj"),this.mesh);
        this.mesh.triConstruct();

        // Projection matrix coefficient value(a require)
        double fNear = 0.1;
        double fFar = 1000;
        double fFov = 90;

        // Projection matrix coefficient definition initialisation
        this.mesh.setMatProj(Matrix.matrixCreateProjection4x4(fNear,fFar,fFov,heightInit,widthInit));
//        printMatrix(this.mesh.getMatProj());

        this.timeLoop = new Timer(16, e -> repaint());
        this.timeLoop.start();
    }

//START OF THE PIPELINE
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Real time aspect actualisation
        if (getWidth() != windowWidth || getHeight() != windowHeight) {
            this.windowWidth = getWidth();
            this.windowHeight = getHeight();

            double fNear = 0.1;
            double fFar = 1000;
            double fFov = 90;

            mesh.setMatProj(Matrix.matrixCreateProjection4x4(fNear, fFar, fFov, windowHeight, windowWidth));
        }


        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = System.nanoTime();

        System.out.println("Elapsed time : " + elapsedTime + ", delta Time : " + deltaTime);

        // Actualisation of theta
//        this.theta += 0.05;
//        System.out.println(this.theta);

        // ROTATION OF THE OBJECT IN THE WORLD
        // Rotation matrices Z-axis
        Matrix matRotZ = Matrix.matrixCreateRotationZ4x4(this.theta + Math.PI);

        // Rotation matrices Y-axis
        Matrix matRotY = Matrix.matrixCreateRotationY4x4(this.theta * 0.5);

        // Rotation matrices X-axis
        Matrix matRotX = Matrix.matrixCreateRotationX4x4(this.theta * 1.5);

        // TOTAL ROTATION
        Matrix matRotationTot = Matrix.matrixMultiplication(matRotZ, Matrix.matrixMultiplication(matRotX,matRotY));


        // Z-Axis Offset
        Matrix matTranslation = Matrix.matrixCreateTranslation4x4(0,0,16);


        // COMBINATION ROTATION + TRANSLATION
        Matrix matWorld = Matrix.matrixMultiplication(matRotationTot,matTranslation);

//        this.vertLookDir.setX(0); normcamdirection
//        this.vertLookDir.setY(0);
//        this.vertLookDir.setZ(1);




//        Vertex3D vertTarget = Vertex3D.vertexAddition(this.vertCamera,this.vertLookDir);

        handleKeyPress();

        Vertex3D vertTarget = new Vertex3D(0,0,1);
        Vertex3D vertUp     = new Vertex3D(0,1,0);
        Vertex3D vertRight  = new Vertex3D(1,0,0);

        Matrix matCameraRotYaw   = Matrix.matrixCreateRotationAroundAxis4x4(cameraYaw,vertUp);

        Vertex3D vertTargetY = Vertex3D.vertexMatrixMultiplication(vertTarget,matCameraRotYaw);
        Vertex3D vertRightY  = Vertex3D.vertexMatrixMultiplication(vertRight,matCameraRotYaw);
        Vertex3D vertUpY     = Vertex3D.vertexMatrixMultiplication(vertUp,matCameraRotYaw);

        Matrix matCameraRotPitch = Matrix.matrixCreateRotationAroundAxis4x4(cameraPitch,vertRightY);

        Vertex3D vertTargetYP = Vertex3D.vertexMatrixMultiplication(vertTargetY,matCameraRotPitch);
        Vertex3D vertRightYP  = Vertex3D.vertexMatrixMultiplication(vertRightY,matCameraRotPitch);
        Vertex3D vertUpYP     = Vertex3D.vertexMatrixMultiplication(vertUpY,matCameraRotPitch);

        Matrix matCameraRotRoll  = Matrix.matrixCreateRotationAroundAxis4x4(cameraRoll,vertTargetYP);

        Vertex3D vertTargetYPR = Vertex3D.vertexMatrixMultiplication(vertTargetYP,matCameraRotRoll);
        Vertex3D vertRightYPR  = Vertex3D.vertexMatrixMultiplication(vertRightYP,matCameraRotRoll);
        Vertex3D vertUpYPR     = Vertex3D.vertexMatrixMultiplication(vertUpYP,matCameraRotRoll);


        vertNormCamDirection = vertTargetYPR;
        vertNormCamUp        = vertUpYPR;
        vertNormCamRight     = vertRightYPR;

        Vertex3D vertTranslatedTargetYPR = Vertex3D.vertexAddition(vertCamPosition, vertNormCamDirection);

        // Creation of the camera matrix
        Matrix matCameraWorld = Matrix.matCreateCamReferentiel(this.vertCamPosition,vertTranslatedTargetYPR,vertNormCamUp);


        // View matrix for the camera
        Matrix matWorldCamera = Matrix.matrixQuickInverse(matCameraWorld);


        // Triangle projection and drawing
        List<Triangle> trisToRaster = new ArrayList<Triangle>();
        for (Triangle triangleToProject : mesh.getTris()) {
            Triangle triTransformed = new Triangle();
            Triangle triProjected = new Triangle();
            Triangle triViewed = new Triangle();

            // Z-axis, Y-axis and X-axis Rotation
            triTransformed.getVertices()[0] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[0],matWorld);
            triTransformed.getVertices()[1] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[1],matWorld);
            triTransformed.getVertices()[2] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[2],matWorld);

            // Line creation for determining the normal
            Vertex3D line1 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[1],triTransformed.getVertices()[0]);
            Vertex3D line2 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[2],triTransformed.getVertices()[0]);

            Vertex3D normal = Vertex3D.crossProduct(line1,line2);
            normal.vertexNormalisation();

            // Casting the ray of the camera
            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0], vertCamPosition);

            // Checking if the ray of the camera is in sight of the normale
            if (Vertex3D.dotProduct(normal, vCameraRay)< 0) {

                Vertex3D lightDirection = new Vertex3D(-1,-1,-1); // Pseudo definition of the light source
                lightDirection.vertexNormalisation();

                double dpLightNorm = Vertex3D.dotProduct(normal,lightDirection);
                Color colorTri = Triangle.grayScale(dpLightNorm);

                // Definition of the greyscale value for the triangle regarding its orientation
                triTransformed.setColor(colorTri);

                // Convert World Space in the Worldview of the camera
                triViewed.getVertices()[0] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[0],matWorldCamera);
                triViewed.getVertices()[1] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[1],matWorldCamera);
                triViewed.getVertices()[2] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[2],matWorldCamera);
                triViewed.setColor(triTransformed.getColor()); // Color transfer

                // Projecting 3D into 2D
                triProjected.getVertices()[0] = Vertex3D.vertexMatrixMultiplication(triViewed.getVertices()[0], this.mesh.getMatProj());
                triProjected.getVertices()[1] = Vertex3D.vertexMatrixMultiplication(triViewed.getVertices()[1], this.mesh.getMatProj());
                triProjected.getVertices()[2] = Vertex3D.vertexMatrixMultiplication(triViewed.getVertices()[2], this.mesh.getMatProj());
                triProjected.setColor(triViewed.getColor()); // Color transfer

                // Normalization of the vertex
                triProjected.getVertices()[0] = Vertex3D.vertexDivision(triProjected.getVertices()[0].getW(),triProjected.getVertices()[0]);
                triProjected.getVertices()[1] = Vertex3D.vertexDivision(triProjected.getVertices()[1].getW(),triProjected.getVertices()[1]);
                triProjected.getVertices()[2] = Vertex3D.vertexDivision(triProjected.getVertices()[2].getW(),triProjected.getVertices()[2]);

                // Offset into visible normalised space
                Vertex3D vOffsetView = new Vertex3D(1,1,0);

                triProjected.getVertices()[0] = Vertex3D.vertexAddition(triProjected.getVertices()[0], vOffsetView );
                triProjected.getVertices()[1] = Vertex3D.vertexAddition(triProjected.getVertices()[1], vOffsetView );
                triProjected.getVertices()[2] = Vertex3D.vertexAddition(triProjected.getVertices()[2], vOffsetView );

                // Scaling to screen dimension
                triProjected.getVertices()[0].setX(triProjected.getVertices()[0].getX() * 0.5 * windowHeight);
                triProjected.getVertices()[0].setY(triProjected.getVertices()[0].getY() * 0.5 * windowWidth);

                triProjected.getVertices()[1].setX(triProjected.getVertices()[1].getX() * 0.5 * windowHeight);
                triProjected.getVertices()[1].setY(triProjected.getVertices()[1].getY() * 0.5 * windowWidth);

                triProjected.getVertices()[2].setX(triProjected.getVertices()[2].getX() * 0.5 * windowHeight);
                triProjected.getVertices()[2].setY(triProjected.getVertices()[2].getY() * 0.5 * windowWidth);

                // Save for later rasterization
                trisToRaster.add(triProjected);
            }
        }

        // Painter's algorithm
        trisToRaster.sort((t1, t2) -> {
            double meanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double meanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(meanZ2,meanZ1);
        });

        for (Triangle triBeingDrawn : trisToRaster) {

            // Getting back the coordinate to draw the 2D triangle
            int[] xs = new int[3];
            int[] ys = new int[3];

            for (int i = 0; i < 3; i++) {
                xs[i] = (int) Math.round(triBeingDrawn.getVertices()[i].getX());
                ys[i] = (int) Math.round(triBeingDrawn.getVertices()[i].getY());
            }

            g.setColor(triBeingDrawn.getColor()); // Setting the correct color

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillPolygon(xs, ys, 3); // Rasterization pas encore faites
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed[e.getKeyCode()] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void handleKeyPress() {

        // TRANSLATION
        // Q = Left
        if (keysPressed[KeyEvent.VK_Q]) {
            vertCamPosition = Vertex3D.vertexSubtraction(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamRight));
            //vertCamera = Vertex3D.vertexSubtraction(vertCamera, Vertex3D.vertexMultiplication(translationCameraSpeed, vertRight));
        }

        // D = Right
        if (keysPressed[KeyEvent.VK_D]) {
            vertCamPosition = Vertex3D.vertexAddition(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamRight));
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keysPressed[KeyEvent.VK_SHIFT]) {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                vertCamPosition = Vertex3D.vertexAddition(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamUp));
            }
        } else {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                vertCamPosition = Vertex3D.vertexSubtraction(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamUp));
            }
        }

        // Z = Forward
        if (keysPressed[KeyEvent.VK_Z]) {
            vertCamPosition = Vertex3D.vertexAddition(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamDirection));
        }
        // S = Behind
        if (keysPressed[KeyEvent.VK_S]) {
            vertCamPosition = Vertex3D.vertexSubtraction(vertCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertNormCamDirection));
        }

        // ROTATION
        // UP = Trigo X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_UP]) {
            cameraPitch -= rotationCameraSpeed;
        }

        // DOWN = Horaire X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_DOWN]) {
            cameraPitch += rotationCameraSpeed;
        }

        // A = Trigo Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_A]) {
            cameraYaw += rotationCameraSpeed;
        }

        // D = Horaire Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_E]) {
            cameraYaw -= rotationCameraSpeed;
        }

        // LEFT = Trigo Z-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_LEFT]) {
            cameraRoll += rotationCameraSpeed;
        }

        // RIGHT = Horaire Z-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_RIGHT]) {
            cameraRoll -= rotationCameraSpeed;
        }
    }

    public Timer getTimeLoop() {
        return timeLoop;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("3D Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 800;
        int height = 600;
        window.setSize(width, height);

        Engine3D engine3D = new Engine3D(width, height);
        window.add(engine3D);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}
