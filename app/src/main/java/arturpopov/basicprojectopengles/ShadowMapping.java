package arturpopov.basicprojectopengles;

import android.graphics.Shader;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL;

import static arturpopov.basicprojectopengles.IPrimitive.BYTES_PER_FLOAT;

/**
 * Created by arturpopov on 23/03/2017.
 */

public class ShadowMapping
{
    private static final int NUMBER_OF_HANDLES = 4;
    private static final int FRAME_BUFFER_HANDLE = 0, RENDER_TARGET_TEXTURE = 1, DEPTH_BUFFER_HANDLE = 2, QUAD_VERTEX_BUFFER = 3;
    int[] handles = new int[NUMBER_OF_HANDLES];

    int width = 888, height = 540;

    static final float g_quad_vertex_buffer_data[] = {
        -1.0f, -1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
        -1.0f,  1.0f, 0.0f,
        1.0f, -1.0f, 0.0f,
        1.0f,  1.0f, 0.0f,
    };

    private int mProgram, mFullScreenQuadProgram;

    private int[] quadHandles = new int[2];
    void createRenderTarget(int mProgram)
    {
        this.mProgram = mProgram;
        GLES20.glGenFramebuffers(1, handles, FRAME_BUFFER_HANDLE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, handles[FRAME_BUFFER_HANDLE]);
        GLES20.glGenTextures(1, handles, RENDER_TARGET_TEXTURE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handles[RENDER_TARGET_TEXTURE]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, width, height, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glGenRenderbuffers(1, handles, DEPTH_BUFFER_HANDLE);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, handles[DEPTH_BUFFER_HANDLE]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_ATTACHMENT, width, height);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, handles[DEPTH_BUFFER_HANDLE]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, handles[RENDER_TARGET_TEXTURE], 0);
        int[] drawbuffers = {GLES20.GL_COLOR_ATTACHMENT0};
        GLES30.glDrawBuffers(drawbuffers.length, drawbuffers, 0);

        if(GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) != GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            Log.d(LogTag.FRAMEBUFFER, "Frame buffer cannot be created");
        }
    }

    void prepareFullScreenQuad(int mFullScreenQuadProgram)
    {
        GLES20.glGenBuffers(1, handles, QUAD_VERTEX_BUFFER);
        GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, handles[QUAD_VERTEX_BUFFER]);
        FloatBuffer quadVerticeBuffer = ByteBuffer.allocateDirect(g_quad_vertex_buffer_data.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        GLES20.glBufferData( GLES20.GL_ARRAY_BUFFER, g_quad_vertex_buffer_data.length, quadVerticeBuffer,  GLES20.GL_STATIC_DRAW);
        this.mFullScreenQuadProgram = mFullScreenQuadProgram;
        quadHandles[0] = GLES20.glGetUniformLocation(mFullScreenQuadProgram, "renderedTexture");
        quadHandles[1] = GLES20.glGetUniformLocation(mFullScreenQuadProgram, "time");
    }


    void bindFrameBuffer()
    {
        GLES20.glUseProgram(mProgram);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, handles[FRAME_BUFFER_HANDLE]);
        GLES20.glViewport(0,0,width, height);
    }


}
