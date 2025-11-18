import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Engine3D extends JPanel {
    private Mesh meshCube;
    private int width;
    private int height;
    private double theta;
    private Vertex3D vCamera;
    private Timer elapsedTime;

    public Engine3D(int width, int height) {
        this.width = width;
        this.height = height;
        this.theta = 0;
        this.vCamera = new Vertex3D();

        this.meshCube = new Mesh();
        setBackground(Color.BLACK);

        this.elapsedTime = new Timer(16, e -> repaint());
        this.elapsedTime.start();


        //.OBJ file reading + construction of the 3D triangle to render
        //Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\premier test.obj"),this.meshCube);

        Document.readObjFile(Paths.get("C:\\Users\\marti\\Desktop\\test2.obj"),this.meshCube);
        this.meshCube.triConstruct();

        //Projection matrix coefficient value(a require)
        double fNear = 0.1;
        double fFar = 1000;
        double fFov = 90;

        //Projection matrix coefficient definition
        this.meshCube.setMatProj(Matrix.matrixCreateProjection4x4(fNear,fFar,fFov,height,width));

        //printMatrix(this.meshCube.getMatProj());
    }

    public void printMatrix(double[][] m) {
        for (int i = 0; i < 4; i++) {
            System.out.printf("| %8.4f %8.4f %8.4f %8.4f |\n",
                    m[i][0], m[i][1], m[i][2], m[i][3]);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.theta += 0.05;
        System.out.println(this.theta);

        //Rotation matrices Z-axis
        Matrix matRotZ = Matrix.matrixCreateRotationZ4x4(theta * 0.5);

        //Rotation matrices X-axis
        Matrix matRotX = Matrix.matrixCreateRotationX4x4(theta);

        //Z-Axis Offset
        Matrix matTrans = Matrix.matrixMultiplication(matRotZ, matRotX);

        Matrix matWorld = Matrix.matrixMultiplication(matTrans,Matrix.matrixCreateTranslation4x4(0,0,-16));

        List<Triangle> trisToRaster = new ArrayList<Triangle>();
        //Triangle projection and drawing
        for (Triangle triangleToProject : meshCube.getTris()) {

            Triangle triTransformed = new Triangle();
            //Z-axis and X-axis Rotation
            Triangle triRotatedZX = new Triangle();

            triangleToProject.getVertices()[0].vertexMatrixMultiplication(triTransformed.getVertices()[0],matWorld);
            triangleToProject.getVertices()[1].vertexMatrixMultiplication(triTransformed.getVertices()[1],matWorld);
            triangleToProject.getVertices()[2].vertexMatrixMultiplication(triTransformed.getVertices()[2],matWorld);

//            //Z-axis Offset
//            Triangle triTranslated;
//            triTranslated = triRotatedZX;
//
//            triTranslated.getVertices()[0].setZ(triTranslated.getVertices()[0].getZ() + 8.5);
//            triTranslated.getVertices()[1].setZ(triTranslated.getVertices()[1].getZ() + 8.5);
//            triTranslated.getVertices()[2].setZ(triTranslated.getVertices()[2].getZ() + 8.5);

            //Line calculation (a revoir)
//            double xLine1 = triTransformed.getVertices()[1].getX() - triTransformed.getVertices()[0].getX();
//            double yLine1 = triTransformed.getVertices()[1].getY() - triTransformed.getVertices()[0].getY();
//            double zLine1 = triTransformed.getVertices()[1].getZ() - triTransformed.getVertices()[0].getZ();
//
//            double xLine2 = triTransformed.getVertices()[2].getX() - triTransformed.getVertices()[0].getX();
//            double yLine2 = triTransformed.getVertices()[2].getY() - triTransformed.getVertices()[0].getY();
//            double zLine2 = triTransformed.getVertices()[2].getZ() - triTransformed.getVertices()[0].getZ();

            Vertex3D line1 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[1],triTransformed.getVertices()[0]);
            Vertex3D line2 = Vertex3D.vertexSubtraction(triTransformed.getVertices()[2],triTransformed.getVertices()[0]);

            Vertex3D normal = Vertex3D.crossProduct(line1,line2);
            normal.vertexNormalisation();

            Vertex3D vCameraRay = Vertex3D.vertexSubtraction(triTransformed.getVertices()[0],vCamera);


            Triangle triProjected = new Triangle();
            if (Vertex3D.dotProduct(normal, vCameraRay)< 0) {

                Vertex3D lighDirection = new Vertex3D(0,0,-1); //Pseudo definition of the light source
                lighDirection.vertexNormalisation();

                double dpLightNorm = Vertex3D.dotProduct(normal,lighDirection);
                Color colorTri = Triangle.grayScale(dpLightNorm);

                triTransformed.setColor(colorTri); //Definition of the greyscale value for the triangle regarding its orienttion

                //Projecting 3D into 2D
                triTransformed.getVertices()[0].vertexMatrixMultiplication(triProjected.getVertices()[0], this.meshCube.getMatProj());
                triTransformed.getVertices()[1].vertexMatrixMultiplication(triProjected.getVertices()[1], this.meshCube.getMatProj());
                triTransformed.getVertices()[2].vertexMatrixMultiplication(triProjected.getVertices()[2], this.meshCube.getMatProj());
                triProjected.setColor(triTransformed.getColor()); //Color transfer

                triProjected.getVertices()[0].vertexDivision(triTransformed.getVertices()[0].getW());
                triProjected.getVertices()[1].vertexDivision(triTransformed.getVertices()[1].getW());
                triProjected.getVertices()[2].vertexDivision(triTransformed.getVertices()[2].getW());

                //Offset into visible normalised space
                Vertex3D vOffsetView = new Vertex3D(1,1,0);

                triProjected.getVertices()[0] = Vertex3D.vertexAddition(triProjected.getVertices()[0], vOffsetView );
                triProjected.getVertices()[1] = Vertex3D.vertexAddition(triProjected.getVertices()[1], vOffsetView );
                triProjected.getVertices()[2] = Vertex3D.vertexAddition(triProjected.getVertices()[2], vOffsetView );


                //Scaling to screen dimension
                triProjected.getVertices()[0].setX(triProjected.getVertices()[0].getX() * 0.5 * getWidth());
                triProjected.getVertices()[0].setY(triProjected.getVertices()[0].getY() * 0.5 * getHeight());

                triProjected.getVertices()[1].setX(triProjected.getVertices()[1].getX() * 0.5 * getWidth());
                triProjected.getVertices()[1].setY(triProjected.getVertices()[1].getY() * 0.5 * getHeight());

                triProjected.getVertices()[2].setX(triProjected.getVertices()[2].getX() * 0.5 * getWidth());
                triProjected.getVertices()[2].setY(triProjected.getVertices()[2].getY() * 0.5 * getHeight());

                //Save for later rasterization
                trisToRaster.add(triProjected);
            }
        }

        trisToRaster.sort((t1, t2) -> {
            double meanZ1 = (t1.getVertices()[0].getZ() + t1.getVertices()[1].getZ() + t1.getVertices()[2].getZ()) / 3;
            double meanZ2 = (t2.getVertices()[0].getZ() + t2.getVertices()[1].getZ() + t2.getVertices()[2].getZ()) / 3;
            return Double.compare(meanZ2,meanZ1);
        });

        for (Triangle triBeingDrawn : trisToRaster) {
            Vertex3D[] v = triBeingDrawn.getVertices();

            int[] xs = new int[3];
            int[] ys = new int[3];

            for (int i = 0; i < 3; i++) {
                xs[i] = (int) Math.round(v[i].getX());
                ys[i] = (int) Math.round(v[i].getY());
            } //Getting back the coordinate to draw the 2D triangle

            g.setColor(triBeingDrawn.getColor()); //Setting the correct color

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
