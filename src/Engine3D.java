import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;

import static java.lang.Boolean.TRUE;
import static java.lang.Math.*;

public class Engine3D extends JPanel {
    private Mesh meshCube;
    private int width;
    private int height;
    private Timer elapsedTime;
    private double theta;
    private Vertex3D vCamera;

    public Engine3D(int width, int height) {
        this.width = width;
        this.height = height;
        this.theta = 0;
        this.vCamera = new Vertex3D();

        this.meshCube = new Mesh();
        setBackground(Color.BLACK);

        this.elapsedTime = new Timer(16, e -> repaint());
        this.elapsedTime.start();


        Color colorCube = new Color(255, 255 , 255, 255);
        //SOUTH
        /**this.meshCube.getTris().add(new Triangle(new Vertex3D(0,0,0), new Vertex3D(0,1,0), new Vertex3D(1,1,0)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(0,0,0), new Vertex3D(1,1,0), new Vertex3D(1,0,0)));

        //EAST
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,0), new Vertex3D(1,1,0), new Vertex3D(1,1,1)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,0), new Vertex3D(1,1,1), new Vertex3D(1,0,1)));

        //NORTH
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,1), new Vertex3D(1,1,1), new Vertex3D(0,1,1)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,1), new Vertex3D(0,1,1), new Vertex3D(0,0,1)));

        //WEST
        this.meshCube.getTris().add(new Triangle(new Vertex3D(0,0,1), new Vertex3D(0,1,1), new Vertex3D(0,1,0)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(0,0,1), new Vertex3D(0,1,0), new Vertex3D(0,0,0)));

        //TOP
        this.meshCube.getTris().add(new Triangle(new Vertex3D(0,1,0), new Vertex3D(0,1,1), new Vertex3D(1,1,1)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(0,1,0), new Vertex3D(1,1,1), new Vertex3D(1,1,0)));

        //BOTTOM
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,1), new Vertex3D(0,0,1), new Vertex3D(0,0,0)));
        this.meshCube.getTris().add(new Triangle(new Vertex3D(1,0,1), new Vertex3D(0,0,0), new Vertex3D(1,0,0)));
*/
        Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj"),this.meshCube);
        this.meshCube.triConstruct();
        System.out.println(this.meshCube.toString());
        //Projection matrix coeefcicent value(a revoir)
        double fNear = 0.1;
        double fFar = 1000;
        double q = fFar / (fFar - fNear);
        double fFov = 90;
        double aspectRatio = (double) this.height / this.width;
        double scalingFactorRad = 1 / tan(fFov * 0.5 / 180 * Math.PI);

        //Projection matrix coeffecient defnition
        this.meshCube.setMatProj(0,0,aspectRatio * scalingFactorRad);
        this.meshCube.setMatProj(1,1,scalingFactorRad);
        this.meshCube.setMatProj(2,2,q);
        this.meshCube.setMatProj(3,2,- fNear * q);
        this.meshCube.setMatProj(2,3,1);
        this.meshCube.setMatProj(3,3,0);

        //printMatrix(this.meshCube.getMatProj());
    }

    public void printMatrix(double[][] m) {
        for (int i = 0; i < 4; i++) {
            System.out.printf("| %8.4f %8.4f %8.4f %8.4f |\n",
                    m[i][0], m[i][1], m[i][2], m[i][3]);
        }
    }

