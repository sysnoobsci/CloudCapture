package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ci.systemware.cloudcapture.R;
import com.squareup.picasso.Picasso;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class LoginFragment extends Fragment {
    static View rootView;
    ImageView cloudBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        setFonts();
        return rootView;
    }

    private void setCloudBackground() {
        Picasso.with(getActivity())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(cloudBackground);
    }

    public void setFonts() {
        TextView txt = (TextView) rootView.findViewById(R.id.textView);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "GOTHIC.TTF");
        txt.setTypeface(font);
    }
}
}
