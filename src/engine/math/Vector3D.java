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

    public void normalized() {
        if (this.getLength() != 0) {
            double vectorLength = this.getLength();

            this.setX(this.getX() / vectorLength);
            this.setY(this.getY() / vectorLength);
            this.setZ(this.getZ() / vectorLength);
        }
    }

    @Override
    public Vector3D scale(double factor) {
        return new Vector3D(super.scale(factor));
    }

    @Override
    public Vector3D divide(double divisor) {
        return new Vector3D(super.divide(divisor));
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
    public Vector3D transform(Matrix matIn) {
        return new Vector3D(super.transform(matIn));
    }
}
