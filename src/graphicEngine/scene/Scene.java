package graphicEngine.scene;

import graphicEngine.io.ObjLoader;
import graphicEngine.math.geometry.Mesh;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    public record MeshData(String name, String path) {}
    public record ObjectData(String name, String meshName) {}

    private final Map<String, Mesh> meshLibrary;
    private final Map<String, GameObject> objectsMap;
    private final List<GameObject> renderQueue;


    public Scene() {
        this.renderQueue = new ArrayList<>();
        this.objectsMap = new HashMap<>();
        this.meshLibrary = new HashMap<>();
    }

    public void loadMeshes(List<MeshData> meshesToLoad) {
        for (MeshData meshData : meshesToLoad) {
            this.addMesh(meshData.name(),ObjLoader.readObjFile(Paths.get(meshData.path())));
        }
    }

    public void addMultipleGameObject(List<ObjectData> objectReferences) {
        for (ObjectData objectData : objectReferences) {
            GameObject gameObject = new GameObject(meshLibrary.get(objectData.meshName()));
            this.addGameObject(objectData.name(), gameObject);

        }
    }

    public void addGameObject(String objectName, GameObject gameObject) {
        gameObject.setName(objectName);
        this.objectsMap.put(objectName,gameObject);
        this.renderQueue.add(gameObject);
    }

    public void addMesh(String meshName, Mesh mesh) {
        mesh.setMeshName(meshName);
        this.meshLibrary.put(meshName,mesh);
    }

    public Map<String, Mesh> getMeshLibrary() {
        return meshLibrary;
    }

    public Map<String, GameObject> getObjectsMap() {
        return objectsMap;
    }

    public GameObject getGameObject(String objectName) {
        return this.objectsMap.get(objectName);
    }

    public List<GameObject> getRenderQueue() {
        return renderQueue;
    }
}
