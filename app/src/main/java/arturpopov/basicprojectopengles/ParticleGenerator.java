package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.renderscript.Matrix3f;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Jama.*;

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

    float spread = 0.2f;

    //Other Ints
    int lastUsedParticleIndex, particleCount;
    int programHandle, texture, textureID, cameraRightWorldSpaceID, cameraUpWorldSpaceID, viewProjectionMatrixID; //TODO fix names
    int[] vertexArrayID = new int[1], billboardBufferHandle = new int[1], particlePositionBufferHandle = new int[1], particleColourHandle = new int[1];
    final int MAX_PARTICLES = 2000;
    final int PARTICLE_SPAWN_LIMIT = (int)(0.016f * 2000);
    double lastTime;

    List<Particle> particleContainer;

    float[] particulePositionDataSize;
    byte[] particuleColourDataSize;


    Random rnd = new Random();

    void create(int programHandle)
    {
        rnd.setSeed(1000);
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


    void drawParticles(float[] viewProjectionMatrix, float[] viewMatrix)
    {
        double currentTime = System.nanoTime();
        double deltaTime = currentTime - lastTime;
        lastTime = currentTime;

        GLES20.glBindBuffer(programHandle, vertexArrayID[0]);


        float[] cameraPosition = GetInverse(viewMatrix);


        int newParticles = (int)(deltaTime * 2000);
        if(newParticles > PARTICLE_SPAWN_LIMIT)
            newParticles = PARTICLE_SPAWN_LIMIT;

        for(int i = 0; i < newParticles; i++)
        {
            int particleIndex = findUnusedParticle();
            particleContainer.get(particleIndex).timeToLive = 0.8f;
            particleContainer.get(particleIndex).position = new float[]{0.f, 0.f, -10.f};
            float spreadF = spread;
            float[] mainDirection = new float[]{0.f, -0.05f, 1.f};
            float[] rndDirection = getRandomSphericalDirection(); //TODO consider using an algorithm that spreads points uniformly.
            particleContainer.get(particleIndex).speed = new float[]{
                    mainDirection[0] + rndDirection[0]*spreadF,
                    mainDirection[1] + rndDirection[1]*spreadF,
                    mainDirection[2] + rndDirection[2]*spreadF,
            };
            particleContainer.get(particleIndex).r = (rnd.nextInt() % 50);
            particleContainer.get(particleIndex).g = (rnd.nextInt() % 50);
            particleContainer.get(particleIndex).b = (rnd.nextInt() % 256);
            particleContainer.get(particleIndex).a = (rnd.nextInt() % 300)/3;

            particleContainer.get(particleIndex).size = ((rnd.nextInt() % 1000) / 2000.f) + 0.1f;

        }

        int particuleCount = 0;
        for(int i = 0; i < MAX_PARTICLES; i++)
        {
            Particle p = particleContainer.get(i);
            if(p.timeToLive > 0.f)
            {
                p.timeToLive -= deltaTime;
                //gravity
                p.speed[1] = p.speed[1] - 9.81f * (float)deltaTime * 0.5f;
                p.position = new float[]
                        {
                                p.position[0] * (float)deltaTime,
                                p.position[1] * (float)deltaTime,
                                p.position[2] * (float)deltaTime,
                        };
            }
        }
    }

    public float[] GetInverse(float[] toInverse)
    {
        float[] result = new float[toInverse.length];
        int sqRoot =  (int)Math.sqrt(toInverse.length);
        double[][] jagged = new double[sqRoot][sqRoot];
        int k = 0;
        for(int i = 0; i < sqRoot; i++)
        {
            for(int j = 0; j < sqRoot; j++)
            {
                jagged[i][j] = toInverse[k++];
            }
        }
        Matrix m = new Matrix(jagged);
        double[] resultingDoubleArray = m.inverse().getRowPackedCopy();

        for(int i = 0; i < resultingDoubleArray.length; i++)
        {
            result[i] = (float)resultingDoubleArray[i];
        }
        return result;
    }

    public float[] getRandomSphericalDirection()
    {
        float[] result = new float[3];
        double x = rnd.nextFloat() -0.5, y = rnd.nextFloat() -0.5, z = rnd.nextFloat() -0.5;
        double k = Math.sqrt(x*x + y*y + z*z);
        while(k < 0.2 || k > 0.3)
        {
            x = rnd.nextFloat() -0.5;
            y = rnd.nextFloat() -0.5;
            z = rnd.nextFloat() -0.5;
            k = Math.sqrt(x*x + y*y + z*z);
        }
        return result = new float[]{(float)(x/k), (float)(y/k), (float)(z/k)};
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
    protected int r;
    protected int g;
    protected int b;
    protected int a;
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

