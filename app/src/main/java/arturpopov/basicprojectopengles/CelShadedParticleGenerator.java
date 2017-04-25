package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class CelShadedParticleGenerator
{
    private static final int BYTES_PER_FLOAT = 4;

    private static final int VERTEX_ARRAY_ID_INDEX = 0, BILLBOARD_BUFFER_HANDLE_INDEX = 1, PARTICULE_POSITION_HANDLE_INDEX = 2;
    private static final int CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX = 0, CAMERA_UP_WORLDSPACE_HANDLE_INDEX = 1, VIEW_PROJECTION_MATRIX_HANDLE_INDEX = 2, TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX = 3, TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX = 4, VIEW_MATRIX_HANDLE_INDEX = 5, TEXTURE_CEL_SHADING_SAMPLER_HANDLE_INDEX = 6, VARIABLE_L_OPTION_HANDLE_INDEX = 7, LIGHT_POSITION_WORLD_SPACE_HANDLE_INDEX = 8;
    private static final int NUMBER_HANDLES = 3, UNIFORM_COUNT = 9;
    private static final int MAX_PARTICLES = 1000;
    private static final int PARTICLE_SPAWN_LIMIT = 10;

    private final Context mContext;
    private int programHandle;

    private final int[] mArrayVertexHandles = new int[NUMBER_HANDLES];
    private final int[] mArrayUniformHandles = new int[UNIFORM_COUNT];
    private int textureColourActiveID, textureNormalAlphaActiveID, textureCelShadingActiveID;
    private List<Particle> mParticleContainer = new ArrayList<>();

    private float[] particulePositionData;
    private double mLastTime;
    private double deltaTime;
    private int lastUsedParticleIndex = 0;
    private final float spread = 0.1f;
    private final Random rnd = new Random();
    private int queuedParticleGeneration;


    private int optionVariation = 0;
    private float[] bias = new float[]{0.f, 0.f, 0.f};

    CelShadedParticleGenerator(Context mContext)
    {
        this.mContext = mContext;
    }

    void create(int toLoadTextureNormalDepthID, int toLoadTextureColourID, int celShadingTextureID, int optionVariation, float[] bias)
    {
        programHandle = ShaderBuilder.LoadProgram("smokeShader", mContext);
        this.bias = bias;
        this.optionVariation = optionVariation;
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

        defineUniformHandles(toLoadTextureNormalDepthID, toLoadTextureColourID, celShadingTextureID);

        if(mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX] == 0
                || mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX] == 0)
        {
            Log.d(LogTag.TEXTURE, "Error Loading Particle Texture");
        }
        mLastTime = System.nanoTime();
    }

    void drawParticles(float[] viewProjectionMatrix, float[] viewMatrix, float[] lightPositionWorldSpace)
    {
        GLES30.glUseProgram(programHandle);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_GREATER);

        GLES30.glBindVertexArray(mArrayVertexHandles[VERTEX_ARRAY_ID_INDEX]);

        double currentTime = System.nanoTime();

        deltaTime = (currentTime - mLastTime) / 1000000000;
        mLastTime = currentTime;

        float[] inverseView = MathUtilities.getInverse(viewMatrix);
        float[] cameraPosition = Arrays.copyOfRange(inverseView, 12, inverseView.length);
        int particuleCount;
        particuleCount = simulateParticles(cameraPosition);
        generateNewParticles(particuleCount);



        mParticleContainer = Particle.sortParticles(mParticleContainer);

        updateBuffers(particuleCount);

        if(optionVariation != 0)
        {
            GLES30.glUniform1i(mArrayUniformHandles[VARIABLE_L_OPTION_HANDLE_INDEX], optionVariation);
        }
        updateUniforms(viewProjectionMatrix, viewMatrix, lightPositionWorldSpace);
        setVertexAttributes(particuleCount);

    }

    private void setVertexAttributes(int particuleCount)
    {
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES20.GL_LESS);

        GLES30.glEnableVertexAttribArray(0);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[BILLBOARD_BUFFER_HANDLE_INDEX]);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, 0);

        GLES30.glEnableVertexAttribArray(1);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_POSITION_HANDLE_INDEX]);
        GLES30.glVertexAttribPointer(
                1, 4, GLES30.GL_FLOAT, false, 0, 0
        );


        GLES30.glVertexAttribDivisor(0, 0);
        GLES30.glVertexAttribDivisor(1, 1);

        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0, 4, particuleCount);
        GLES30.glVertexAttribDivisor(0, 0);
        GLES30.glVertexAttribDivisor(1, 0);

        GLES30.glDisable(GLES30.GL_BLEND);
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);
    }

    private void updateBuffers(int particuleCount)
    {
        //POSITIONAL DATA
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_POSITION_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * BYTES_PER_FLOAT, null, GLES30.GL_STREAM_DRAW);
        FloatBuffer mParticulePositionBuffer = ByteBuffer.allocateDirect(particulePositionData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mParticulePositionBuffer.put(particulePositionData).position(0);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, particuleCount * BYTES_PER_FLOAT * 4, mParticulePositionBuffer);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private int simulateParticles(float[] cameraPosition)
    {
        int particuleCount = 0;
        for(int i = 0; i < MAX_PARTICLES; i++)
        {

            Particle p = mParticleContainer.get(i);
            if (p.timeToLive > 0.0f)
            {
                p.timeToLive -= deltaTime;
                if (p.timeToLive > 0.f)
                {
                    //gravity
                    float gravity = p.speed[1] + 9.81f * (float) deltaTime; //define gravity here
                    p.speed[1] = gravity * 0.8f; //Adjust gravity strength
                    float rebinder = 1.0f;
                    if(p.halfLife > p.timeToLive)
                        rebinder = -0.2f;
                    p.position = new float[]
                            {
                                    p.position[0] + ((p.speed[0] + bias[0])* (float) deltaTime) * rebinder,
                                    p.position[1] + ((p.speed[1] + bias[0]) * (float) deltaTime) ,
                                    p.position[2] + ((p.speed[2] + bias[0])* (float) deltaTime) * rebinder,
                            };
                    p.distanceCamera = MathUtilities.squaredLengthVector3(new float[]
                            {
                                    p.position[0] - cameraPosition[0],
                                    p.position[1] - cameraPosition[1],
                                    p.position[2] - cameraPosition[2]
                            });
                    int vertexIndex = 4 * particuleCount;
                    particulePositionData[vertexIndex + 0] = p.position[0];
                    particulePositionData[vertexIndex + 1] = p.position[1];
                    particulePositionData[vertexIndex + 2] = p.position[2];
                    p.size += p.size*0.1f * deltaTime;
                    particulePositionData[vertexIndex + 3] = p.size;

                } else
                {
                    p.distanceCamera = -1.f;
                    MainRenderer.polygonCounter--;
                }
                particuleCount++;

            }
        }
        return particuleCount;
    }

    private void generateNewParticles(int particuleCount)
    {
        double newParticles = (MAX_PARTICLES - particuleCount) * deltaTime;
        if(newParticles > PARTICLE_SPAWN_LIMIT)
            newParticles = PARTICLE_SPAWN_LIMIT;
        MainRenderer.polygonCounter += newParticles;
        for(int i = 0; i < newParticles; i++)
        {
            int particleIndex = findUnusedParticle();
            mParticleContainer.get(particleIndex).setLifeSpan(10.f);
            mParticleContainer.get(particleIndex).position = new float[]{0.f, 0.f, 0.f};
            float spreadF = spread;
            float[] mainDirection = new float[]{0.f, 0.05f, -0.1f};
            float[] rndDirection = MathUtilities.GetRandomSphericalDirection(rnd);
            mParticleContainer.get(particleIndex).speed = new float[]{
                    mainDirection[0] + rndDirection[0]*spreadF,
                    mainDirection[1] + rndDirection[1]*spreadF,
                    mainDirection[2] + rndDirection[2]*spreadF,
            };
            mParticleContainer.get(particleIndex).size = 0.3f;
        }
    }

    public void queueSmokePuff(int puffParticleSize)
    {
        this.queuedParticleGeneration += puffParticleSize;
    }

    void drawQueuedParticles(float[] viewProjectionMatrix, float[] viewMatrix, float[] lightPositionWorldSpace)
    {
        GLES30.glUseProgram(programHandle);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES30.GL_GREATER);

        GLES30.glBindVertexArray(mArrayVertexHandles[VERTEX_ARRAY_ID_INDEX]);

        double currentTime = System.nanoTime();

        deltaTime = (currentTime - mLastTime) / 1000000000;
        mLastTime = currentTime;

        float[] inverseView = MathUtilities.getInverse(viewMatrix);
        float[] cameraPosition = Arrays.copyOfRange(inverseView, 12, inverseView.length);
        int particuleCount;
        particuleCount = simulateParticles(cameraPosition);
        generateQueuedParticles();

        if(optionVariation != 0)
        {
            GLES30.glUniform1i(mArrayUniformHandles[VARIABLE_L_OPTION_HANDLE_INDEX], optionVariation);
        }

        mParticleContainer = Particle.sortParticles(mParticleContainer);

        updateBuffers(particuleCount);

        updateUniforms(viewProjectionMatrix, viewMatrix, lightPositionWorldSpace);
        setVertexAttributes(particuleCount);

    }

    private void generateQueuedParticles()
    {
        if(queuedParticleGeneration <= 0)
        {
            return;
        }
        double newParticles =  deltaTime * 50;
        if(newParticles > PARTICLE_SPAWN_LIMIT)
            newParticles = PARTICLE_SPAWN_LIMIT;
        queuedParticleGeneration -= newParticles;

        MainRenderer.polygonCounter += newParticles;
        for(int i = 0; i < newParticles; i++)
        {
            int particleIndex = findUnusedParticle();
            mParticleContainer.get(particleIndex).setLifeSpan(10.f);
            mParticleContainer.get(particleIndex).position = new float[]{0.f, -0.7f, 0.f};
            float spreadF = spread;
            float[] mainDirection = new float[]{0.f, 0.05f, 0.1f};
            float[] rndDirection = MathUtilities.GetRandomSphericalDirection(rnd);
            mParticleContainer.get(particleIndex).speed = new float[]{
                    mainDirection[0] + rndDirection[0]*spreadF,
                    mainDirection[1] + rndDirection[1]*spreadF,
                    mainDirection[2] + rndDirection[2]*spreadF,
            };
            mParticleContainer.get(particleIndex).size = 0.3f;
        }
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


    private void defineUniformHandles(int toLoadTextureNormalDepthID, int toLoadTextureColourID, int toLoadTextureCelShading)
    {
        GLES30.glUseProgram(programHandle);

        mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraRightWorldSpace");
        mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraUpWorldSpace");
        mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_ViewProjectionMatrix");
        mArrayUniformHandles[VIEW_MATRIX_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_ViewMatrix");
        mArrayUniformHandles[VARIABLE_L_OPTION_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_LVariation");
        mArrayUniformHandles[LIGHT_POSITION_WORLD_SPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_LightPositionWorldSpace");


        mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureColourID);
        mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureNormalDepthID);
        mArrayUniformHandles[TEXTURE_CEL_SHADING_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureCelShading);


        textureColourActiveID = GLES30.glGetUniformLocation(programHandle, "textureColourDepth");
        textureNormalAlphaActiveID = GLES30.glGetUniformLocation(programHandle, "textureNormalAlpha");
        textureCelShadingActiveID = GLES30.glGetUniformLocation(programHandle, "textureCelShading");

    }


    private void updateUniforms(float[] viewProjectionMatrix, float[] viewMatrix, float[] lightPositionWorldSpace)
    {



        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX], viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX], viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        GLES30.glUniform3fv(mArrayUniformHandles[LIGHT_POSITION_WORLD_SPACE_HANDLE_INDEX], 1, lightPositionWorldSpace, 0);

        GLES30.glUniformMatrix4fv(
                mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX], 1, false, viewProjectionMatrix, 0
        );
        GLES30.glUniformMatrix4fv(
                mArrayUniformHandles[VIEW_MATRIX_HANDLE_INDEX], 1, false, viewMatrix, 0
        );

        GLES30.glUniform1i(textureColourActiveID, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_COLOUR_SAMPLER_HANDLE_INDEX]);


        GLES30.glUniform1i(textureNormalAlphaActiveID, 1);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_NORMAL_DEPTH_SAMPLER_HANDLE_INDEX]);

        GLES30.glUniform1i(textureCelShadingActiveID, 2);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE2);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_CEL_SHADING_SAMPLER_HANDLE_INDEX]);
    }


    private int findUnusedParticle()
    {
        int result = 0;

        for(int i = lastUsedParticleIndex; i < mParticleContainer.size(); i++)
        {
            if(mParticleContainer.get(i).timeToLive < 0)
            {
                lastUsedParticleIndex = i;
                result = i;
                break;
            }
        }
        if(result == 0)
        {
            for (int i = 0; i < lastUsedParticleIndex; i++)
            {
                if (mParticleContainer.get(i).timeToLive < 0)
                {
                    lastUsedParticleIndex = i;
                    result = i;
                    break;
                }
            }
        }
        return result;
    }


}
