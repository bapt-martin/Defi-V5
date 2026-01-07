package graphicEngine.core;

import graphicEngine.input.InputManager;
import graphicEngine.math.tools.Matrix;
import graphicEngine.overlay.HeadUpDisplay;
import graphicEngine.renderer.Camera;
import graphicEngine.renderer.Pipeline;
import graphicEngine.scene.GameObject;
import graphicEngine.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphicEngine extends JPanel implements Runnable {
    private final Camera camera;
    private final InputManager inputManager;
    private final HeadUpDisplay headUpDisplay;
    private final Pipeline pipeline;
    private final Scene scene;
    private final GraphicEngineContext graphicEngineContext;
    private final Timer timeLoop;

    public GraphicEngine(int widthInit, int heightInit) {
        this.graphicEngineContext = new GraphicEngineContext(this, widthInit, heightInit);


        this.camera = new Camera(0.1,1000,90, graphicEngineContext);


        this.setBackground(new Color(150,150,200));


        this.setFocusable(true);
//        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.inputManager = new InputManager(this,camera);
        inputManager.attachTo(this);

        this.scene = new Scene();

        this.scene.loadMeshes(
                List.of(new Scene.MeshData("teapot","obj model\\teapot.obj"),
                        new Scene.MeshData("axis","obj model\\axis.obj"),
                        new Scene.MeshData("cube","obj model\\cube.obj")
        ));

        this.scene.addMultipleGameObjects(
                List.of(new Scene.ObjectData("teapot1", "teapot"),
                        new Scene.ObjectData("teapot2", "teapot"),
                        new Scene.ObjectData("teapot3", "teapot"),
                        new Scene.ObjectData("teapot4", "teapot"),
                        new Scene.ObjectData("axis1",   "axis"),
                        new Scene.ObjectData("cube1",   "cube")

        ));

        scene.getGameObject(4).setPosition(0, 0, 0);
        scene.getGameObject(4).setScale(-1, 1, 1);
        scene.getGameObject(4).setRendered(true);


        scene.getGameObject(5).setPosition(0, 0, 0);
        scene.getGameObject(5).setRendered(true);


        GameObject t1 = scene.getGameObject(0);
        t1.setPosition(-6, 0, 8);
        t1.setRotation(0, 0, 0);
        t1.setScale(1, 1, 1);
        t1.setRendered(true);

        GameObject t2 = scene.getGameObject(1);
        t2.setPosition(6, 0, 8);
        t2.setRotation(45, 0, 0);
        t2.setScale(1.5, 1.5, 1.5);
        t2.setRendered(true);

        GameObject t3 = scene.getGameObject(2);
        t3.setPosition(0, 5, 8);
        t3.setRotation(0, 0, 180);
        t3.setScale(0.5, 0.5, 0.5);
        t3.setRendered(true);

        GameObject t4 = scene.getGameObject(3);
        t4.setPosition(0, -5, 8);
        t4.setRotation(-45, 45, 0);
        t4.setScale(2, 0.6, 1.2);
        t4.setRendered(true);

        this.timeLoop = new Timer(1, e -> repaint());

        this.headUpDisplay = new HeadUpDisplay(graphicEngineContext);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        this.add(headUpDisplay);


        this.graphicEngineContext.setCamera(this.camera);
        this.pipeline = new Pipeline(camera, scene, graphicEngineContext);
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
        this.graphicEngineContext.resetNbRenderedTriangle();

        // Real time aspect actualisation
        camera.updateWindowProjectionMatrix();

        inputManager.handleKeyPress();
//        inputManager.handleMouseWheelInput();

        camera.updateCamReferentialMatrix();
        camera.updateProjectionMatrix();

        pipeline.execution(g);

        headUpDisplay.update();
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Graphic Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 800;
        int height = 600;
        window.setSize(width, height);

        GraphicEngine graphicEngine = new GraphicEngine(width, height);
        window.add(graphicEngine);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public GraphicEngineContext getEngineContext() {
        return graphicEngineContext;
    }

    public void setWorldTransformMatrices(List<Integer> objectsId, List<Matrix> worldTransformMatrices) {
        this.scene.setWorldTransformMatrices(objectsId, worldTransformMatrices);
    }

    public void setObjectsVisibility(List<Integer> objectsId, List<Boolean> renderedStatus) {
        this.scene.setObjectsVisibility(objectsId, renderedStatus);
    }

    public void addMultipleGameObjects(List<Scene.ObjectData> objectReferences) {
        this.scene.addMultipleGameObjects(objectReferences);
    }

    public List<Scene.IdSwap> removeMultipleGameObjects(List<Integer> indexList) {
        return this.scene.removeMultipleGameObject(indexList);
    }

    public Camera getCamera() {
        return camera;
    }
}


