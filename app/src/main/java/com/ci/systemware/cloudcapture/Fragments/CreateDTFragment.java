package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ci.systemware.cloudcapture.R;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class CreateDTFragment extends Fragment {
    static View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_createdt, container, false);
        setFonts();
        return rootView;
    }

    public void setFonts() {
        TextView txt = (TextView) rootView.findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "GOTHIC.TTF");
        txt.setTypeface(font);
    }
}
