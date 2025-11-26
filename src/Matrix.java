import static java.lang.Math.*;

public class Matrix {
    private double[][] matrix;

    public Matrix() {
        this.matrix = new double[4][4];
    }

    public Matrix(int nbRow, int nbCol) {
        this.matrix = new double[nbRow][nbCol];
    }

    public static Matrix matCreateCamReferentiel(Vertex3D vertPosition, Vertex3D vertTarget, Vertex3D vertUp) {
        //New forward direction
        Vertex3D vertNewForward = Vertex3D.vertexSubtraction(vertTarget, vertPosition);
        vertNewForward.vertexNormalisation();

        //New up direction
        Vertex3D a = Vertex3D.vertexMultiplication(Vertex3D.dotProduct(vertUp, vertNewForward),vertNewForward);
        Vertex3D vertNewUp = Vertex3D.vertexSubtraction(vertUp, a);
        vertNewUp.vertexNormalisation();

        //New right direction
        Vertex3D vertNewRight = Vertex3D.crossProduct(vertNewUp, vertNewForward);
        vertNewRight.vertexNormalisation();

        Matrix matPointAt = new Matrix();
        matPointAt.getMatrix()[0][0] = vertNewRight.getX();   matPointAt.getMatrix()[0][1] = vertNewRight.getY();   matPointAt.getMatrix()[0][2] = vertNewRight.getZ();   matPointAt.getMatrix()[0][3] = 0;
        matPointAt.getMatrix()[1][0] = vertNewUp.getX();      matPointAt.getMatrix()[1][1] = vertNewUp.getY();      matPointAt.getMatrix()[1][2] = vertNewUp.getZ();      matPointAt.getMatrix()[1][3] = 0;
        matPointAt.getMatrix()[2][0] = vertNewForward.getX(); matPointAt.getMatrix()[2][1] = vertNewForward.getY(); matPointAt.getMatrix()[2][2] = vertNewForward.getZ(); matPointAt.getMatrix()[2][3] = 0;
        matPointAt.getMatrix()[3][0] = vertPosition.getX();   matPointAt.getMatrix()[3][1] = vertPosition.getY();   matPointAt.getMatrix()[3][2] = vertPosition.getZ();   matPointAt.getMatrix()[3][3] = 1;

        return matPointAt;
    }

    public static Matrix matQuickInverse(Matrix matIn){ //Only Rotation/Translation matrices
        Matrix matInverse = new Matrix();

        matInverse.getMatrix()[0][0] = matIn.getMatrix()[0][0]; matInverse.getMatrix()[0][1] = matIn.getMatrix()[1][0]; matInverse.getMatrix()[0][2] = matIn.getMatrix()[2][0];
        matInverse.getMatrix()[1][0] = matIn.getMatrix()[0][1]; matInverse.getMatrix()[1][1] = matIn.getMatrix()[1][1]; matInverse.getMatrix()[1][2] = matIn.getMatrix()[2][1];
        matInverse.getMatrix()[2][0] = matIn.getMatrix()[0][2]; matInverse.getMatrix()[2][1] = matIn.getMatrix()[1][2]; matInverse.getMatrix()[2][2] = matIn.getMatrix()[2][2];

        matInverse.getMatrix()[3][0] = -(matIn.getMatrix()[3][0] * matInverse.getMatrix()[0][0] + matIn.getMatrix()[3][1] * matInverse.getMatrix()[1][0] + matIn.getMatrix()[3][2] * matInverse.getMatrix()[2][0]);
        matInverse.getMatrix()[3][1] = -(matIn.getMatrix()[3][0] * matInverse.getMatrix()[0][1] + matIn.getMatrix()[3][1] * matInverse.getMatrix()[1][1] + matIn.getMatrix()[3][2] * matInverse.getMatrix()[2][1]);
        matInverse.getMatrix()[3][2] = -(matIn.getMatrix()[3][0] * matInverse.getMatrix()[0][2] + matIn.getMatrix()[3][1] * matInverse.getMatrix()[1][2] + matIn.getMatrix()[3][2] * matInverse.getMatrix()[2][2]);
        matInverse.getMatrix()[3][3] = 1;

        return  matInverse;
    }

    public static Matrix matCreateIdentity(int nbRow, int nbCol) {
        Matrix matrix = new Matrix(nbRow, nbCol);
        for (int i = 0; i< nbRow; i++) {
            for (int j= 0; j< nbCol; j++) {
                if (i == j) {
                    matrix.getMatrix()[i][j] = 1;
                }
            }
        }

        return matrix;
    }

    public static Matrix matCreateRotationX4x4(double theta){
        Matrix matRotX = new Matrix();
        matRotX.getMatrix()[0][0] = 1;
        matRotX.getMatrix()[1][1] = cos(theta);
        matRotX.getMatrix()[1][2] = sin(theta);
        matRotX.getMatrix()[2][1] = -sin(theta);
        matRotX.getMatrix()[2][2] = cos(theta);
        matRotX.getMatrix()[3][3] = 1;

        return matRotX;
    }

