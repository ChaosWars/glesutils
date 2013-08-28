package com.zendeka.glesutils.gles20.shader;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lawrence on 8/5/13.
 */
public final class ShaderProgram {
    private interface Location {
        public String getType();
        public int getLocation(int program, String name);
    }

    private static class UniformLocation implements Location {
        @Override
        public String getType() {
            return "Uniform";
        }

        @Override
        public int getLocation(int program, String name) throws IllegalArgumentException {
            return GLES20.glGetUniformLocation(program, name);
        }
    }

    private static class AttributeLocation implements Location {
        @Override
        public String getType() {
            return "Attribute";
        }

        @Override
        public int getLocation(int program, String name) throws IllegalArgumentException {
            return GLES20.glGetAttribLocation(program, name);
        }
    }

    private String mTag;
    private List<Shader> mShaders = new ArrayList<Shader>();
    private int mName;
    private String mBuildLog;
    private boolean mValid;
    private String mValidationLog;
    private Map<String, Integer> mUniformLocations;
    private Map<String, Integer> mAttributeLocations;
    private UniformLocation mUniformLocation;
    private  AttributeLocation mAttributeLocation;

    public void setTag(final String tag) {
        mTag = tag;
    }

    public boolean isBuilt() {
        return mName != 0;
    }

    public String getBuildLog() {
        return mBuildLog;
    }

    public String getValidationLog() {
        return mValidationLog;
    }

    public void addShader(final Shader shader) {
        mShaders.add(shader);
    }

    public void build() throws IllegalArgumentException, IllegalStateException {
        if (isBuilt()) {
            return;
        }

        mName = GLES20.glCreateProgram();

        if (mName < 1) {
            throw new IllegalStateException("Failed to create OpenGL ES shader program");
        }

        for (final Shader shader : mShaders) {
            try {
                if (!shader.isCompiled()) {
                    shader.compile();
                }

                shader.attachToProgram(mName);
            } catch (IllegalArgumentException e) {
                unload();
                throw e;
            } catch (IllegalStateException e) {
                unload();
                throw e;
            }
        }

        GLES20.glLinkProgram(mName);

        for (final Shader shader : mShaders) {
            try {
                shader.detachFromProgram(mName);
            } catch (IllegalArgumentException e) {
                unload();
                throw e;
            } catch (IllegalStateException e) {
                unload();
                throw e;
            }
        }

        IntBuffer params = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(mName, GLES20.GL_LINK_STATUS, params);

        String infoLog = GLES20.glGetProgramInfoLog(mName);
        mBuildLog = "Shader program build log: " + infoLog;

        boolean linked = params.get(0) != 0;

        if (!linked) {
            Log.e(mTag, mBuildLog);
            unload();

            throw new IllegalStateException();
        }
    }

    public void unload() throws IllegalArgumentException, IllegalStateException {
        for (final Shader shader : mShaders) {
            if (shader.isCompiled() && shader.isAttachedToProgram(mName)) {
                shader.detachFromProgram(mName);
            }
        }

        mShaders.clear();
    }

    public void validate() throws IllegalStateException {
        checkBuilt();
        GLES20.glValidateProgram(mName);

        IntBuffer params = IntBuffer.allocate(1);
        GLES20.glGetShaderiv(mName, GLES20.GL_VALIDATE_STATUS, params);

        String infoLog = GLES20.glGetProgramInfoLog(mName);
        mValidationLog = "Shader program validation log: " + infoLog;

        mValid = params.get(0) != 0;

        if (!mValid) {
            Log.e(mTag, mValidationLog);
        }
    }

    public boolean isValid() {
        return mValid;
    }

    public void use() throws IllegalStateException {
        checkBuilt();
        GLES20.glUseProgram(mName);
    }

