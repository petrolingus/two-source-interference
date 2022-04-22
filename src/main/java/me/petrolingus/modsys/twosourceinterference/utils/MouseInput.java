package me.petrolingus.modsys.twosourceinterference.utils;

import org.joml.Vector2d;
import org.joml.Vector2f;

public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    private boolean isDragged = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        if (isDragged && previousPos.x > 0 && previousPos.y > 0) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public void setMousePos(double xpos, double ypos) {
        currentPos.x = xpos;
        currentPos.y = ypos;
    }

    public void setDragged(boolean dragged) {
        isDragged = dragged;
    }
}
