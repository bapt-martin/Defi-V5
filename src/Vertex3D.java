import static java.lang.Math.sqrt;

public class Vertex3D {
    private double x;
    private double y;
    private double z;
    private double w;

    public Vertex3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    // To create a vector not affected by translation
    public Vertex3D(double w) {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public Vertex3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Vertex3D(Vertex3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }


    public static Vertex3D vert_IntersectPlane (Vertex3D planePoint, Vertex3D planeNorm, Vertex3D lineStart, Vertex3D lineEnd) {
        planeNorm.vertexNormalisation();
        double planeOrigin = -Vertex3D.dotProduct(planeNorm, planePoint);
        double ad = Vertex3D.dotProduct(lineStart,planeNorm);
        double bd = Vertex3D.dotProduct(lineEnd,planeNorm);
        double lengthIntersecting = (-planeOrigin - ad) / (bd - ad);
        Vertex3D lineStartToEnd = Vertex3D.vertexSubtraction(lineEnd, lineStart);
        Vertex3D lineIntersecting = Vertex3D.vertexMultiplication(lengthIntersecting,lineStartToEnd);

        return Vertex3D.vertexAddition(lineStart, lineIntersecting);
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
        return new Vertex3D(vert1.getX() - vert2.getX(), vert1.getY() - vert2.getY(),vert1.getZ() - vert2.getZ());
    }

    public void vertexOffset(double xOffset, double yOffset, double zOffset) {
        this.setX(xOffset);
        this.setY(yOffset);
        this.setZ(zOffset);
    }

    public static Vertex3D vertexMultiplication (double factor, Vertex3D vertIn) {
        Vertex3D vertOut = new Vertex3D();

        vertOut.setX(vertIn.getX() * factor);
        vertOut.setY(vertIn.getY() * factor);
        vertOut.setZ(vertIn.getZ() * factor);

        return vertOut;
    }

    public static Vertex3D vertexDivision (double divisor, Vertex3D vertIn) {
        Vertex3D vertOut = new Vertex3D();

        if (divisor != 0) {
            vertOut.setX(vertIn.getX() / divisor);
            vertOut.setY(vertIn.getY() / divisor);
            vertOut.setZ(vertIn.getZ() / divisor);
        }

        return vertOut;
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

    public static Vertex3D vertexMatrixMultiplication(Vertex3D vertIn, Matrix mat) {
        Vertex3D vertOut = new Vertex3D();

        vertOut.setX(vertIn.getX() * mat.getMatrix()[0][0] + vertIn.getY() * mat.getMatrix()[1][0] + vertIn.getZ() * mat.getMatrix()[2][0] + vertIn.getW() * mat.getMatrix()[3][0]);
        vertOut.setY(vertIn.getX() * mat.getMatrix()[0][1] + vertIn.getY() * mat.getMatrix()[1][1] + vertIn.getZ() * mat.getMatrix()[2][1] + vertIn.getW() * mat.getMatrix()[3][1]);
        vertOut.setZ(vertIn.getX() * mat.getMatrix()[0][2] + vertIn.getY() * mat.getMatrix()[1][2] + vertIn.getZ() * mat.getMatrix()[2][2] + vertIn.getW() * mat.getMatrix()[3][2]);
        vertOut.setW(vertIn.getX() * mat.getMatrix()[0][3] + vertIn.getY() * mat.getMatrix()[1][3] + vertIn.getZ() * mat.getMatrix()[2][3] + vertIn.getW() * mat.getMatrix()[3][3]);

        return vertOut;
    }
}
