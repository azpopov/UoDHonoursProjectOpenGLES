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

@SuppressWarnings("PointlessArithmeticExpression")
public class ParticleGenerator
{
    private static final int BYTES_PER_FLOAT = 4;

    private static final int VERTEX_ARRAY_ID_INDEX = 0, BILLBOARD_BUFFER_HANDLE_INDEX = 1, PARTICULE_POSITION_HANDLE_INDEX = 2, PARTICULE_COLOUR_HANDLE_INDEX = 3;
    private static final int TEXTURE_SAMPLER_HANDLE_INDEX = 0, CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX = 1, CAMERA_UP_WORLDSPACE_HANDLE_INDEX = 2, VIEW_PROJECTION_MATRIX_HANDLE_INDEX = 3, VIEW_MATRIX_HANDLE_INDEX = 4, LIGHT_POSITION_WORLD_SPACE_HANDLE_INDEX = 5 ;
    private static final int NUMBER_HANDLES = 4, UNIFORM_COUNT = 5;

    private final Context mContext;


    ParticleGenerator(Context mContext)
    {
        lastUsedParticleIndex = 0;
        this.mContext = mContext;
    }

    @SuppressWarnings("WeakerAccess")
    public final float spread = 0.5f;

    //Other Ints
    int lastUsedParticleIndex;
    private int programHandle;
    private int textureActiveID;
    private final int[] mArrayVertexHandles = new int[NUMBER_HANDLES];
    private final int[] mArrayUniformHandles = new int[UNIFORM_COUNT];

    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_PARTICLES = 1000, PARTICLE_SPAWN_LIMIT = (int)(0.016f * 2000);
    private double mLastTime, deltaTime;

    List<Particle> mParticleContainer = new ArrayList<>();

    private float[] particulePositionData;
    private byte[] particuleColourData;


    private final Random rnd = new Random();

