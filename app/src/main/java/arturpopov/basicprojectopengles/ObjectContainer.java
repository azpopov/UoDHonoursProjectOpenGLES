package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by arturpopov on 10/02/2017.
 */

public class ObjectContainer implements IPrimitive
{
    private FloatBuffer mVerticeBuffer, mTexCoordBuffer, mNormalBuffer, mTangentBuffer, mBiTangentBuffer, mIndexBuffer;
    private int[] buffers;
    public void initialize(String fileName, Context context)
    {
        ArrayList<ArrayList<Float>> objData = ObjectLoader.loadObjFile("testBamboo.obj", context);
        objData = ObjectLoader.IndexObject(objData);

        GLES20.glGenBuffers(6, buffers, 0);

        mVerticeBuffer = ByteBuffer.allocateDirect(objData.get(ObjectLoader.VERTEX_ARRAY_INDEX).size() * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        float[] floatValues = new float[objData.get(ObjectLoader.VERTEX_ARRAY_INDEX).size()];
        //BalusC - Java convert Arraylist<Float> to float[]
        int i = 0;
        for (Float f : objData.get(ObjectLoader.VERTEX_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        mVerticeBuffer.put(floatValues).position(0);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVerticeBuffer.capacity() * BYTES_PER_FLOAT,
                mVerticeBuffer, GLES20.GL_STATIC_DRAW);
        i = 0;
        floatValues = new float[objData.get(ObjectLoader.TEXTURE_COORDINATE_ARRAY_INDEX).size()];
        for (Float f : objData.get(ObjectLoader.TEXTURE_COORDINATE_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        //TODO FINISH
    }

    @Override
    public void draw(int pProgramHandle)
    {

    }
}
