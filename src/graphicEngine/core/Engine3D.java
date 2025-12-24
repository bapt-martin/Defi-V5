package graphicEngine.core;

import graphicEngine.input.InputManager;
import graphicEngine.overlay.HeadUpDisplay;
import graphicEngine.renderer.Camera;
import graphicEngine.renderer.Pipeline;
import graphicEngine.scene.Scene;

import javax.swing.*;
import java.awt.*;

public class Engine3D extends JPanel implements Runnable {
    private final Camera camera;
    private final InputManager inputManager;
    private final HeadUpDisplay headUpDisplay;
    private final Pipeline pipeline;
    private final Scene scene;
    private final EngineContext engineContext;

    private double worldRotationAngle;

    private final Timer timeLoop; //generale data?

    public Engine3D(int widthInit,int heightInit) {
        this.engineContext = new EngineContext(this, widthInit, heightInit);


        this.camera = new Camera(0.1,1000,90,engineContext);


        this.worldRotationAngle = 0;


        this.setBackground(new Color(150,150,200));


        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.inputManager = new InputManager(this,camera);
        inputManager.attachTo(this);


        this.scene = new Scene(
                new String[] {"obj model\\teapot.obj",
                        "obj model\\teapot.obj",
                        "obj model\\teapot.obj",
                        "obj model\\teapot.obj",
                        "obj model\\axis.obj"},
                new String[] {"teapot",
                        "teapot",
                        "teapot",
                        "teapot",
                        "axis"}
        );


        this.timeLoop = new Timer(16, e -> repaint());

        this.headUpDisplay = new HeadUpDisplay(engineContext);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.add(headUpDisplay);


        this.pipeline = new Pipeline(camera, scene, engineContext);
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

    @Override
    public void run() {

    }

    //START OF THE PIPELINE
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.engineContext.resetNbRenderedTriangle();

        // Real time aspect actualisation
        camera.updateWindowProjectionMatrix();

        inputManager.handleKeyPress();
//        inputManager.handleMouseWheelInput();

        camera.updateCamReferentialMatrix();
        camera.updateProjectionMatrix();

//        worldRotationAngle += 0.05;
        pipeline.setWorldRotationAngle(worldRotationAngle);

        pipeline.pipelineExecution(g);

        headUpDisplay.updateStats();
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

    public EngineContext getEngineContext() {
        return engineContext;
    }
}


