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
import java.awt.image.BufferStrategy;
import java.util.List;

public class GraphicEngine extends Canvas implements Runnable {
    private Thread gameThread;
    private final Camera camera;
    private final InputManager inputManager;
    private final HeadUpDisplay headUpDisplay;
    private final Pipeline pipeline;
    private final Scene scene;
    private final GraphicEngineContext graphicEngineContext;

    private int currentFPS = 0;
    private int currentUPS = 0;

    private double angleTheta = 0;
    private double anglePhi = 0;

    private boolean isRunning;
    private boolean isBenchmarkMode = false;

    public GraphicEngine(int widthInit, int heightInit) {
        this.graphicEngineContext = new GraphicEngineContext(this, widthInit, heightInit);


        this.camera = new Camera(0.1,1000,90, graphicEngineContext);


        this.setBackground(new Color(150,150,200));


        this.setFocusable(true);
//        this.setFocusTraversalKeysEnabled(false);
        this.requestFocusInWindow();

        this.graphicEngineContext.setCamera(this.camera);

        this.inputManager = new InputManager(this,camera);
        inputManager.attachTo(this);

        this.scene = new Scene();

        this.scene.loadMeshes(
                List.of(new Scene.MeshData("teapot","obj model\\teapot.obj"),
                        new Scene.MeshData("axis","obj model\\axis.obj"),
                        new Scene.MeshData("cube","obj model\\cube.obj")
//                        new Scene.MeshData("F1","obj model\\F1.obj")
        ));

        this.scene.addMultipleGameObjects(
                List.of(new Scene.ObjectData("teapot1", "teapot"),
                        new Scene.ObjectData("teapot2", "teapot"),
                        new Scene.ObjectData("teapot3", "teapot"),
                        new Scene.ObjectData("teapot4", "teapot"),
                        new Scene.ObjectData("axis1",   "axis"),
                        new Scene.ObjectData("cube1",   "cube"),
                        new Scene.ObjectData("teapot5", "teapot"),
                        new Scene.ObjectData("teapot6", "teapot")
//                        new Scene.ObjectData("F1", "F1")
        ));


//        scene.getGameObject(8).setScale(2.5, 2.5, 2.5);

        scene.getGameObject(4).setPosition(0, 0, 0);
        scene.getGameObject(4).setScale(-1, 1, 1);
        scene.getGameObject(4).setRendered(true);


        scene.getGameObject(5).setPosition(0, 0, 0);
        scene.getGameObject(5).setRendered(true);

        scene.getGameObject(6).setRendered(false);
        scene.getGameObject(7).setRendered(false);


        GameObject t1 = scene.getGameObject(0);
        t1.setPosition(-6, 0, 8);
        t1.setRotation(0, 0, 0);
        t1.setScale(1, 1, 1);
        t1.setRendered(false);

        GameObject t2 = scene.getGameObject(1);
        t2.setPosition(6, 0, 8);
        t2.setRotation(45, 0, 0);
        t2.setScale(1.5, 1.5, 1.5);
        t2.setRendered(false);

        GameObject t3 = scene.getGameObject(2);
        t3.setPosition(0, 5, 8);
        t3.setRotation(0, 0, 180);
        t3.setScale(0.5, 0.5, 0.5);
        t3.setRendered(false);

        GameObject t4 = scene.getGameObject(3);
        t4.setPosition(0, -5, 8);
        t4.setRotation(-45, 45, 0);
        t4.setScale(2, 0.6, 1.2);
        t4.setRendered(false);


        this.headUpDisplay = new HeadUpDisplay(graphicEngineContext);

        this.pipeline = new Pipeline(camera, scene, graphicEngineContext);

        this.isBenchmarkMode = true;

        this.repaint();
    }

    @Override
    public void addNotify() {
        super.addNotify();

        this.start();

        SwingUtilities.invokeLater(() -> {
            this.graphicEngineContext.updateWindowInformation();
            this.createBufferStrategy(3);
            this.requestFocusInWindow();
            inputManager.centerMouse();
        });
    }

    public synchronized void start() {
        if (isRunning) return;
        isRunning = true;
        gameThread = new Thread(this, "EngineThread");
        gameThread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.graphicEngineContext.updateWindowInformation();

        double deltaU = 0;

        long timer = System.currentTimeMillis();

        long startTime = System.nanoTime();
        long previousTime = startTime;

        while (isRunning) {
            if (isBenchmarkMode && graphicEngineContext.getElapsedFrame() >= 300) {
                System.out.println("=== RÉSULTAT BENCHMARK ===");
                System.out.println("Temps total : " + graphicEngineContext.getElapsedTime());
                System.out.println("FPS Moyen : " + (300 / graphicEngineContext.getElapsedTime()));
                System.exit(0);
            }

            long currentTime = System.nanoTime();

            double deltaTime = (currentTime - previousTime) / 1_000_000_000.0;
            graphicEngineContext.setDeltaTime(deltaTime);
            System.out.println(deltaTime);

            if (deltaTime > 0.25) {
                deltaTime = 0.25;
            }

            double elapsedTime = (currentTime - startTime) / 1_000_000_000.0;
            graphicEngineContext.setElapsedTime(elapsedTime);

            deltaU += deltaTime * graphicEngineContext.getUPS_TARGET();

            previousTime = currentTime;

            boolean needsRender = false;
            while (deltaU >= 1) {
                this.update();
                currentUPS++;
                deltaU--;
                needsRender = true;
            }

            if (needsRender) {
                this.render();
                currentFPS++;
                graphicEngineContext.incrementElapsedFrame();
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                graphicEngineContext.setCurrentUPS(currentUPS);
                graphicEngineContext.setCurrentFPS(currentFPS);

                currentUPS = 0;
                currentFPS = 0;
            }
        }
    }

    private void update() {
        inputManager.handleKeyPress();

        camera.updateWindowProjectionMatrix();
        camera.updateCamReferentialMatrix();
        camera.updateProjectionMatrix();

        scene.getGameObject(1).rotate(10,0,0);
        scene.getGameObject(2).rotate(0,5,5);
        scene.getGameObject(3).rotate(7,5,3);


        angleTheta += 0.07;
        anglePhi += 0.01;

        double r = 11.0;

        double y = r * Math.sin(anglePhi);
        double hR = r * Math.cos(anglePhi);
        double x = hR * Math.cos(angleTheta);
        double z = hR * Math.sin(angleTheta);

        double sX = 1.0 + (0.5 * Math.sin(anglePhi));
        double sY = 1.0 + (0.5 * Math.sin(angleTheta));
        double sZ = 1.0 + (0.5 * Math.cos(anglePhi));

        scene.getGameObject(5).setScale(sX, sY, sZ);
        scene.getGameObject(5).rotate(2,2,2);
        scene.getGameObject(5).setPosition(x, y, z);

        headUpDisplay.updateStats();
    }

    private void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        g.setColor(this.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        camera.updateWindowProjectionMatrix();
        camera.updateProjectionMatrix();

        graphicEngineContext.resetNbRenderedTriangle();
        pipeline.execution(g);

        headUpDisplay.draw(g);

        g.dispose();
        bs.show();
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

    public int getCurrentFPS() {
        return currentFPS;
    }

    public int getCurrentUPS() {
        return currentUPS;
    }
}


