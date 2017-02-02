package arturpopov.basicprojectopengles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by arturpopov on 01/02/2017.
 */

public class Triangle
{
    private final FloatBuffer mVetriceBuffer;
    private final FloatBuffer mColourBuffer;

    private final int mStridePositionBytes = 3 * Float.SIZE; //4 is bytes per float
    private final int mStrideColourBytes = 4 * Float.SIZE;

    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;

    private final int mColourOffset = 0;
    private final int mColourDataSize = 4;

    public Triangle()
    {
        final float[] triangleVetriceData =
                {
                        -0.5f, -0.25f, 0.5f, //X
                        0.5f, -0.25f, 0.0f, //Y
                        0.0f, 0.56f, 0.0f //Z
                };
        final float[] triangleColourData =
                {
                        1.0f, 0.0f, 0.0f, 1.0f,
                        0.0f, 1.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 1.0f
                };
        mVetriceBuffer = ByteBuffer.allocateDirect(triangleVetriceData.length * Float.SIZE ).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVetriceBuffer.put(triangleVetriceData).position(0);

        mColourBuffer = ByteBuffer.allocateDirect(triangleColourData.length * Float.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColourBuffer.put(triangleColourData).position(0);
    }

    public void drawTriangle(int pProgramHandle)
    {
        int mPositionHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Position");
        int mColourHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Colour");

        mVetriceBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStridePositionBytes, mVetriceBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mVetriceBuffer.position(mColourOffset);
        GLES20.glVertexAttribPointer(mColourHandle, mColourDataSize, GLES20.GL_FLOAT, false, mStrideColourBytes, mColourBuffer);
        GLES20.glEnableVertexAttribArray(mColourHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
