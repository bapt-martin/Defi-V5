import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private Mesh mesh;

    private int winWidth;
    private int winHeight;

    private double theta;

    private Vertex3D pointCamPosition;
    private Vertex3D vertCamDirection;
    private Vertex3D vertCamUp;
    private Vertex3D vertCamRight;

    private double cameraPitch;
    private double cameraYaw;
    private double cameraRoll;

    private Camera camera;

    private final boolean[] keysPressed = new boolean[256];
    private final double translationCameraSpeed = 0.1;
    private final double rotationCameraSpeed = 0.5;

    private Vertex3D winLastMousePosition;
    private final double mouseSensibility = 0.01;
    private boolean firstMouseMove = true;

    private final Timer timeLoop;
    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private double deltaTime = 0;
    private double elapsedTime;


    public Engine3D(int widthInit, int heightInit) {
        this.winWidth = widthInit;
        this.winHeight = heightInit;

        this.camera = new Camera();

        this.theta = 0;
        this.pointCamPosition = new Vertex3D(0,0,1);
        this.vertCamDirection = new Vertex3D(0);

        this.mesh = new Mesh();
        setBackground(Color.BLACK);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.winLastMousePosition = new Vertex3D(0,0,0);

        // .OBJ file reading + construction of the 3D triangle to render
        Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\obj model\\axis.obj"),this.mesh);
        this.mesh.triConstruct();

        // Projection matrix coefficient value(a require)
        double fNear = 0.1;
        double fFar = 1000;
        double fFov = 90;

        // Projection matrix coefficient definition initialisation
        this.mesh.setMatProj(Matrix.matCreateProjection4x4(fNear,fFar,fFov, widthInit, heightInit));

        this.timeLoop = new Timer(1, e -> repaint());
    }

    @Override
    public void addNotify() {
        super.addNotify();

        SwingUtilities.invokeLater(() -> {
            centerMouse();
            timeLoop.start();
            requestFocusInWindow();
        });
    }

    public void centerMouse() {
        try {
            Robot robot = new Robot();
            Point winPosition = this.getLocationOnScreen();
            Point winCenter = new Point(winWidth /2, winHeight /2);

            int winPanelCenterX = winPosition.x + winCenter.x;
            int winPanelCenterY = winPosition.y + winCenter.y;

            robot.mouseMove(winPanelCenterX, winPanelCenterY); // Robot move relatively of the whole screen
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        handleMouseMoving(e);
    }

    private void handleMouseMoving(MouseEvent e) {
        Point winPanelCenter = new Point(winWidth /2, winHeight /2);
        Point winMousePos = e.getPoint();

        if(firstMouseMove) {
            // Skip the first delta to avoid initial Jump
            winLastMousePosition.setX(winPanelCenter.x);
            winLastMousePosition.setY(winPanelCenter.y);
            firstMouseMove = false;
            return;
        }

        int dx = winMousePos.x - (int) winLastMousePosition.getX();
        int dy = winMousePos.y - (int) winLastMousePosition.getY();

        cameraPitch += rotationCameraSpeed * -dy * mouseSensibility;
        cameraYaw   += rotationCameraSpeed * dx * mouseSensibility;

        // Update last position
        winLastMousePosition.setX(winPanelCenter.x);
        winLastMousePosition.setY(winPanelCenter.y);

        try {
            centerMouse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragggggged : " + e.getX() + ", " + e.getY());
        requestFocusInWindow();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clic souris : " + e.getX() + ", " + e.getY());
        requestFocusInWindow();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println("PRESSEEEED  " +e.getX() +" : "+ e.getY());
        requestFocusInWindow();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("REALEASSSSEED  " +e.getX() +" : "+ e.getY());
        requestFocusInWindow();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        System.out.println("zrreg" + e.getX() +" : "+ e.getY());
        requestFocusInWindow();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        System.out.println(e.getX() +" : "+ e.getY());
        requestFocusInWindow();
    }

//START OF THE PIPELINE
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Real time aspect actualisation
        Camera.matProjectionActualisation(g, this);

        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = now;

        // Actualisation of theta
//        this.theta += 0.05;
//        System.out.println(this.theta);

        // ROTATION OF THE OBJECT IN THE WORLD (adding a rotation mat per object in the futur)
        // Rotation matrices Z-axis
        Matrix matRotZ = Matrix.matCreateRotationZ4x4(this.theta);

        // Rotation matrices Y-axis
        Matrix matRotY = Matrix.matCreateRotationY4x4(this.theta * 0.5);

        // Rotation matrices X-axis
        Matrix matRotX = Matrix.matCreateRotationX4x4(this.theta * 1.5);

        // TOTAL ROTATION
        Matrix matRotationTot = Matrix.matMultiplication(matRotZ, Matrix.matMultiplication(matRotX,matRotY));

        // Z-Axis Offset
        Matrix matTranslation = Matrix.matCreateTranslation4x4(0,0,16);

        // COMBINATION ROTATION + TRANSLATION
        Matrix matWorld = Matrix.matMultiplication(matRotationTot,matTranslation);

        handleKeyPress();
        //camera.camActualisation();

        Vertex3D vertTarget = new Vertex3D(0,0,1);
        Vertex3D vertUp     = new Vertex3D(0,1,0);
        Vertex3D vertRight  = new Vertex3D(1,0,0);


        // Rotation arround local Y-axis
        Matrix matCameraRotYaw   = Matrix.matCreateRotationAroundAxis4x4(cameraYaw,vertUp);

        Vertex3D vertTargetY = Vertex3D.vertexMatrixMultiplication(vertTarget,matCameraRotYaw);
        Vertex3D vertRightY  = Vertex3D.vertexMatrixMultiplication(vertRight,matCameraRotYaw);
        Vertex3D vertUpY     = Vertex3D.vertexMatrixMultiplication(vertUp,matCameraRotYaw);

        vertTargetY.vertexNormalisation();
        vertRightY.vertexNormalisation();
        vertUpY.vertexNormalisation();

        // Rotation arround local X-axis
        Matrix matCameraRotPitch = Matrix.matCreateRotationAroundAxis4x4(cameraPitch,vertRightY);

        Vertex3D vertTargetYP = Vertex3D.vertexMatrixMultiplication(vertTargetY,matCameraRotPitch);
        Vertex3D vertRightYP  = Vertex3D.vertexMatrixMultiplication(vertRightY,matCameraRotPitch);
        Vertex3D vertUpYP     = Vertex3D.vertexMatrixMultiplication(vertUpY,matCameraRotPitch);

        vertTargetY.vertexNormalisation();
        vertRightY.vertexNormalisation();
        vertUpY.vertexNormalisation();

        // Rotation arround local Z-axis
        Matrix matCameraRotRoll  = Matrix.matCreateRotationAroundAxis4x4(cameraRoll,vertTargetYP);

        Vertex3D vertTargetYPR = Vertex3D.vertexMatrixMultiplication(vertTargetYP,matCameraRotRoll);
        Vertex3D vertRightYPR  = Vertex3D.vertexMatrixMultiplication(vertRightYP,matCameraRotRoll);
        Vertex3D vertUpYPR     = Vertex3D.vertexMatrixMultiplication(vertUpYP,matCameraRotRoll);

        vertCamDirection = vertTargetYPR;
        vertCamUp = vertUpYPR;
        vertCamRight = vertRightYPR;

//        camera.camUpdate();
//
//        vertCamDirection = camera.getVertCamDirection();
//        vertCamUp = camera.getVertCamUp();
//        vertCamRight = camera.getVertCamRight();

//        System.out.println("target "+vertTarget.toString() +" : " + vertTargetYPR.toString());
//        System.out.println("right  "+vertRight.toString()  +" : " + vertRightYPR.toString());
//        System.out.println("up     "+vertUp.toString()     +" : " + vertUpYPR.toString());

        // Creation of the camera matrix
        Matrix matCameraWorld = Matrix.matCreateCamReferentiel(this.pointCamPosition, vertCamDirection, vertCamUp);


        // View matrix for the camera
        Matrix matWorldCamera = Matrix.matQuickInverse(matCameraWorld);

        // Triangle projection and drawing
        List<Triangle> trisToRaster = new ArrayList<Triangle>();
        for (Triangle triangleToProject : mesh.getTris()) {
            Triangle triTransformed = new Triangle();
//            Triangle triProjected = new Triangle();
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
            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0], pointCamPosition);

            // Checking if the ray of the camera is in sight of the normale
            if (Vertex3D.dotProduct(normal, vCameraRay)< 0) {

                Vertex3D lightDirection = new Vertex3D(0,0,-1); // Pseudo definition of the light source
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

                Triangle[] clipped = new Triangle[2];
                clipped[0] = new Triangle();
                clipped[1] = new Triangle();

                 int nbClippedTris = Triangle.trisClippingPlane(new Vertex3D(0,0, 0.1), new Vertex3D(0,0,1), triViewed, clipped[0], clipped[1]);

                 for (int n = 0; n < nbClippedTris; n++) {
                     Triangle triProjected = new Triangle();

                     for (int ind = 0; ind < 3; ind++ ) {
                         // Projecting 3D into 2D
                         triProjected.getVertices()[ind] = Vertex3D.vertexMatrixMultiplication(clipped[n].getVertices()[ind], this.mesh.getMatProj());

                         // Normalization of the vertex
                         triProjected.getVertices()[ind] = Vertex3D.vertexDivision(triProjected.getVertices()[ind].getW(),triProjected.getVertices()[ind]);

//                         // X/Y Inverted so need to put them back???
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * -1);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * -1);

                         // Offset into visible normalised space
                         Vertex3D vOffsetView = new Vertex3D(1,1,0);
                         triProjected.getVertices()[ind] = Vertex3D.vertexAddition(triProjected.getVertices()[ind], vOffsetView);

                         // Scaling to screen dimension
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * 0.5 * winHeight);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * 0.5 * winWidth);

                         // Color transfer
                         triProjected.setColor(clipped[n].getColor());
                     }

                     // Save for later rasterization
                     trisToRaster.add(triProjected);
                }
            }
        }

        // Painter's algorithm
        trisToRaster.sort((t1, t2) -> {
            double meanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double meanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(meanZ2,meanZ1);
        });

        int nbTriPerFrame = 0;
        for (Triangle triToClip : trisToRaster) {
            List<Triangle> clippingQueue = new ArrayList<>();
            clippingQueue.add(triToClip);

            for (int p = 0; p < 4; p++) {
                int nbTrisToAdd = 1;
                List<Triangle> futurTestToClip = new ArrayList<>();
                for(Triangle test : clippingQueue) {
                    Triangle[] clipped = new Triangle[2];
                    clipped[0] = new Triangle();
                    clipped[1] = new Triangle();

                    switch (p) {
                        case 0:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(0, 0, 0), new Vertex3D(0, 1, 0), test, clipped[0], clipped[1]);
                            break;
                        case 1:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(0, winHeight - 1, 0), new Vertex3D(0, -1, 0), test, clipped[0], clipped[1]);
                            break;
                        case 2:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(0, 0, 0), new Vertex3D(1, 0, 0), test, clipped[0], clipped[1]);
                            break;
                        case 3:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(winWidth - 1, 0, 0), new Vertex3D(-1, 0, 0), test, clipped[0], clipped[1]);
                            break;
                    }

                    for (int w = 0; w < nbTrisToAdd; w++) {
                        Triangle temp = new Triangle();
                        temp.CopyTriangle(clipped[w]);
                        futurTestToClip.add(temp);
                    }
                }
                clippingQueue = futurTestToClip;

            }

            for (Triangle triToDraw : clippingQueue) {
                // Getting back the coordinate to draw the 2D triangle
                int[] xs = new int[3];
                int[] ys = new int[3];

                for (int i = 0; i < 3; i++) {
                    xs[i] = (int) Math.round(triToDraw.getVertices()[i].getX());
                    ys[i] = (int) Math.round(triToDraw.getVertices()[i].getY());
                }

                g.setColor(triToDraw.getColor()); // Setting the correct color

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.fillPolygon(xs, ys, 3);
                g2.setColor(Color.BLACK);
                g2.drawPolygon(xs, ys, 3);
//                System.out.println(nbTriPerFrame++);
            }
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
            pointCamPosition = Vertex3D.vertexAddition(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamRight));
            //vertCamera = Vertex3D.vertexSubtraction(vertCamera, Vertex3D.vertexMultiplication(translationCameraSpeed, vertRight));
        }

        // D = Right
        if (keysPressed[KeyEvent.VK_D]) {
            pointCamPosition = Vertex3D.vertexSubtraction(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamRight));
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keysPressed[KeyEvent.VK_SHIFT]) {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                pointCamPosition = Vertex3D.vertexSubtraction(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamUp));
            }
        } else {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                pointCamPosition = Vertex3D.vertexAddition(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamUp));
            }
        }

        // Z = Forward
        if (keysPressed[KeyEvent.VK_Z]) {
            pointCamPosition = Vertex3D.vertexAddition(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamDirection));
        }
        // S = Behind
        if (keysPressed[KeyEvent.VK_S]) {
            pointCamPosition = Vertex3D.vertexSubtraction(pointCamPosition, Vertex3D.vertexMultiplication(translationCameraSpeed, vertCamDirection));
        }

        // ROTATION
        // UP = Trigo X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_UP]) {
            cameraPitch += rotationCameraSpeed * deltaTime;
        }

        // DOWN = Horaire X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_DOWN]) {
            cameraPitch -= rotationCameraSpeed * deltaTime;
        }

        // RIGHT = Trigo Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_RIGHT]) {
            cameraYaw += rotationCameraSpeed * deltaTime;
        }

        // LEFT = Horaire Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_LEFT]) {
            cameraYaw -= rotationCameraSpeed * deltaTime;
        }

        // A = Trigo Z-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_A]) {
            cameraRoll += rotationCameraSpeed * deltaTime;
        }

        // E = Horaire Y-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_E]) {
            cameraRoll -= rotationCameraSpeed * deltaTime;
        }
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

    public Timer getTimeLoop() {
        return timeLoop;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public int getWinWidth() {
        return winWidth;
    }

    public int getWinHeight() {
        return winHeight;
    }

    public void setWinWidth(int winWidth) {
        this.winWidth = winWidth;
    }

    public void setWinHeight(int winHeight) {
        this.winHeight = winHeight;
    }

    public double getTheta() {
        return theta;
    }

    public Vertex3D getPointCamPosition() {
        return pointCamPosition;
    }

    public Vertex3D getVertCamDirection() {
        return vertCamDirection;
    }

    public Vertex3D getVertCamUp() {
        return vertCamUp;
    }

    public Vertex3D getVertCamRight() {
        return vertCamRight;
    }

    public double getCameraPitch() {
        return cameraPitch;
    }

    public double getCameraYaw() {
        return cameraYaw;
    }

    public double getCameraRoll() {
        return cameraRoll;
    }

    public boolean[] getKeysPressed() {
        return keysPressed;
    }

    public double getTranslationCameraSpeed() {
        return translationCameraSpeed;
    }

    public double getRotationCameraSpeed() {
        return rotationCameraSpeed;
    }

    public Vertex3D getWinLastMousePosition() {
        return winLastMousePosition;
    }

    public double getMouseSensibility() {
        return mouseSensibility;
    }

    public boolean isFirstMouseMove() {
        return firstMouseMove;
    }

    public long getStartFrameTime() {
        return startFrameTime;
    }

    public long getLastFrameTime() {
        return lastFrameTime;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }
}
