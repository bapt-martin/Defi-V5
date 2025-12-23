package engine.core;

import engine.input.InputManager;
import engine.overlay.HeadUpDisplay;
import engine.renderer.Camera;
import engine.renderer.Pipeline;

import javax.swing.*;
import java.awt.*;

public class Engine3D extends JPanel {
    private final Camera camera;
    private final InputManager inputManager;
    private final HeadUpDisplay headUpDisplay;
    private final Pipeline pipeline;
    private final Scene scene;

    private int windowWidth = 0;
    private int windowWHeight = 0;

    private double worldRotationAngle;

    private final Timer timeLoop; //generale data?
    private long startFrameTime = System.nanoTime(); //generale data?
    private long lastFrameTime = System.nanoTime(); //generale data?
    private double lastFrameDuration = 0; //generale data?
    private double elapsedTime; //generale data?
    private int nbFrames; //generale data?
    private double fpsScoreTarget = ((double) 1/60) / 50000.1;//secondes/triangle rendered 50 000 triangle 60 times by second

    private int nbTriRender = 0;

    public Engine3D(int iWidthInit, int iHeightInit) {
        fpsScoreTarget = 0.0912/13252;
        this.camera = new Camera(0.1,1000,90);

        this.worldRotationAngle = 0;

        this.setBackground(new Color(150,150,200));

        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.inputManager = new InputManager(this,this.camera);

        inputManager.attachTo(this);

        this.scene = new Scene(
                new String[] {"obj model\\teapot.obj", "obj model\\axis.obj"},
                new String[] {"teapot"               ,"axis"}
        );


//        this.scene.addMesh(ObjLoader.readObjFile(Paths.get("obj model\\teapot.obj")), "teapot");
//
//        this.scene.addMesh(ObjLoader.readObjFile(Paths.get("obj model\\axis.obj")), "axis");

        this.timeLoop = new Timer(16, e -> repaint());

        this.headUpDisplay = new HeadUpDisplay();
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        this.add(headUpDisplay);

        this.pipeline = new Pipeline(camera, scene);

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
        this.setNbTriRender(0);

        // Real time aspect actualisation
        camera.updateWindowProjectionMatrix(this);

        long now = System.nanoTime();
        elapsedTime = (now - startFrameTime) / 1_000_000_000.0; // secondes
        lastFrameDuration = (now - lastFrameTime) / 1_000_000_000.0; // secondes
        lastFrameTime = now;

        inputManager.handleKeyPress();
//        inputManager.handleMouseWheelInput();

        camera.updateCamReferentialMatrix();
        camera.updateProjectionMatrix(this);

    //      objectWorldRotationAngle += 0.05;
        pipeline.setWorldRotationAngle(worldRotationAngle);

        pipeline.pipelineExecution(windowWidth, windowWHeight,g,this);

        headUpDisplay.updateStats(this);
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

    public void setWindowHeight(int windowWHeight) {
        this.windowWHeight = windowWHeight;
    }

    public double getLastFrameDuration() {
        return lastFrameDuration;
    }

    public int getNbFrames() {
        return nbFrames;
    }

    public void setNbFrames(int nbFrames) {
        this.nbFrames = nbFrames;
    }

    public double getFpsScoreTarget() {
        return fpsScoreTarget;
    }

    public void setNbTriRender(int nbTriRender) {
        this.nbTriRender = nbTriRender;
    }

    public int getNbTriRender() {
        return nbTriRender;
    }
}


