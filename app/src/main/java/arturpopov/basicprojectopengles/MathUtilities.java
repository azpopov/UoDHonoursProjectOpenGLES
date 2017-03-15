package arturpopov.basicprojectopengles;

import java.util.Random;

import Jama.Matrix;

/**
 * Created by arturpopov on 13/03/2017.
 */

public class MathUtilities
{
    public static float[] getInverse(float[] toInverse)
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

    public static float squaredLengthVector3(float[] vector)
    {
        return (vector[0] * vector[0]) + (vector[1] * vector[1]) + (vector[2] * vector[2]);
    }

    static float[] GetRandomSphericalDirection(Random rnd)
    {
        double x = rnd.nextFloat() -0.5, y = rnd.nextFloat() -0.5, z = rnd.nextFloat() -0.5;
        double k = Math.sqrt(x*x + y*y + z*z);
        while(k < 0.2 || k > 0.3)
        {
            x = rnd.nextFloat() -0.5;
            y = rnd.nextFloat() -0.5;
            z = rnd.nextFloat() -0.5;
            k = Math.sqrt(x*x + y*y + z*z);
        }
        return new float[]{(float)(x/k), (float)(y/k), (float)(z/k)};
    }
}