    void create( int toLoadTextureID)
    {
        rnd.setSeed(1000);
        programHandle = ShaderBuilder.LoadProgram("particleGenerator", mContext);
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
        particuleColourData = new byte[MAX_PARTICLES * 4];


        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[BILLBOARD_BUFFER_HANDLE_INDEX]);
        FloatBuffer squareVerticesBuffer = ByteBuffer.allocateDirect(squareVertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        squareVerticesBuffer.put(squareVertexData).position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, squareVerticesBuffer.capacity() * BYTES_PER_FLOAT, squareVerticesBuffer, GLES30.GL_STATIC_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_POSITION_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * BYTES_PER_FLOAT, null, GLES30.GL_STREAM_DRAW);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_COLOUR_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4, null, GLES30.GL_STREAM_DRAW);

        defineUniformHandles(toLoadTextureID);



        if(mArrayUniformHandles[TEXTURE_SAMPLER_HANDLE_INDEX] == 0)
        {
            Log.d(LogTag.TEXTURE, "Error Loading Particle Texture");
        }
        mLastTime = System.nanoTime();
    }

    private void defineVertexHandles()
    {
        GLES30.glGenVertexArrays(1, mArrayVertexHandles, VERTEX_ARRAY_ID_INDEX);
        GLES30.glGenBuffers(1, mArrayVertexHandles, BILLBOARD_BUFFER_HANDLE_INDEX);
        GLES30.glGenBuffers(1, mArrayVertexHandles, PARTICULE_POSITION_HANDLE_INDEX);
        GLES30.glGenBuffers(1, mArrayVertexHandles, PARTICULE_COLOUR_HANDLE_INDEX);
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

        float[] inverseView = MathUtilities.getInverse(viewMatrix);
        float[] cameraPosition = Arrays.copyOfRange(inverseView, 12, inverseView.length);

        generateNewParticles();

        int particuleCount;
        particuleCount = simulateParticles(cameraPosition);
        mParticleContainer = Particle.sortParticles(mParticleContainer);

        updateBuffers(particuleCount);





        updateUniforms(viewProjectionMatrix, viewMatrix);
        setVertexAttributes(particuleCount);


    }

    private void updateUniforms(float[] viewProjectionMatrix, float[] viewMatrix)
    {
        GLES30.glUniform1i(textureActiveID, 1);
        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX], viewMatrix[0], viewMatrix[4], viewMatrix[8]);
        GLES30.glUniform3f(mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX], viewMatrix[1], viewMatrix[5], viewMatrix[9]);
        GLES30.glUniformMatrix4fv(
                mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX], 1, false, viewProjectionMatrix, 0
        );
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mArrayUniformHandles[TEXTURE_SAMPLER_HANDLE_INDEX]);
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

        GLES30.glEnableVertexAttribArray(2);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_COLOUR_HANDLE_INDEX]);
        GLES30.glVertexAttribPointer(
                2, 4, GLES30.GL_UNSIGNED_BYTE, true, 0, 0
        );


        GLES30.glVertexAttribDivisor(0, 0); // particles vertices : always reuse the same 4 vertices -> 0
        GLES30.glVertexAttribDivisor(1, 1); // positions : one per quad (its center)                 -> 1
        GLES30.glVertexAttribDivisor(2, 1); // color : one per quad                                  -> 1


        GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0, 4, particuleCount);
        GLES30.glVertexAttribDivisor(0, 0); // particles vertices : always reuse the same 4 vertices -> 0
        GLES30.glVertexAttribDivisor(1, 0); // positions : one per quad (its center)                 -> 1
        GLES30.glVertexAttribDivisor(2, 0); // color : one per quad                                  -> 1

        GLES30.glDisable(GLES30.GL_BLEND);
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);
    }

    private void updateBuffers(int particuleCount)
    {
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_POSITION_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * BYTES_PER_FLOAT, null, GLES30.GL_STREAM_DRAW);
        FloatBuffer mParticulePositionBuffer = ByteBuffer.allocateDirect(particulePositionData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mParticulePositionBuffer.put(particulePositionData).position(0);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, particuleCount * BYTES_PER_FLOAT * 4, mParticulePositionBuffer);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mArrayVertexHandles[PARTICULE_COLOUR_HANDLE_INDEX]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, MAX_PARTICLES * 4, null, GLES30.GL_STREAM_DRAW);
        ByteBuffer mParticuleColourBuffer = ByteBuffer.allocateDirect(particuleColourData.length)
                .order(ByteOrder.nativeOrder());
        mParticuleColourBuffer.put(particuleColourData).position(0);
        GLES30.glBufferSubData(GLES30.GL_ARRAY_BUFFER, 0, particuleCount * 4, mParticuleColourBuffer);
    }


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
                    p.speed[1] = p.speed[1] - 9.81f * (float) deltaTime * 0.5f;
                    p.position = new float[]
                            {
                                    p.position[0] + (p.speed[0] * (float) deltaTime),
                                    p.position[1] + (p.speed[1] * (float) deltaTime),
                                    p.position[2] + (p.speed[2] * (float) deltaTime),
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
                    particulePositionData[vertexIndex + 3] = p.size;

                    particuleColourData[vertexIndex + 0] = p.r;
                    particuleColourData[vertexIndex + 1] = p.g;
                    particuleColourData[vertexIndex + 2] = p.b;
                    particuleColourData[vertexIndex + 3] = p.a;


                } else
                {
                    p.distanceCamera = -1.f;
                }
                particuleCount++;
            }
        }
        return particuleCount;
    }

    private void generateNewParticles()
    {
        int newParticles = (int)(deltaTime * 2000);
        if(newParticles > PARTICLE_SPAWN_LIMIT)
            newParticles = PARTICLE_SPAWN_LIMIT;
        Log.d(LogTag.PARTICLE_EFFECT, "\nNewParticleCount: " +newParticles);
        for(int i = 0; i < newParticles; i++)
        {
            int particleIndex = findUnusedParticle();
            mParticleContainer.get(particleIndex).timeToLive = 0.8f;
            mParticleContainer.get(particleIndex).position = new float[]{0.f, 1.f, 0.f};
            float spreadF = spread;
            float[] mainDirection = new float[]{0.f, -0.05f, 1.f};
            float[] rndDirection = MathUtilities.GetRandomSphericalDirection(rnd);
            mParticleContainer.get(particleIndex).speed = new float[]{
                    mainDirection[0] + rndDirection[0]*spreadF,
                    mainDirection[1] + rndDirection[1]*spreadF,
                    mainDirection[2] + rndDirection[2]*spreadF,
            };
            mParticleContainer.get(particleIndex).r = (byte)(rnd.nextInt() % 50);
            mParticleContainer.get(particleIndex).g = (byte)Math.abs(rnd.nextInt() % 50);
            mParticleContainer.get(particleIndex).b = (byte)(rnd.nextInt() % 256);
            mParticleContainer.get(particleIndex).a = (byte)((rnd.nextInt() % 300)/3);

            mParticleContainer.get(particleIndex).size = ((rnd.nextInt() % 1000) / 2000.f) + 0.1f;

        }
    }

    private void defineUniformHandles(int toLoadTextureID)
    {
        GLES30.glUseProgram(programHandle);

        mArrayUniformHandles[CAMERA_RIGHT_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraRightWorldSpace");
        mArrayUniformHandles[CAMERA_UP_WORLDSPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_CameraUpWorldSpace");
        mArrayUniformHandles[VIEW_PROJECTION_MATRIX_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_ViewProjectionMatrix");
        mArrayUniformHandles[VIEW_MATRIX_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_ViewMatrix");
        mArrayUniformHandles[LIGHT_POSITION_WORLD_SPACE_HANDLE_INDEX] = GLES30.glGetUniformLocation(programHandle, "u_LightPositionWorldSpace");
        mArrayUniformHandles[TEXTURE_SAMPLER_HANDLE_INDEX] = TextureLoader.loadTexture(mContext, toLoadTextureID);
        textureActiveID = GLES30.glGetUniformLocation(programHandle, "u_TextureSampler");
    }

    int findUnusedParticle()
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

