import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh {
    private List<Vertex3D> vertices;
    private List<int []> faceIndices;
    private final List<Triangle> tris;
    private Matrix matProj;

    public void setMatProj(Matrix matProj) {
        this.matProj = matProj;
    }

    public Mesh() {
        this.tris = new ArrayList<>();
        this.matProj = null;
        this.vertices = new ArrayList<Vertex3D>();
        this.faceIndices = new ArrayList<>();

    }

    public Mesh(List<Triangle> tris) {
        this.tris = tris;
    }

    public void triConstruct() {
        for (int[] face : this.getFaceIndices()) {
            Vertex3D vert1 = this.getVertices().get(face[0]);
            Vertex3D vert2 = this.getVertices().get(face[1]);
            Vertex3D vert3 = this.getVertices().get(face[2]);
            Triangle triBuilded = new Triangle(vert1, vert2, vert3);
            this.getTris().add(triBuilded);
        }
    }

    public List<Triangle> getTris() {
        return tris;
    }

    public Matrix getMatProj() {
        return matProj;
    }

//    public void setMatProj(int i, int j, double value) {
//        this.matProj.getMatrix()[i][j] = value;
//    }

    public List<Vertex3D> getVertices() {
        return vertices;
    }

    public List<int []> getFaceIndices() {
        return faceIndices;
    }

    public void printMesh() {
        System.out.println("Vertices:");
        for (int i = 0; i < vertices.size(); i++) {
            Vertex3D v = vertices.get(i);
            System.out.printf("  [%d] x=%.3f y=%.3f z=%.3f%n", i, v.getX(), v.getY(), v.getZ());
        }

        System.out.println("Face indices:");
        for (int i = 0; i < faceIndices.size(); i++) {
            int[] f = faceIndices.get(i);
            System.out.printf("  [%d] %d, %d, %d%n", i, f[0], f[1], f[2]);
        }
    }

    @Override
    public String toString() {
        return "Mesh{" +
                "matProj=" + Arrays.toString(matProj.getMatrix()) +
                '}';
    }


}
