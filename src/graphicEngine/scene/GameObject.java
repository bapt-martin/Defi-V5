package graphicEngine.scene;

import graphicEngine.math.geometry.Mesh;
import graphicEngine.math.tools.Matrix;

public class GameObject {
    private Mesh mesh;
    private Matrix worldTransformMatrix;

    public GameObject(Mesh mesh, Matrix worldTransformMatrix) {
        this.mesh = mesh;
        this.worldTransformMatrix = worldTransformMatrix;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Matrix getWorldTransformMatrix() {
        return worldTransformMatrix;
    }

    public void setWorldTransformMatrix(Matrix worldTransformMatrix) {
        this.worldTransformMatrix = worldTransformMatrix;
    }
}