    public static Matrix matCreateRotationY4x4(double theta) {
        Matrix matRotY = new Matrix();
        matRotY.getMatrix()[0][0] = Math.cos(theta);
        matRotY.getMatrix()[0][2] = Math.sin(theta);
        matRotY.getMatrix()[1][1] = 1;
        matRotY.getMatrix()[2][0] = -Math.sin(theta);
        matRotY.getMatrix()[2][2] = Math.cos(theta);
        matRotY.getMatrix()[3][3] = 1;

        return matRotY;
    }

    public static Matrix matCreateRotationZ4x4(double theta){
        Matrix matRotZ = new Matrix();
        matRotZ.getMatrix()[0][0] = cos(theta);
        matRotZ.getMatrix()[0][1] = sin(theta);
        matRotZ.getMatrix()[1][0] = -sin(theta);
        matRotZ.getMatrix()[1][1] = cos(theta);
        matRotZ.getMatrix()[2][2] = 1;
        matRotZ.getMatrix()[3][3] = 1;

        return matRotZ;
    }

    public static Matrix matCreateRotationAroundAxis4x4(double theta, Vertex3D axis) {
        axis.vertexNormalisation(); // To ensure the rotation axis is normalized

        double ux = axis.getX();
        double uy = axis.getY();
        double uz = axis.getZ();

        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        Matrix mat = new Matrix();

        mat.getMatrix()[0][0] = cos + ux*ux*(1 - cos);
        mat.getMatrix()[0][1] = ux*uy*(1 - cos) - uz*sin;
        mat.getMatrix()[0][2] = ux*uz*(1 - cos) + uy*sin;
        mat.getMatrix()[0][3] = 0;

        mat.getMatrix()[1][0] = uy*ux*(1 - cos) + uz*sin;
        mat.getMatrix()[1][1] = cos + uy*uy*(1 - cos);
        mat.getMatrix()[1][2] = uy*uz*(1 - cos) - ux*sin;
        mat.getMatrix()[1][3] = 0;

        mat.getMatrix()[2][0] = uz*ux*(1 - cos) - uy*sin;
        mat.getMatrix()[2][1] = uz*uy*(1 - cos) + ux*sin;
        mat.getMatrix()[2][2] = cos + uz*uz*(1 - cos);
        mat.getMatrix()[2][3] = 0;

        mat.getMatrix()[3][0] = 0;
        mat.getMatrix()[3][1] = 0;
        mat.getMatrix()[3][2] = 0;
        mat.getMatrix()[3][3] = 1;

        return mat;
    }




    public static Matrix matCreateProjection4x4(double fNear, double fFar, double fFov, int height, int width) {
        double q = fFar / (fFar - fNear);
        double aspectRatio = (double) width / height;
        double scalingFactorRad = 1 / tan(fFov * 0.5 / 180 * Math.PI);

        Matrix matProj = new Matrix();
        matProj.getMatrix()[0][0] = aspectRatio * scalingFactorRad;
        matProj.getMatrix()[1][1] = scalingFactorRad;
        matProj.getMatrix()[2][2] = q;
        matProj.getMatrix()[3][2] = - fNear * q;
        matProj.getMatrix()[2][3] = 1;
        matProj.getMatrix()[3][3] = 0;

        return matProj;
    }

    public static Matrix matMultiplication(Matrix mat1, Matrix mat2 ) {
        int nbRow = mat1.getMatrix().length;
        int nbCol = mat2.getMatrix()[0].length;

        Matrix matrix = new Matrix(nbRow, nbCol);
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbCol; j++) {
                matrix.getMatrix()[i][j] = mat1.getMatrix()[i][0] * mat2.getMatrix()[0][j] + mat1.getMatrix()[i][1] * mat2.getMatrix()[1][j] + mat1.getMatrix()[i][2] * mat2.getMatrix()[2][j] + mat1.getMatrix()[i][3] * mat2.getMatrix()[3][j];
            }
        }

        return matrix;
    }

    public static Matrix matCreateTranslation4x4(double x, double y, double z) {
        Matrix matrixTranslation = matCreateIdentity(4,4);

        matrixTranslation.getMatrix()[3][0] = x;
        matrixTranslation.getMatrix()[3][1] = y;
        matrixTranslation.getMatrix()[3][2] = z;

        return matrixTranslation;
    }

    public void matPrint() {
        for (int i = 0; i < 4; i++) {
            System.out.printf("| %8.4f %8.4f %8.4f %8.4f |\n",
                    this.getMatrix()[i][0], this.getMatrix()[i][1], this.getMatrix()[i][2], this.getMatrix()[i][3]);
        }
    }

    public double[][] getMatrix() {
        return matrix;
    }
}
