package arturpopov.basicprojectopengles;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by arturpopov on 31/01/2017.
 */

public class MainSurfaceView extends GLSurfaceView {
    private MainRenderer mRenderer;


    private VelocityTracker mVelocityTracker = null;
    public MainSurfaceView(Context context) {
        super(context);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);


        if(event != null && mRenderer != null)
        {
            switch(action)
            {
                case MotionEvent.ACTION_DOWN:
                    if(mVelocityTracker == null)
                        mVelocityTracker = VelocityTracker.obtain();
                    else
                        mVelocityTracker.clear();
                    mVelocityTracker.addMovement(event);
                   break;
                case MotionEvent.ACTION_UP:
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
