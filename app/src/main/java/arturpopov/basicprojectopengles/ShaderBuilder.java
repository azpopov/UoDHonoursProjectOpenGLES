package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.util.Log;

/**
 * Created by arturpopov on 01/02/2017.
 */

class ShaderBuilder
{
    /**
     * Loads, Links Vertex & Fragment Shader.
     * RuntimeException on Failure.
     * @return program
     */
    public static int LoadProgram(String shaderName, Context context, String[] attributes)
    {
        int programHandle = GLES30.glCreateProgram();
        if(programHandle != 0)
        {
            GLES30.glAttachShader(programHandle, LoadVertexShader(shaderName, context));
            GLES30.glAttachShader(programHandle, LoadFragmentShader(shaderName, context));
            for(int i = 0; i < attributes.length; i++)
            {
                GLES30.glBindAttribLocation(programHandle, i, attributes[i]);
            }
            GLES30.glLinkProgram(programHandle);
        }

        linkProgram(programHandle);

        return programHandle;
    }

    private static void linkProgram(int programHandle)
    {
        final int[] linkStatus = new int[1];
        GLES30.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        int[] length = new int[1];
        GLES30.glGetProgramiv(programHandle, GLES20.GL_INFO_LOG_LENGTH, length, 0);
        if(linkStatus[0] == 0)
        {
            int err;
            while((err = GLES30.glGetError()) != GLES30.GL_NO_ERROR)
            {
                int err2 = err;
            }
            String infoLog;
            infoLog = GLES30.glGetProgramInfoLog(programHandle);
            Log.d(LogTag.SHADERS, "Error Creating Program." + infoLog);
            GLES20.glDeleteProgram(programHandle);
            throw new RuntimeException("Error Creating Program");
        }
    }

    public static int LoadProgram(String shaderName, Context context)
    {
        int programHandle = GLES30.glCreateProgram();
        if(programHandle != 0)
        {
            GLES30.glAttachShader(programHandle, LoadVertexShader(shaderName, context));
            GLES30.glAttachShader(programHandle, LoadFragmentShader(shaderName, context));
            GLES30.glLinkProgram(programHandle);
        }

        linkProgram(programHandle);

        return programHandle;
    }



    public static int LoadVertexShader(String shaderName, Context context)
    {
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        String vertexShader = FileReader.readFile(context.getResources().getString(R.string.relativeShaderPath) + shaderName + ".vert", context);
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
    public static int LoadFragmentShader(String shaderName, Context context)
    {
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        String fragmentShader = FileReader.readFile(context.getResources().getString(R.string.relativeShaderPath) + shaderName + ".frag", context);
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

}
