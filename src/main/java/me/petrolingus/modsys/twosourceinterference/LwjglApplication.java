package me.petrolingus.modsys.twosourceinterference;

import me.petrolingus.modsys.twosourceinterference.utils.ShaderProgram;
import me.petrolingus.modsys.twosourceinterference.utils.ShaderProgramV2;
import me.petrolingus.modsys.twosourceinterference.utils.Utils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LwjglApplication {

    private long window;

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
        glfwShowWindow(window);
    }

    private void loop() throws Exception {

        GL.createCapabilities();

        // Create shader
        String vertexShaderPath = "src/main/resources/shaders/vertex.shader";
        String fragmentShaderPath = "src/main/resources/shaders/fragment.shader";
        ShaderProgramV2 shaderProgram = new ShaderProgramV2(vertexShaderPath, fragmentShaderPath);
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("iGlobalTime");

        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

//        Matrix4f projectionMatrix = new Matrix4f().setPerspective(
//                (float) Math.toRadians(60.0f),
//                (float) 800 / 800,
//                0.01f,
//                1000.f);

        Matrix4f projectionMatrix = new Matrix4f().setOrtho(-2f, 2f, -2f, 2f, 0.01f, 1000.f);

        double rotationX = 35;
        double rotationY = -45;
        float cameraPosX = 1;
        float cameraPosY = 0;
        float cameraPosZ = 0;

        // Top View
//        rotationX = 90;
//        rotationY = 0;
//        cameraPosX = 0;
//        cameraPosY = 1;
//        cameraPosZ = 0;

        float t = 0;

        Vector3f origin = new Vector3f(0, 0, 0);
        Vector3f up = new Vector3f(0, 1, 0);

        while (!glfwWindowShouldClose(window)) {

            t += 0.01f;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//            Matrix4f viewMatrix = new Matrix4f()
//                    .identity()
//                    .rotate((float) Math.toRadians(rotationX), new Vector3f(1, 0, 0))
//                    .rotate((float) Math.toRadians(rotationY), new Vector3f(0, 1, 0))
//                    .translate(-cameraPosX, -cameraPosY, -cameraPosZ);

            Matrix4f viewMatrix = new Matrix4f().setLookAt(new Vector3f(cameraPosX, cameraPosY, cameraPosZ), origin, up);
            cameraPosX = (float) Math.cos(0.1 * t);
            cameraPosZ = (float) Math.sin(0.1 * t);

            shaderProgram.bind();
            {
                shaderProgram.setUniform("projectionMatrix", projectionMatrix);
                shaderProgram.setUniform("modelViewMatrix", viewMatrix);
                shaderProgram.setUniform("iGlobalTime", t);
                int n = 128;
                float step = 2.0f / (n - 1);
                for (int i = 0; i < n; i++) {
                    float posY = -1 + i * step;
                    for (int j = 0; j < n; j++) {
                        float posX = -1 + j * step;
                        glBegin(GL_POLYGON);
                        {
                            glVertex3f(posX, 0.0f, posY);
                            glVertex3f(posX + step, 0.0f, posY);

                            glVertex3f(posX + step, 0.0f, posY);
                            glVertex3f(posX + step, 0.0f, posY + step);

                            glVertex3f(posX + step, 0.0f, posY + step);
                            glVertex3f(posX, 0.0f, posY + step);

                            glVertex3f(posX, 0.0f, posY + step);
                            glVertex3f(posX, 0.0f, posY);
                        }
                        glEnd();
                    }
//
//                    glBegin(GL_LINE_STRIP);
//                    {
//                        glVertex3f(pos, 0.0f, -1.0f);
//                        glVertex3f(pos, 0.0f, 1.0f);
//                    }
//                    glEnd();
                }
            }


            shaderProgram.unbind();

            glfwSwapBuffers(window);

            glfwPollEvents();
        }
    }

    public static void main(String[] args) throws Exception {
        new LwjglApplication().run();
    }

}
