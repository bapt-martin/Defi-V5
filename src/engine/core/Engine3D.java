package engine.core;

import engine.input.InputManager;
import engine.io.Document;
import engine.math.*;
import engine.overlay.GeneralData;
import engine.renderer.Pipeline;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

public class Engine3D extends JPanel {
    private Mesh mesh;
    private Camera camera;
    private InputManager inputManager;
    private GeneralData generalData;
    private Pipeline pipeline;

    private int windowWidth = 0;
    private int windowWHeight = 0;

    private double worldRotationAngle;

    private final boolean[] keysPressed = new boolean[256]; //move to imput manager

    private Vertex3D pWinLastMousePosition;// To input manager?
    private final double mouseSensibility = 0.01;// To input manager?
    private boolean firstMouseMove = true;// To input manager?

    private final Timer timeLoop; //generale data?
    private long startFrameTime = System.nanoTime(); //generale data?
    private long lastFrameTime = System.nanoTime(); //generale data?
    private long lastFPSTime = System.nanoTime(); //generale data?
    private double deltaTime = 0; //generale data?
    private double elapsedTime; //generale data?
    private int nbFrames; //generale data?

    private int nbTriRender = 0;

    public Engine3D(int iWidthInit, int iHeightInit) {
        this.camera = new Camera(0.1,1000,90);

        this.worldRotationAngle = 0;

        this.setBackground(new Color(150,150,200));

        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.inputManager = new InputManager(this,this.camera);

        inputManager.attachTo(this);

        this.pWinLastMousePosition = new Vertex3D(0,0,0);

        // .OBJ file reading + construction of the 3D triangle to render
        this.mesh = Document.readObjFile(Paths.get("obj model\\teapot.obj"));

        // Projection matrix coefficient definition initialisation
        this.camera.updateProjectionMatrix(this);

        this.timeLoop = new Timer(16, e -> repaint());
        this.generalData = new GeneralData();

        this.pipeline = new Pipeline(mesh, camera);

        this.setLayout(null); // on utilise null pour positioner manuellement
        generalData.setBounds(10, 10, 80, 20); // x, y, largeur, hauteur
        this.add(generalData);
        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        SwingUtilities.invokeLater(() -> {
            inputManager.centerMouse();
            timeLoop.start();
            requestFocusInWindow();
        });
    }

    //START OF THE PIPELINE
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Real time aspect actualisation
        camera.updateProjectionMatrix(this);

        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        deltaTime = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = now;
//        System.out.println(deltaTime);

        inputManager.handleKeyPress();
        camera.updateCamReferential();
        generalData.calcFpsPerFrame(this);

        // Actualisation of theta
//        worldRotationAngle += 0.05;
//        System.out.println(this.theta);
        pipeline.setWorldRotationAngle(worldRotationAngle);

        pipeline.pipelineExecution(windowWidth, windowWHeight,g);
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

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowWHeight() {
        return windowWHeight;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public void setWindowWHeight(int windowWHeight) {
        this.windowWHeight = windowWHeight;
    }

    public boolean[] getKeysPressed() {
        return keysPressed;
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

    public double getDeltaTime() {
        return deltaTime;
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