    public void multiplyMatrixVector(Vertex3D vertIn, Vertex3D vertOut, double[][] mat) {
        vertOut.setX((vertIn.getX() * mat[0][0] + vertIn.getY() * mat[1][0] + vertIn.getZ() * mat[2][0] + mat[3][0]));
        vertOut.setY((vertIn.getX() * mat[0][1] + vertIn.getY() * mat[1][1] + vertIn.getZ() * mat[2][1] + mat[3][1]));
        vertOut.setZ((vertIn.getX() * mat[0][2] + vertIn.getY() * mat[1][2] + vertIn.getZ() * mat[2][2] + mat[3][2]));
        double w = vertIn.getX() * mat[0][3] + vertIn.getY() * mat[1][3] + vertIn.getZ() * mat[2][3] + mat[3][3];

        if (w != 0) {
            vertOut.setX((vertOut.getX() / w));
            vertOut.setY((vertOut.getY() / w));
            vertOut.setZ((vertOut.getZ() / w));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        double[][] matRotZ = new double[4][4];
        double[][] matRotX = new double[4][4];
        this.theta += 0.05;
        System.out.println(this.theta);

        //rotation Z
        matRotZ[0][0] = cos(theta);
        matRotZ[0][1] = sin(theta);
        matRotZ[1][0] = -sin(theta);
        matRotZ[1][1] = cos(theta);
        matRotZ[2][2] = 1;
        matRotZ[3][3] = 1;

        //rotation X
        matRotX[0][0] = 1;
        matRotX[1][1] = cos(theta * 0.5);
        matRotX[1][2] = sin(theta * 0.5);
        matRotX[2][1] = -sin(theta * 0.5);
        matRotX[2][2] = cos(theta * 0.5);
        matRotX[3][3] = 1;

        for (Triangle t : meshCube.getTris()) {
            //triangle projection
            Triangle triProjected = new Triangle();
            Triangle triRotatedZ = new Triangle();
            Triangle triRotatedZX = new Triangle();

            //Z axis Rotation
            multiplyMatrixVector(t.getVertices()[0],triRotatedZ.getVertices()[0],matRotZ);
            multiplyMatrixVector(t.getVertices()[1],triRotatedZ.getVertices()[1],matRotZ);
            multiplyMatrixVector(t.getVertices()[2],triRotatedZ.getVertices()[2],matRotZ);

            //X axis Rotation
            multiplyMatrixVector(triRotatedZ.getVertices()[0],triRotatedZX.getVertices()[0],matRotX);
            multiplyMatrixVector(triRotatedZ.getVertices()[1],triRotatedZX.getVertices()[1],matRotX);
            multiplyMatrixVector(triRotatedZ.getVertices()[2],triRotatedZX.getVertices()[2],matRotX);


            //Z axis Offset
            Triangle triTranslated  = triRotatedZX;
            triTranslated.getVertices()[0].setZ(triTranslated.getVertices()[0].getZ() + 3);
            triTranslated.getVertices()[1].setZ(triTranslated.getVertices()[1].getZ() + 3);
            triTranslated.getVertices()[2].setZ(triTranslated.getVertices()[2].getZ() + 3);

            //Line calculation (a revoir)
            double xLine1 = triTranslated.getVertices()[1].getX() - triTranslated.getVertices()[0].getX();
            double yLine1 = triTranslated.getVertices()[1].getY() - triTranslated.getVertices()[0].getY();
            double zLine1 = triTranslated.getVertices()[1].getZ() - triTranslated.getVertices()[0].getZ();

            double xLine2 = triTranslated.getVertices()[2].getX() - triTranslated.getVertices()[0].getX();
            double yLine2 = triTranslated.getVertices()[2].getY() - triTranslated.getVertices()[0].getY();
            double zLine2 = triTranslated.getVertices()[2].getZ() - triTranslated.getVertices()[0].getZ();

            Vertex3D line1 = new Vertex3D(xLine1,yLine1,zLine1);
            Vertex3D line2 = new Vertex3D(xLine2,yLine2,zLine2);

            Vertex3D normal = Vertex3D.crossProduct(line1,line2);
            normal.vertexNormalisation();

            //if (normal.getZ() < 0) {
            if (Vertex3D.dotProduct(normal,
                    new Vertex3D(
                            triTranslated.getVertices()[0].getX() - vCamera.getX(),
                            triTranslated.getVertices()[0].getY() - vCamera.getY(),
                            triTranslated.getVertices()[0].getZ() - vCamera.getZ())) < 0) {

                Vertex3D lighDirection = new Vertex3D(0,0,-1);
                lighDirection.vertexNormalisation();

                double dpLightNorm = Vertex3D.dotProduct(normal,lighDirection);
                Color colorTri = Triangle.grayScale(dpLightNorm);

                triTranslated.setColor(colorTri);

                //Projecting 3D into 2D
                multiplyMatrixVector(triTranslated.getVertices()[0],triProjected.getVertices()[0], this.meshCube.getMatProj());
                multiplyMatrixVector(triTranslated.getVertices()[1],triProjected.getVertices()[1], this.meshCube.getMatProj());
                multiplyMatrixVector(triTranslated.getVertices()[2],triProjected.getVertices()[2], this.meshCube.getMatProj());
                triProjected.setColor(triTranslated.getColor()); //Color transfer

                //Centralisation
                triProjected.getVertices()[0].setX(triProjected.getVertices()[0].getX() + 1);
                triProjected.getVertices()[0].setY(triProjected.getVertices()[0].getY() + 1);

                triProjected.getVertices()[1].setX(triProjected.getVertices()[1].getX() + 1);
                triProjected.getVertices()[1].setY(triProjected.getVertices()[1].getY() + 1);

                triProjected.getVertices()[2].setX(triProjected.getVertices()[2].getX() + 1);
                triProjected.getVertices()[2].setY(triProjected.getVertices()[2].getY() + 1);

                //Scaling to screen dimension
                triProjected.getVertices()[0].setX(triProjected.getVertices()[0].getX() * 0.5 * getWidth());
                triProjected.getVertices()[0].setY(triProjected.getVertices()[0].getY() * 0.5 * getHeight());

                triProjected.getVertices()[1].setX(triProjected.getVertices()[1].getX() * 0.5 * getWidth());
                triProjected.getVertices()[1].setY(triProjected.getVertices()[1].getY() * 0.5 * getHeight());

                triProjected.getVertices()[2].setX(triProjected.getVertices()[2].getX() * 0.5 * getWidth());
                triProjected.getVertices()[2].setY(triProjected.getVertices()[2].getY() * 0.5 * getHeight());
            }

            Vertex3D[] v = triProjected.getVertices();

            int[] xs = new int[3];
            int[] ys = new int[3];

            for (int i = 0; i < 3; i++) {
                xs[i] = (int) Math.round(v[i].getX());
                ys[i] = (int) Math.round(v[i].getY());
            }

            //g.setColor(Color.WHITE);
            g.setColor(triProjected.getColor());
            Graphics2D g2 = (Graphics2D) g;
            //g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.fillPolygon(xs, ys, 3); //Rasterization pas encore faites

        }

    }

    public Timer getElapsedTime() {
        return elapsedTime;
    }

    public Mesh getMeshCube() {
        return meshCube;
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("3D Engine");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        int width = 800;
        int height = 600;
        window.setSize(width, height);

        Engine3D engine3D = new Engine3D(width, height);
        window.add(engine3D);              // d'abord ajouter le panel
        window.setLocationRelativeTo(null); // centrer la fenêtre
        window.setVisible(true);// puis afficher
    }
}
