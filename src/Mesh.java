import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh {
    //    private final List<Vertex3D> vertices;
//    private final List<Integer> indices;
    private final List<Triangle> tris;
    private double[][] matProj;

    public Mesh() {
        this.tris = new ArrayList<>();
        matProj = new double[4][4];


    }

    public Mesh(List<Triangle> tris) {
        this.tris = tris;
    }

    public List<Triangle> getTris() {
        return tris;
    }

    public double[][] getMatProj() {
        return matProj;
    }

    public void setMatProj(int i, int j, double value) {
        this.matProj[i][j] = value;
    }

    @Override
    public String toString() {
        return "Mesh{" +
                "matProj=" + Arrays.toString(matProj) +
                '}';
    }
}
