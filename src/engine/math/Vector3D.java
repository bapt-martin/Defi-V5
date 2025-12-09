package engine.math;

public class Vector3D extends Tuple3D{
    public Vector3D() {
        super();
    }

    public Vector3D(double x, double y, double z) {
        super(x,y,z,0);
    }

    public Vector3D(Tuple3D tuple3D) {
        super(tuple3D);
    }

    @Override
    public Vector3D crossProduct(Tuple3D tupleIn2) {
        return new Vector3D(super.crossProduct(tupleIn2));
    }

    public Vector3D selfNormalize() {
        if (this.getLength() != 0) {
            double vectorLength = this.getLength();

            this.setX(this.getX() / vectorLength);
            this.setY(this.getY() / vectorLength);
            this.setZ(this.getZ() / vectorLength);
        }
        return this;
    }

    public Vector3D normalized() {
        if (this.getLength() != 0) {
            double vectorLength = this.getLength();

            double x = this.getX() / vectorLength;
            double y = this.getY() / vectorLength;
            double z = this.getZ() / vectorLength;
        }

        return new Vector3D(x,y,z);
    }

    @Override
    public Vector3D scaled(double factor) {
        return new Vector3D(super.scaled(factor));
    }

    @Override
    public Vector3D divided(double divisor) {
        return new Vector3D(super.divided(divisor));
    }

    @Override
    public Vector3D divideInPlace(double divisor) {
        super.divideInPlace(divisor);
        return this;
    }

    @Override
    public Vector3D scaleInPlace(double factor) {
        super.scaleInPlace(factor);
        return this;
    }

    @Override
    public Vector3D sub(Tuple3D tupleIn2) {
        return new Vector3D(super.sub(tupleIn2));
    }

    @Override
    public Vector3D add(Tuple3D tupleIn2) {
        return new Vector3D(super.add(tupleIn2));
    }

    @Override
    public Vector3D transformed(Matrix matIn) {
        return new Vector3D(super.transformed(matIn));
    }

    @Override
    public Vector3D transformInPlace(Matrix mat) {
        super.transformInPlace(mat);
        return this;
    }
}
