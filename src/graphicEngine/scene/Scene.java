package graphicEngine.scene;

import graphicEngine.io.ObjLoader;
import graphicEngine.math.geometry.Mesh;
import graphicEngine.math.geometry.Triangle;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private List<Mesh> meshList;
    private List<Triangle> triangleList;


    public Scene(String[] meshesPath, String[] meshesName) {
        int size = meshesPath.length;
        this.meshList = new ArrayList<>();
        for (int i=0; i<size; i++) {
            this.addMesh(ObjLoader.readObjFile(Paths.get(meshesPath[i])), meshesName[i]);
        }
    }

    public void addMesh(Mesh mesh, String meshName) {
        mesh.setMeshName(meshName);
        this.meshList.add(mesh);
    }

    public void initiateTriangleList() {
        List<Triangle> sceneTriangleList = new ArrayList<>();
        List<Mesh> meshList = this.getMeshList();

        for (Mesh mesh : meshList) {
            List<Triangle> meshTriangleList = mesh.getMeshTriangle();
            sceneTriangleList.addAll(meshTriangleList);
        }

        this.triangleList = sceneTriangleList;
    }

    public List<Triangle> getTriangleList() {
        return triangleList;
    }

    public List<Mesh> getMeshList() {
        return meshList;
    }

    public void setMeshList(List<Mesh> meshList) {
        this.meshList = meshList;
    }
}
