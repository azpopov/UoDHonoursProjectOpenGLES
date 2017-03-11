package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Created by arturpopov on 10/02/2017.
 */

public class ObjectContainer implements IPrimitive
{
    private FloatBuffer mVerticeBuffer, mTexCoordBuffer, mNormalBuffer, mTangentBuffer, mBiTangentBuffer;
    private Integer verticeHandle, texCoordHandle, normalHandle, tangentHandle, biTangentHandle;
    public Integer mNormalMapTextureDataHandle, mDiffuseTextureDataHandle, mNormalMapTextureUniform, mDiffuseTextureUniform;

    private int[] buffers = new int[6];

    public void initialize(String fileName, Context context, int textureDefault, int textureNormalMap)
    {
        ArrayList<ArrayList<Float>> objData = ObjectLoader.loadObjFile(fileName, context);

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



        mTexCoordBuffer = ByteBuffer.allocateDirect(objData.get(ObjectLoader.TEXTURE_COORDINATE_ARRAY_INDEX).size() * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        i = 0;
        floatValues = new float[objData.get(ObjectLoader.TEXTURE_COORDINATE_ARRAY_INDEX).size()];
        for (Float f : objData.get(ObjectLoader.TEXTURE_COORDINATE_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        mTexCoordBuffer.put(floatValues).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mTexCoordBuffer.capacity() * BYTES_PER_FLOAT,
                mTexCoordBuffer, GLES20.GL_STATIC_DRAW);


        mNormalBuffer = ByteBuffer.allocateDirect(objData.get(ObjectLoader.NORMAL_ARRAY_INDEX).size() * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        i = 0;
        floatValues = new float[objData.get(ObjectLoader.NORMAL_ARRAY_INDEX).size()];
        for (Float f : objData.get(ObjectLoader.NORMAL_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        mNormalBuffer.put(floatValues).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mNormalBuffer.capacity() * BYTES_PER_FLOAT,
                mNormalBuffer, GLES20.GL_STATIC_DRAW);


        mTangentBuffer = ByteBuffer.allocateDirect(objData.get(ObjectLoader.TANGENT_ARRAY_INDEX).size() * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        i = 0;
        floatValues = new float[objData.get(ObjectLoader.TANGENT_ARRAY_INDEX).size()];
        for (Float f : objData.get(ObjectLoader.TANGENT_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        mTangentBuffer.put(floatValues).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mTangentBuffer.capacity() * BYTES_PER_FLOAT,
                mTangentBuffer, GLES20.GL_STATIC_DRAW);


        mBiTangentBuffer = ByteBuffer.allocateDirect(objData.get(ObjectLoader.BITANGENT_ARRAY_INDEX).size() * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        i = 0;
        floatValues = new float[objData.get(ObjectLoader.BITANGENT_ARRAY_INDEX).size()];
        for (Float f : objData.get(ObjectLoader.BITANGENT_ARRAY_INDEX))
        {
            floatValues[i++] = (f != null ? f : Float.NaN);
        }
        mBiTangentBuffer.put(floatValues).position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[4]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mBiTangentBuffer.capacity() * BYTES_PER_FLOAT,
                mBiTangentBuffer, GLES20.GL_STATIC_DRAW);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        mDiffuseTextureDataHandle = TextureLoader.loadTexture(context, textureDefault);
        mNormalMapTextureDataHandle = TextureLoader.loadTexture(context, textureNormalMap);
    }

    @Override
    public void draw(int pProgramHandle)
    {

        if (mVerticeBuffer == null)
        {
            Log.d(LogTag.PRIMITIVE, "Attempted to draw Uninitialized Cylinder");
        }

        GLES20.glUseProgram(pProgramHandle);

        if (verticeHandle == null || texCoordHandle == null || normalHandle == null || tangentHandle == null || biTangentHandle == null || mDiffuseTextureUniform == null || mNormalMapTextureUniform == null)
        {
            verticeHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Position");
            texCoordHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_UV");
            normalHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Normal");
            tangentHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_Tangent");
            biTangentHandle = GLES20.glGetAttribLocation(pProgramHandle, "a_BiTangent");
            mDiffuseTextureUniform = GLES20.glGetUniformLocation(pProgramHandle, "u_DiffuseTextureSampler");
            mNormalMapTextureUniform = GLES20.glGetUniformLocation(pProgramHandle, "u_NormalTextureSampler");
        }

        if (mDiffuseTextureDataHandle != 0)
        {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mDiffuseTextureDataHandle);
            GLES20.glUniform1i(mDiffuseTextureUniform, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mNormalMapTextureDataHandle);
            GLES20.glUniform1i(mNormalMapTextureUniform, 1);
        }
        mVerticeBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0]);
        GLES20.glVertexAttribPointer(verticeHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(verticeHandle);

        mTexCoordBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[1]);
        GLES20.glVertexAttribPointer(texCoordHandle, UV_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        mNormalBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[2]);
        GLES20.glVertexAttribPointer(normalHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(normalHandle);

        mTangentBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[3]);
        GLES20.glVertexAttribPointer(tangentHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(tangentHandle);

        mBiTangentBuffer.position(0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[4]);
        GLES20.glVertexAttribPointer(biTangentHandle, POSITION_SIZE, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(biTangentHandle);


        GLES30.glEnable(GLES20.GL_DEPTH_TEST);
        GLES30.glDepthFunc(GLES20.GL_LESS);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVerticeBuffer.capacity() / 3);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES30.glDisable(GLES20.GL_DEPTH_TEST);
    }


}
