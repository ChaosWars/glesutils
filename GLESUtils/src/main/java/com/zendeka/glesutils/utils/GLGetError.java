package com.zendeka.glesutils.utils;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.util.Log;

/**
 * Created by Lawrence on 9/1/13.
 */
public class GLGetError {
    public static void getOpenGLErrors(String tag) {
//        if (BuildConfig.DEBUG) {
            int error = GLES20.glGetError();

            while (error != GLES20.GL_NO_ERROR) {
                Log.e(tag, GLU.gluErrorString(error));
                error = GLES20.glGetError();
            }
//        }
    }
}
