package arturpopov.basicprojectopengles;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLES20;
import android.util.Log;
import org.apache.commons.io.IOUtils;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by arturpopov on 01/02/2017.
 */

public class ShaderBuilder
{
    /**
     * Loads, Links Vertex & Fragment Shader.
     * RuntimeException on Failure.
     * @return program
     */
    public int LoadProgram(String shaderName)
    {
        int programHandle = GLES20.glCreateProgram();
        if(programHandle != 0)
        {
            GLES20.glAttachShader(programHandle, LoadVertexShader(shaderName));
            GLES20.glAttachShader(programHandle, LoadFragmentShader(shaderName));

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


    private String readShaderFile(String fileName)
    {
        Context context = MainActivity.getContext();
        if(context == null)
        {
            Log.d(LogTag.CONTEXT, "Context null");
            throw new RuntimeException("Context Null");
        }

        AssetManager assetManager = context.getAssets();
        String everything;
        try
        {
            InputStream inputStream = assetManager.open("shaders/"+fileName);
            everything = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        }
        catch (java.io.IOException e)
        {
            Log.d(LogTag.SHADERS, "Failed to open Shader File");
            throw new RuntimeException("Failed to open Shader File");
        }

        return everything;
    }

    public int LoadVertexShader(String shaderName)
    {
        int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        String vertexShader = readShaderFile(shaderName + ".vert");
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
    public int LoadFragmentShader(String shaderName)
    {
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        String fragmentShader = readShaderFile(shaderName + ".frag");
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
