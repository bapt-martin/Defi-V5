package graphicEngine.math.geometry;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private List<Triangle> meshTriangle;
    private String meshName = "";

    public Mesh() {
        this.meshTriangle = new ArrayList<>();
    }

    public void triConstruct(List<int[]> indicesFaces, List<Vertex3D> vertices) {
        List<Triangle> meshTriangle = this.getMeshTriangle();

        for (int[] face : indicesFaces) {
            Vertex3D vert1 = vertices.get(face[0]);
            Vertex3D vert2 = vertices.get(face[1]);
            Vertex3D vert3 = vertices.get(face[2]);

            meshTriangle.add(new Triangle(vert1, vert2, vert3));
        }

        this.meshTriangle = meshTriangle;
    }

    public List<Triangle> getMeshTriangle() {
        return meshTriangle;
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }


}
