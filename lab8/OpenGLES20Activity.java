package com.labs.aj28;

import android.app.Activity;
import android.content.Context;


import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;



import javax.microedition.khronos.opengles.GL10;

public class OpenGLES20Activity extends Activity {
    static final String TAG = "OpenGLES20Activity";
    GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    public class MyGLSurfaceView extends GLSurfaceView {
        private final MyGLRenderer mRenderer;
        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new MyGLRenderer();
            setRenderer(mRenderer);
        }
        float mPrevX; float mPrevY;
        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX(); float y = e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE: float dx = x - mPrevX;
                    float dy = y- mPrevY;
                    mRenderer.setAngle(mRenderer.getAngle() + ((dx + dy) * 180.0f / 320));
                    requestRender();
            }
            mPrevX = x; mPrevY = y;
            return true;
        }
    }

    public class MyGLRenderer implements GLSurfaceView.Renderer {
        private Triangle mTriangle;
        private Square mSquare;
        private final float[] mMVPMatrix = new float[16];
        private final float[] mProjectionMatrix = new float[16];
        private final float[] mViewMatrix = new float[16];
        private final float[] mRotationMatrix = new float[16];
        private float mAngle;

        @Override
        public void onSurfaceCreated(GL10 gl10, javax.microedition.khronos.egl.EGLConfig eglConfig) {
            GLES20.glClearColor(0.0f, 0.0f, 0.3f, 1.0f);
            mTriangle = new Triangle(); mSquare = new Square();
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            float[] scratch = new float[16];
            GLES20.glClearDepthf(1.0f);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            long time = SystemClock.uptimeMillis();
            float angle = 0.00050f * ((int)time);
            Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, 1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            Matrix.setLookAtM(mViewMatrix, 0, (float)(4*Math.sin(angle)), 0,
                    (float)(-4*Math.cos(angle)), 0f, 0f, 0f, 0f, 1.0f, 0.0f);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix,0);
            mSquare.draw(mMVPMatrix);
            Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            mTriangle.draw(scratch);
        }

        @Override
        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            float ratio = (float) width/height;
            Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        }
        public float getAngle() { return mAngle; }
        public void setAngle(float angle) { mAngle = angle; }
    }

}