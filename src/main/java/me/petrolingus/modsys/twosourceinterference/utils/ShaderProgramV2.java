package me.petrolingus.modsys.twosourceinterference.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgramV2 {

    private final int programId;

    private final Map<String, Integer> uniforms;

    public ShaderProgramV2(String vertexShaderPath, String fragmentShaderPath) throws Exception {
        programId = GL30.glCreateProgram();
        if (programId == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
        createShaders(vertexShaderPath, fragmentShaderPath);
    }

    private void createShaders(String vertexShaderPath, String fragmentShaderPath) throws Exception {

//        int vertexShaderId = createShader(GL30.GL_VERTEX_SHADER, vertexShaderPath);
//        int fragmentShaderId = createShader(GL30.GL_FRAGMENT_SHADER, fragmentShaderPath);

        // Create vertex shader
        int vertexShaderId = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        if (vertexShaderId == 0) {
            throw new Exception("Error creating shader. Type: " + GL30.GL_VERTEX_SHADER);
        }
        String vertexShaderCode = Files.lines(Paths.get(vertexShaderPath)).collect(Collectors.joining("\n"));
        GL30.glShaderSource(vertexShaderId, vertexShaderCode);
        GL30.glCompileShader(vertexShaderId);
        if (glGetShaderi(vertexShaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(vertexShaderId, 1024));
        }
        GL30.glAttachShader(programId, vertexShaderId);

        // Create fragment shader
        int fragmentShaderId = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
        if (fragmentShaderId == 0) {
            throw new Exception("Error creating shader. Type: " + GL30.GL_FRAGMENT_SHADER);
        }
        String fragmentShaderCode = Files.lines(Paths.get(fragmentShaderPath)).collect(Collectors.joining("\n"));
        GL30.glShaderSource(fragmentShaderId, fragmentShaderCode);
        GL30.glCompileShader(fragmentShaderId);
        if (glGetShaderi(fragmentShaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(fragmentShaderId, 1024));
        }
        GL30.glAttachShader(programId, fragmentShaderId);

        // Link shaders to program
        GL30.glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
        GL30.glDetachShader(programId, vertexShaderId);
        GL30.glDetachShader(programId, fragmentShaderId);
        GL30.glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }
    }

    private int createShader(int shaderType, String shaderPath) throws Exception {
        int shaderId = GL30.glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }
        String vertexShaderCode = Files.lines(Paths.get(shaderPath)).collect(Collectors.joining("\n"));
        GL30.glShaderSource(shaderId, vertexShaderCode);
        GL30.glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }
        GL30.glAttachShader(programId, shaderId);
        return shaderId;
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL30.glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, float value) {
        GL30.glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        GL30.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void bind() {
        GL30.glUseProgram(programId);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }
}
