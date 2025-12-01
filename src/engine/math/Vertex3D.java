package engine.math;

import static java.lang.Math.sqrt;

public class Vertex3D {
    private double x;
    private double y;
    private double z;
    private double w;

    // Create a point by default
    public Vertex3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 1;
    }

    public Vertex3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = 1;
    }

    public static Vertex3D createPoint(double x, double y, double z) {
        return new Vertex3D(x,y,z);
    }

    public Vertex3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public static Vertex3D createVector(double x, double y, double z) {
        return new Vertex3D(x,y,z,0);
    }

    public Vertex3D(Vertex3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public Vertex3D copyFrom(Vertex3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }

    public void convertToVector() {
        this.w = 0;
    }


    public static Vertex3D pIntersectPlanePoint(Vertex3D pPlanePoint, Vertex3D vPlaneNorm, Vertex3D pLineStart, Vertex3D pLineEnd) {
        vPlaneNorm.vertNormalisation();

        double dPlaneConstant = -Vertex3D.dotProduct(vPlaneNorm, pPlanePoint);
        double ad = Vertex3D.dotProduct(pLineStart,vPlaneNorm);
        double bd = Vertex3D.dotProduct(pLineEnd,vPlaneNorm);

        double dLengthIntersecting = (-dPlaneConstant - ad) / (bd - ad);

        Vertex3D vLineStartToEnd = Vertex3D.vertexSubtraction(pLineEnd, pLineStart);
        vLineStartToEnd.convertToVector();
        Vertex3D vLineIntersecting = Vertex3D.verMultiplicationScalar(dLengthIntersecting,vLineStartToEnd);
        vLineIntersecting.convertToVector();

        return Vertex3D.vertexAddition(pLineStart, vLineIntersecting);
    }

    public double vertexLength() {
        return sqrt(this.getX() * this.getX() +
                    this.getY() * this.getY() +
                    this.getZ() * this.getZ());
    }

    public void vertNormalisation() {
        double vertexLength = this.vertexLength();

        this.setX(this.getX() / vertexLength);
        this.setY(this.getY() / vertexLength);
        this.setZ(this.getZ() / vertexLength);
    }

    public static Vertex3D crossProduct(Vertex3D vertIn1, Vertex3D vertIn2) {
        double x = vertIn1.getY() * vertIn2 .getZ() - vertIn1.getZ() * vertIn2.getY();
        double y = vertIn1.getZ() * vertIn2 .getX() - vertIn1.getX() * vertIn2.getZ();
        double z = vertIn1.getX() * vertIn2 .getY() - vertIn1.getY() * vertIn2.getX();

        return new Vertex3D(x,y,z);
    }

    public static double dotProduct(Vertex3D vertIn1, Vertex3D vertIn2) {
        return vertIn1.getX() * vertIn2.getX() +
               vertIn1.getY() * vertIn2.getY() +
               vertIn1.getZ() * vertIn2.getZ();
    }

    public static Vertex3D vertexAddition(Vertex3D vert1, Vertex3D vert2) {
        return new Vertex3D(vert1.getX() + vert2.getX(),
                            vert1.getY() + vert2.getY(),
                            vert1.getZ() + vert2.getZ());
    }

    public static Vertex3D vertexSubtraction(Vertex3D vert1, Vertex3D vert2) {
        return new Vertex3D(vert1.getX() - vert2.getX(),
                            vert1.getY() - vert2.getY(),
                            vert1.getZ() - vert2.getZ());
    }

    public void vertexOffset(double xOffset, double yOffset, double zOffset) {
        this.setX(this.getX() + xOffset);
        this.setY(this.getY() + yOffset);
        this.setZ(this.getZ() + zOffset);
    }

    public static Vertex3D verMultiplicationScalar(double factor, Vertex3D vertIn) {
        Vertex3D vertOut = new Vertex3D();

        vertOut.setX(vertIn.getX() * factor);
        vertOut.setY(vertIn.getY() * factor);
        vertOut.setZ(vertIn.getZ() * factor);

        return vertOut;
    }

    public Vertex3D verMultiplicationScalar(double factor) {
        this.x *= factor;
        this.y *= factor;
        this.z *= factor;

        return this;
    }

    public static Vertex3D vertDivisionScalar(double divisor, Vertex3D vertIn) {
        Vertex3D vertOut = new Vertex3D();

        if (divisor != 0) {
            vertOut.setX(vertIn.getX() / divisor);
            vertOut.setY(vertIn.getY() / divisor);
            vertOut.setZ(vertIn.getZ() / divisor);
        }

        return vertOut;
    }

    public Vertex3D vertDivisionScalar(double dDivisor) {
        if (dDivisor != 0) {
            this.x /= dDivisor;
            this.y /= dDivisor;
            this.z /= dDivisor;
        }

        return this;
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
        return "engine.math.Vertex3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Vertex3D vertexMatrixMultiplication(Vertex3D vertIn, Matrix mat) {
        Vertex3D vertOut = new Vertex3D();
        double[][] M = mat.getMatrix();

        vertOut.setX(vertIn.getX() * M[0][0] + vertIn.getY() * M[1][0] + vertIn.getZ() * M[2][0] + vertIn.getW() * M[3][0]);
        vertOut.setY(vertIn.getX() * M[0][1] + vertIn.getY() * M[1][1] + vertIn.getZ() * M[2][1] + vertIn.getW() * M[3][1]);
        vertOut.setZ(vertIn.getX() * M[0][2] + vertIn.getY() * M[1][2] + vertIn.getZ() * M[2][2] + vertIn.getW() * M[3][2]);
        vertOut.setW(vertIn.getX() * M[0][3] + vertIn.getY() * M[1][3] + vertIn.getZ() * M[2][3] + vertIn.getW() * M[3][3]);

        return vertOut;
    }

}
