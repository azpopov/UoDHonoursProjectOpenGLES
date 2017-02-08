package arturpopov.basicprojectopengles;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * Created by arturpopov on 06/02/2017.
 */

public class Cylinder implements IPrimitive
{
    private FloatBuffer mVerticeBuffer;
    private FloatBuffer mColourBuffer;

    private int length, radius, sidesOfCylinder;
    private float r, g, b, a;

    private float[] vetriceData;
    private float[] colourData;

    private Integer mPositionHandle;
    private Integer mColourHandle;

    private final int buffers[] = new int[3]; //buffer[0] is Vertex [1] is colour and [2] is normals (not implemented normals yet)
    private final int ibo[] = new int[1];
    Cylinder()
    {
        length = 10;
        radius = 2;
        sidesOfCylinder = 500;
        setColours(0.0f, 0.5f, 0.7f, 1.0f);

    }

    public void setSize(int length, int radius, int sidesOfCylinder)
    {
        if(sidesOfCylinder % 2 != 0)
        {
            Log.d(LogTag.PRIMITIVE, "Number of Sides not Divisible by 2 in Cylinder");
        }
        this.sidesOfCylinder = sidesOfCylinder;
        setSize(length, radius);
    }

    public void setSize(int length, int radius)
    {
        this.length = length;
        this.radius = radius;
    }

    public void initialize()
    {
        initializeVerticeData();
        GLES20.glGenBuffers(3, buffers, 0);

        mVerticeBuffer = ByteBuffer.allocateDirect(vetriceData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVerticeBuffer.put(vetriceData).position(0);
       // vetriceData = null;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVerticeBuffer.capacity() * BYTES_PER_FLOAT,
                mVerticeBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        initializeColourData();
        mColourBuffer = ByteBuffer.allocateDirect(colourData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mColourBuffer.put(colourData).position(0);
        colourData = null;

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mColourBuffer.capacity() * BYTES_PER_FLOAT,
                mColourBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        setIndices();


    }

    @Override
    public void draw(int pProgramHandle)
    {
        if (mVerticeBuffer == null || mColourBuffer == null)
        {
            Log.d(LogTag.PRIMITIVE, "Attempted to draw Uninitialized Cylinder");
        }
        if(mPositionHandle == null || mColourHandle == null)
        {
            mPositionHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Position");
            mColourHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Colour");
        }
        mVerticeBuffer.position(0);
        // Pass in the position information
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mColourBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glVertexAttribPointer(mColourHandle, COLOUR_SIZE, GLES20.GL_FLOAT, false, COLOUR_STRIDE_BYTES, 0);
        GLES20.glEnableVertexAttribArray(mColourHandle);

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, sidesOfCylinder + 2, GLES20.GL_UNSIGNED_SHORT, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, sidesOfCylinder + 2, GLES20.GL_UNSIGNED_SHORT, (sidesOfCylinder + 2)* BYTES_PER_SHORT);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, (sidesOfCylinder*2)+2, GLES20.GL_UNSIGNED_SHORT, ((sidesOfCylinder + 2) * 2) * BYTES_PER_SHORT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void initializeVerticeData()
    {

        vetriceData = new float[((sidesOfCylinder  * 4) + 2) * POSITION_SIZE];
        int firstCircleOffset = 0;
        vetriceData[(firstCircleOffset*3)] = 0.0f;
        vetriceData[(firstCircleOffset*3) + 1] = 0.0f;
        vetriceData[(firstCircleOffset*3) + 2] = 0.0f;
        addUnitCircleToVertexData(firstCircleOffset + 1);

        int secondCircleOffset = sidesOfCylinder + 1;
        vetriceData[(secondCircleOffset*3)] = 0.0f;
        vetriceData[(secondCircleOffset*3) + 1] = 0.0f;
        vetriceData[(secondCircleOffset*3) + 2] = -length;
        addUnitCircleToVertexData(secondCircleOffset + 1, length);

        int thirdCircleOffset = (sidesOfCylinder * 2) + 2;
        addUnitCircleToVertexData(thirdCircleOffset);

        int fourthCircleOffset = ((sidesOfCylinder * 2) + sidesOfCylinder) + 2;
        addUnitCircleToVertexData(fourthCircleOffset, length);

    }

    private void addUnitCircleToVertexData(int offset)
    {
        int verticesInCircle = sidesOfCylinder;

        float theta = (float)(2 * 3.1415926 / (verticesInCircle));
        float c = (float)Math.cos(theta);
        float s = (float)Math.sin(theta);
        float t;

        float x = radius;
        float y = 0;
        for (int i = offset; i < offset + verticesInCircle; i++)
        {
            vetriceData[i * 3] = x;
            vetriceData[i * 3 + 1] = y;
            vetriceData[i * 3 + 2] = 0;

            t = x;
            x = c * x - s * y;
            y = s * t + c * y;
        }
    }
    private void addUnitCircleToVertexData(int offset, int length)
    {
        addUnitCircleToVertexData(offset);
        int verticesInCircle = sidesOfCylinder;
        for (int i = offset; i < verticesInCircle + offset; i++)
        {
            vetriceData[i * 3 + 2] = -length;
        }
    }

    private void initializeColourData()
    {
        colourData = new float[((sidesOfCylinder * 4) + 2) * COLOUR_SIZE];
        for (int i = 0; i < (sidesOfCylinder * 4) + 2 ; i++)
        {
            colourData[i * 4] = r;
            colourData[i * 4 + 1] = g;
            colourData[i * 4 + 2] = b;
            colourData[i * 4 + 3] = a;
        }
    }

    public void setColours(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    private void setIndices()
    {
        short[] indiceData = new short[(sidesOfCylinder * 4) + 6];
        // fill "indices" to define triangle strips
        int index = 0;		// Current index
        final int firstCircleCondition = sidesOfCylinder + 1;
        // Define indices for the first triangle fan of front circle
        for (int i = index; i < firstCircleCondition; i++)
        {
            indiceData[index++] = (short)i;
        }
        indiceData[index++] = 1;	// Join last triangle in the triangle fan

        final int secondCircleIndex = sidesOfCylinder + 1;
        final int secondCircleCondition = index + sidesOfCylinder;
        // Define indices for the last triangle fan for the back circle
        for (int i = secondCircleIndex; i < secondCircleCondition; i++)
        {
            indiceData[index++] = (short)i;
        }
        // Join last triangle in the triangle fan
        indiceData[index++] = (short)(secondCircleIndex + 1);

        final int startCondition = (sidesOfCylinder * 2) + 2;
        final int stripCondition = startCondition + sidesOfCylinder;

        int j = stripCondition;
        for (int i = startCondition; i < stripCondition; i++)
        {
            indiceData[index++] = (short)i;
            indiceData[index++] = (short)j++;
        }
        //Define Last Indices of Strip
        indiceData[index++] = (short)startCondition;
        indiceData[index] = (short)stripCondition;


        ShortBuffer mIndiceBuffer = ByteBuffer
                .allocateDirect(indiceData.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
                .asShortBuffer();
        mIndiceBuffer.put(indiceData).position(0);
        // Generate a buffer for the indices
        GLES20.glGenBuffers(1, ibo, 0 );
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndiceBuffer.capacity() * BYTES_PER_SHORT, mIndiceBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

}
