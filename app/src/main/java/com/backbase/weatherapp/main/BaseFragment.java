package com.backbase.weatherapp.main;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Souvik on 22/06/17.
 */

public class BaseFragment extends Fragment {

    private OnActivityAttachListener onActivityAttachListener;

    public void getAttachedActivity(OnActivityAttachListener listener) {
        if (getActivity() != null) {
            listener.onAvailable(getActivity());
        } else {
            onActivityAttachListener = listener;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (onActivityAttachListener != null)
            onActivityAttachListener.onAvailable((MainActivity)context);
    }

}
