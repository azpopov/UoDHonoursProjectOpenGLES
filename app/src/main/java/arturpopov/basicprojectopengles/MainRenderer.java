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
    ObjectContainerDefault floorObj, backWallObj, leftWallObj, lightSourceObj, shadowingObject;
    //Shader Handles
    @SuppressWarnings("FieldCanBeLocal")
    private int programDefaultHandle, programNormalMapHandle, programParticlesHandle, programObjDefaultHandle;

    //Uniform Handles
    private int mMVPMatrixHandle, mViewMatrixHandle, mModelMatrixHandle, mNormalMatrixMatrixHandle, mLightPositionWorldSpaceHandle, mMVPNormalShaderHandle;
    private int u_MVPMatrix_ObjDefaultHandle, u_ViewMatrix_ObjDefaultHandle, u_ModelMatrix_ObjDefaultHandle, u_LightPositionWorldSpace_ObjDefaultHandle, u_NormalMatrix_ObjDefaultHandle, u_EmitMode_ObjDefaultHandle;
    public float eyeX, eyeY, eyeZ;


    MainRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    //ParticleGenerator particleGenerator;
    CelShadedParticleGenerator celShadedParticleGenerator;

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {


        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        eyeX = 0.0f;
        eyeY = 0.0f;
        eyeZ = 3.f;



        BuildShaders();

       bambooObj = new ObjectContainer();
        bambooObj.initialize("testBamboo.obj", mContext, R.drawable.bamboo, R.drawable.bamboo_normal_map);

        floorObj = new ObjectContainerDefault();
        floorObj.initialize("cube.obj", mContext, R.drawable.cube);
        backWallObj = new ObjectContainerDefault();
        backWallObj.initialize("cube.obj", mContext, R.drawable.cube);
        leftWallObj = new ObjectContainerDefault();
        leftWallObj.initialize("cube.obj", mContext, R.drawable.cube);
        lightSourceObj = new ObjectContainerDefault();
        lightSourceObj.initialize("cube.obj", mContext, R.drawable.cube);
        shadowingObject = new ObjectContainerDefault();
        shadowingObject.initialize("cube.obj", mContext, R.drawable.cube);


        defineUniformHandles();
        //particleGenerator = new ParticleGenerator(mContext);
        //particleGenerator.create(R.drawable.droplet);
        celShadedParticleGenerator = new CelShadedParticleGenerator(mContext);
        celShadedParticleGenerator.create(R.drawable.particule_normal4, R.drawable.particule_colour_depth3);


    }

    private void defineUniformHandles()
    {
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programDefaultHandle, "u_MVPMatrix");

        mMVPNormalShaderHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_MVPMatrix");
        mModelMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ModelMatrix");
        mViewMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ViewMatrix");
        mLightPositionWorldSpaceHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_LightPositionWorldSpace");
        mNormalMatrixMatrixHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_MV3x3");

        u_MVPMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_MVPMatrix");
        u_ViewMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_ViewMatrix");
        u_ModelMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_ModelMatrix");
        u_LightPositionWorldSpace_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_LightPositionWorldSpace");
        u_NormalMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_NormalMatrix");
        u_EmitMode_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_EmitMode");

    }

    private void BuildShaders()
    {
        programParticlesHandle = ShaderBuilder.LoadProgram("particleGenerator", mContext);
        programDefaultHandle = ShaderBuilder.LoadProgram("default", mContext, new String[]{"a_Position", "a_Colour"});
        programNormalMapHandle = ShaderBuilder.LoadProgram("normalMapped", mContext, new String[]{"a_Position", "a_UV", "a_Normal","a_Tangent","a_BiTangent"});
        programObjDefaultHandle = ShaderBuilder.LoadProgram("objDefault", mContext);
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

        Matrix.setLookAtM(mViewMatrix, 0,
                0.f, 0.f, 3.f, //EYE x,y,z
                0.0f, 0.0f, 0.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)

        Matrix.translateM(mViewMatrix, 0, 0.0f, 0.0f, 0.f);
        //Demonstration of model.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);
        float[] lightPosition = {2.0f, eyeX, eyeY};

        Matrix.setIdentityM(mVPMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //celShadedParticleGenerator.drawParticles(mVPMatrix, mViewMatrix);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        Matrix4f normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);


        updateNormalMappingUniforms(lightPosition, normalMatrix);
        //bambooObj.draw(programNormalMapHandle); //debug ONLY


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 1.0f, 0.1f, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPosition, normalMatrix);

        floorObj.draw(programObjDefaultHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -1.0f);
        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 1.0f, 1.0f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPosition, normalMatrix);

        backWallObj.draw(programObjDefaultHandle);


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -1.0f, 0.0f, 0.0f);

        Matrix.scaleM(mModelMatrix, 0 , 0.10f, 1.0f, 1.f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPosition, normalMatrix);

        leftWallObj.draw(programObjDefaultHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.5f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, 180, 0.0f, 1.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 0.3f, 0.3f, 0.3f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPosition, normalMatrix);

        shadowingObject.draw(programObjDefaultHandle);


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, lightPosition[0], lightPosition[1], lightPosition[2]);
        Matrix.scaleM(mModelMatrix, 0 , 0.05f, 0.05f, 0.05f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrix = new Matrix4f(mMVPMatrix);
        normalMatrix.inverseTranspose();

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 1);
        updateObjDefaultUniforms(lightPosition, normalMatrix);

        lightSourceObj.draw(programObjDefaultHandle);
        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 0);

    }

    private void updateDefaultUniforms()
    {
        GLES20.glUseProgram(programDefaultHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    private void updateNormalMappingUniforms(float[] lightPosition, Matrix4f normalMatrix)
    {
        GLES20.glUseProgram(programNormalMapHandle);
        GLES20.glUniformMatrix4fv(mMVPNormalShaderHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform3fv(mLightPositionWorldSpaceHandle, 1, lightPosition, 0);
        GLES20.glUniformMatrix3fv(mNormalMatrixMatrixHandle, 1, false, normalMatrix.getArray(), 0);
    }

    private void updateObjDefaultUniforms(float[] lightPosition, Matrix4f normalMatrix)
    {
        GLES20.glUseProgram(programObjDefaultHandle);
        GLES20.glUniformMatrix4fv(u_MVPMatrix_ObjDefaultHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ViewMatrix_ObjDefaultHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ModelMatrix_ObjDefaultHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniform3fv(u_LightPositionWorldSpace_ObjDefaultHandle, 1, lightPosition, 0);
        GLES20.glUniformMatrix3fv(u_NormalMatrix_ObjDefaultHandle, 1, false, normalMatrix.getArray(), 0);
    }

}
