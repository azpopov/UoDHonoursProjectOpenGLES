package arturpopov.basicprojectopengles;

import java.util.Collections;
import java.util.List;

/**
 * Created by arturpopov on 02/03/2017.
 */

public class ParticleGenerator
{

    public ParticleGenerator()
    {
        lastUsedParticleIndex = 0;
    }

    float spread = 0.f;
    //Handles
    int billboardBufferHandle, particlePositionBufferHandle, particleColourHandle;
    //Other Ints
    int lastUsedParticleIndex, particleCount;
    int vertexArrayID, programHandle, texture, textureID, cameraRightWorldSpaceID, cameraUpWorldSpaceID, viewProjectionMatrixID; //TODO fix names
    final int MAX_PARTICLES = 2000;

    double lastTime;

    List<Particle> particleContainer;

    float[] particulePositionDataSize, particleColourDataSize;

    void create(int programHandle)
    {
        this.programHandle = programHandle;
        for (int i = 0; i < MAX_PARTICLES; i++)
        {
            particleContainer.add(new Particle());
        }
    }

    int findUnusedParticle()
    {
        int result = 0;

        return result;
    }

    void sortParticles()
    {
        Collections.sort(particleContainer);
    }


    void drawParticles(float[] projectionMatrix, float[] viewMatrix)
    {

    }

    void defineUniforms()
    {

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

