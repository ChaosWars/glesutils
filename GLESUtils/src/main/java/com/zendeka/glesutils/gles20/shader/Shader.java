package com.zendeka.glesutils.gles20.shader;

import android.opengl.GLES20;
import android.util.Log;
import android.util.SparseIntArray;

import java.nio.IntBuffer;

/**
 * Created by Lawrence on 8/5/13.
 */
public final class Shader {
    public enum Type {
        FRAGMENT,
        VERTEX
    }

    private Type mType;
    private int mName;
    private String mSource;
    private String mCompileLog;
    private SparseIntArray mAttachedPrograms = new SparseIntArray();
    private String mTag;

    public Shader(final Type type, final String source, final String tag) {
        mType = type;
        mSource = source;
        mTag = tag;
    }

    public void release() {
        if (isCompiled()) {
            GLES20.glDeleteShader(mName);
            mName = 0;
        }

        detachFromPrograms();
    }

    public Type getType() {
        return mType;
    }

    public int getName() {
        return mName;
    }

    public boolean isCompiled() {
        return mName > 0;
    }

    public String getCompileLog() {
        return mCompileLog;
    }

    public SparseIntArray getAttachedPrograms() {
        return mAttachedPrograms;
    }

    public String getTag() {
        return mTag;
    }

    public void setTag(final String tag) {
        mTag = tag;
    }

    public void compile() throws IllegalStateException {
        if (isCompiled()) {
            return;
        }

        int type = 0;

        switch (mType) {
            case FRAGMENT:
                type = GLES20.GL_FRAGMENT_SHADER;
                break;
            case VERTEX:
                type = GLES20.GL_VERTEX_SHADER;
                break;
        }

        String shaderType = getShaderTypeString();
        mName = GLES20.glCreateShader(type);

        if (mName < 1) {
            throw new IllegalStateException("(" + mTag + ") Failed to create OpenGL ES " + shaderType + " shader");
        }

        GLES20.glShaderSource(mName, mSource);
        GLES20.glCompileShader(mName);

        IntBuffer params = IntBuffer.allocate(1);

        GLES20.glGetShaderiv(mName, GLES20.GL_COMPILE_STATUS, params);

        boolean compiled = params.get(0) != 0;

        if (!compiled) {
            String infoLog = GLES20.glGetShaderInfoLog(mName);
            mCompileLog = shaderType + " shader compile log: " + infoLog;
            Log.e(mTag, mCompileLog);

            unload();

            throw new IllegalStateException();
        }
    }

    public String getShaderTypeString() {
        String shaderType = "";

        switch (mType) {
            case FRAGMENT:
                shaderType = "Fragment";
                break;
            case VERTEX:
                shaderType = "Vertex";
                break;
        }

        return shaderType;
    }

    public void unload() throws IllegalStateException {
        if (!isCompiled()) {
            throw new IllegalStateException(getShaderTypeString() + " (" + mTag + ") shader is not compiled");
        }

        detachFromPrograms();
    }

    public boolean isAttachedToProgram(final int program) {
        return mAttachedPrograms.get(program) != 0;
    }

    public void attachToProgram(final int program) throws IllegalArgumentException, IllegalStateException {
        if (program < 1) {
            throw new IllegalArgumentException("Invalid OpenGL ES shader program");
        }

        if (!isCompiled()) {
            throw new IllegalStateException(getShaderTypeString() + " (" + mTag + ") shader is not compiled");
        }

        if (isAttachedToProgram(program)) {
            throw new IllegalArgumentException(getShaderTypeString() + " (" + mTag + ") already attached to program " + program);
        }

        GLES20.glAttachShader(program, mName);
        mAttachedPrograms.put(program, 1);
    }

    public void detachFromProgram(final int program) throws IllegalArgumentException, IllegalStateException {
        if (program < 1) {
            throw new IllegalArgumentException("Invalid OpenGL ES shader program");
        }

        if (mName < 1) {
            throw new IllegalStateException("Invalid OpenGL ES shader name");
        }

        if (!isAttachedToProgram(program)) {
            throw new IllegalArgumentException(getShaderTypeString() + " (" + mTag + ")" + " shader not attached to program " + program);
        }

        GLES20.glDetachShader(program, mName);
        mAttachedPrograms.delete(program);
    }

    private void detachFromPrograms() {
        for (int index = 0; index < mAttachedPrograms.size(); ++index) {
            int key = mAttachedPrograms.keyAt(index);
            int program = mAttachedPrograms.get(key);

            if (isAttachedToProgram(program)) {
                detachFromProgram(program);
            }
        }

        mAttachedPrograms.clear();
    }
}
