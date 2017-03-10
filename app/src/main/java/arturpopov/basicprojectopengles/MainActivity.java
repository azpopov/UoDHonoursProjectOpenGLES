package arturpopov.basicprojectopengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by arturpopov on 31/01/2017.
 */

public class MainActivity extends Activity
{
    private GLSurfaceView mglSurfaceView;
    private Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mglSurfaceView = new MainSurfaceView(this);
        mContext = this;
        //Version Check
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsES2 = (configurationInfo.reqGlEsVersion >= 0x2000);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (!supportsES2)
        {
            Log.d(LogTag.UNSUPPORTEDES2, "ES2 is not Supported on this Device");
            throw new RuntimeException("ES2 is not Supported on this Device");
        }
        else
        {
            mglSurfaceView.setEGLContextClientVersion(2);
            mglSurfaceView.setRenderer(new MainRenderer(mContext));
            setContentView(mglSurfaceView);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mglSurfaceView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mglSurfaceView.onResume();
    }
}
