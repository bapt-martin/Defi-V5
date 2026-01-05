package graphicEngine.scene;

import graphicEngine.math.geometry.Mesh;
import graphicEngine.math.tools.Matrix;
import graphicEngine.math.tools.Vector3D;

public class GameObject {
    private Mesh mesh;
    private Matrix worldTransformMatrix;
    private String name;

    private Vector3D scale;
    private Vector3D rotation;
    private Vector3D position;

    private boolean isDirty = true;

    public GameObject(Mesh mesh) {
        this.mesh = mesh;
        this.scale    = new Vector3D(1,1,1);
        this.rotation = new Vector3D();
        this.position = new Vector3D();
        this.updateWorldTransformMatrix();
    }

    public void updateWorldTransformMatrix() {
        this.worldTransformMatrix = Matrix.createWorldTransformMatrix(
                scale.getX(),    scale.getY(),    scale.getZ(),
                rotation.getX(), rotation.getY(), rotation.getZ(),
                position.getX(), position.getY(), position.getZ()
        );
    }

    public void setScale(double x, double y, double z) {
        this.scale = new Vector3D(x, y, z);
        this.isDirty = true;
    }

    public void setPosition(double x, double y, double z) {
        this.position = new Vector3D(-x, y, z);
        this.isDirty = true;
    }

    public void setRotation(double x, double y, double z) {
        this.rotation = new Vector3D(x, y, z);
        this.isDirty = true;
    }

    public void move(double dx, double dy, double dz) {
        this.position = this.position.add(new Vector3D(dx, dy, dz));
        this.isDirty = true;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Matrix getWorldTransformMatrix() {
        if (isDirty) {
            updateWorldTransformMatrix();
            isDirty = false;
        }
        return worldTransformMatrix;
    }

    public void setWorldTransformMatrix(Matrix worldTransformMatrix) {
        this.worldTransformMatrix = worldTransformMatrix;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
