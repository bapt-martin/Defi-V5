import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel implements KeyListener {
    private final Mesh mesh;

    private int prevWidth;
    private int prevHeight;

    private double theta;

    private final Vertex3D vertCamera;
    private final Vertex3D vertLookDir;
    private double cameraYaw;

    private final boolean[] keysPressed = new boolean[256];
    private final double translationCameraSpeed = 1;
    private final double rotationCameraSpeed = 1;


    private Timer timeLoop;
    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private double deltaTime = System.nanoTime();
    private double elapsedTime;


    public Engine3D(int widthInit, int heightInit) {
        this.prevWidth = widthInit;
        this.prevHeight = heightInit;
        this.theta = 0;
        this.vertCamera = new Vertex3D(0,0,1);
        this.vertLookDir = new Vertex3D();

        this.mesh = new Mesh();
        setBackground(Color.BLACK);

        this.timeLoop = new Timer(16, e -> repaint());
        this.timeLoop.start();

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
    }

    public void printMatrix(Matrix m) {
        for (int i = 0; i < 4; i++) {
            System.out.printf("| %8.4f %8.4f %8.4f %8.4f |\n",
                    m.getMatrix()[i][0], m.getMatrix()[i][1], m.getMatrix()[i][2], m.getMatrix()[i][3]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        handleKeyPress();

        int currentWidth = getWidth();
        int currentHeight = getHeight();
        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = System.nanoTime();

        System.out.println("Elapsed time : " + elapsedTime + ", delta Time : " + deltaTime);

        if (currentWidth != prevWidth || currentHeight != prevHeight) {
            this.prevWidth = currentWidth;
            this.prevHeight = currentHeight;

            double fNear = 0.1;
            double fFar = 1000;
            double fFov = 90;

            mesh.setMatProj(Matrix.matrixCreateProjection4x4(fNear, fFar, fFov, prevHeight, prevWidth));

            prevWidth = currentWidth;
            prevHeight = currentHeight;
        }

        // Actualisation of theta
//        this.theta += 0.05;
//        System.out.println(this.theta);

        // Rotation matrices Z-axis
        Matrix matRotZ = Matrix.matrixCreateRotationZ4x4(this.theta + Math.PI);

        // Rotation matrices Y-axis
        Matrix matRotY = Matrix.matrixCreateRotationY4x4(this.theta * 0.5);

        // Rotation matrices X-axis
        Matrix matRotX = Matrix.matrixCreateRotationX4x4(this.theta * 1.5);

        // Z-Axis Offset
        Matrix matTrans = Matrix.matrixMultiplication(matRotZ, Matrix.matrixMultiplication(matRotX,matRotY));
        Matrix matWorld = Matrix.matrixMultiplication(matTrans,Matrix.matrixCreateTranslation4x4(0,0,16));

        this.vertLookDir.setX(0);
        this.vertLookDir.setY(0);
        this.vertLookDir.setZ(1);

        Vertex3D vertUp = new Vertex3D(0,1,0);
        Vertex3D vertTarget = Vertex3D.vertexAddition(this.vertCamera,this.vertLookDir);

        Matrix matCamera = Matrix.matrixCreatePointAt(this.vertCamera,vertTarget,vertUp);

        // View matrix for the camera
        Matrix matView = Matrix.matrixQuickInverse(matCamera);

        List<Triangle> trisToRaster = new ArrayList<Triangle>();
        // Triangle projection and drawing
        for (Triangle triangleToProject : mesh.getTris()) {
            Triangle triTransformed = new Triangle();
            Triangle triProjected = new Triangle();
            Triangle triViewed = new Triangle();

            // Z-axis and X-axis Rotation
            triangleToProject.getVertices()[0].vertex_Matrix_Multiplication(triTransformed.getVertices()[0],matWorld);
            triangleToProject.getVertices()[1].vertex_Matrix_Multiplication(triTransformed.getVertices()[1],matWorld);
            triangleToProject.getVertices()[2].vertex_Matrix_Multiplication(triTransformed.getVertices()[2],matWorld);

            // Line creation for determining the normal
            Vertex3D line1 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[1],triTransformed.getVertices()[0]);
            Vertex3D line2 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[2],triTransformed.getVertices()[0]);

            Vertex3D normal = Vertex3D.crossProduct(line1,line2);
            normal.vertexNormalisation();

            // Casting the ray of the camera
            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0], vertCamera);

            // Checking if the ray of the camera is in sight of the normale
            if (Vertex3D.dotProduct(normal, vCameraRay)< 0) {

                Vertex3D lighDirection = new Vertex3D(0,0,-1); // Pseudo definition of the light source
                lighDirection.vertexNormalisation();

                double dpLightNorm = Vertex3D.dotProduct(normal,lighDirection);
                Color colorTri = Triangle.grayScale(dpLightNorm);

                triTransformed.setColor(colorTri); // Definition of the greyscale value for the triangle regarding its orienttion

                // Convert World Space in the Worldview of the camera
                triTransformed.getVertices()[0].vertex_Matrix_Multiplication(triViewed.getVertices()[0],matView);
                triTransformed.getVertices()[1].vertex_Matrix_Multiplication(triViewed.getVertices()[1],matView);
                triTransformed.getVertices()[2].vertex_Matrix_Multiplication(triViewed.getVertices()[2],matView);
                triViewed.setColor(triTransformed.getColor()); // Color transfer

                // Projecting 3D into 2D
                triViewed.getVertices()[0].vertex_Matrix_Multiplication(triProjected.getVertices()[0], this.mesh.getMatProj());
                triViewed.getVertices()[1].vertex_Matrix_Multiplication(triProjected.getVertices()[1], this.mesh.getMatProj());
                triViewed.getVertices()[2].vertex_Matrix_Multiplication(triProjected.getVertices()[2], this.mesh.getMatProj());
                triProjected.setColor(triViewed.getColor()); // Color transfer

                // Normalization of the vertex
                triProjected.getVertices()[0].vertexDivision(triProjected.getVertices()[0].getW());
                triProjected.getVertices()[1].vertexDivision(triProjected.getVertices()[1].getW());
                triProjected.getVertices()[2].vertexDivision(triProjected.getVertices()[2].getW());

                // Offset into visible normalised space
                Vertex3D vOffsetView = new Vertex3D(1,1,0);

                triProjected.getVertices()[0] = Vertex3D.vertexAddition(triProjected.getVertices()[0], vOffsetView );
                triProjected.getVertices()[1] = Vertex3D.vertexAddition(triProjected.getVertices()[1], vOffsetView );
                triProjected.getVertices()[2] = Vertex3D.vertexAddition(triProjected.getVertices()[2], vOffsetView );

                // Scaling to screen dimension
                triProjected.getVertices()[0].setX(triProjected.getVertices()[0].getX() * 0.5 * getWidth());
                triProjected.getVertices()[0].setY(triProjected.getVertices()[0].getY() * 0.5 * getHeight());

                triProjected.getVertices()[1].setX(triProjected.getVertices()[1].getX() * 0.5 * getWidth());
                triProjected.getVertices()[1].setY(triProjected.getVertices()[1].getY() * 0.5 * getHeight());

                triProjected.getVertices()[2].setX(triProjected.getVertices()[2].getX() * 0.5 * getWidth());
                triProjected.getVertices()[2].setY(triProjected.getVertices()[2].getY() * 0.5 * getHeight());

                // Save for later rasterization
                trisToRaster.add(triProjected);
            }
        }

        trisToRaster.sort((t1, t2) -> {
            double meanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double meanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(meanZ2,meanZ1);
        }); // Painter's algorithm

        for (Triangle triBeingDrawn : trisToRaster) {

            int[] xs = new int[3];
            int[] ys = new int[3];

            for (int i = 0; i < 3; i++) {
                xs[i] = (int) Math.round(triBeingDrawn.getVertices()[i].getX());
                ys[i] = (int) Math.round(triBeingDrawn.getVertices()[i].getY());
            } // Getting back the coordinate to draw the 2D triangle

            g.setColor(triBeingDrawn.getColor()); // Setting the correct color

            Graphics2D g2 = (Graphics2D) g;
//            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        // Construire un vecteur droite à partir de LookDir
        //Vertex3D right = new Vertex3D(
//                vertLookDir.getZ(),
//                0,
//                -vertLookDir.getX()
//        );
//        right.vertexNormalisation();

        double cameraEvoX = vertCamera.getX();
        double cameraEvoY = vertCamera.getY();
        double cameraEvoZ = vertCamera.getZ();

        // Q = Left
        if (keysPressed[KeyEvent.VK_Q]) {
            cameraEvoX += -translationCameraSpeed;
        }

        // D = Right
        if (keysPressed[KeyEvent.VK_D]) {
            cameraEvoX += translationCameraSpeed;
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keysPressed[KeyEvent.VK_SHIFT]) {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                cameraEvoY += translationCameraSpeed;
            }
        } else {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                cameraEvoY += -translationCameraSpeed;
            }
        }

        Vertex3D vertForward = new Vertex3D(vertLookDir);
        vertForward.vertexMultiplication(rotationCameraSpeed);

        // Z = Forward
        if (keysPressed[KeyEvent.VK_Z]) {
            cameraEvoZ += translationCameraSpeed;
        }
        // S = Behind
        if (keysPressed[KeyEvent.VK_S]) {
            cameraEvoZ += -translationCameraSpeed;
        }

        // A = Trigo Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_A]) {
            cameraYaw += -rotationCameraSpeed;
        }

        if (keysPressed[KeyEvent.VK_E]) {
            cameraYaw += rotationCameraSpeed;
        }

        vertCamera.setX(cameraEvoX);
        vertCamera.setY(cameraEvoY);
        vertCamera.setZ(cameraEvoZ);
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
