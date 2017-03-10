package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.renderscript.Matrix4f;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by arturpopov on 31/01/2017.
 */

class MainRenderer implements GLSurfaceView.Renderer
{

    //Matrices
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mVPMatrix = new float[16];

    private Context mContext;

    ObjectContainer bambooObj;
    //Shader Handles
    @SuppressWarnings("FieldCanBeLocal")
    private int programDefaultHandle, programNormalMapHandle, programParticlesHandle, mMVPNormalShaderHandle;

    //Uniform Handles
    private int mMVPMatrixHandle, mViewMatrixHandle, mModelMatrixHandle, mModelView3x3MatrixHandle, mLightPositionWorldSpaceHandle;

    float eyeX, eyeY, eyeZ;


    MainRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    ParticleGenerator particleGenerator;

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {


        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        eyeX = 0.0f;
        eyeY = 0.0f;
        eyeZ = 3.f;
        Matrix.setLookAtM(mViewMatrix, 0,
                eyeX, eyeY, eyeZ, //EYE x,y,z
                0.0f, 0.0f, 0.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)


        BuildShaders();

//        bambooObj = new ObjectContainer();
//        bambooObj.initialize("testBamboo.obj", mContext, R.drawable.bamboo, R.drawable.bamboo_normal_map);

        defineUniformHandles();
//        particleGenerator = new ParticleGenerator(mContext);
//        particleGenerator.create(programParticlesHandle, R.drawable.droplet);

    }

    private void defineUniformHandles()
    {
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programDefaultHandle, "u_MVPMatrix");

        mMVPNormalShaderHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_MVPMatrix");
        mModelMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ModelMatrix");
        mViewMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ViewMatrix");
        mLightPositionWorldSpaceHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_LightPositionWorldSpace");
        mModelView3x3MatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_MV3x3");
    }

    private void BuildShaders()
    {
        ShaderBuilder shaderBuilder = new ShaderBuilder();
        programParticlesHandle = shaderBuilder.LoadProgram("particleGenerator", mContext);
        programDefaultHandle = shaderBuilder.LoadProgram("default", mContext, new String[]{"a_Position", "a_Colour"});
        programNormalMapHandle = shaderBuilder.LoadProgram("normalMapped", mContext, new String[]{"a_Position", "a_UV", "a_Normal","a_Tangent","a_BiTangent"});
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
        float[] lightPosition = {-2.0f, 1.0f, 4.0f};

        Matrix.setIdentityM(mVPMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        particleGenerator.drawParticles(mVPMatrix, mViewMatrix);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        Matrix4f matrixMV3x3 = new Matrix4f(mMVPMatrix);
        matrixMV3x3.inverseTranspose();
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);


        updateDefaultUniforms();
        updateNormalMappingUniforms(lightPosition, matrixMV3x3);
        //bambooObj.draw(programNormalMapHandle); //debug ONLY
    }

    private void updateDefaultUniforms()
    {
        GLES20.glUseProgram(programDefaultHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    private void updateNormalMappingUniforms(float[] lightPosition, Matrix4f matrixMV3x3)
    {
        GLES20.glUseProgram(programNormalMapHandle);
        GLES20.glUniformMatrix4fv(mMVPNormalShaderHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform3fv(mLightPositionWorldSpaceHandle, 1, lightPosition, 0);
        GLES20.glUniformMatrix3fv(mModelView3x3MatrixHandle, 1, false, matrixMV3x3.getArray(), 0);
    }

}
