package arturpopov.basicprojectopengles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by arturpopov on 01/02/2017.
 */

public class Triangle implements IPrimitive
{

    private final FloatBuffer mVerticeBuffer;
    private final FloatBuffer mColourBuffer;

    private final int mPositionOffset = 0;
    private final int mColourOffset = 0;

    Integer mPositionHandle;
    Integer mColourHandle;


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
        mVerticeBuffer = ByteBuffer.allocateDirect(triangleVerticeData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticeBuffer.put(triangleVerticeData).position(0);

        mColourBuffer = ByteBuffer.allocateDirect(triangleColourData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColourBuffer.put(triangleColourData).position(0);
    }

    @Override
    public void draw(int pProgramHandle)
    {
        if(mPositionHandle == null || mColourHandle == null)
        {
            mPositionHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Position");
            mColourHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Colour");
        }
        mVerticeBuffer.position(mPositionOffset);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, POSITION_STRIDE_BYTES, mVerticeBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColourBuffer.position(mColourOffset);
        GLES20.glVertexAttribPointer(mColourHandle, COLOUR_SIZE, GLES20.GL_FLOAT, false, COLOUR_STRIDE_BYTES, mColourBuffer);
        GLES20.glEnableVertexAttribArray(mColourHandle);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }
}
