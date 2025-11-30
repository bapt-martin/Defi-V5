package engine.core;

import engine.input.InputManager;
import engine.io.Document;
import engine.math.Matrix;
import engine.math.Mesh;
import engine.math.Triangle;
import engine.math.Vertex3D;
import engine.overlay.GeneralData;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel {
    private Mesh mesh;
    private Camera camera;
    private InputManager inputManager;

    private int iWinWidth;
    private int iWinHeight;

    private double dtheta;

    private final boolean[] keysPressed = new boolean[256];
    private final double translationCameraSpeed = 0.1;
    private final double rotationCameraSpeed = 0.5;

    private Vertex3D pWinLastMousePosition;
    private final double mouseSensibility = 0.01;
    private boolean firstMouseMove = true;

    private final Timer timeLoop;
    private long startFrameTime = System.nanoTime();
    private long lastFrameTime = System.nanoTime();
    private long lastFPSTime = System.nanoTime();
    private double deltaTime = 0;
    private double elapsedTime;
    private int nbFrames;


    private int nbTriRender = 0;

    private GeneralData generalData;


    public Engine3D(int iWidthInit, int iHeightInit) {
        this.iWinWidth = iWidthInit;
        this.iWinHeight = iHeightInit;

        this.camera = new Camera(0.1,1000,90);

        this.dtheta = 0;

        this.mesh = new Mesh();
        setBackground(Color.BLACK);

        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();

        this.inputManager = new InputManager(this,this.camera);
        addKeyListener(inputManager);
        addMouseListener(inputManager);
        addMouseMotionListener(inputManager);

        this.pWinLastMousePosition = new Vertex3D(0,0,0);

        // .OBJ file reading + construction of the 3D triangle to render
        Document.readObjFile(Paths.get("obj model\\axis.obj"),this.mesh);
        this.mesh.triConstruct();

        // Projection matrix coefficient definition initialisation
        this.camera.matCreateCamProjection(iWinWidth, iWinHeight);

        this.timeLoop = new Timer(16, e -> repaint());
        this.generalData = new GeneralData();
        this.setLayout(null); // on utilise null pour positionner manuellement
        generalData.setBounds(10, 10, 80, 20); // x, y, largeur, hauteur
        this.add(generalData);
        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        SwingUtilities.invokeLater(() -> {
            inputManager.centerMouse(this);
            timeLoop.start();
            requestFocusInWindow();
        });
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
//        this.dTheta += 0.05;
//        System.out.println(this.theta);

        // ROTATION OF THE OBJECT IN THE WORLD (adding a rotation mat per object in the future)
        // Rotation matrices Z-axis
        Matrix matRotZ = Matrix.matCreateRotationZ4x4(this.dtheta);

        // Rotation matrices Y-axis
        Matrix matRotY = Matrix.matCreateRotationY4x4(this.dtheta * 0.5);

        // Rotation matrices X-axis
        Matrix matRotX = Matrix.matCreateRotationX4x4(this.dtheta * 1.5);

        // TOTAL ROTATION
        Matrix matRotationTot = Matrix.matMultiplication4x4(matRotZ, Matrix.matMultiplication4x4(matRotX,matRotY));

        // Z-Axis Offset
        Matrix matTranslation = Matrix.matCreateTranslation4x4(0,0,16);

        // COMBINATION ROTATION + TRANSLATION
        Matrix matWorld = Matrix.matMultiplication4x4(matRotationTot,matTranslation);

        inputManager.handleKeyPress();
        camera.camUpdate();
        generalData.CalcFpsPerFrame(this);

//        System.out.println("target "+vertTarget.toString() +" : " + vertTargetYPR.toString());
//        System.out.println("right  "+vertRight.toString()  +" : " + vertRightYPR.toString());
//        System.out.println("up     "+vertUp.toString()     +" : " + vertUpYPR.toString());

        // Creation of the camera matrix
        Matrix matCameraWorld = Matrix.matCreateCamReferential(camera.getpCamPosition(), camera.getvCamDirection(), camera.getvCamUp());

        // View matrix for the camera
        Matrix matWorldCamera = Matrix.matQuickInverse(matCameraWorld);

        // engine.math.Triangle projection and drawing
        List<Triangle> trisToRaster = new ArrayList<>();
        for (Triangle triangleToProject : mesh.getTris()) {
            Triangle triTransformed = new Triangle();
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
            vNormal.vertNormalisation();

            // Casting the ray of the camera
            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0], camera.getpCamPosition());
            vCameraRay.convertToVector();

            // Checking if the ray of the camera is in sight of the normale
            if (Vertex3D.dotProduct(vNormal, vCameraRay)< 0) {

                Vertex3D vLightDirection = new Vertex3D(0,0,-1,0); // Pseudo definition of the light source
                vLightDirection.vertNormalisation();

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
                         triProjected.getVertices()[ind] = Vertex3D.vertDivisionScalar(triProjected.getVertices()[ind].getW(),triProjected.getVertices()[ind]);

//                         // X/Y Inverted so need to put them back???
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * -1);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * -1);

                         // Offset into visible normalised space
                         Vertex3D vOffsetView = new Vertex3D(1,1,0,0);
                         triProjected.getVertices()[ind] = Vertex3D.vertexAddition(triProjected.getVertices()[ind], vOffsetView);

                         // Scaling to screen dimension
                         triProjected.getVertices()[ind].setX(triProjected.getVertices()[ind].getX() * 0.5 * iWinHeight);
                         triProjected.getVertices()[ind].setY(triProjected.getVertices()[ind].getY() * 0.5 * iWinWidth);

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
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(0, iWinHeight - 1, 0), new Vertex3D(0, -1, 0), test, clipped[0], clipped[1]);
                            break;
                        case 2:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(0, 0, 0), new Vertex3D(1, 0, 0), test, clipped[0], clipped[1]);
                            break;
                        case 3:
                            nbTrisToAdd = Triangle.trisClippingPlane(new Vertex3D(iWinWidth - 1, 0, 0), new Vertex3D(-1, 0, 0), test, clipped[0], clipped[1]);
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

    public int getiWinWidth() {
        return iWinWidth;
    }

    public int getiWinHeight() {
        return iWinHeight;
    }

    public void setiWinWidth(int iWinWidth) {
        this.iWinWidth = iWinWidth;
    }

    public void setiWinHeight(int iWinHeight) {
        this.iWinHeight = iWinHeight;
    }

    public double getDtheta() {
        return dtheta;
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

    public Vertex3D getpWinLastMousePosition() {
        return pWinLastMousePosition;
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

    public int getNbFrames() {
        return nbFrames;
    }

    public void setNbFrames(int nbFrames) {
        this.nbFrames = nbFrames;
    }

    public long getLastFPSTime() {
        return lastFPSTime;
    }

    public void setLastFPSTime(long lastFPSTime) {
        this.lastFPSTime = lastFPSTime;
    }
}


