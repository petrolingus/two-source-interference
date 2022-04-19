package me.petrolingus.modsys.twosourceinterference.utils;

import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;

    private final Vector3f rotation;

    private float zoom;

    public Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(33, -45, 0), 1.0f);
    }

    public Camera(Vector3f position, Vector3f rotation) {
        this(position, rotation, 1.0f);
    }

    public Camera(Vector3f position, Vector3f rotation, float zoom) {
        this.position = position;
        this.rotation = rotation;
        this.zoom = zoom;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public float getZoom() {
        return zoom;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        rotation.x += offsetX;
        rotation.y += offsetY;
        rotation.z += offsetZ;
    }

    public void addZoom(float zoom) {
        this.zoom += zoom;
    }
}