    public void setUniform(String name, float x) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1f(location, x);
    }

    public void setUniform1fv(String name, int count, FloatBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1fv(location, count, v);
    }

    public void setUniform1fv(String name, int count, float[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1fv(location, count, v, offset);
    }

    public void setUniform(String name, int x) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1i(location, x);
    }

    public void setUniform1iv(String name, int count, IntBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1iv(location, count, v);
    }

    public void setUniform1iv(String name, int count, int[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform1iv(location, count, v, offset);
    }

    public void setUniform(String name, float x, float y) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2f(location, x, y);
    }

    public void setUniform2fv(String name, int count, FloatBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2fv(location, count, v);
    }

    public void setUniform2fv(String name, int count, float[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2fv(location, count, v, offset);
    }

    public void setUniform(String name, int x, int y) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2i(location, x, y);
    }

    public void setUniform2iv(String name, int count, IntBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2iv(location, count, v);
    }

    public void setUniform2iv(String name, int count, int[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform2iv(location, count, v, offset);
    }

    public void setUniform(String name, float x, float y, float z) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3f(location, x, y, z);
    }

    public void setUniform3fv(String name, int count, float[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3fv(location, count, v, offset);
    }

    public void setUniform3fv(String name, int count, FloatBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3fv(location, count, v);
    }

    public void setUniform(String name, int x, int y, int z) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3i(location, x, y, z);
    }

    public void setUniform3iv(String name, int count, int[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3iv(location, count, v, offset);
    }

    public void setUniform3iv(String name, int count, IntBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform3iv(location, count, v);
    }

    public void setUniform(String name, float x, float y, float z, float w) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform4f(location, x, y, z, w);
    }

    public void setUniform4fv(String name, int count, FloatBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform4fv(location, count, v);
    }

    public void setUniform4fv(String name, int count, float[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform4fv(location, count, v, offset);
    }

    public void setUniform(String name, int x, int y, int z, int w) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniform4i(location, x, y, z, w);
    }

    public void setUniform4iv(String name, int count, int[] v, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20. glUniform4iv(location, count, v, offset);
    }

    public void setUniform4iv(String name, int count, IntBuffer v) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20. glUniform4iv(location, count, v);
    }

    public void setUniformMatrix2fv(String name, int count, boolean transpose, FloatBuffer value) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20. glUniformMatrix2fv(location, count, transpose, value);
    }

    public void setUniformMatrix2fv(String name, int count, boolean transpose, float[] value, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20. glUniformMatrix2fv(location, count, transpose, value, offset);
    }

    public void setUniformMatrix3fv(String name, int count, boolean transpose, float[] value, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniformMatrix3fv(location, count, transpose, value, offset);
    }

    public void setUniformMatrix3fv(String name, int count, boolean transpose, FloatBuffer value) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniformMatrix3fv(location, count, transpose, value);
    }

    public void setUniformMatrix4fv(String name, int count, boolean transpose, float[] value, int offset) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniformMatrix4fv(location, count, transpose, value, offset);
    }

    public void setUniformMatrix4fv(String name, int count, boolean transpose, FloatBuffer value) throws IllegalStateException {
        int location = getLocation(name, mUniformLocations, mUniformLocation);
        GLES20.glUniformMatrix4fv(location, count, transpose, value);
    }

    public void setAttribute(String name, float x) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib1f(indx, x);
    }

    public void setAttribute1fv(String name, FloatBuffer values) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib1fv(indx, values);
    }

    public void setAttribute1fv(String name, float[] values, int offset) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib1fv(indx, values, offset);
    }

    public void setAttribute(String name, float x, float y) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib2f(indx, x, y);
    }

    public void setAttribute2fv(String name, float[] values, int offset) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib2fv(indx, values, offset);
    }

    public void setAttribute2fv(String name, FloatBuffer values) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib2fv(indx, values);
    }

    public void setAttribute(String name, float x, float y, float z) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib3f(indx, x, y, z);
    }

    public void setAttribute3fv(String name, FloatBuffer values) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib3fv(indx, values);
    }

    public void setAttribute3fv(String name, float[] values, int offset) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib3fv(indx, values, offset);
    }

    public void setAttribute(String name, float x, float y, float z, float w) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib4f(indx, x, y, z, w);
    }

    public void setAttribute4fv(String name, FloatBuffer values) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib4fv(indx, values);
    }

    public void setAttribute4fv(String name, float[] values, int offset) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttrib4fv(indx, values, offset);
    }

    public void setAttributePointer(String name, int size, int type, boolean normalized, int stride, int offset) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, offset);
    }

    public void setAttributePointer(String name, int size, int type, boolean normalized, int stride, Buffer ptr) throws IllegalStateException {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glVertexAttribPointer(indx, size, type, normalized, stride, ptr);
    }

    public void enableAttribute(String name) {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glEnableVertexAttribArray(indx);
    }

    public void disableAttribute(String name) {
        int indx = getLocation(name, mAttributeLocations, mAttributeLocation);
        GLES20.glDisableVertexAttribArray(indx);
    }

    public boolean hasAttribute(String name) {
        try {
            int location = getLocation(name, mAttributeLocations, mAttributeLocation);
            return location >= 0;
        } catch (IllegalArgumentException e) {
            //Suppress error
        }

        return false;
    }

    public boolean hasUniform(String name) {
        try {
            int location = getLocation(name, mUniformLocations, mUniformLocation);
            return location >= 0;
        } catch (IllegalArgumentException e) {
            //Suppress error
        }

        return false;
    }

    private int getLocation(String name, Map<String, Integer> locations, Location location) throws IllegalStateException, IllegalArgumentException {
        checkBuilt();

        Integer value = locations.get(name);

        if (value != null) {
            return value;
        }

        value = location.getLocation(mName, name);

        if (value < 0) {
            throw new IllegalArgumentException(location.getType() + " \"" + name + "\" not found in shader program");
        }

        locations.put(name, value);
        return value;
    }

    private void checkBuilt() throws IllegalStateException {
        if (!isBuilt()) {
            throw new IllegalStateException("Shader program not built");
        }
    }
}
