package arturpopov.basicprojectopengles;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by arturpopov on 01/02/2017.
 */

public class ShaderBuilder
{
    final String vertexShader =
            "uniform mat4 u_MVPMatrix;      \n"		// A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"		// Per-vertex position information we will pass in.
                    + "attribute vec4 a_Colour;        \n"		// Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"		// This will be passed into the fragment shader.

                    + "void main()                    \n"		// The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Colour;          \n"		// Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n" 	// gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    final String fragmentShader =
            "precision mediump float;       \n"		// Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Colour;          \n"		// This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"		// The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Colour;     \n"		// Pass the color directly through the pipeline.
                    + "}                              \n";


    public int LoadVertexShader()
    {
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

        if(vertexShaderHandle != 0)
            {
                GLES20.glShaderSource(vertexShaderHandle, vertexShader);
                GLES20.glCompileShader(vertexShaderHandle);
                final int [] compileStatus = new int[1];
                GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

                //If the compilation failed, delete the shader
                if(compileStatus[0] == 0)
                {
                    GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }

        }
        else
        {
            Log.d(LogTag.SHADERS, "Error Building Vertex Shader.");
            throw new RuntimeException("Error Building Vertex Shader.");
        }
        return vertexShaderHandle;
    }
    public int LoadFragmentShader()
    {
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        if(fragmentShaderHandle != 0)
        {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
            GLES20.glCompileShader(fragmentShaderHandle);
            final int [] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            //If the compilation failed, delete the shader
            if(compileStatus[0] == 0)
            {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }

        }
        else
        {
            Log.d(LogTag.SHADERS, "Error Building Fragment Shader.");
            throw new RuntimeException("Error Building Fragment Shader.");
        }
        return fragmentShaderHandle;
    }

    /**
     * Loads, Links Vertex & Fragment Shader.
     * RuntimeException on Failure.
     * @return program
     */
    public int LoadProgram()
    {
        int programHandle = GLES20.glCreateProgram();
        if(programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, LoadVertexShader());
            GLES20.glAttachShader(programHandle, LoadFragmentShader());

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glBindAttribLocation(programHandle, 1, "a_Colour");

            GLES20.glLinkProgram(programHandle);
        }

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if(linkStatus[0] == 0)
        {
            GLES20.glDeleteProgram(programHandle);
            Log.d(LogTag.SHADERS, "Error Creating Program.");
            throw new RuntimeException("Error Creating Program");
        }

        return programHandle;
    }

}
