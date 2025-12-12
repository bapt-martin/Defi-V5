package engine.math;

import java.awt.*;
import java.util.List;

public class Plane {
    private Vertex3D origin;
    private Vector3D normal;

    public Plane(Vertex3D origin, Vector3D normal) {
        this.origin = origin;
        this.normal = normal;
    }

    public Plane() {
        this.origin = new Vertex3D();
        this.normal = new Vector3D();
    }

    public Plane(Plane other) {
        this(other.origin, other.normal);
    }

    public static Plane planeToClipAgainst(int borderNumber, int iWinWidth, int iWinHeight) {
        return switch (borderNumber) {
            case 0 -> new Plane(new Vertex3D(0, 0, 0), new Vector3D(0, 1, 0));
            case 1 -> new Plane(new Vertex3D(0, iWinHeight - 1, 0), new Vector3D(0, -1, 0));
            case 2 -> new Plane(new Vertex3D(0, 0, 0), new Vector3D(1, 0, 0));
            case 3 -> new Plane(new Vertex3D(iWinWidth - 1, 0, 0), new Vector3D(-1, 0, 0));
            default -> new Plane();
        };
    }

    public int clipTriangleAgainstPlane(Triangle triIn, List<Triangle> trisOut) {
        Vertex3D planePoint = this.getOrigin();
        Vector3D planeNorm = this.getNormal();

        planeNorm.normalizeInPlace();

        Vertex3D[] ptsInside  = new Vertex3D[3]; int nbPointsInside = 0;
        Vertex3D[] ptsOutside = new Vertex3D[3]; int nbPointsOutside = 0;

        Vertex3D[] vertsIn = triIn.getVertices();

        // Classification of the point
        for (int i = 0; i < 3; i++) {
            double dDistPoint = this.signedDistanceToPoint(triIn.getVertices()[i]);
            if (dDistPoint >= 0) {
                ptsInside[nbPointsInside++]   = vertsIn[i];
            } else {
                ptsOutside[nbPointsOutside++] = vertsIn[i];
            }
        }

        if (nbPointsInside == 3) {
            // All points are inside the plane
            trisOut.add(triIn);
            return 1;
        }
        if (nbPointsInside == 1) {
            // The triangle simply become a smaller triangle
            Vertex3D[] vertsOut1 = new Vertex3D[3];

            vertsOut1[0] = ptsInside[0];
            vertsOut1[1] = this.intersectSegmentWithPlane(ptsInside[0],ptsOutside[0]);
            vertsOut1[2] = this.intersectSegmentWithPlane(ptsInside[0],ptsOutside[1]);

//            trisOut.add(new Triangle(vertsOut1, triIn.getColor()));
            trisOut.add(new Triangle(vertsOut1, Color.RED));

            return 1;
        }
        if (nbPointsInside == 2) {
            Vertex3D[] vertsOut1 = new Vertex3D[3];
            Vertex3D[] vertsOut2 = new Vertex3D[3];

            vertsOut1[0] = ptsInside[0];
            vertsOut1[1] = ptsInside[1];
            vertsOut1[2] = this.intersectSegmentWithPlane(ptsInside[0],ptsOutside[0]);

            vertsOut2[0] = ptsInside[1];
            vertsOut2[1] = this.intersectSegmentWithPlane(ptsInside[0],ptsOutside[0]);; // dans mon livre faut inverser les 2
            vertsOut2[2] = this.intersectSegmentWithPlane(ptsInside[1],ptsOutside[0]);

            trisOut.add(new Triangle(vertsOut1, triIn.getColor()));
            trisOut.add(new Triangle(vertsOut2, triIn.getColor()));
            trisOut.add(new Triangle(vertsOut1, Color.BLUE));
            trisOut.add(new Triangle(vertsOut2, Color.GREEN));

            return 2;
        }
        else {
            return 0;
        }
    }

    public double signedDistanceToPoint(Vertex3D pPoint) {
        Vertex3D vertPlanePoint = this.getOrigin();
        Vector3D vectPlaneNorm = this.getNormal();

        vectPlaneNorm.normalizeInPlace();

        return vectPlaneNorm.dotProduct(pPoint) - vertPlanePoint.dotProduct(vectPlaneNorm);
    }


    public Vertex3D intersectSegmentWithPlane(Vertex3D pLineStart, Vertex3D pLineEnd) {
        Vertex3D vertPlanePoint = this.getOrigin();
        Vector3D vectPlaneNorm = this.getNormal();

        vectPlaneNorm.normalizeInPlace();

        double dPlaneConstant = -vectPlaneNorm.dotProduct(vertPlanePoint);
        double ad = pLineStart.dotProduct(vectPlaneNorm);
        double bd = pLineEnd.dotProduct(vectPlaneNorm);

        double dLengthIntersecting = (-dPlaneConstant - ad) / (bd - ad);

        Vector3D vectLineStartToEnd = new Vector3D(pLineEnd.sub(pLineStart));
        Vector3D vectLineIntersecting = vectLineStartToEnd.scaled(dLengthIntersecting);

        return pLineStart.translated(vectLineIntersecting);
    }

    public Vertex3D getOrigin() {
        return origin;
    }

    public void setOrigin(Vertex3D origin) {
        this.origin = origin;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public void setNormal(Vector3D normal) {
        this.normal = normal;
    }
}
