package engine.math;
import static java.lang.Math.*;

public class Matrix {
    // ROW-MAJOR CONVENTION
    private double[][] matrix;

    public Matrix(int nbRow, int nbCol) {
        this.matrix = new double[nbRow][nbCol];
    }

    public Matrix() {
        this(new Matrix(4,4));
    }

    public Matrix(double[][] matrix) {
        this.matrix = new double[4][4];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(matrix[i], 0, this.matrix[i], 0, 4);
        }
    }

    public Matrix(Matrix other) {
        this(other.matrix);
    }

    public void copyFrom(Matrix other) {
        double[][] mat = other.getMatrix();
        int rows = mat.length;
        int cols = mat[0].length;

        for (int i = 0; i < rows; i++) {
            System.arraycopy(mat[i], 0, this.matrix[i], 0, cols);
        }
    }

    public void rotateBasisInPlace(Vector3D[] localAxes) {
        for (int i = 0; i<3; i++) {
            localAxes[i] = localAxes[i].transformInPlace(this);
            localAxes[i].normalizeInPlace();
        }
    }

    public static Matrix createProjectionMatrix(double far, double near, double fov, int width, int height) {
        double q = far / (far - near);
        double aspectRatio = (double) width / height;
        double scalingFactorRad = 1 / tan(fov * 0.5 / 180 * Math.PI);

        double[][] matProj = new double[4][4];
        matProj[0][0] = aspectRatio * scalingFactorRad;
        matProj[1][1] = scalingFactorRad;
        matProj[2][2] = q;
        matProj[3][3] = 0;

        matProj[3][2] = -near * q;
        matProj[2][3] = 1;

        return new Matrix(matProj);
    }

    public static Matrix createEulerRotation(double theta, double phi, double psi) {
        Matrix matRotX = Matrix.createRotationZ(theta);
        Matrix matRotY = Matrix.createRotationY(phi);
        Matrix matRotZ = Matrix.createRotationX(psi);

        return matRotX.multiply(matRotY).multiply(matRotZ);
    }

    public static Matrix createWorldTransformMatrix(double theta, double phi, double psi, double x, double y, double z) {
        Matrix matRotationTot = Matrix.createEulerRotation(theta,phi,psi);

        Matrix matTranslation = Matrix.createTranslation(x,y,z);

        return matRotationTot.multiply(matTranslation);
    }

    public static Matrix createViewMatrix(Vertex3D pTargetPosition, Vector3D vTargetDirection, Vector3D vUp) {
        Vertex3D pTranslatedTarget = pTargetPosition.translated(vTargetDirection);

        //New forward direction
        Vector3D vNewForward = pTranslatedTarget.sub(pTargetPosition);
        vNewForward.normalizeInPlace();

        //New up direction
        Vector3D vScale = vNewForward.scaled(vUp.dotProduct(vNewForward));
        Vector3D vNewUp = vUp.sub(vScale);
        vNewUp.normalizeInPlace();

        //New right direction
        Vector3D vNewRight = vNewUp.crossProduct(vNewForward);
        vNewRight.normalizeInPlace();

        double[][] matOut = new double[4][4];
        matOut[0][0] = vNewRight.getX();       matOut[0][1] = vNewRight.getY();       matOut[0][2] = vNewRight.getZ();       matOut[0][3] = 0;
        matOut[1][0] = vNewUp.getX();          matOut[1][1] = vNewUp.getY();          matOut[1][2] = vNewUp.getZ();          matOut[1][3] = 0;
        matOut[2][0] = vNewForward.getX();     matOut[2][1] = vNewForward.getY();     matOut[2][2] = vNewForward.getZ();     matOut[2][3] = 0;
        matOut[3][0] = pTargetPosition.getX(); matOut[3][1] = pTargetPosition.getY(); matOut[3][2] = pTargetPosition.getZ(); matOut[3][3] = 1;

        return new Matrix(matOut).invertRotationTranslation();
    }

    public Matrix invertRotationTranslation(){ //Only Rotation/Translation matrices
        double[][] mat = this.getMatrix();
        double[][] matInverse = new double[4][4];

        matInverse[0][0] = mat[0][0]; matInverse[0][1] = mat[1][0]; matInverse[0][2] = mat[2][0];
        matInverse[1][0] = mat[0][1]; matInverse[1][1] = mat[1][1]; matInverse[1][2] = mat[2][1];
        matInverse[2][0] = mat[0][2]; matInverse[2][1] = mat[1][2]; matInverse[2][2] = mat[2][2];
                                                                                                  matInverse[3][3] = 1;

        matInverse[3][0] = -(mat[3][0] * matInverse[0][0] + mat[3][1] * matInverse[1][0] + mat[3][2] * matInverse[2][0]);
        matInverse[3][1] = -(mat[3][0] * matInverse[0][1] + mat[3][1] * matInverse[1][1] + mat[3][2] * matInverse[2][1]);
        matInverse[3][2] = -(mat[3][0] * matInverse[0][2] + mat[3][1] * matInverse[1][2] + mat[3][2] * matInverse[2][2]);

        this.setMatrix(matInverse);

        return this;
    }

    public static Matrix createIdentity(int nbRow, int nbCol) {
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

    public static Matrix createRotationX(double theta){
        double[][] matRotX = new double[4][4];

        matRotX[0][0] = 1;
        matRotX[1][1] = cos(theta);

        matRotX[1][2] = sin(theta);
        matRotX[2][1] = -sin(theta);

        matRotX[2][2] = cos(theta);
        matRotX[3][3] = 1;

        return new Matrix(matRotX);
    }

    public static Matrix createRotationY(double theta) {
        double[][] matRotY = new double[4][4];

        matRotY[0][0] = Math.cos(theta);
        matRotY[0][2] = Math.sin(theta);

        matRotY[1][1] = 1;
        matRotY[2][0] = -Math.sin(theta);

        matRotY[2][2] = Math.cos(theta);
        matRotY[3][3] = 1;

        return new Matrix(matRotY);
    }

    public static Matrix createRotationZ(double theta){
        double[][] matRotZ = new double[4][4];

        matRotZ[0][0] = cos(theta);
        matRotZ[0][1] = sin(theta);

        matRotZ[1][0] = -sin(theta);
        matRotZ[1][1] = cos(theta);

        matRotZ[2][2] = 1;
        matRotZ[3][3] = 1;

        return new Matrix(matRotZ);
    }

    public static Matrix createRotationAroundAxis(double theta, Vector3D vAxis) {
        vAxis.normalizeInPlace();
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

    public Matrix multiply(Matrix matIn2) {
        double[][] mat1 = this.getMatrix();
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

    public static Matrix createTranslation(double x, double y, double z) {
        double[][] matrixTranslation = createIdentity(4,4).getMatrix();

        matrixTranslation[3][0] = x;
        matrixTranslation[3][1] = y;
        matrixTranslation[3][2] = z;

        return new Matrix(matrixTranslation);
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
