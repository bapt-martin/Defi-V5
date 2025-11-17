import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.awt.Color.WHITE;

public class Triangle {
    private final Vertex3D[] vertices = new Vertex3D[3];
    private Color color;

    public Triangle() {
        vertices[0] = new Vertex3D();
        vertices[1] = new Vertex3D();
        vertices[2] = new Vertex3D();
        this.color = WHITE;
    }

    public Triangle(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
        vertices[0] = p1;
        vertices[1] = p2;
        vertices[2] = p3;
        color = WHITE;
    }

    public Triangle(Vertex3D p1, Vertex3D p2, Vertex3D p3, Color color) {
        vertices[0] = p1;
        vertices[1] = p2;
        vertices[2] = p3;
        this.color = color;
    }

    public Vertex3D[] getVertices() {
        return vertices;
    }

    public static Color grayScale(double t) {
        // Clamp t to [0, 1] in case the caller passes invalid values
        t = Math.max(0, Math.min(1, t));

        int v = (int) (t * 255);   // convert 0–1 to 0–255
        return new Color(v, v, v);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Triangle{" +
                "vertices=" + Arrays.toString(vertices) +
                '}';
    }
}
