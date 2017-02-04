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
    private final int mBytesPerFloat = 4;

    private final FloatBuffer mVerticeBuffer;
    private final FloatBuffer mColourBuffer;

    private final int mStridePositionBytes = 3 * mBytesPerFloat; //4 is bytes per float
    private final int mStrideColourBytes = 4 * mBytesPerFloat;

    private final int mPositionOffset = 0;
    private final int mPositionDataSize = 3;

    private final int mColourOffset = 0;
    private final int mColourDataSize = 4;

    int mPositionHandle;
    int mColourHandle;


    public Triangle()
    {
        final float[] triangleVerticeData =
                {
                        -0.5f, -0.25f, 0.5f, //X

                        0.5f, -0.25f, 0.0f, //Y

                        0.0f, 0.56f, 0.0f, //Z

                };
        final float[] triangleColourData =
                {
                        1.0f, 1.0f, 1.0f, 1.0f,
                        0.5f, 0.5f, 0.5f, 1.0f,
                        0.0f, 0.0f, 0.0f, 1.0f,
                };
        mVerticeBuffer = ByteBuffer.allocateDirect(triangleVerticeData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticeBuffer.put(triangleVerticeData).position(0);

        mColourBuffer = ByteBuffer.allocateDirect(triangleColourData.length * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColourBuffer.put(triangleColourData).position(0);
    }

    public void drawTriangle(int pProgramHandle)
    {


        if(mPositionHandle == mColourHandle)
        {
            mPositionHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Position");
            mColourHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Colour");
        }
        mVerticeBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStridePositionBytes, mVerticeBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColourBuffer.position(mColourOffset);
        GLES20.glVertexAttribPointer(mColourHandle, mColourDataSize, GLES20.GL_FLOAT, false, mStrideColourBytes, mColourBuffer);
        GLES20.glEnableVertexAttribArray(mColourHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
