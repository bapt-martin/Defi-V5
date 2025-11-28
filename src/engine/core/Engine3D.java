package engine.core;

import engine.input.InputManager;
import engine.io.Document;
import engine.math.Matrix;
import engine.math.Mesh;
import engine.math.Triangle;
import engine.math.Vertex3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private Mesh mesh;
    private Camera camera;
    private InputManager inputManager;

    private int winWidth;
    private int winHeight;

    private double theta;

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

    private int nbTriRender = 0;


    public Engine3D(int widthInit, int heightInit) {
        this.winWidth = widthInit;
        this.winHeight = heightInit;

        this.camera = new Camera(0.1,1000,90);

        this.theta = 0;

        this.mesh = new Mesh();
        setBackground(Color.BLACK);



        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        this.inputManager = new InputManager(this,this.camera);

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.winLastMousePosition = new Vertex3D(0,0,0);

        // .OBJ file reading + construction of the 3D triangle to render
        Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\obj model\\axis.obj"),this.mesh);
        this.mesh.triConstruct();

        // Projection matrix coefficient definition initialisation
        this.camera.matCreateCamProjection(winWidth, winHeight);

        this.timeLoop = new Timer(16, e -> repaint());
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
            Point winPanelCenter = new Point(winWidth /2, winHeight /2);

            int winPanelCenterX = winPosition.x + winPanelCenter.x;
            int winPanelCenterY = winPosition.y + winPanelCenter.y;

            robot.mouseMove(winPanelCenterX, winPanelCenterY); // Robot move relatively of the whole screen
            // Update last position
            winLastMousePosition.setX(winPanelCenter.x);
            winLastMousePosition.setY(winPanelCenter.y);
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

        camera.setdCamPitch(camera.getdCamPitch() + rotationCameraSpeed * -dy * mouseSensibility);
        camera.setdCamYaw(camera.getdCamYaw() + rotationCameraSpeed * dx * mouseSensibility);



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
        camera.matProjectionActualisation(g, this);

        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = now;
//        System.out.println(deltaTime);

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

        inputManager.handleKeyPress();
        camera.camUpdate();

//        System.out.println("target "+vertTarget.toString() +" : " + vertTargetYPR.toString());
//        System.out.println("right  "+vertRight.toString()  +" : " + vertRightYPR.toString());
//        System.out.println("up     "+vertUp.toString()     +" : " + vertUpYPR.toString());

        // Creation of the camera matrix
        Matrix matCameraWorld = Matrix.matCreateCamReferentiel(camera.getpCamPosition(), camera.getvCamDirection(), camera.getvCamUp());


        // View matrix for the camera
        Matrix matWorldCamera = Matrix.matQuickInverse(matCameraWorld);

        // engine.math.Triangle projection and drawing
        List<Triangle> trisToRaster = new ArrayList<Triangle>();
        for (Triangle triangleToProject : mesh.getTris()) {
            Triangle triTransformed = new Triangle();
//            engine.math.Triangle triProjected = new engine.math.Triangle();
            Triangle triViewed = new Triangle();

            // Z-axis, Y-axis and X-axis Rotation
            triTransformed.getVertices()[0] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[0],matWorld);
            triTransformed.getVertices()[1] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[1],matWorld);
            triTransformed.getVertices()[2] = Vertex3D.vertexMatrixMultiplication(triangleToProject.getVertices()[2],matWorld);

            // Line creation for determining the normal
            Vertex3D vLine1 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[1],triTransformed.getVertices()[0]);
            Vertex3D vLine2 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[2],triTransformed.getVertices()[0]);

            vLine1.convertToVector();
            vLine2.convertToVector();


            Vertex3D vNormal = Vertex3D.crossProduct(vLine1,vLine2);
            vNormal.vertexNormalisation();

            // Casting the ray of the camera
            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0], camera.getpCamPosition());
            vCameraRay.convertToVector();

            // Checking if the ray of the camera is in sight of the normale
            if (Vertex3D.dotProduct(vNormal, vCameraRay)< 0) {

                Vertex3D vLightDirection = new Vertex3D(0,0,-1,0); // Pseudo definition of the light source
                vLightDirection.vertexNormalisation();

                double dpLightNorm = Vertex3D.dotProduct(vNormal,vLightDirection);
                Color colorTri = Triangle.grayScale(dpLightNorm);

                // Definition of the greyscale value for the triangle regarding its orientation
                triTransformed.setColor(colorTri);

                // Convert World Space in the Worldview of the camera
                triViewed.getVertices()[0] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[0],matWorldCamera);
                triViewed.getVertices()[1] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[1],matWorldCamera);
                triViewed.getVertices()[2] = Vertex3D.vertexMatrixMultiplication(triTransformed.getVertices()[2],matWorldCamera);
                triViewed.setColor(triTransformed.getColor()); // Color transfer

                Triangle[] trisClipped = new Triangle[2];
                trisClipped[0] = new Triangle();
                trisClipped[1] = new Triangle();

                 int nbClippedTris = Triangle.trisClippingPlane(new Vertex3D(0,0, 0.1), new Vertex3D(0,0,1,0), triViewed, trisClipped[0], trisClipped[1]);

                 for (int n = 0; n < nbClippedTris; n++) {
                     Triangle triProjected = new Triangle();

                     for (int ind = 0; ind < 3; ind++ ) {
                         // Projecting 3D into 2D
                         triProjected.getVertices()[ind] = Vertex3D.vertexMatrixMultiplication(trisClipped[n].getVertices()[ind], this.camera.getMatProjection());

                         // Normalization of the vertex
                         triProjected.getVertices()[ind] = Vertex3D.vertexDivision(triProjected.getVertices()[ind].getW(),triProjected.getVertices()[ind]);

//                         // X/Y Inverted so need to put them back???
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * -1);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * -1);

                         // Offset into visible normalised space
                         Vertex3D vOffsetView = new Vertex3D(1,1,0,0);
                         triProjected.getVertices()[ind] = Vertex3D.vertexAddition(triProjected.getVertices()[ind], vOffsetView);

                         // Scaling to screen dimension
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * 0.5 * winHeight);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * 0.5 * winWidth);

                         // Color transfer
                         triProjected.setColor(trisClipped[n].getColor());
                     }

                     // Save for later rasterization
                     trisToRaster.add(triProjected);
                }
            }
        }

        // Painter's algorithm
        trisToRaster.sort((t1, t2) -> {
            double dMeanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double dMeanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(dMeanZ2,dMeanZ1);
        });

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
                g2.fillPolygon(xs, ys, 3);
                g2.setColor(Color.BLACK);
                g2.drawPolygon(xs, ys, 3);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                //System.out.println(nbTriRender++);
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
            camera.setpCamPosition(Vertex3D.vertexAddition(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamRight())));
        }

        // D = Right
        if (keysPressed[KeyEvent.VK_D]) {
            camera.setpCamPosition(Vertex3D.vertexSubtraction(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamRight())));
        }

        // SHIFT + SPACE = Down
        // SPACE = Up
        if (keysPressed[KeyEvent.VK_SHIFT]) {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                camera.setpCamPosition(Vertex3D.vertexSubtraction(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamUp())));
            }
        } else {
            if (keysPressed[KeyEvent.VK_SPACE]) {
                camera.setpCamPosition(Vertex3D.vertexAddition(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamUp())));
            }
        }

        // Z = Forward
        if (keysPressed[KeyEvent.VK_Z]) {
            camera.setpCamPosition(Vertex3D.vertexAddition(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamDirection())));
        }
        // S = Behind
        if (keysPressed[KeyEvent.VK_S]) {
            camera.setpCamPosition(Vertex3D.vertexSubtraction(camera.getpCamPosition(), Vertex3D.vertexMultiplication(translationCameraSpeed, camera.getvCamDirection())));
        }

        // ROTATION
        // UP = Trigo X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_UP]) {
            camera.setdCamPitch(camera.getdCamPitch() + rotationCameraSpeed * deltaTime);
        }

        // DOWN = Horaire X-Axis rotation Pitch
        if (keysPressed[KeyEvent.VK_DOWN]) {
            camera.setdCamPitch(camera.getdCamPitch() - rotationCameraSpeed * deltaTime);
        }

        // RIGHT = Trigo Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_RIGHT]) {
            camera.setdCamYaw(camera.getdCamYaw() + rotationCameraSpeed * deltaTime);
        }

        // LEFT = Horaire Y-Axis rotation Yaw
        if (keysPressed[KeyEvent.VK_LEFT]) {
            camera.setdCamYaw(camera.getdCamYaw() - rotationCameraSpeed * deltaTime);
        }

        // A = Trigo Z-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_A]) {
            camera.setdCamRoll(camera.getdCamRoll() + rotationCameraSpeed * deltaTime);
        }

        // E = Horaire Y-Axis rotation Roll
        if (keysPressed[KeyEvent.VK_E]) {
            camera.setdCamRoll(camera.getdCamRoll() - rotationCameraSpeed * deltaTime);
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

    public void setFirstMouseMove(boolean firstMouseMove) {
        this.firstMouseMove = firstMouseMove;
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

    public Camera getCamera() {
        return camera;
    }


}


