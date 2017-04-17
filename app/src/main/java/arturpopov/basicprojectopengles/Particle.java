package arturpopov.basicprojectopengles;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import static java.lang.Float.compare;

/**
 * Created by arturpopov on 13/03/2017.
 */
class Particle implements Comparable<Particle>
{
    float[] position = new float[3];
    float[] speed = new float[3];
    byte r;
    byte g;
    byte b;
    byte a;
    float size;
    float timeToLive;
    float halfLife;
    float distanceCamera;

    public void setLifeSpan(float lifeSpan)
    {
        this.halfLife = lifeSpan / 2;
        timeToLive = lifeSpan;
    }

    static List<Particle> sortParticles(List<Particle> collection)
    {
        Collections.sort(collection);
        return collection;
    }

    @Override
    public int compareTo(@NonNull Particle other)
    {
        return compare(other.distanceCamera, this.distanceCamera);
    }
}
