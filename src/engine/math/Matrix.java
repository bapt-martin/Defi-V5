package engine.math;
import static java.lang.Math.*;

public class Matrix {
    private double[][] matrix;

    public Matrix() {
        this.matrix = new double[4][4];
    }

    public Matrix(int nbRow, int nbCol) {
        this.matrix = new double[nbRow][nbCol];
    }

    public static Matrix matCreateCamReferentiel(Vertex3D pPosition, Vertex3D vTarget, Vertex3D vUp) {
        Vertex3D pTranslatedTarget = Vertex3D.vertexAddition(pPosition, vTarget);

        //New forward direction
        Vertex3D vNewForward = Vertex3D.vertexSubtraction(pTranslatedTarget, pPosition);
        vNewForward.vertexNormalisation();

        //New up direction
        Vertex3D vScale = Vertex3D.vertexMultiplication(Vertex3D.dotProduct(vUp, vNewForward),vNewForward);
        Vertex3D vNewUp = Vertex3D.vertexSubtraction(vUp, vScale);
        vNewUp.vertexNormalisation();

        //New right direction
        Vertex3D vNewRight = Vertex3D.crossProduct(vNewUp, vNewForward);
        vNewRight.vertexNormalisation();

        Matrix matPointAt = new Matrix();
        matPointAt.getMatrix()[0][0] = vNewRight.getX();   matPointAt.getMatrix()[0][1] = vNewRight.getY();   matPointAt.getMatrix()[0][2] = vNewRight.getZ();   matPointAt.getMatrix()[0][3] = 0;
        matPointAt.getMatrix()[1][0] = vNewUp.getX();      matPointAt.getMatrix()[1][1] = vNewUp.getY();      matPointAt.getMatrix()[1][2] = vNewUp.getZ();      matPointAt.getMatrix()[1][3] = 0;
        matPointAt.getMatrix()[2][0] = vNewForward.getX(); matPointAt.getMatrix()[2][1] = vNewForward.getY(); matPointAt.getMatrix()[2][2] = vNewForward.getZ(); matPointAt.getMatrix()[2][3] = 0;
        matPointAt.getMatrix()[3][0] = pPosition.getX();   matPointAt.getMatrix()[3][1] = pPosition.getY();   matPointAt.getMatrix()[3][2] = pPosition.getZ();   matPointAt.getMatrix()[3][3] = 1;

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
        Matrix matIdentity = new Matrix(nbRow, nbCol);

        for (int i = 0; i< nbRow; i++) {
            for (int j= 0; j< nbCol; j++) {
                if (i == j) {
                    matIdentity.getMatrix()[i][j] = 1;
                }
            }
        }

        return matIdentity;
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

    public static Matrix matCreateRotationAroundAxis4x4(double theta, Vertex3D vAxis) {
        vAxis.vertexNormalisation();

        double dUx = vAxis.getX();
        double dUy = vAxis.getY();
        double dUz = vAxis.getZ();

        double dCos = Math.cos(theta);
        double dSin = Math.sin(theta);

        Matrix matRotationAxis = new Matrix();

        matRotationAxis.getMatrix()[0][0] = dCos + dUx*dUx*(1 - dCos);
        matRotationAxis.getMatrix()[0][1] = dUx*dUy*(1 - dCos) - dUz*dSin;
        matRotationAxis.getMatrix()[0][2] = dUx*dUz*(1 - dCos) + dUy*dSin;
        matRotationAxis.getMatrix()[0][3] = 0;

        matRotationAxis.getMatrix()[1][0] = dUy*dUx*(1 - dCos) + dUz*dSin;
        matRotationAxis.getMatrix()[1][1] = dCos + dUy*dUy*(1 - dCos);
        matRotationAxis.getMatrix()[1][2] = dUy*dUz*(1 - dCos) - dUx*dSin;
        matRotationAxis.getMatrix()[1][3] = 0;

        matRotationAxis.getMatrix()[2][0] = dUz*dUx*(1 - dCos) - dUy*dSin;
        matRotationAxis.getMatrix()[2][1] = dUz*dUy*(1 - dCos) + dUx*dSin;
        matRotationAxis.getMatrix()[2][2] = dCos + dUz*dUz*(1 - dCos);
        matRotationAxis.getMatrix()[2][3] = 0;

        matRotationAxis.getMatrix()[3][0] = 0;
        matRotationAxis.getMatrix()[3][1] = 0;
        matRotationAxis.getMatrix()[3][2] = 0;
        matRotationAxis.getMatrix()[3][3] = 1;

        return matRotationAxis;
    }


    public static Matrix matMultiplication(Matrix mat1, Matrix mat2 ) {
        int nbRow = mat1.getMatrix().length;
        int nbCol = mat2.getMatrix()[0].length;

        Matrix matResult = new Matrix(nbRow, nbCol);
        for (int i = 0; i < nbRow; i++) {
            for (int j = 0; j < nbCol; j++) {
                matResult.getMatrix()[i][j] = mat1.getMatrix()[i][0] * mat2.getMatrix()[0][j] + mat1.getMatrix()[i][1] * mat2.getMatrix()[1][j] + mat1.getMatrix()[i][2] * mat2.getMatrix()[2][j] + mat1.getMatrix()[i][3] * mat2.getMatrix()[3][j];
            }
        }

        return matResult;
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
