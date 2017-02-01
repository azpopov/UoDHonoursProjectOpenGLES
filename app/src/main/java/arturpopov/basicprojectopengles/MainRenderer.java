package arturpopov.basicprojectopengles;

import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by arturpopov on 31/01/2017.
 */

public class MainRenderer implements GLSurfaceView.Renderer
{
    //Class Members
    //Display Objects
    private Triangle mTriangle;
    //Matrices
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    //Uniform Handles
    private int mMVPMatrixHandle, mPositionHandle, mColourHandle;
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        mTriangle = new Triangle(1);
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, 1.5f, //EYE x,y,z
                0.0f, 0.0f, -5.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)

        ShaderBuilder shaderBuilder = new ShaderBuilder();
        int programHandle = shaderBuilder.LoadProgram();

        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
        mColourHandle = GLES20.glGetAttribLocation(programHandle, "a_Colour");
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");

        GLES20.glUseProgram(programHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        GLES20.glViewport(0,0, width, height);
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 1000.0f) * ((int)time);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
        mTriangle.drawTriangle();
    }
}
