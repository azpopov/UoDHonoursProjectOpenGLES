package arturpopov.basicprojectopengles;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class ParticleGeneratorTest
{
    @Test
    public void sortsReverseWithTwoParticles() throws Exception {
        ParticleGenerator gen = new ParticleGenerator();

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
}
