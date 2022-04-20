package me.petrolingus.modsys.twosourceinterference;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import me.petrolingus.modsys.twosourceinterference.utils.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglApplication {

    public static long window;


    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private final static Vector3f cameraInc = new Vector3f();
    private final static Camera camera = new Camera();
    private final static MouseInput mouseInput = new MouseInput();

    public static Canvas canvas;
    private static final int width = 800;
    private static final int height = 800;// Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    private static final int bpp = 4;
    private static final int whb = width * height * bpp;
    public static ByteBuffer buffer  = BufferUtils.createByteBuffer(whb);;
    private float between;

    public void run() throws Exception {
        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);

        // Create the window
        window = glfwCreateWindow(800, 800, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwHideWindow(window);

        mouseInput.init(window);
        camera.setPosition(2, 2, 2);
    }

    private void loop() throws Exception {

//        Service3d service3d = new Service3d(canvas);
//        service3d.start();

        GL.createCapabilities();

        // Create shader
        String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
        String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
        ShaderProgramV2 shaderProgram = new ShaderProgramV2(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("iGlobalTime");
        shaderProgram.createUniform("between");

        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

//        Matrix4f projectionMatrix = new Matrix4f().setPerspective(
//                (float) Math.toRadians(60.0f),
//                (float) 800 / 800,
//                0.01f,
//                1000.f);

        Matrix4f projectionMatrix = new Matrix4f().setOrtho(-1f, 1f, -1f, 1f, 0.01f, 1000.f);


        float t = 0;

        Mesh mesh = OBJLoader.loadMesh("/models/plane16x16.obj");

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CCW);

        while (!glfwWindowShouldClose(window)) {

            input();

            t += 0.05f;

            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

//            Matrix4f viewMatrix = new Matrix4f()
//                    .identity()
//                    .rotate((float) Math.toRadians(rotationX), new Vector3f(1, 0, 0))
//                    .rotate((float) Math.toRadians(rotationY), new Vector3f(0, 1, 0))
//                    .translate(-cameraPosX, -cameraPosY, -cameraPosZ);

//            Matrix4f viewMatrix = new Matrix4f().setLookAt(new Vector3f(cameraPosX, cameraPosY, cameraPosZ), origin, up);
////            cameraPosX = (float) Math.cos(0.1 * t);
////            cameraPosZ = (float) Math.sin(0.1 * t);

            // Update view Matrix
            Matrix4f viewMatrix = getViewMatrix(camera);

            shaderProgram.bind();
            {
                shaderProgram.setUniform("projectionMatrix", projectionMatrix);
                shaderProgram.setUniform("modelViewMatrix", viewMatrix);
                shaderProgram.setUniform("iGlobalTime", t);
                shaderProgram.setUniform("between", between);
                mesh.render();
            }
            shaderProgram.unbind();

            GL11.glReadBuffer(GL_FRONT_AND_BACK);
//            buffer = BufferUtils.createByteBuffer(whb);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

//        service3d.cancel();
    }

    private static void input() {

        mouseInput.input();

        // Input
        cameraInc.set(0, 0, 0);
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            cameraInc.z = -1;
        } else if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            cameraInc.z = 1;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            cameraInc.x = -1;
        } else if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            cameraInc.x = 1;
        }
        if (glfwGetKey(window, GLFW_KEY_Z) == GLFW_PRESS) {
            cameraInc.y = -1;
        } else if (glfwGetKey(window, GLFW_KEY_X) == GLFW_PRESS) {
            cameraInc.y = 1;
        }

        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        camera.addZoom(mouseInput.getZoom());

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }

    private static Matrix4f getViewMatrix(Camera camera) {

        float cameraZoom = camera.getZoom();
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        return new Matrix4f()
                .identity()
                .rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)
                .scale(cameraZoom);
    }

    public static void main(String[] args) throws Exception {
        new LwjglApplication().run();
    }

    public void setBetween(float value) {
        between = value;
    }
}
