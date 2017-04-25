package arturpopov.basicprojectopengles;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity
{
    private GLSurfaceView mglSurfaceView;
    private int options = 0;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Get the Intent that started this activity and extract the string
        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("LEVEL");

        getOptions(message);

        startGame();

    }

    private void getOptions(String message)
    {
        options = Integer.parseInt(message);
    }

    private void startGame()
    {

        mglSurfaceView = new MainSurfaceView(this);
        Context mContext = this;
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
            MainRenderer mainRenderer = new MainRenderer(mContext);
            mainRenderer.setOptions(options);
            mglSurfaceView.setRenderer(mainRenderer);

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
    //http://stackoverflow.com/questions/6609414/how-to-programmatically-restart-android-app
    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Log.e(LogTag.CONTEXT, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Log.e(LogTag.CONTEXT, "Was not able to restart application, PM null");
                }
            } else {
                Log.e(LogTag.CONTEXT, "Was not able to restart application, Context null");
            }
        } catch (Exception ex) {
            Log.e(LogTag.CONTEXT, "Was not able to restart application");
        }
    }
}
