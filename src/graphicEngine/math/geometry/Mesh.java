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

        int count = 0;
        for (int[] face : indicesFaces) {
            Vertex3D vert1 = vertices.get(face[0]);
            Vertex3D vert2 = vertices.get(face[1]);
            Vertex3D vert3 = vertices.get(face[2]);


            Vertex2D tVert1 = new Vertex2D();
            Vertex2D tVert2 = new Vertex2D();
            Vertex2D tVert3 = new Vertex2D();

            if (count%2 == 0) {
                tVert1 = new Vertex2D(0,1);
                tVert2 = new Vertex2D(0,0);
                tVert3 = new Vertex2D(1,0);
            } else {
                tVert1 = new Vertex2D(0,1);
                tVert2 = new Vertex2D(1,0);
                tVert3 = new Vertex2D(1,1);
            }

            count++;

            meshTriangle.add(new Triangle(vert1, vert2, vert3, tVert1, tVert2, tVert3));
//            meshTriangle.add(new Triangle(vert1, vert2, vert3));
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
