package me.petrolingus.modsys.twosourceinterference.utils;

import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;

    float previousMouseWheelVelocity = 0;
    float currentMouseWheelVelocity = 0;
    float zoom = 0;
    boolean isZooming = false;

    private boolean inWindow = false;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    @SuppressWarnings("resource")
    public void init(long window) {
        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window, (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window, (windowHandle, button, action, mode) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
        glfwSetScrollCallback(window, (win, dx, dy) -> {
            if (dy > 0) {
                currentMouseWheelVelocity += 0.1f;
            } else {
                currentMouseWheelVelocity += -0.1f;
            }
            isZooming = true;
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
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

        zoom = 0;
        if (Math.abs(currentMouseWheelVelocity) < 1.0f) {
            zoom = currentMouseWheelVelocity - previousMouseWheelVelocity;
        }
        previousMouseWheelVelocity = currentMouseWheelVelocity;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public float getZoom() {
        return zoom;
    }
}
