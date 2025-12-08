package engine.math;

import static java.lang.Math.sqrt;

public class Tuple3D {
    double x, y, z, w;

    public Tuple3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Tuple3D(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Tuple3D(Tuple3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public Tuple3D() {
        this(0,0,0);
    }

    public void copyFrom(Tuple3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public Tuple3D divide(double divisor) {
        double x = 0;
        double y = 0;
        double z = 0;

        if (divisor != 0) {
            x = this.getX() / divisor;
            y = this.getY() / divisor;
            z = this.getZ() / divisor;
        }

        return new Tuple3D(x,y,z);
    }

    public Tuple3D scale(double factor) {
        double x = 0;
        double y = 0;
        double z = 0;

        x = this.x * factor;
        y = this.y * factor;
        z = this.z * factor;

        return new Tuple3D(x,y,z);
    }

    public Tuple3D crossProduct(Tuple3D tupleIn2) {
        double x = this.y * tupleIn2.z - this.z * tupleIn2.y;
        double y = this.z * tupleIn2.x - this.x * tupleIn2.z;
        double z = this.x * tupleIn2.y - this.y * tupleIn2.x;

        return new Tuple3D(x, y, z);
    }

    public double dotProduct(Tuple3D tupleIn2) {
        return this.x * tupleIn2.x +
               this.y * tupleIn2.y +
               this.z * tupleIn2.z;
    }

    public Tuple3D sub(Tuple3D tupleIn2) {
        return new Tuple3D(this.x - tupleIn2.x,
                           this.y - tupleIn2.y,
                           this.z - tupleIn2.z);
    }

    public Tuple3D add(Tuple3D tupleIn2) {
        return new Tuple3D(this.x + tupleIn2.x,
                           this.y + tupleIn2.y,
                           this.z + tupleIn2.z);
    }

    public Tuple3D transform(Matrix mat) {
        double[][] M = mat.getMatrix();
        double xIn = this.x;
        double yIn = this.y;
        double zIn = this.z;
        double wIn = this.w;

        double x = xIn * M[0][0] + yIn * M[1][0] + zIn * M[2][0] + wIn * M[3][0];
        double y = xIn * M[0][1] + yIn * M[1][1] + zIn * M[2][1] + wIn * M[3][1];
        double z = xIn * M[0][2] + yIn * M[1][2] + zIn * M[2][2] + wIn * M[3][2];
        double w = xIn * M[0][3] + yIn * M[1][3] + zIn * M[2][3] + wIn * M[3][3];

        return new Tuple3D(x,y,z,w);
    }



    public double getLength() {
        return sqrt(this.x * this.x +
                    this.y * this.y +
                    this.z * this.z);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
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
}
