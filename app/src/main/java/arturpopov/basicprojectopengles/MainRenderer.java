package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by arturpopov on 31/01/2017.
 */

class MainRenderer implements GLSurfaceView.Renderer
{
    //Class Members
    //Display Objects
    private Triangle mTriangle;
    private Cylinder mCylinder;
    private ObjectContainer bambooObj;
    //Matrices
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private Context mContext;
    //Shader Handles
    private int programDefaultHandle, programNormalMapHandle;

    //Uniform Handles
    private int mMVPMatrixHandle, mViewMatrixHandle, mModelMatrixHandle, mModelView3x3Matrix, mLightPositionWorldSpaceHandle;


    MainRenderer(Context mContext)
    {
        this.mContext = mContext;
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //Model Initializations

        mCylinder = new Cylinder();
        mCylinder.setSize(10, 5, 400);

        mCylinder.initialize();
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, 1.5f, //EYE x,y,z
                0.0f, 0.0f, -5.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)



        ShaderBuilder shaderBuilder = new ShaderBuilder();
        programDefaultHandle = shaderBuilder.LoadProgram("default", mContext, new String[]{"a_Position", "a_Colour"});
        programNormalMapHandle = shaderBuilder.LoadProgram("normalMapped", mContext, new String[]{"a_Position", "a_UV", "a_Normal","a_Tangent","a_BiTangent"});



        bambooObj = new ObjectContainer();
        bambooObj.initialize("testBamboo.obj", mContext);


        mMVPMatrixHandle = GLES20.glGetUniformLocation(programDefaultHandle, "u_MVPMatrix");
        mModelMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ModelMatrix");
        mViewMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ViewMatrix");
        mLightPositionWorldSpaceHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_LightPositionWorldSpace");

        GLES20.glUseProgram(programDefaultHandle);
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

        //Demonstration of model.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float[] lightPosition = {0.0f, 0.0f, 4.0f};


        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform3fv(mLightPositionWorldSpaceHandle, 1, lightPosition, 0);
        //mCylinder.draw(programDefaultHandle);
        bambooObj.draw(programNormalMapHandle);

    }
}
