package arturpopov.basicprojectopengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

/**
 * Created by arturpopov on 31/01/2017.
 */

public class MainSurfaceView extends GLSurfaceView {
    private MainRenderer mRenderer;


    private VelocityTracker mVelocityTracker = null;
    private long timePressed;

    public MainSurfaceView(Context context) {
        super(context);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);


        if(mRenderer != null)
        {
            switch(action)
            {
                case MotionEvent.ACTION_DOWN:
                    if(mVelocityTracker == null)
                        mVelocityTracker = VelocityTracker.obtain();
                    else
                        mVelocityTracker.clear();
                    timePressed = System.currentTimeMillis();
                    mVelocityTracker.addMovement(event);
                   break;
                case MotionEvent.ACTION_UP:
                    //mRenderer.celShadedParticleGenerator.queueSmokePuff(10);
                    mRenderer.clicks++;
                    if(System.currentTimeMillis() - timePressed > 5000)
                        MainActivity.doRestart(getContext());
                    Log.d(LogTag.TOUCH_EVENT, "CLICK");
                    break;
                case MotionEvent.ACTION_MOVE:
                    mVelocityTracker.addMovement(event);
                    mVelocityTracker.computeCurrentVelocity(1);
                    Log.d(LogTag.TOUCH_EVENT, "X velocity: " +
                            VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                    pointerId));
                    Log.d(LogTag.TOUCH_EVENT, "Y velocity: " +
                            VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                    pointerId));
                    mRenderer.eyeX += VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                            pointerId);
                    mRenderer.eyeY += VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                            pointerId);
                    this.invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mVelocityTracker.recycle();
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public void setRenderer(Renderer renderer)
    {
        mRenderer = (MainRenderer) renderer;
        super.setRenderer(renderer);
    }

}
