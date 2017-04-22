package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by arturpopov on 31/01/2017.
 */

class MainRenderer implements GLSurfaceView.Renderer
{
    public static long polygonCounter = 0;
    double frameRate, startTime, endTime, elapsedTime;
    //Matrices
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mVPMatrix = new float[16];

    private Context mContext;

    ObjectContainerDefault terrain;



    ObjectContainerDefault fireplaceObj;

    ObjectContainerDefault trees;
    ObjectContainerDefault grass;
    ObjectContainer stump;
    Skybox skybox;
    //Shader Handles
    @SuppressWarnings("FieldCanBeLocal")
    private int programDefaultHandle, programNormalMapHandle,  programObjDefaultHandle;

    //Uniform Handles
    private int mMVPMatrixHandle, u_ViewMatrix_NormalMapHandle, u_ModelMatrix_NormalMapHandle, u_NormalMatrix_NormalMapHandle, u_LightPositionWorldSpace_NormalMapHandle, u_MVPMatrix_NormalMapHandle, u_ViewPositionWorldSpace_NormalMapHandle;
    private int u_MVPMatrix_ObjDefaultHandle, u_ViewMatrix_ObjDefaultHandle, u_ModelMatrix_ObjDefaultHandle, u_Option_ObjDefaultHandle, u_LightPositionWorldSpace_ObjDefaultHandle, u_ViewPositionWorldSpace_ObjDefaultHandle, u_NormalMatrix_ObjDefaultHandle,u_ShadowProjMatrix_ObjDefaultHandle, u_EmitMode_ObjDefaultHandle;
    public float eyeX, eyeY, eyeZ;
    private float[] mLightProjectionMatrix = new float[16];
    private int normalAlphaID = R.drawable.particule_normaleastwind;
    private int colourDepthID = R.drawable.particule_colour_depth3;
    private int quantizedID = R.drawable.whiteyellow;
    private int[] skyboxIDs;
    private float[] bias = new float[]{0.f, 0.f, 0.f};


    MainRenderer(Context mContext)
    {
        this.mContext = mContext;
    }

    //ParticleGenerator particleGenerator;
    CelShadedParticleGenerator celShadedParticleGenerator;

