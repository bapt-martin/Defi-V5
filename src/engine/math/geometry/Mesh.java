package engine.math.geometry;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private List<Vertex3D> vertices;
    private List<int[]> indicesFaces;
    private List<Triangle> meshTriangle;
    private String meshName ="";

    public Mesh() {
        this.meshTriangle = new ArrayList<>();
        this.vertices = new ArrayList<Vertex3D>();
        this.indicesFaces = new ArrayList<>();
    }

    public Mesh(List<int[]> indicesFaces, List<Vertex3D> vertices) {
        this.meshTriangle = new ArrayList<>();
        this.indicesFaces = indicesFaces;
        this.vertices = vertices;
    }

    public Mesh(String meshName) {
        this();
        this.meshName = meshName;
    }

    public Mesh(List<Triangle> meshTriangle) {
        this.meshTriangle = new ArrayList<>(meshTriangle);
        this.vertices = new ArrayList<Vertex3D>();
        this.indicesFaces = new ArrayList<>();
    }

    public void triConstruct() {
        List<Vertex3D> vertices = this.getVertices();
        List<Triangle> meshTriangle = this.getMeshTriangle();

        for (int[] face : this.getIndicesFaces()) {
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

    public List<Vertex3D> getVertices() {
        return vertices;
    }

    public List<int []> getIndicesFaces() {
        return indicesFaces;
    }

    public void printMesh() {
        System.out.println("Vertices:");
        for (int i = 0; i < vertices.size(); i++) {
            Vertex3D v = vertices.get(i);
            System.out.printf("  [%d] x=%.3f y=%.3f z=%.3f%n", i, v.getX(), v.getY(), v.getZ());
        }

        System.out.println("Face indices:");
        for (int i = 0; i < indicesFaces.size(); i++) {
            int[] f = indicesFaces.get(i);
            System.out.printf("  [%d] %d, %d, %d%n", i, f[0], f[1], f[2]);
        }
    }

    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }


}
