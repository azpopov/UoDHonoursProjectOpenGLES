package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by arturpopov on 11/03/2017.
 */

public class CelShadedParticleGenerator
{
    private static final int BYTES_PER_FLOAT = 4;

    private static final int VERTEX_ARRAY_ID_INDEX = 0, BILLBOARD_BUFFER_HANDLE_INDEX = 1, PARTICULE_POSITION_HANDLE_INDEX = 2;
    private static final int CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX = 0, CAMERA_UP_WORLDSPACE_HANDLE_INDEX = 1, VIEW_PROJECTION_MATRIX_HANDLE_INDEX = 2, TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX = 3, TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX = 4;
    private static final int NUMBER_HANDLES = 3, UNIFORM_COUNT = 4;
    private static final int MAX_PARTICLES = 100;

    private final Context mContext;
    private int programHandle;

    private int[] mArrayVertexHandles = new int[NUMBER_HANDLES];
    private int[] mArrayUniformHandles = new int[UNIFORM_COUNT];
    private int textureColourActiveID, textureNormalDepthActiveID;
    private List<Particle> mParticleContainer = new ArrayList<>();

    private float[] particulePositionData;
    private double mLastTime;
    private double deltaTime;

    CelShadedParticleGenerator(Context mContext)
    {
        this.mContext = mContext;
    }

    void create(int toLoadTextureNormalDepthID, int toLoadTextureColourID)
    {
        programHandle = ShaderBuilder.LoadProgram("smokeShader", mContext);
        GLES30.glUseProgram(programHandle);

        for (int i = 0; i < MAX_PARTICLES; i++)
        {
            mParticleContainer.add(new Particle());
            mParticleContainer.get(i).timeToLive = -1.f;
            mParticleContainer.get(i).distanceCamera = -1.f;
        }

        defineVertexHandles();

        GLES30.glBindVertexArray(mArrayVertexHandles[VERTEX_ARRAY_ID_INDEX]);

        final float[] squareVertexData = new float[]{ //Instancing Data
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f,
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
        };
        particulePositionData = new float[MAX_PARTICLES * 4];

        setupBuffers(squareVertexData);

        defineUniformHandles(toLoadTextureNormalDepthID, toLoadTextureColourID);

        if(mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX] == 0
                || mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX] == 0)
        {
            Log.d(LogTag.TEXTURE, "Error Loading Particle Texture");
        }
        mLastTime = System.nanoTime();
    }

    void drawParticles(float[] viewProjectionMatrix, float[] viewMatrix)
    {
        GLES30.glUseProgram(programHandle);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_LESS);

        GLES30.glBindVertexArray(mArrayVertexHandles[VERTEX_ARRAY_ID_INDEX]);

        double currentTime = System.nanoTime();

        deltaTime = (currentTime - mLastTime) / 1000000000;
        mLastTime = currentTime;

        float[] inverseView = getInverse(viewMatrix);
        float[] cameraPosition = Arrays.copyOfRange(inverseView, 12, inverseView.length);

    }

    private float[] getInverse(float[] viewMatrix)
    {
        return new float[]{};
    }


    private void setupBuffers(float[] squareVertexData)
    {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[BILLBOARD_BUFFER_HANDLE_INDEX]);
        FloatBuffer squareVerticesBuffer = ByteBuffer.allocateDirect(squareVertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        squareVerticesBuffer.put(squareVertexData).position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, squareVerticesBuffer.capacity() * BYTES_PER_FLOAT, squareVerticesBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_POSITION_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * BYTES_PER_FLOAT, null, GLES30.GL_STREAM_DRAW);
    }

    private void defineVertexHandles()
    {
        GLES30.glGenVertexArrays(1, mArrayVertexHandles, VERTEX_ARRAY_ID_INDEX);
        GLES30.glGenBuffers(1, mArrayVertexHandles, BILLBOARD_BUFFER_HANDLE_INDEX);
        GLES30.glGenBuffers(1, mArrayVertexHandles, PARTICULE_POSITION_HANDLE_INDEX);
    }


    private void defineUniformHandles(int toLoadTextureNormalDepthID, int toLoadTextureColourID)
    {
        GLES30.glUseProgram(programHandle);

        mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraRightWorldSpace");
        mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraUpWorldSpace");
        mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_ViewProjectionMatrix");

        mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureColourID);
        mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureNormalDepthID);

        textureColourActiveID = GLES30.glGetUniformLocation(programHandle, "textureColourAlpha");
        textureNormalDepthActiveID = GLES30.glGetUniformLocation(programHandle, "textureNormalDepth");
    }


    private void updateUniforms(float[] viewProjectionMatrix, float[] viewMatrix)
    {
        GLES30.glUniform1i(textureColourActiveID, 0);
        GLES30.glUniform1i(textureNormalDepthActiveID, 1);

        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX], viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX], viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        GLES30.glUniformMatrix4fv(
                mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX], 1, false, viewProjectionMatrix, 0
        );

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX]);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX]);
    }



}
