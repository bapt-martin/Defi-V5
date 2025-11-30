package engine.math;
import static java.lang.Math.*;

public class Matrix {
    // ROW-MAJOR CONVENTION
    private double[][] matrix;

    public Matrix() {
        this.matrix = new double[4][4];
    }

    public Matrix(double[][] matrix) {
        this.matrix = new double[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, 4);
        }
    }

    public Matrix(int nbRow, int nbCol) {
        this.matrix = new double[nbRow][nbCol];
    }

    public static Matrix matCreateCamReferential(Vertex3D pTargetPosition, Vertex3D vTargetDirection, Vertex3D vUp) {
        Vertex3D pTranslatedTarget = Vertex3D.vertexAddition(pTargetPosition, vTargetDirection);

        //New forward direction
        Vertex3D vNewForward = Vertex3D.vertexSubtraction(pTranslatedTarget, pTargetPosition);
        vNewForward.convertToVector();
        vNewForward.vertNormalisation();

        //New up direction
        Vertex3D vScale = Vertex3D.verMultiplicationScalar(Vertex3D.dotProduct(vUp, vNewForward),vNewForward);
        Vertex3D vNewUp = Vertex3D.vertexSubtraction(vUp, vScale);
        vNewUp.vertNormalisation();
        vNewUp.convertToVector();

        //New right direction
        Vertex3D vNewRight = Vertex3D.crossProduct(vNewUp, vNewForward);
        vNewRight.vertNormalisation();
        vNewRight.convertToVector();


        double[][] matTemp = new double[4][4];
        matTemp[0][0] = vNewRight.getX();       matTemp[0][1] = vNewRight.getY();       matTemp[0][2] = vNewRight.getZ();       matTemp[0][3] = 0;
        matTemp[1][0] = vNewUp.getX();          matTemp[1][1] = vNewUp.getY();          matTemp[1][2] = vNewUp.getZ();          matTemp[1][3] = 0;
        matTemp[2][0] = vNewForward.getX();     matTemp[2][1] = vNewForward.getY();     matTemp[2][2] = vNewForward.getZ();     matTemp[2][3] = 0;
        matTemp[3][0] = pTargetPosition.getX(); matTemp[3][1] = pTargetPosition.getY(); matTemp[3][2] = pTargetPosition.getZ(); matTemp[3][3] = 1;

        return new Matrix(matTemp);
    }

    public static Matrix matQuickInverse(Matrix matIn){ //Only Rotation/Translation matrices
        double[][] mat = matIn.getMatrix();
        double[][] matInverse = new double[4][4];

        matInverse[0][0] = mat[0][0]; matInverse[0][1] = mat[1][0]; matInverse[0][2] = mat[2][0];
        matInverse[1][0] = mat[0][1]; matInverse[1][1] = mat[1][1]; matInverse[1][2] = mat[2][1];
        matInverse[2][0] = mat[0][2]; matInverse[2][1] = mat[1][2]; matInverse[2][2] = mat[2][2];

        matInverse[3][0] = -(mat[3][0] * matInverse[0][0] + mat[3][1] * matInverse[1][0] + mat[3][2] * matInverse[2][0]);
        matInverse[3][1] = -(mat[3][0] * matInverse[0][1] + mat[3][1] * matInverse[1][1] + mat[3][2] * matInverse[2][1]);
        matInverse[3][2] = -(mat[3][0] * matInverse[0][2] + mat[3][1] * matInverse[1][2] + mat[3][2] * matInverse[2][2]);
        matInverse[3][3] = 1;

        return new Matrix(matInverse);
    }

    public static Matrix matCreateIdentity(int nbRow, int nbCol) {
        double[][] matIdentity = new double[nbRow][nbCol];

        for (int i = 0; i< nbRow; i++) {
            for (int j= 0; j< nbCol; j++) {
                if (i == j) {
                    matIdentity[i][j] = 1;
                }
            }
        }

        return new Matrix(matIdentity);
    }

    public static Matrix matCreateRotationX4x4(double theta){
        double[][] matRotX = new double[4][4];

        matRotX[0][0] = 1;
        matRotX[1][1] = cos(theta);

        matRotX[1][2] = sin(theta);
        matRotX[2][1] = -sin(theta);

        matRotX[2][2] = cos(theta);
        matRotX[3][3] = 1;

        return new Matrix(matRotX);
    }

    public static Matrix matCreateRotationY4x4(double theta) {
        double[][] matRotY = new double[4][4];

        matRotY[0][0] = Math.cos(theta);
        matRotY[0][2] = Math.sin(theta);

        matRotY[1][1] = 1;
        matRotY[2][0] = -Math.sin(theta);

        matRotY[2][2] = Math.cos(theta);
        matRotY[3][3] = 1;

        return new Matrix(matRotY);
    }

    public static Matrix matCreateRotationZ4x4(double theta){
        double[][] matRotZ = new double[4][4];

        matRotZ[0][0] = cos(theta);
        matRotZ[0][1] = sin(theta);

        matRotZ[1][0] = -sin(theta);
        matRotZ[1][1] = cos(theta);

        matRotZ[2][2] = 1;
        matRotZ[3][3] = 1;

        return new Matrix(matRotZ);
    }

    public static Matrix matCreateRotationAroundAxis4x4(double theta, Vertex3D vAxis) {
        vAxis.vertNormalisation();
        vAxis.convertToVector();
        double[][] matRotationAxis = new double[4][4];

        double dUx = vAxis.getX();
        double dUy = vAxis.getY();
        double dUz = vAxis.getZ();

        double dCos = Math.cos(theta);
        double dSin = Math.sin(theta);

        matRotationAxis[0][0] = dCos + dUx*dUx*(1 - dCos);     matRotationAxis[0][1] = dUx*dUy*(1 - dCos) - dUz*dSin; matRotationAxis[0][2] = dUx*dUz*(1 - dCos) + dUy*dSin; matRotationAxis[0][3] = 0;
        matRotationAxis[1][0] = dUy*dUx*(1 - dCos) + dUz*dSin; matRotationAxis[1][1] = dCos + dUy*dUy*(1 - dCos);     matRotationAxis[1][2] = dUy*dUz*(1 - dCos) - dUx*dSin; matRotationAxis[1][3] = 0;
        matRotationAxis[2][0] = dUz*dUx*(1 - dCos) - dUy*dSin; matRotationAxis[2][1] = dUz*dUy*(1 - dCos) + dUx*dSin; matRotationAxis[2][2] = dCos + dUz*dUz*(1 - dCos);     matRotationAxis[2][3] = 0;
        matRotationAxis[3][0] = 0;                             matRotationAxis[3][1] = 0;                             matRotationAxis[3][2] = 0;                             matRotationAxis[3][3] = 1;

        return new Matrix(matRotationAxis);
    }

    public static Matrix matMultiplication4x4(Matrix matIn1, Matrix matIn2 ) {
        double[][] mat1 = matIn1.getMatrix();
        double[][] mat2 = matIn2.getMatrix();
        double[][] matResult = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                matResult[i][j] = mat1[i][0] * mat2[0][j]
                                + mat1[i][1] * mat2[1][j]
                                + mat1[i][2] * mat2[2][j]
                                + mat1[i][3] * mat2[3][j];
            }
        }

        return new Matrix(matResult);
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

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }
}
