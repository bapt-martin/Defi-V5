import static java.lang.Math.sqrt;

public class Vertex3D {
    private double x;
    private double y;
    private double z;
    private double w;
    //private Color color;

    public Vertex3D() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
        //color = null;
    }

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 0;
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

    public static Vertex3D vertexAddition(Vertex3D vert1, Vertex3D vert2) {
        return new Vertex3D(vert1.getX() + vert2.getX(), vert1.getY() + vert2.getY(),vert1.getZ() + vert2.getZ());
    }

    public static Vertex3D vertexSubtraction(Vertex3D vert1, Vertex3D vert2) {
        return new Vertex3D(vert1.getX() + vert2.getX(), vert1.getY() + vert2.getY(),vert1.getZ() + vert2.getZ());
    }

    public void vertexOffset(double xOffset, double yOffset, double zOffset) {
        this.setX(xOffset);
        this.setY(yOffset);
        this.setZ(zOffset);
    }

    public void vertexMultiplication (double factor) {
        this.setX(this.getX() * factor);
        this.setY(this.getY() * factor);
        this.setZ(this.getZ() * factor);
    }

    public void vertexDivision (double factor) {
        if (factor != 0) {
            this.setX(this.getX() / factor);
            this.setY(this.getY() / factor);
            this.setZ(this.getZ() / factor);
        }

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

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    @Override
    public String toString() {
        return "Vertex3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public void vertexMatrixMultiplication(Vertex3D vertOut, Matrix mat) {
        vertOut.setX(getX() * mat.getMatrix()[0][0] + getY() * mat.getMatrix()[1][0] + getZ() * mat.getMatrix()[2][0] + getW() * mat.getMatrix()[3][0]);
        vertOut.setY(getX() * mat.getMatrix()[0][1] + getY() * mat.getMatrix()[1][1] + getZ() * mat.getMatrix()[2][1] + getW() * mat.getMatrix()[3][1]);
        vertOut.setZ(getX() * mat.getMatrix()[0][2] + getY() * mat.getMatrix()[1][2] + getZ() * mat.getMatrix()[2][2] + getW() * mat.getMatrix()[3][2]);
        vertOut.setW(getX() * mat.getMatrix()[0][3] + getY() * mat.getMatrix()[1][3] + getZ() * mat.getMatrix()[2][3] + getW() * mat.getMatrix()[3][3]);
    }
}
