package engine.math;

public class Vertex3D extends Tuple3D{
    public Vertex3D() {
        super();
    }

    public Vertex3D(double x, double y, double z) {
        super(x,y,z,1);
    }

    public Vertex3D(Tuple3D tuple3D) {
        super(tuple3D);
    }

    @Override
    public Vertex3D crossProduct(Tuple3D tupleIn2) {
        return new Vertex3D(super.crossProduct(tupleIn2));
    }

    @Override
    public Vertex3D scale(double factor) {
        return new Vertex3D(super.scale(factor));
    }

    @Override
    public Vertex3D divide(double divisor) {
        return new Vertex3D(super.divide(divisor));
    }

    public Vertex3D translate(Vector3D vectTranslation) {
        double x = this.getX() + vectTranslation.getX();
        double y = this.getY() + vectTranslation.getY();
        double z = this.getZ() + vectTranslation.getZ();

        return new Vertex3D(x,y,z);
    }


    @Override
    public Vertex3D add(Tuple3D tupleIn2) {
        return new Vertex3D(super.add(tupleIn2));
    }

    @Override
    public Vector3D sub(Tuple3D tupleIn2) {
        return new Vector3D(super.sub(tupleIn2));
    }

    @Override
    public Vertex3D transform(Matrix matIn) {
        return new Vertex3D(super.transform(matIn));
    }

    @Override
    public String toString() {
        return "engine.math.Vertex3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

}
