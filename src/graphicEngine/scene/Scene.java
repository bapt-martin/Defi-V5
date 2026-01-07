package graphicEngine.scene;

import graphicEngine.io.ObjLoader;
import graphicEngine.math.geometry.Mesh;
import graphicEngine.math.tools.Matrix;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    public record MeshData(String name, String path) {}
    public record ObjectData(String name, String meshName) {}
    public record IdSwap(int oldId, int newId) {}

    private final Map<String, Mesh> meshLibrary;
    private final List<GameObject> renderQueue;

    private final List<IdSwap> pendingIdSwaps = new ArrayList<>();


    public Scene() {
        this.renderQueue = new ArrayList<>();
        this.meshLibrary = new HashMap<>();
    }

    public void loadMeshes(List<MeshData> meshesToLoad) {
        for (MeshData meshData : meshesToLoad) {
            this.addMesh(meshData.name(),ObjLoader.readObjFile(Paths.get(meshData.path())));
        }
    }

    public void setWorldTransformMatrices(List<Integer> objectsId, List<Matrix> worldTransformMatrices) {
        int size = Math.min(objectsId.size(), worldTransformMatrices.size());
        for (int i = 0; i<size; i++) {
            GameObject obj = renderQueue.get(objectsId.get(i));

            if (obj != null) {
                obj.setWorldTransformMatrix(worldTransformMatrices.get(i));
            }
        }
    }

    public void setObjectsVisibility(List<Integer> objectsId, List<Boolean> renderedStatus) {
        int size = Math.min(objectsId.size(), renderedStatus.size());
        for (int i = 0; i<size; i++) {
            GameObject obj = renderQueue.get(objectsId.get(i));

            if (obj != null) {
                obj.setRendered(renderedStatus.get(i));
            }
        }
    }

    public void addMultipleGameObjects(List<ObjectData> objectReferences) {
        for (ObjectData objectData : objectReferences) {
            GameObject gameObject = new GameObject(meshLibrary.get(objectData.meshName()));
            this.addGameObject(objectData.name(), gameObject);
        }
    }

    private void addGameObject(String objectName, GameObject gameObject) {
        gameObject.setName(objectName);
        gameObject.setId(this.renderQueue.size());
        this.renderQueue.add(gameObject);
    }

    public List<IdSwap> removeMultipleGameObject(List<Integer> indexList) {
        indexList.sort(java.util.Collections.reverseOrder());

        for (Integer index : indexList) {
            this.removeGameObject(index);
        }

        return this.pendingIdSwaps;
    }

    private void removeGameObject(int objectId) {
        int lastIndex = this.renderQueue.size()-1;

        if (objectId!=lastIndex) {
            this.renderQueue.set(objectId,this.renderQueue.removeLast());
            this.renderQueue.get(objectId).setId(objectId);
            this.pendingIdSwaps.add(new IdSwap(lastIndex,objectId));

        } else {
            this.renderQueue.removeLast();
        }
    }

    public void addMesh(String meshName, Mesh mesh) {
        mesh.setMeshName(meshName);
        this.meshLibrary.put(meshName,mesh);
    }

    public GameObject getGameObject(int id) {
        return this.renderQueue.get(id);
    }

    public Map<String, Mesh> getMeshLibrary() {
        return meshLibrary;
    }

    public List<GameObject> getRenderQueue() {
        return renderQueue;
    }
}
