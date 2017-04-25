package arturpopov.basicprojectopengles;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


class FileReader
{
    public static String readFile(String resourcePath, Context context)
    {
        if(context == null)
        {
            Log.d(LogTag.CONTEXT, "Context null");
            throw new RuntimeException("Context Null");
        }

        AssetManager assetManager = context.getAssets();
        String everything;
        try
        {
            InputStream inputStream = assetManager.open(resourcePath);
            everything = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        }
        catch (java.io.IOException e)
        {
            Log.d(LogTag.SHADERS, "Failed to open File\nParameter filename: "+ resourcePath);
            throw new RuntimeException("Failed to open File\nParameter filename: "+ resourcePath);
        }

        return everything;
    }
}
