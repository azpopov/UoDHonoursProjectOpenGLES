package arturpopov.basicprojectopengles;

import android.icu.text.MessagePattern;
import android.test.mock.MockContentProvider;
import android.test.mock.MockContext;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ParticleGeneratorTest
{
    @Test
    public void sortsReverseWithTwoParticles() throws Exception {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());

        Particle particle1 = new Particle();
        particle1.distanceCamera = 5.f;

        Particle particle2 = new Particle();
        particle2.distanceCamera = 2.f;

        List<Particle> expected = Arrays.asList(particle1, particle2);

        List<Particle> exampleList = Arrays.asList(particle2, particle1);

        gen.particleContainer = exampleList;

        gen.sortParticles();

        List<Particle> actual = gen.particleContainer;
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void sortsReverseWithZeroParticles() throws Exception {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());


        List<Particle> expected = Arrays.asList();

        List<Particle> exampleList = Arrays.asList();

        gen.particleContainer = exampleList;

        gen.sortParticles();

        List<Particle> actual = gen.particleContainer;
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void sortsReverseWithManyParticles() throws Exception
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());

        Random rnd = new Random();
        rnd.setSeed(12345);
        List<Particle> expected = new Vector<>();
        for (int i = 500; i > 0; i--)
        {
            Particle particle = new Particle();
            particle.distanceCamera = (float) i;
            expected.add(particle);
        }
        List<Particle> exampleList = new ArrayList<>(expected);
        //http://stackoverflow.com/questions/1519736/random-shuffling-of-an-array
        for (int i = expected.size() - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            Particle a = exampleList.get(index);
            exampleList.set(index, exampleList.get(i));
            exampleList.set(i, a);
        }
        gen.particleContainer = exampleList;

        gen.sortParticles();

        List<Particle> actual = gen.particleContainer;
        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    public void findUnusedParticleWhereFirstCheck()
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());
        Particle particle1 = new Particle();
        particle1.timeToLive = -1.f;

        Particle particle2 = new Particle();
        particle2.timeToLive = 2.f;


        List<Particle> exampleList = Arrays.asList( particle1, particle2 );
        int expected = 0;

        gen.particleContainer = exampleList;

        int actual = gen.findUnusedParticle();

        assertEquals(expected, actual);
    }

    @Test
    public void findUnusedParticleWhereSecond()
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());
        Particle particle1 = new Particle();
        particle1.timeToLive = 2.f;

        Particle particle2 = new Particle();
        particle2.timeToLive = -12.f;


        List<Particle> exampleList = Arrays.asList( particle1, particle2 );
        int expected = 1;

        gen.particleContainer = exampleList;

        int actual = gen.findUnusedParticle();

        assertEquals(expected, actual);
    }

    @Test
    public void findUnusedParticleWhereInBetween()
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());
        Particle particle1 = new Particle();
        particle1.timeToLive = 2.f;

        Particle particle2 = new Particle();
        particle2.timeToLive = -12.f;

        Particle particle3 = new Particle();
        particle3.timeToLive = 5.f;

        List<Particle> exampleList = Arrays.asList( particle1, particle2, particle3 );
        int expected = 1;

        gen.particleContainer = exampleList;

        int actual = gen.findUnusedParticle();

        assertEquals(expected, actual);
    }

    @Test
    public void findUnusedParticleWhereNone()
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());
        Particle particle1 = new Particle();
        particle1.timeToLive = 2.f;

        Particle particle2 = new Particle();
        particle2.timeToLive = 12.f;

        Particle particle3 = new Particle();
        particle3.timeToLive = 5.f;

        List<Particle> exampleList = Arrays.asList( particle1, particle2, particle3 );
        int expected = 0;

        gen.particleContainer = exampleList;

        int actual = gen.findUnusedParticle();

        assertEquals(expected, actual);
    }

    @Test
    public void findUnusedParticleWhereLastUsedNonZero()
    {
        ParticleGenerator gen = new ParticleGenerator(new MockContext());
        Particle particle1 = new Particle();
        particle1.timeToLive = -2.f;

        Particle particle2 = new Particle();
        particle2.timeToLive = 12.f;

        Particle particle3 = new Particle();
        particle3.timeToLive = -5.f;

        List<Particle> exampleList = Arrays.asList( particle1, particle2, particle3 );
        int expected = 2;

        gen.particleContainer = exampleList;
        gen.lastUsedParticleIndex = 1;

        int actual = gen.findUnusedParticle();

        assertEquals(expected, actual);
    }



}
