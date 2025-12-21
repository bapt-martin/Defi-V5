package engine.core;

import engine.math.geometry.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List meshList;
    private Engine3D engine3D;

    public Scene(Engine3D engine3D) {
        this.meshList = new ArrayList<>();
        this.engine3D = engine3D;
    }

    public void addMesh(Mesh mesh) {
        this.meshList.add(mesh);
    }

    public List getMeshList() {
        return meshList;
    }

    public void setMeshList(List meshList) {
        this.meshList = meshList;
    }
}
