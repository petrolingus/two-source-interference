package me.petrolingus.modsys.twosourceinterference;

import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import me.petrolingus.modsys.twosourceinterference.utils.*;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglApplication {

    public static long window;

    private final static Camera camera = new Camera();

    public static Canvas canvas;
    private static final int width = 800;
    private static final int height = 800;// Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
    private static final int bpp = 4;
    private static final int whb = width * height * bpp;
    public static ByteBuffer buffer = BufferUtils.createByteBuffer(whb);
    private float between;

    private float amplitude;
    private float cyclicFrequency;
    private float wavelength;
    private float initialPhase;
    private float timeMul;
    private float colorDelimiter;
    private Vector3f minColor;
    private Vector3f maxColor;
    private boolean is3DActive;

    private boolean isDistanceChanged = true;

    private MouseInput mouseInput;

    public void setMouseInput(MouseInput mouseInput) {
        this.mouseInput = mouseInput;
    }

    private Vector3f angle;

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

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwHideWindow(window);
    }

    private void loop() throws Exception {

        // OpenGL config
        GL.createCapabilities();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL_STENCIL_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CCW);
        GL11.glClearColor(0.8f, 0.8f, 0.8f, 1.0f);

        // Create shader
        String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
        String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
        ShaderProgram shaderProgram = new ShaderProgram(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("iGlobalTime");
        shaderProgram.createUniform("amplitude");
        shaderProgram.createUniform("wavelength");
        shaderProgram.createUniform("cyclicFrequency");
        shaderProgram.createUniform("initialPhase");
        shaderProgram.createUniform("between");
        shaderProgram.createUniform("timeMul");

        // Create mesh of plane
        Mesh mesh = OBJLoader.loadMesh("/models/plane.obj");

        // Initial camera position
        double theta = Math.toRadians(60);
        double phi = Math.toRadians(45);

        // Global timer
        float t = 0;

        // Create FloatBuffer for pass vertex height to vertex shader
        float[] positions = mesh.getPositions();
        int positionsLength = positions.length;
        FloatBuffer positionsFloatBuffer = MemoryUtil.memAllocFloat(positionsLength);
        positionsFloatBuffer.put(positions).flip();

        float[] distancesToSource1 = new float[positionsLength];
        float[] distancesToSource2 = new float[positionsLength];

        while (!glfwWindowShouldClose(window)) {

            mouseInput.input();

            GL11.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

            // If source distance changed we need to recalculate distances
            if (isDistanceChanged) {
                float pos = (float) Math.sqrt(between * between / 2);
                for (int i = 0; i < positionsLength; i += 3) {
                    float x = 800 * positions[i];
                    float z = 800 * positions[i + 2];
                    distancesToSource1[i] = (float) Math.sqrt(Math.pow(x - pos, 2) + Math.pow(z + pos, 2));
                    distancesToSource2[i] = (float) Math.sqrt(Math.pow(x + pos, 2) + Math.pow(z - pos, 2));
                }
                isDistanceChanged = false;
            }

            double realTime = t * timeMul;
            double k = 2 * Math.PI / wavelength;
            for (int i = 0; i < positionsLength; i += 3) {

                double dist1 = distancesToSource1[i];
                double localAmplitude1 = amplitude / dist1;
                double value1 = localAmplitude1 * Math.sin(cyclicFrequency * realTime - k * dist1 + initialPhase);

                double dist2 = distancesToSource2[i];
                double localAmplitude2 = amplitude / dist2;
                double value2 = localAmplitude2 * Math.sin(cyclicFrequency * realTime - k * dist2 + initialPhase);

                double l = localAmplitude1 + localAmplitude2;
                double mappedValue = value1 + value2;
                positions[i + 1] = (float) mappedValue;
            }

            positionsFloatBuffer.put(positions).flip();
            mesh.bufferDataUpdate(positionsFloatBuffer);

            // Update projection and view matrices
            Matrix4f projectionMatrix = new Matrix4f().setOrtho(-1f, 1f, -1f, 1f, 0.01f, 1000.f);
            Matrix4f viewMatrix;
            if (is3DActive) {
                theta -= mouseInput.getDisplVec().x * 0.01;
                if (theta < 0 || theta > Math.PI) {
                    theta += mouseInput.getDisplVec().x * 0.01;
                }
                phi += mouseInput.getDisplVec().y * 0.01;
                float x = (float) (2 * Math.sin(theta) * Math.cos(phi));
                float y = (float) (2 * Math.sin(theta) * Math.sin(phi));
                float z = (float) (2 * Math.cos(theta));
                camera.setPosition(x, z, y);
                Vector3f cameraPos = camera.getPosition();
                Vector3f zeroVector = new Vector3f(0, 0, 0);
                Vector3f upVector = new Vector3f(0, 1, 0);
                viewMatrix = new Matrix4f().setLookAt(cameraPos, zeroVector, upVector);
                projectionMatrix.scale(0.7f);
            } else {
                // 2D
                camera.setPosition(0, 2, 0);
                camera.setRotation(90, 0, 0);
                projectionMatrix.scale(1);
                viewMatrix = camera.getViewMatrix();
            }

            // Draw plane
            shaderProgram.bind();
            {
                shaderProgram.setUniform("projectionMatrix", projectionMatrix);
                shaderProgram.setUniform("viewMatrix", viewMatrix);
                shaderProgram.setUniform("iGlobalTime", t++);
                shaderProgram.setUniform("timeMul", timeMul);

                shaderProgram.setUniform("amplitude", amplitude);
                shaderProgram.setUniform("wavelength", wavelength);
                shaderProgram.setUniform("cyclicFrequency", cyclicFrequency);
                shaderProgram.setUniform("initialPhase", initialPhase);
                shaderProgram.setUniform("between", between);
                mesh.render();
            }
            shaderProgram.unbind();

            // Prepare image for JavaFX canvas
            GL11.glReadBuffer(GL_FRONT_AND_BACK);
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    // Setters
    public void setBetween(double value) {
        between = (float) value;
        isDistanceChanged = true;
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = (float) amplitude;
    }

    public void setCyclicFrequency(double cyclicFrequency) {
        this.cyclicFrequency = (float) cyclicFrequency;
    }

    public void setWavelength(double wavelength) {
        this.wavelength = (float) wavelength;
    }

    public void setInitialPhase(double initialPhase) {
        this.initialPhase = (float) initialPhase;
    }

    public void setTimeMul(double timeMul) {
        this.timeMul = (float) timeMul;
    }

    public void setColorDelimiter(double colorDelimiter) {
        this.colorDelimiter = (float) colorDelimiter;
    }

    public void setMinColor(Vector3f minColor) {
        this.minColor = minColor;
    }

    public void setMaxColor(Vector3f maxColor) {
        this.maxColor = maxColor;
    }

    public void setIs3DActive(boolean is3DActive) {
        this.is3DActive = is3DActive;
    }

    public void setAngle(Vector3f angle) {
        this.angle = angle;
    }
}