    //OPTIONS
    int optionVariationOnL = 0;
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {

        startTime = System.currentTimeMillis();

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        eyeX = 0.0f;
        eyeY = 0.0f;
        eyeZ = 3.f;


        BuildShaders();

        fireplaceObj =  new ObjectContainerDefault();
        fireplaceObj.initialize("fireplace.obj", mContext, R.drawable.fireplace);

        terrain = new ObjectContainerDefault();
        terrain.initializeTerrain("terrain.obj", mContext, R.drawable.terrain_texture);

        trees = new ObjectContainerDefault();
        trees.initialize("scenetrees.obj", mContext, R.drawable.pine);

        grass = new ObjectContainerDefault();
        grass.initialize("grass.obj", mContext, R.drawable.grass_texture);

        stump = new ObjectContainer();
        stump.initialize("pine_stump.obj", mContext, R.drawable.tree_stomp_colmap, R.drawable.tree_stomp_normap);

        defineUniformHandles();
        celShadedParticleGenerator = new CelShadedParticleGenerator(mContext);

        celShadedParticleGenerator.create(normalAlphaID, colourDepthID, quantizedID, optionVariationOnL, bias );

        skybox = new Skybox();

        skybox.setFaceTextures(skyboxIDs[0],skyboxIDs[1],skyboxIDs[2],skyboxIDs[3],skyboxIDs[4],skyboxIDs[5]);
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
        final float far = 25.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.frustumM(mLightProjectionMatrix, 0, 1.1f*left, 1.1f*right, 1.1f*bottom, 1.1f*top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        Log.d(LogTag.COUNTER, "triangles in scene in scene: " + polygonCounter);
        float[] viewPosition = {0.f, 0.5f, 3.f};
        eyeX = Math.min(eyeX, 10.f);
        eyeX = Math.max(eyeX, -10.f);
        eyeY = Math.min(eyeY, 20.f);
        eyeY = Math.max(eyeY, -10.f);
        Matrix.setLookAtM(mViewMatrix, 0,
                viewPosition[0], viewPosition[1], viewPosition[2], //EYE x,y,z
                eyeX / 10, eyeY / 10, 0.0f, //LOOKING DIRECTION x,y,z
                0.0f, 1.0f, 0.0f); //Define 'UP' direction)
        Matrix.translateM(mViewMatrix, 0, 0.0f, 0.0f, 0.f);


        float[] lightPosition = new float[]{ 0.f, 1.f, -1.f, 1.0f};


        Matrix.setIdentityM(mVPMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        skybox.renderSkybox(mViewMatrix, mProjectionMatrix);
        celShadedParticleGenerator.drawQueuedParticles(mVPMatrix, mViewMatrix, lightPosition);


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.2f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        float[] normalMatrix = new float[16];
        Matrix.multiplyMM(normalMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        float[] normalMatrixInverted = new float[16];
        Matrix.invertM(normalMatrixInverted, 0, normalMatrix, 0);
        Matrix.setIdentityM(normalMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, normalMatrixInverted, 0);

        float[] lightPositionWorldSpace = new float[4];
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mModelMatrix, 0, lightPosition, 0);
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mViewMatrix, 0, lightPositionWorldSpace, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPositionWorldSpace, normalMatrix, viewPosition, 0);
        terrain.draw(programObjDefaultHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.f, -1.2f, 0.f);
        Matrix.scaleM(mModelMatrix, 0 , 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        normalMatrix = new float[16];
        Matrix.multiplyMM(normalMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrixInverted = new float[16];
        Matrix.invertM(normalMatrixInverted, 0, normalMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, normalMatrixInverted, 0);

        lightPositionWorldSpace = new float[4];
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mModelMatrix, 0, lightPosition, 0);
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mViewMatrix, 0, lightPositionWorldSpace, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPositionWorldSpace, normalMatrix,viewPosition, 0);
        fireplaceObj.draw(programObjDefaultHandle);
        int err = GLES20.GL_INVALID_OPERATION  ;
        while((err = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
        {
            Log.d(LogTag.FRAMEBUFFER, "Error is "+err);
        }
        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 1);
        updateObjDefaultUniforms(lightPosition, normalMatrix, viewPosition, 0);

        //lightSourceObj.draw(programObjDefaultHandle);
        GLES20.glUniform1i(u_EmitMode_ObjDefaultHandle, 0);


        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, 0.0f);
        //Matrix.scaleM(mModelMatrix, 0, 0.1f, 0.1f, 0.1f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        normalMatrix = new float[16];
        Matrix.multiplyMM(normalMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrixInverted = new float[16];
        Matrix.invertM(normalMatrixInverted, 0, normalMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, normalMatrixInverted, 0);


        lightPositionWorldSpace = new float[4];
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mModelMatrix, 0, lightPosition, 0);
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mViewMatrix, 0, lightPositionWorldSpace, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateObjDefaultUniforms(lightPosition, normalMatrix, viewPosition, 1);
        trees.draw(programObjDefaultHandle);

        grass.draw(programObjDefaultHandle);

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, -3.0f, -1.0f, 0.5f);
        Matrix.rotateM(mModelMatrix, 0, -90, 0, 1.f, 0);
        Matrix.scaleM(mModelMatrix, 0, 0.5f, 0.5f, 0.5f);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        normalMatrix = new float[16];
        Matrix.multiplyMM(normalMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        normalMatrixInverted = new float[16];
        Matrix.invertM(normalMatrixInverted, 0, normalMatrix, 0);
        Matrix.transposeM(normalMatrix, 0, normalMatrixInverted, 0);


        lightPositionWorldSpace = new float[4];
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mModelMatrix, 0, lightPosition, 0);
        Matrix.multiplyMV(lightPositionWorldSpace, 0, mViewMatrix, 0, lightPositionWorldSpace, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        updateNormalMappingUniforms(lightPosition, viewPosition, normalMatrix);
        stump.draw(programNormalMapHandle);


        elapsedTime = System.currentTimeMillis() - startTime;
        double fps = 1000 / elapsedTime;
        Log.d(LogTag.FRAMERATE, "FrameRate is " + fps + " fps");
        startTime = System.currentTimeMillis();
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

    private void updateObjDefaultUniforms(float[] lightPosition, float[] normalMatrix, float[] viewPosition, int u_Option)
    {
        GLES20.glUseProgram(programObjDefaultHandle);
        GLES20.glUniformMatrix4fv(u_MVPMatrix_ObjDefaultHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ViewMatrix_ObjDefaultHandle, 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(u_ModelMatrix_ObjDefaultHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniform3fv(u_LightPositionWorldSpace_ObjDefaultHandle, 1, lightPosition, 0);
        GLES20.glUniform3fv(u_ViewPositionWorldSpace_ObjDefaultHandle, 1, viewPosition, 0);
        GLES20.glUniformMatrix4fv(u_NormalMatrix_ObjDefaultHandle, 1, false, normalMatrix, 0);
        GLES20.glUniform1i(u_Option_ObjDefaultHandle, u_Option);
    }

    public void setOptions(int options) {
        switch (options)
        {
            case 1:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normal4;
                colourDepthID = R.drawable.particule_colour_depth2;
                quantizedID = R.drawable.greytintwhitecenter;
                skyboxIDs = new int[]{
                                R.drawable.green_nebula_right1,
                                R.drawable.green_nebula_left2,
                                R.drawable.green_nebula_top3,
                                R.drawable.green_nebula_bottom4,
                                R.drawable.green_nebula_back6,
                                R.drawable.green_nebula_front5
                                };
                break;
            case 2:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normaleastwind;
                colourDepthID = R.drawable.particule_colour_depth2;
                quantizedID = R.drawable.greytintwhitecenterwithgradient;
                bias = new float[]{ 0.2f, -.2f, 0.f};
                skyboxIDs = new int[]{
                        R.drawable.standart_sky_right1,
                        R.drawable.standart_sky_left2,
                        R.drawable.standart_sky_top3,
                        R.drawable.standart_sky_bottom4,
                        R.drawable.standart_sky_back6,
                        R.drawable.standart_sky_front5
                };
                break;

            case 3:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normal4;
                colourDepthID = R.drawable.particule_colour_depth3;
                quantizedID = R.drawable.greytintwhitecenterwithgradient;
                skyboxIDs = new int[]{
                        R.drawable.green_nebula_right1,
                        R.drawable.green_nebula_left2,
                        R.drawable.green_nebula_top3,
                        R.drawable.green_nebula_bottom4,
                        R.drawable.green_nebula_back6,
                        R.drawable.green_nebula_front5
                };
                break;
            case 4:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normal;
                colourDepthID = R.drawable.particule_colour_depth3;
                quantizedID = R.drawable.gredgreenblacktellow;
                bias = new float[]{ 0.2f, -.2f, 0.f};
                skyboxIDs = new int[]{
                        R.drawable.green_nebula_right1,
                        R.drawable.green_nebula_left2,
                        R.drawable.green_nebula_top3,
                        R.drawable.green_nebula_bottom4,
                        R.drawable.green_nebula_back6,
                        R.drawable.green_nebula_front5
                };
                break;
            case 5:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normal6;
                colourDepthID = R.drawable.particule_colour_depth2;
                quantizedID = R.drawable.red;
                skyboxIDs = new int[]{
                        R.drawable.green_nebula_right1,
                        R.drawable.green_nebula_left2,
                        R.drawable.green_nebula_top3,
                        R.drawable.green_nebula_bottom4,
                        R.drawable.green_nebula_back6,
                        R.drawable.green_nebula_front5
                };
                break;
            case 6:
                optionVariationOnL = 0;
                normalAlphaID = R.drawable.particule_normal6;
                colourDepthID = R.drawable.particule_colour_depth2;
                quantizedID = R.drawable.gredgreenblacktellow;
                skyboxIDs = new int[]{
                        R.drawable.green_nebula_right1,
                        R.drawable.green_nebula_left2,
                        R.drawable.green_nebula_top3,
                        R.drawable.green_nebula_bottom4,
                        R.drawable.green_nebula_back6,
                        R.drawable.green_nebula_front5
                };
                break;
            default:
                System.exit(0);
                break;
        }
    }
}
