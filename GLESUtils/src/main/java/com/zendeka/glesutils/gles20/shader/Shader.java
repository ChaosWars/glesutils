package com.zendeka.glesutils.gles20.shader;

import android.util.Log;
import android.util.SparseIntArray;

import com.zendeka.glesutils.utils.GLGetError;

import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Lawrence on 8/5/13.
 */
public final class Shader {
    public enum Type {
        FRAGMENT,
        VERTEX
    }

    private final Type mType;
    private final String mSource;
    private final String mTag;

    private int mName;
    private String mCompileLog;
    private SparseIntArray mAttachedPrograms = new SparseIntArray();

    public Shader(final Type type, final String source, final String tag) {
        mType = type;
        mSource = source;
        mTag = tag;
    }

    public void release() {
        if (isCompiled()) {
            glDeleteShader(mName); GLGetError.getOpenGLErrors(mTag);
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

    public void compile() throws IllegalStateException {
        if (isCompiled()) {
            return;
        }

        int type = 0;

        switch (mType) {
            case FRAGMENT:
                type = GL_FRAGMENT_SHADER;
                break;
            case VERTEX:
                type = GL_VERTEX_SHADER;
                break;
        }

        String shaderType = getShaderTypeString();
        mName = glCreateShader(type); GLGetError.getOpenGLErrors(mTag);

        if (mName < 1) {
            throw new IllegalStateException("(" + mTag + ") Failed to create OpenGL ES " + shaderType + " shader");
        }

        glShaderSource(mName, mSource); GLGetError.getOpenGLErrors(mTag);
        glCompileShader(mName); GLGetError.getOpenGLErrors(mTag);

        IntBuffer params = IntBuffer.allocate(1);

        glGetShaderiv(mName, GL_COMPILE_STATUS, params); GLGetError.getOpenGLErrors(mTag);

        boolean compiled = params.get(0) != 0;

        if (!compiled) {
            String infoLog = glGetShaderInfoLog(mName); GLGetError.getOpenGLErrors(mTag);
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

        glAttachShader(program, mName); GLGetError.getOpenGLErrors(mTag);
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

        glDetachShader(program, mName); GLGetError.getOpenGLErrors(mTag);
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
