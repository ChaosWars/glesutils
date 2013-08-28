package com.zendeka.glesutils.gles20;

import android.opengl.GLES20;

import java.nio.Buffer;
import java.nio.IntBuffer;

/**
 * Created by Lawrence on 8/8/13.
 */
public class VertexBufferObject {
    public enum Target {
        ARRAY_BUFFER(GLES20.GL_ARRAY_BUFFER),
        ELEMENT_ARRAY_BUFFER(GLES20.GL_ELEMENT_ARRAY_BUFFER);

        private final int mTarget;

        Target(int target) {
            mTarget = target;
        }

        public int getTarget() {
            return mTarget;
        }
    }

    public enum Usage {
        STATIC_DRAW(GLES20.GL_STATIC_DRAW),
        DYNAMIC_DRAW(GLES20.GL_DYNAMIC_DRAW),
        STREAM_DRAW(GLES20.GL_STREAM_DRAW);

        private final int mUsage;

        Usage(int usage) {
            mUsage = usage;
        }

        public int getUsage() {
            return  mUsage;
        }
    }

    private final Target mTarget;
    private final Usage mUsage;

    private int mName;
    private int mSize;

    public VertexBufferObject(final Target target, final Usage usage) {
        mTarget = target;
        mUsage = usage;

        IntBuffer buffers = IntBuffer.allocate(1);
        GLES20.glGenBuffers(1, buffers);
        mName = buffers.get(0);
    }

    public VertexBufferObject(final Target target, final Usage usage, Buffer data, int size) throws IllegalStateException {
        mTarget = target;
        mUsage = usage;

        IntBuffer buffers = IntBuffer.allocate(1);
        GLES20.glGenBuffers(1, buffers);
        mName = buffers.get(0);

        bind();

        allocateAndBufferData(size, data);
    }

    public void release() {
        if (mName > 0) {
            IntBuffer buffers = IntBuffer.allocate(1);
            buffers.put(0, mName);

            GLES20.glDeleteBuffers(1, buffers);
        }
    }

    public Target getBufferTarget() {
        return mTarget;
    }

    public Usage getBufferUsage() {
        return mUsage;
    }

    public int getName() {
        return mName;
    }

    public int getSize() {
        return mSize;
    }

    public void deleteBuffer() throws IllegalStateException{
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        IntBuffer buffers = IntBuffer.allocate(1);
        buffers.put(0, mName);

        GLES20.glDeleteBuffers(1, buffers);
    }

    public void allocate(int size) throws IllegalStateException {
        allocateAndBufferData(size, null);
    }

    public void allocateAndBufferData(int size, Buffer data) throws IllegalStateException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        mSize = size;
        GLES20.glBufferData(mTarget.getTarget(), size, data, mUsage.getUsage());
    }

    public void updateData(int offset, int size, Buffer data) throws IllegalStateException, IllegalArgumentException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        if (offset + size > mSize) {
            throw new IllegalArgumentException("Vertex buffer size exceeded: offset + size > internal size: " + offset + " + " + size + " > " + mSize);
        }

        GLES20.glBufferSubData(mTarget.getTarget(), offset, size, data);
    }

    public void bind() throws IllegalStateException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        GLES20.glBindBuffer(mTarget.getTarget(), mName);
    }
}
