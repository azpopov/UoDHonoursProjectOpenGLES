package arturpopov.basicprojectopengles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by arturpopov on 01/02/2017.
 */

public class Triangle
{
    public final FloatBuffer mVetriceBuffer;

    public Triangle(int size)
    {
        final float[] triangleVetriceData =
                {
                        -0.5f, -0.25f, 0.5f, //X
                        0.5f, -0.25f, 0.0f, //Y
                        0.0f, 0.56f, 0.0f, //Z
                };

        mVetriceBuffer = ByteBuffer.allocateDirect(triangleVetriceData.length * Float.SIZE ).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVetriceBuffer.put(triangleVetriceData).position(0);
    }

    public void drawTriangle()
    {

    }
}
