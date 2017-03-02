package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;


/**
 * Created by arturpopov on 02/03/2017.
 */

public class ParticleGenerator
{
    static final int BYTES_PER_FLOAT = 4;
    private final Context mContext;


    public ParticleGenerator(Context mContext)
    {
        lastUsedParticleIndex = 0;
        this.mContext = mContext;
    }

    float spread = 0.f;

    //Other Ints
    int lastUsedParticleIndex, particleCount;
    int programHandle, texture, textureID, cameraRightWorldSpaceID, cameraUpWorldSpaceID, viewProjectionMatrixID; //TODO fix names
    int[] vertexArrayID = new int[1], billboardBufferHandle = new int[1], particlePositionBufferHandle = new int[1], particleColourHandle = new int[1];
    final int MAX_PARTICLES = 2000;

    double lastTime;

    List<Particle> particleContainer;

    float[] particulePositionDataSize;
    byte[] particuleColourDataSize;

    void create(int programHandle)
    {
        this.programHandle = programHandle;
        for (int i = 0; i < MAX_PARTICLES; i++)
        {
            particleContainer.add(new Particle());
        }
        GLES20.glGenBuffers(1, vertexArrayID, 0);
        GLES20.glBindBuffer(programHandle, vertexArrayID[0]);

        GLES20.glUseProgram(programHandle);


       final float[] vertexBufferData = new float[]{ //Instancing Data
               -0.5f, -0.5f, 0.0f,
               0.5f, -0.5f, 0.0f,
               -0.5f, 0.5f, 0.0f,
               0.5f, 0.5f, 0.0f,
       };
        particulePositionDataSize = new float[MAX_PARTICLES * 4];
        particuleColourDataSize = new byte[MAX_PARTICLES * 4];

        for(int i = 0; i < MAX_PARTICLES; i++)
        {
            particleContainer.get(i).timeToLive = -1.f;
            particleContainer.get(i).distanceCamera = -1.f;
        }

        GLES20.glGenBuffers(1, billboardBufferHandle, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, billboardBufferHandle[0]);

        FloatBuffer mVerticeBuffer = ByteBuffer.allocateDirect(vertexBufferData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticeBuffer.put(vertexBufferData).position(0);

        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVerticeBuffer.capacity() * BYTES_PER_FLOAT, mVerticeBuffer, GLES20.GL_STATIC_DRAW);


        GLES20.glGenBuffers(1, particlePositionBufferHandle, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, particlePositionBufferHandle[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * BYTES_PER_FLOAT, null, GLES20.GL_STREAM_DRAW);

        GLES20.glGenBuffers(1, particleColourHandle, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, particleColourHandle[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, MAX_PARTICLES * 4, null, GLES20.GL_STREAM_DRAW);

        defineUniforms();

        texture = MainRenderer.loadTexture(mContext, R.drawable.droplet);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);

        if(texture == 0)
        {
            Log.d(LogTag.TEXTURE, "Error Loading Particle Texture");
        }

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);


        lastTime = System.nanoTime();
    }

    int findUnusedParticle()
    {
        int result = 0;

        for(int i = lastUsedParticleIndex; i < particleContainer.size(); i++)
        {
            if(particleContainer.get(i).timeToLive < 0)
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
                if (particleContainer.get(i).timeToLive < 0)
                {
                    lastUsedParticleIndex = i;
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    void sortParticles()
    {
        Collections.sort(particleContainer);
    }


    void drawParticles(float[] projectionMatrix, float[] viewMatrix)
    {
        double currentTime = System.nanoTime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;
    }

    void defineUniforms()
    {
        GLES20.glBindBuffer(programHandle, vertexArrayID[0]);
        GLES20.glUseProgram(programHandle);

        cameraRightWorldSpaceID = GLES20.glGetUniformLocation(programHandle, "u_CameraRightWorldSpace");
        cameraUpWorldSpaceID = GLES20.glGetUniformLocation(programHandle, "u_CameraUpWorldSpace");
        viewProjectionMatrixID = GLES20.glGetUniformLocation(programHandle, "u_ViewProjectionMatrix");

        textureID = GLES20.glGetUniformLocation(programHandle, "u_TextureSampler");
    }



}
class Particle implements Comparable<Particle>
{
    protected float[] position = new float[3], speed = new float[3];
    protected char r,g,b,a;
    protected float size, angle, weight;
    float timeToLive;
    float distanceCamera;

    @Override
    public int compareTo(Particle other)
    {
        float result = other.distanceCamera - this.distanceCamera; //Reverse Order
        return (int)result;
    }

       /* bool operator<(const Particle& that) const {
        // Sort in reverse order : far particles drawn first.
        return this->cameradistance > that.cameradistance;*/

}

