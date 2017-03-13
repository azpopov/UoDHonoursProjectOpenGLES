package arturpopov.basicprojectopengles;

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
}
