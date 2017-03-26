package arturpopov.basicprojectopengles;


import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Collections;

import static arturpopov.basicprojectopengles.IPrimitive.BYTES_PER_FLOAT;

public class Skybox
{
    private int right,  left,  top,  bottom,  back,  front;
    private final static int CUBEMAP_HANDLE_INDEX = 0, PROJECTION_MATRIX_INDEX = 1, VIEW_MATRIX_INDEX = 2, PROGRAM_HANDLE_INDEX = 3, SAMPLER_UNIFORM_HANDLE_INDEX = 4, CUBE_VERTEX_ARRAY_ID_INDEX = 5, SKYBOX_BUFFER_INDEX = 6, CUBE_BUFFFER_INDEX = 7, SKYBOX_VERTEX_ARRAY_OBJECT_INDEX = 8;
    private final static int NUMBER_OF_HANDLES = 9;
    private int[] handles = new int[NUMBER_OF_HANDLES];

    float skyboxVertices[] = {
            // Positions
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f
    };

    float cubeVertices[] = {
            // Positions          // Normals
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f
    };
    public void setFaceTextures(int right, int left, int top, int bottom, int back, int front)
    {
        this.right = right;
        this.left = left;
        this.top = top;
        this.bottom = bottom;
        this.back = back;
        this.front = front;

    }

    public void initialize(Context context)
    {
        handles[CUBEMAP_HANDLE_INDEX] = TextureLoader.loadCubeMap(context, new int[]{  left,right,  top,  bottom,  back,  front});
        handles[PROGRAM_HANDLE_INDEX] = ShaderBuilder.LoadProgram("skybox", context);

        handles[PROJECTION_MATRIX_INDEX] = GLES20.glGetUniformLocation(handles[PROGRAM_HANDLE_INDEX], "u_Projection");
        handles[VIEW_MATRIX_INDEX] = GLES20.glGetUniformLocation(handles[PROGRAM_HANDLE_INDEX], "u_View");
        handles[SAMPLER_UNIFORM_HANDLE_INDEX] = GLES20.glGetUniformLocation(handles[PROGRAM_HANDLE_INDEX], "skyboxSampler");

        setupPositionBuffer();
    }

    private void setupPositionBuffer()
    {
//        GLES30.glGenVertexArrays(1, handles, CUBE_VERTEX_ARRAY_ID_INDEX);
//        GLES30.glBindVertexArray(handles[CUBE_VERTEX_ARRAY_ID_INDEX]);
//
//        GLES30.glGenBuffers(1, handles, CUBE_BUFFFER_INDEX);
//        GLES30.glBindBuffer(GLES20.GL_ARRAY_BUFFER, handles[CUBE_BUFFFER_INDEX]);
//
//        FloatBuffer cubePositionBuffer = ByteBuffer.allocateDirect(cubeVertices.length * BYTES_PER_FLOAT)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        cubePositionBuffer.put(cubeVertices).position(0);
//        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, cubePositionBuffer.capacity() * BYTES_PER_FLOAT, cubePositionBuffer, GLES30.GL_STATIC_DRAW);
//
//        GLES30.glEnableVertexAttribArray(0);
//        GLES30.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 6 * BYTES_PER_FLOAT, 0);
//        GLES30.glEnableVertexAttribArray(1);
//        GLES30.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, 6 * BYTES_PER_FLOAT, 3 * BYTES_PER_FLOAT);
//        GLES30.glBindVertexArray(0);

        GLES30.glGenVertexArrays(1, handles, SKYBOX_VERTEX_ARRAY_OBJECT_INDEX);
        GLES20.glGenBuffers(1, handles, SKYBOX_BUFFER_INDEX);
        GLES30.glBindVertexArray(handles[SKYBOX_VERTEX_ARRAY_OBJECT_INDEX]);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, handles[SKYBOX_BUFFER_INDEX]);

        FloatBuffer skyboxPositionBuffer = ByteBuffer.allocateDirect(skyboxVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        skyboxPositionBuffer.put(skyboxVertices).position(0);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, skyboxPositionBuffer.capacity() * BYTES_PER_FLOAT, skyboxPositionBuffer, GLES30.GL_STATIC_DRAW);
        GLES30.glEnableVertexAttribArray(0);
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * BYTES_PER_FLOAT, 0);
        GLES30.glBindVertexArray(0);

    }

    public void renderSkybox(float[] viewMatrix, float[] projectionMatrix)
    {
        if(left == 0 || right == 0 || bottom == 0)
        {
            Log.d(LogTag.SKYBOX, "skybox faces not set");
        }
        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
       // GLES20.glDisable(GLES20.GL_DEPTH_TEST);
       // GLES20.glDepthMask(false);
        GLES20.glUseProgram(handles[PROGRAM_HANDLE_INDEX]);
        GLES20.glUniformMatrix4fv(handles[VIEW_MATRIX_INDEX], 1, false, viewMatrix, 0);
        GLES20.glUniformMatrix4fv(handles[PROJECTION_MATRIX_INDEX], 1, false, projectionMatrix, 0);

        GLES30.glBindVertexArray(handles[SKYBOX_VERTEX_ARRAY_OBJECT_INDEX]);
        GLES30.glBindTexture( GLES30.GL_TEXTURE_CUBE_MAP, handles[CUBEMAP_HANDLE_INDEX]);
        GLES30.glUniform1i(handles[SAMPLER_UNIFORM_HANDLE_INDEX], 0);
//
//        GLES30.glEnableVertexAttribArray(0);
//        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 3 * 4, 0);

        //GLES20.glUniform1i(handles[SAMPLER_UNIFORM_HANDLE_INDEX], 0);
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 36);
        GLES30.glBindVertexArray(0);
       // GLES20.glDepthMask( true);
        GLES20.glDepthFunc(GLES20.GL_LESS);
      //  GLES20.glEnable(GLES20.GL_DEPTH_TEST);


    }
}
