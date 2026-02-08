package graphicEngine.math.geometry;

import graphicEngine.math.tools.Tuple3D;

public class Vertex2D {
    private double u, v;

    public Vertex2D(double u, double v) {
        this.u = u;
        this.v = v;
    }

    public Vertex2D() {
        this.u = 0;
        this.v = 0;
    }

    public Vertex2D(Vertex2D other) {
        this.u = other.u;
        this.v = other.v;
    }
}
