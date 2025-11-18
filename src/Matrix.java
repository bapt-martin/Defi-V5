import static java.lang.Math.*;

public class Matrix {
    private double[][] matrix;

    public Matrix() {
        this.matrix = new double[4][4];
    }

    public Matrix(int nbRow, int nbCol) {
        this.matrix = new double[nbRow][nbCol];
    }

    public static Matrix matrixCreateIdentity(int nbRow, int nbCol) {
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

    public static Matrix matrixCreateRotationZ4x4 (double theta){
        Matrix matRotZ = new Matrix();
        matRotZ.getMatrix()[0][0] = cos(theta);
        matRotZ.getMatrix()[0][1] = sin(theta);
        matRotZ.getMatrix()[1][0] = -sin(theta);
        matRotZ.getMatrix()[1][1] = cos(theta);
        matRotZ.getMatrix()[2][2] = 1;
        matRotZ.getMatrix()[3][3] = 1;

        return matRotZ;
    }

    public static Matrix matrixCreateRotationX4x4 (double theta){
        Matrix matRotX = new Matrix();
        matRotX.getMatrix()[0][0] = 1;
        matRotX.getMatrix()[1][1] = cos(theta);
        matRotX.getMatrix()[1][2] = sin(theta);
        matRotX.getMatrix()[2][1] = -sin(theta);
        matRotX.getMatrix()[2][2] = cos(theta);
        matRotX.getMatrix()[3][3] = 1;

        return matRotX;
    }

    public static Matrix matrixCreateProjection4x4 (double fNear, double fFar, double fFov, int height, int width) {
        double q = fFar / (fFar - fNear);
        double aspectRatio = (double) height / width;
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

    public static Matrix matrixMultiplication (Matrix mat1, Matrix mat2 ) {
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

    public static Matrix matrixCreateTranslation4x4(double x,double y, double z) {
        Matrix matrixTranslation = matrixCreateIdentity(4,4);
        matrixTranslation.getMatrix()[3][0] = x;
        matrixTranslation.getMatrix()[3][1] = y;
        matrixTranslation.getMatrix()[3][2] = z;

        return matrixTranslation;
    }



    public double[][] getMatrix() {
        return matrix;
    }
}
