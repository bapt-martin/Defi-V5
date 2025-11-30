package engine.math;

import java.awt.*;
import java.util.Arrays;

import static java.awt.Color.*;

public class Triangle {
    private final Vertex3D[] vertices = new Vertex3D[3];
    private Color color;

    public Triangle() {
        vertices[0] = new Vertex3D();
        vertices[1] = new Vertex3D();
        vertices[2] = new Vertex3D();
        this.color = BLUE;
    }

    public Triangle(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
        vertices[0] = p1;
        vertices[1] = p2;
        vertices[2] = p3;
        color = BLACK;
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

    private static double distPointToPlane(Vertex3D pPlanePoint, Vertex3D vPlaneNorm, Vertex3D pPoint) {
        vPlaneNorm.vertNormalisation();

        return Vertex3D.dotProduct(vPlaneNorm, pPoint) - Vertex3D.dotProduct(pPlanePoint, vPlaneNorm);
    }

    public static int trisClippingPlane (Vertex3D planePoint, Vertex3D planeNorm, Triangle triIn, Triangle triOut1, Triangle triOut2) {
        planeNorm.vertNormalisation();
        planeNorm.convertToVector();

        Vertex3D[] ptsInside  = new Vertex3D[3]; int nbPointsInside = 0;
        Vertex3D[] ptsOutside = new Vertex3D[3]; int nbPointsOutside = 0;

        // Classification of the point
        for (int i = 0; i < 3; i++) {
            double dDistPoint = distPointToPlane(planePoint, planeNorm, triIn.getVertices()[i]);
            if (dDistPoint >= 0) {
                ptsInside[nbPointsInside++] = triIn.getVertices()[i];
            } else {
                ptsOutside[nbPointsOutside++] = triIn.getVertices()[i];
            }
        }

        if (nbPointsInside == 3) {
            // All points are inside the plane
            triOut1.CopyTriangle(triIn);
            return 1;
        }
        if (nbPointsInside == 1) {
            // The triangle simply becomme a smaller triangle
            triOut1.setColor(triIn.getColor());
//            triOut1.setColor(Color.RED);

            triOut1.getVertices()[0] = ptsInside[0];
            triOut1.getVertices()[1] = Vertex3D.pIntersectPlanePoint(planePoint, planeNorm, ptsInside[0],ptsOutside[0]);
            triOut1.getVertices()[2] = Vertex3D.pIntersectPlanePoint(planePoint, planeNorm, ptsInside[0],ptsOutside[1]);
            return 1;
        }
        if (nbPointsInside == 2) {
            triOut1.setColor(triIn.getColor());
            triOut2.setColor(triIn.getColor());
//            triOut1.setColor(Color.GREEN);
//            triOut2.setColor(Color.BLUE);

            triOut1.getVertices()[0] = ptsInside[0];
            triOut1.getVertices()[1] = ptsInside[1];
            triOut1.getVertices()[2] = Vertex3D.pIntersectPlanePoint(planePoint, planeNorm, ptsInside[0],ptsOutside[0]);

            triOut2.getVertices()[0] = ptsInside[1];
            triOut2.getVertices()[1] = Vertex3D.pIntersectPlanePoint(planePoint, planeNorm, ptsInside[0],ptsOutside[0]);; // dans mon livre faut inverser les 2
            triOut2.getVertices()[2] = Vertex3D.pIntersectPlanePoint(planePoint, planeNorm, ptsInside[1],ptsOutside[0]);
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
        return "engine.math.Triangle{" +
                "vertices=" + Arrays.toString(vertices) +
                '}';
    }
}
