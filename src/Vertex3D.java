import java.awt.*;

import static java.lang.Math.sqrt;

public class Vertex3D {
    private double x;
    private double y;
    private double z;
    //private Color color;

    public Vertex3D() {
        x = 0;
        y = 0;
        z = 0;
        //color = null;
    }

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double vertexLength(Vertex3D vertIn) {
        double res = sqrt(vertIn.getX()*vertIn.getX() + vertIn.getY()*vertIn.getY() + vertIn.getZ()*vertIn.getZ());

        return res;
    }

    public void vertexNormalisation() {
        double vertexLength = vertexLength(this);

        this.setX(this.getX() / vertexLength);
        this.setY(this.getY() / vertexLength);
        this.setZ(this.getZ() / vertexLength);
    }

    public static Vertex3D crossProduct(Vertex3D vertIn1, Vertex3D vertIn2) {
        double x = vertIn1.getY() * vertIn2 .getZ() - vertIn1.getZ() * vertIn2.getY();
        double y = vertIn1.getZ() * vertIn2 .getX() - vertIn1.getX() * vertIn2.getZ();
        double z = vertIn1.getX() * vertIn2 .getY() - vertIn1.getY() * vertIn2.getX();

        Vertex3D vertOut = new Vertex3D(x,y,z);

        return vertOut;
    }

    public static double dotProduct(Vertex3D vertIn1, Vertex3D vertIn2) {
        double res = vertIn1.getX() * vertIn2.getX() + vertIn1.getY() * vertIn2.getY() + vertIn1.getZ() * vertIn2.getZ();
        return res;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vertex3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
