package com.zendeka.glesutils.gles20;

import android.opengl.GLES20;

import com.zendeka.glesutils.utils.GLGetError;

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

    private String mTag;

    private int mName;
    private int mNumElements;
    private int mSizeBytes;

    public VertexBufferObject(final Target target, final Usage usage) {
        mTarget = target;
        mUsage = usage;

        IntBuffer buffers = IntBuffer.allocate(1);
        GLES20.glGenBuffers(1, buffers); GLGetError.getOpenGLErrors(mTag);
        mName = buffers.get(0);
    }

    public VertexBufferObject(final Target target, final Usage usage, Buffer data, int numElements, int sizeBytes) throws IllegalStateException {
        mTarget = target;
        mUsage = usage;

        IntBuffer buffers = IntBuffer.allocate(1);
        GLES20.glGenBuffers(1, buffers); GLGetError.getOpenGLErrors(mTag);
        mName = buffers.get(0);

        bind();

        bufferData(data, numElements, sizeBytes);
    }

    public void release() {
        if (mName > 0) {
            IntBuffer buffers = IntBuffer.allocate(1);
            buffers.put(0, mName);

            GLES20.glDeleteBuffers(1, buffers); GLGetError.getOpenGLErrors(mTag);
        }
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
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

    public int getNumElements() {
        return mNumElements;
    }

    public int getSizeBytes() {
        return mSizeBytes;
    }

    public void deleteBuffer() throws IllegalStateException{
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        IntBuffer buffers = IntBuffer.allocate(1);
        buffers.put(0, mName);

        GLES20.glDeleteBuffers(1, buffers); GLGetError.getOpenGLErrors(mTag);
    }

    public void allocate(int sizeBytes) throws IllegalStateException {
        bufferData(null, 0, sizeBytes);
    }

    public void bufferData(Buffer data, int numElements, int sizeBytes) throws IllegalStateException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        mNumElements = numElements;
        mSizeBytes = sizeBytes;
        GLES20.glBufferData(mTarget.getTarget(), sizeBytes, data, mUsage.getUsage()); GLGetError.getOpenGLErrors(mTag);
    }

    public void updateData(Buffer data, int offset, int sizeBytes) throws IllegalStateException, IllegalArgumentException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        if (offset + sizeBytes > mSizeBytes) {
            throw new IllegalArgumentException("Vertex buffer size exceeded: offset + size > internal size: " + offset + " + " + sizeBytes + " > " + mSizeBytes);
        }

        GLES20.glBufferSubData(mTarget.getTarget(), offset, sizeBytes, data); GLGetError.getOpenGLErrors(mTag);
    }

    public void bind() throws IllegalStateException {
        if (mName == 0) {
            throw new IllegalStateException("Vertex buffer not initialized");
        }

        GLES20.glBindBuffer(mTarget.getTarget(), mName); GLGetError.getOpenGLErrors(mTag);
    }
}
