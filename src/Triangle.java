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

    public void CopyTriangle(Triangle other) {
        this.getVertices()[0] = other.getVertices()[0];
        this.getVertices()[1] = other.getVertices()[1];
        this.getVertices()[2] = other.getVertices()[2];
        this.setColor(other.getColor());

    }

    public Vertex3D[] getVertices() {
        return vertices;
    }

    private static double distPointToPlane(Vertex3D planePoint, Vertex3D planeNorm, Vertex3D point) {
        planeNorm.vertexNormalisation();

        return Vertex3D.dotProduct(planeNorm, point) - Vertex3D.dotProduct(planePoint, planeNorm);
    }

    public static int trisClippingPlane (Vertex3D planePoint, Vertex3D planeNorm, Triangle triIn, Triangle triOut1, Triangle triOut2) {
        planeNorm.vertexNormalisation();

        Vertex3D[] pointsInside  = new Vertex3D[3]; int nbPointsInside = 0;
        Vertex3D[] pointsOutside = new Vertex3D[3]; int nbPointsOutside = 0;

        double[] pointsToClassify = new double[3];

        double d0 = distPointToPlane(planePoint, planeNorm, triIn.getVertices()[0]);
        double d1 = distPointToPlane(planePoint, planeNorm, triIn.getVertices()[1]);
        double d2 = distPointToPlane(planePoint, planeNorm, triIn.getVertices()[2]);

        pointsToClassify[0] = d0;
        pointsToClassify[1] = d1;
        pointsToClassify[2] = d2;

//        for (double distPoint : pointsToClassify) {
//            if (distPoint >= 0) {
//                pointsInside[nbPointsInside++] = triIn.getVertices()[nbPointsInside + nbPointsOutside - 1];
//            } else {
//                pointsOutside[nbPointsOutside++] = triIn.getVertices()[nbPointsInside + nbPointsOutside - 1];
//            }
//        }

        if (d0 >= 0) {
            pointsInside[nbPointsInside++] = triIn.getVertices()[0];
        } else {
            pointsOutside[nbPointsOutside++] = triIn.getVertices()[0];
        }

        if (d1 >= 0) {
            pointsInside[nbPointsInside++] = triIn.getVertices()[1];
        } else {
            pointsOutside[nbPointsOutside++] = triIn.getVertices()[1];
        }

        if (d2 >= 0) {
            pointsInside[nbPointsInside++] = triIn.getVertices()[2];
        } else {
            pointsOutside[nbPointsOutside++] = triIn.getVertices()[2];
        }

        if (nbPointsInside == 3) {
            // All points are inside the plane
            triOut1.CopyTriangle(triIn);
            return 1;
        }
        if (nbPointsInside == 1) {
            // The triangle simply becomme a smaller triangle
//            triOut1.setColor(triIn.getColor());
            triOut1.setColor(Color.RED);

            triOut1.getVertices()[0] = pointsInside[0];
            triOut1.getVertices()[1] = Vertex3D.vert_IntersectPlane(planePoint, planeNorm, pointsInside[0],pointsOutside[0]);
            triOut1.getVertices()[2] = Vertex3D.vert_IntersectPlane(planePoint, planeNorm, pointsInside[0],pointsOutside[1]);
            return 1;
        }
        if (nbPointsInside == 2) {
//            triOut1.setColor(triIn.getColor());
//            triOut2.setColor(triIn.getColor());
            triOut1.setColor(Color.GREEN);
            triOut2.setColor(Color.BLUE);

            triOut1.getVertices()[0] = pointsInside[0];
            triOut1.getVertices()[1] = pointsInside[1];
            triOut1.getVertices()[2] = Vertex3D.vert_IntersectPlane(planePoint, planeNorm, pointsInside[0],pointsOutside[0]);

            triOut2.getVertices()[0] = pointsInside[1];
            triOut2.getVertices()[1] = Vertex3D.vert_IntersectPlane(planePoint, planeNorm, pointsInside[0],pointsOutside[0]);; // dans mon livre faut inverser les 2
            triOut2.getVertices()[2] = Vertex3D.vert_IntersectPlane(planePoint, planeNorm, pointsInside[1],pointsOutside[0]);
            return 2;
        }
        else {
            return 0;
        }
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
