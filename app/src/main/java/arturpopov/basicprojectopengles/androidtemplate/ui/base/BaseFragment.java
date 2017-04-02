package arturpopov.basicprojectopengles.androidtemplate.ui.base;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import arturpopov.basicprojectopengles.MainActivity;
import arturpopov.basicprojectopengles.R;
import butterknife.ButterKnife;
import arturpopov.basicprojectopengles.androidtemplate.util.LogUtil;

import static arturpopov.basicprojectopengles.androidtemplate.util.LogUtil.makeLogTag;

/**
 * The base class for all fragment classes.
 *
 * Created by Andreas Schrade on 14.12.2015.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = makeLogTag(BaseFragment.class);

    /**
     * Inflates the layout and binds the view via ButterKnife.
     * @param inflater the inflater
     * @param container the layout container
     * @param layout the layout resource
     * @return the inflated view
     */
    public View inflateAndBind(LayoutInflater inflater, ViewGroup container, int layout) {
        View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);



        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(v);
            }
        });

        LogUtil.logD(TAG, ">>> view inflated");
        return view;
    }
    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
    }


}
