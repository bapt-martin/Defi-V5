package graphicEngine.core;

import graphicEngine.input.InputManager;
import graphicEngine.overlay.HeadUpDisplay;
import graphicEngine.renderer.Camera;
import graphicEngine.renderer.Pipeline;
import graphicEngine.scene.GameObject;
import graphicEngine.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Engine3D extends JPanel implements Runnable {
    private final Camera camera;
    private final InputManager inputManager;
    private final HeadUpDisplay headUpDisplay;
    private final Pipeline pipeline;
    private final Scene scene;
    private final EngineContext engineContext;
    private final Timer timeLoop;

    public Engine3D(int widthInit,int heightInit) {
        this.engineContext = new EngineContext(this, widthInit, heightInit);


        this.camera = new Camera(0.1,1000,90,engineContext);


        this.setBackground(new Color(150,150,200));


        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.inputManager = new InputManager(this,camera);
        inputManager.attachTo(this);

        this.scene = new Scene();

        this.scene.loadMeshes(
                List.of(
            new Scene.MeshData("teapot","obj model\\teapot.obj"),
            new Scene.MeshData("axis","obj model\\axis.obj")
        ));

        this.scene.addMultipleGameObject(
                List.of(
            new Scene.ObjectData("teapot1", "teapot"),
            new Scene.ObjectData("teapot2", "teapot"),
            new Scene.ObjectData("teapot3", "teapot"),
            new Scene.ObjectData("teapot4", "teapot"),
            new Scene.ObjectData("axis1", "axis")
        ));

        scene.getGameObject("axis1").setPosition(0, 0, 0);

        GameObject t1 = scene.getGameObject("teapot1");
        t1.setPosition(-6, 0, 8);
        t1.setRotation(0, 90, 0);
        t1.setScale(1, 1, 1);

        GameObject t2 = scene.getGameObject("teapot2");
        t2.setPosition(6, 0, 8);
        t2.setRotation(0.45, 0, 0);
        t2.setScale(1.5, 1.5, 1.5);

        GameObject t3 = scene.getGameObject("teapot3");
        t3.setPosition(0, 5, 8);
        t3.setRotation(0, 0, 180);
        t3.setScale(0.5, 0.5, 0.5);

        GameObject t4 = scene.getGameObject("teapot4");
        t4.setPosition(0, -5, 8);
        t4.setRotation(-45, 45, 0);
        t4.setScale(2, 0.6, 1.2);

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


