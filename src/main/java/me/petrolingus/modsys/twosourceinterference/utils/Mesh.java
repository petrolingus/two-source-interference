package me.petrolingus.modsys.twosourceinterference.utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final int vaoId;

    private final List<Integer> vboIdList;

    private final int vertexCount;

    private final float[] positions;

    public Mesh(float[] positions, float[] textCoords, float[] normals, int[] indices) {

        System.out.println("MESH CONSTRUCTOR:");

        FloatBuffer posBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        this.positions = positions;
        IntBuffer indicesBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();
            System.out.println("VERTEX COUNT: " + vertexCount);

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int vboId = glGenBuffers();
            vboIdList.add(vboId);
            posBuffer = MemoryUtil.memAllocFloat(positions.length);
            System.out.println("positions.length: " + positions.length);
            posBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STREAM_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

//            // Texture coordinates VBO
//            vboId = glGenBuffers();
//            vboIdList.add(vboId);
//            texturesVertexCount = textCoords.length;
//            System.out.println("textCoords.length: " + textCoords.length);
//            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.length);
//            textCoordsBuffer.put(textCoords).flip();
//            glBindBuffer(GL_ARRAY_BUFFER, vboId);
//            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STREAM_DRAW);
//            glEnableVertexAttribArray(1);
//            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
//
//            // Vertex normals VBO
//            vboId = glGenBuffers();
//            vboIdList.add(vboId);
//            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
//            vecNormalsBuffer.put(normals).flip();
//            glBindBuffer(GL_ARRAY_BUFFER, vboId);
//            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
//            glEnableVertexAttribArray(2);
//            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            indicesBuffer = MemoryUtil.memAllocInt(indices.length);
            indicesBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer);
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer);
            }
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render() {
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    public void bufferDataUpdate(FloatBuffer textCoordsBuffer) {
        glBindBuffer(GL_ARRAY_BUFFER, vboIdList.get(0));
        glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STREAM_DRAW);
    }

    public void cleanUp() {
        glDisableVertexAttribArray(0);

        // Delete the VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList) {
            glDeleteBuffers(vboId);
        }

        // Delete the VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

    public float[] getPositions() {
        return positions;
    }
}

