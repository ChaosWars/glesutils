package com.zendeka.glesutils.utils;

import android.util.Log;

import static android.opengl.GLES20.*;
import static android.opengl.GLU.*;

/**
 * Created by Lawrence on 9/1/13.
 */
public class GLGetError {
    public static void getOpenGLErrors(String tag) {
//        if (BuildConfig.DEBUG) {
            int error = glGetError();

            while (error != GL_NO_ERROR) {
                Log.e(tag, gluErrorString(error));
                error = glGetError();
            }
//        }
    }
}
