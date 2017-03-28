package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

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
    float[] lightView = new float[16];
    private Context mContext;

    ObjectContainer bambooObj;
    ObjectContainerDefault floorObj, backWallObj, leftWallObj, lightSourceObj, shadowingObject, terrain;
    Skybox skybox;
    //Shader Handles
    @SuppressWarnings("FieldCanBeLocal")
    private int programDefaultHandle, programNormalMapHandle,  programObjDefaultHandle;

    //Uniform Handles
    private int mMVPMatrixHandle, u_ViewMatrix_NormalMapHandle, u_ModelMatrix_NormalMapHandle, u_NormalMatrix_NormalMapHandle, u_LightPositionWorldSpace_NormalMapHandle, u_MVPMatrix_NormalMapHandle, u_ViewPositionWorldSpace_NormalMapHandle;
    private int u_MVPMatrix_ObjDefaultHandle, u_ViewMatrix_ObjDefaultHandle, u_ModelMatrix_ObjDefaultHandle, u_Option_ObjDefaultHandle, u_LightPositionWorldSpace_ObjDefaultHandle, u_ViewPositionWorldSpace_ObjDefaultHandle, u_NormalMatrix_ObjDefaultHandle,u_ShadowProjMatrix_ObjDefaultHandle, u_EmitMode_ObjDefaultHandle;
    public float eyeX, eyeY, eyeZ;
    private float[] mLightProjectionMatrix = new float[16];


    MainRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    //ParticleGenerator particleGenerator;
    CelShadedParticleGenerator celShadedParticleGenerator;
    ShadowMapping shadowMapper;
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {


        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        eyeX = 0.0f;
        eyeY = 0.0f;
        eyeZ = 3.f;



        BuildShaders();

        //shadowMapper = new ShadowMapping();

        //shadowMapper.createRenderDepthFrameBuffer();
        //shadowMapper.prepareFullScreenQuad(ShaderBuilder.LoadProgram("passthrough", mContext));

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
        shadowingObject.initialize("skinny_bush.obj", mContext, R.drawable.skinny_bush);
        terrain = new ObjectContainerDefault();
        terrain.initializeTerrain("terrain2.obj", mContext, R.drawable.terrain_texture);

        defineUniformHandles();
        //particleGenerator = new ParticleGenerator(mContext);
        //particleGenerator.create(R.drawable.droplet);
        celShadedParticleGenerator = new CelShadedParticleGenerator(mContext);
        celShadedParticleGenerator.create(R.drawable.particule_normal4, R.drawable.particule_colour_depth3);

        skybox = new Skybox();
        skybox.setFaceTextures(R.drawable.day_right, R.drawable.day_left, R.drawable.day_top, R.drawable.day_bottom, R.drawable.day_back, R.drawable.day_front);
        skybox.initialize(mContext);
    }

    private void defineUniformHandles()
    {
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programDefaultHandle, "u_MVPMatrix");

        u_MVPMatrix_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_MVPMatrix");
        u_ModelMatrix_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ModelMatrix");
        u_ViewMatrix_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ViewMatrix");
        u_LightPositionWorldSpace_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_LightPositionWorldSpace");
        u_NormalMatrix_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_NormalMatrix");
        u_ViewPositionWorldSpace_NormalMapHandle = GLES20.glGetUniformLocation(programNormalMapHandle, "u_ViewPositionWorldSpace");


        u_MVPMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_MVPMatrix");
        u_ViewMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_ViewMatrix");
        u_ModelMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_ModelMatrix");
        u_LightPositionWorldSpace_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_LightPositionWorldSpace");
        u_NormalMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_NormalMatrix");
        u_EmitMode_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_EmitMode");
        u_ShadowProjMatrix_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "uShadowProjMatrix");
        u_ViewPositionWorldSpace_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_ViewPositionWorldSpace");
        u_Option_ObjDefaultHandle = GLES20.glGetUniformLocation(programObjDefaultHandle, "u_Option");
    }

    private void BuildShaders()
    {
        programDefaultHandle = ShaderBuilder.LoadProgram("default", mContext, new String[]{"a_Position", "a_Colour"});
        programNormalMapHandle = ShaderBuilder.LoadProgram("normalMapped2", mContext);
        programObjDefaultHandle = ShaderBuilder.LoadProgram("objDefault2", mContext);
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
        final float far = 20.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.frustumM(mLightProjectionMatrix, 0, 1.1f*left, 1.1f*right, 1.1f*bottom, 1.1f*top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        float[] viewPosition = {0.f, 0.5f, 3.f};
        Matrix.setLookAtM(mViewMatrix, 0,
                viewPosition[0], viewPosition[1], viewPosition[2], //EYE x,y,z
                0.0f, 0.0f, 0.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)
        Matrix.translateM(mViewMatrix, 0, 0.0f, 0.0f, 0.f);
        Matrix.rotateM(mViewMatrix, 0, eyeX, 0.0f, 1.f, 0.f);

        float[] lightPosition = new float[]{eyeX,eyeY,2.0f, 1.0f};

        Matrix.setLookAtM(lightView, 0,
                lightPosition[0],lightPosition[1], lightPosition[2],
                lightPosition[0], -lightPosition[1], lightPosition[2],
                -lightPosition[0], 0, -lightPosition[2]
                );
        Matrix.translateM(lightView, 0, 0.0f, 0.0f, 0.f);
        //Demonstration of model.
        float time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);




        Matrix.setIdentityM(mVPMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        float[] MVPLightMatrix = new float[16];
        Matrix.multiplyMM(MVPLightMatrix, 0, mLightProjectionMatrix, 0, lightView, 0);


        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        skybox.renderSkybox(mViewMatrix, mProjectionMatrix);
        celShadedParticleGenerator.drawParticles(mVPMatrix, mViewMatrix);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 0.0f, 0.0f);
        Matrix.scaleM(mModelMatrix, 0 , 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        float[] normalMatrix = new float[16];
        Matrix.multiplyMM(normalMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        float[] normalMatrixInverted = new float[16];
        Matrix.invertM(normalMatrixInverted, 0, normalMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, normalMatrixInverted, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        int err = GLES20.GL_INVALID_OPERATION  ;
        while((err = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
        {
            Log.d(LogTag.FRAMEBUFFER, "Error is "+err);
        }
        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 1);
        updateObjDefaultUniforms(lightPosition, normalMatrix,MVPLightMatrix, viewPosition, 0);

        lightSourceObj.draw(programObjDefaultHandle);
        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 0);



    }

    private void updateDefaultUniforms()
    {
        GLES20.glUseProgram(programDefaultHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
    }

    private void updateNormalMappingUniforms(float[] lightPosition, float[] viewPosition, float[] normalMatrix)
    {
        GLES20.glUseProgram(programNormalMapHandle);
        GLES20.glUniformMatrix4fv(u_MVPMatrix_NormalMapHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ModelMatrix_NormalMapHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ViewMatrix_NormalMapHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniform3fv(u_LightPositionWorldSpace_NormalMapHandle, 1, lightPosition, 0);
        GLES20.glUniformMatrix4fv(u_NormalMatrix_NormalMapHandle, 1, false, normalMatrix, 0);
        GLES20.glUniform3fv(u_ViewPositionWorldSpace_NormalMapHandle, 1, viewPosition, 0);
    }

    private void updateObjDefaultUniforms(float[] lightPosition, float[] normalMatrix, float[] MVPLightMatrix, float[] viewPosition, int u_Option)
    {
        GLES20.glUseProgram(programObjDefaultHandle);
        GLES20.glUniformMatrix4fv(u_MVPMatrix_ObjDefaultHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ViewMatrix_ObjDefaultHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ModelMatrix_ObjDefaultHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniform3fv(u_LightPositionWorldSpace_ObjDefaultHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(u_ViewPositionWorldSpace_ObjDefaultHandle, 1, viewPosition, 0);
        GLES20.glUniformMatrix4fv(u_NormalMatrix_ObjDefaultHandle, 1, false, normalMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ShadowProjMatrix_ObjDefaultHandle, 1, false, MVPLightMatrix, 0);
        GLES20.glUniform1i(u_Option_ObjDefaultHandle, u_Option);
    }

}
