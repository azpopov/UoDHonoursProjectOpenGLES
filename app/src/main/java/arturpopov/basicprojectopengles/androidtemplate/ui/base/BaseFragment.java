package arturpopov.basicprojectopengles.androidtemplate.ui.base;

import android.app.Fragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import arturpopov.basicprojectopengles.MainActivity;
import arturpopov.basicprojectopengles.R;
import arturpopov.basicprojectopengles.androidtemplate.dummy.DummyContent;
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
     * @param dummyItem
     * @return the inflated view
     */
    protected View inflateAndBind(LayoutInflater inflater, ViewGroup container, int layout, final DummyContent.DummyItem dummyItem) {
        View view = inflater.inflate(layout, container, false);
        ButterKnife.bind(this, view);



        FloatingActionButton myFab = (FloatingActionButton) view.findViewById(R.id.fab);

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMessage(v, dummyItem);
            }
        });


        LogUtil.logD(TAG, ">>> view inflated");
        return view;
    }
    /** Called when the user taps the Send button */
    private void sendMessage(View view, DummyContent.DummyItem dummyItem) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("LEVEL", dummyItem.id);
        startActivity(intent);
    }
}
