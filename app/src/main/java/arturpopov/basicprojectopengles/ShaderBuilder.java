package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

/**
 * Created by arturpopov on 01/02/2017.
 */

class ShaderBuilder
{
    public static final String POSITION_HANDLE = "a_Position";
    public static final String COLOUR_HANDLE = "a_Colour";
    public static final String NORMAL_HANDLE = "a_Normal";
    public static final String TANGENT_HANDLE = "a_Tangent";
    public static final String BITANGENT_HANDLE = "a_BiTangent";
    public static final String TEXTURE_COORDINATE_HANDLE = "a_UV";
    /**
     * Loads, Links Vertex & Fragment Shader.
     * RuntimeException on Failure.
     * @return program
     */
    public int LoadProgram(String shaderName, Context context)
    {
        int programHandle = GLES20.glCreateProgram();
        if(programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, LoadVertexShader(shaderName, context));
            GLES20.glAttachShader(programHandle, LoadFragmentShader(shaderName, context));
            GLES20.glBindAttribLocation(programHandle, 0, POSITION_HANDLE);
            if (shaderName.contains("default"))
            {
                GLES20.glBindAttribLocation(programHandle, 1, COLOUR_HANDLE);
            } else if (shaderName.contains("normalMapped"))
            {
                GLES20.glBindAttribLocation(programHandle, 1, TEXTURE_COORDINATE_HANDLE);
                GLES20.glBindAttribLocation(programHandle, 2, NORMAL_HANDLE);
                GLES20.glBindAttribLocation(programHandle, 3, TANGENT_HANDLE);
                GLES20.glBindAttribLocation(programHandle, 4, BITANGENT_HANDLE);
            }
            else
            {
                Log.d(LogTag.SHADERS, "Log: Unrecognized shader filename");
            }
            GLES20.glLinkProgram(programHandle);
        }

        final int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if(linkStatus[0] == 0)
        {
            String infoLog = GLES20.glGetShaderInfoLog (programHandle);
            Log.d(LogTag.SHADERS, "Error Creating Program." + infoLog);
            GLES20.glDeleteProgram(programHandle);
            throw new RuntimeException("Error Creating Program");
        }

        return programHandle;
    }




    public int LoadVertexShader(String shaderName, Context context)
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
    public int LoadFragmentShader(String shaderName, Context context)
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
