package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ci.systemware.cloudcapture.R;
import com.squareup.picasso.Picasso;

/**
 * Created by adrian.meraz on 10/16/2014.
 */
public class HomeFragment extends Fragment {
    static View rootView;
    ImageView cloudBackground;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cloudBackground = (ImageView) rootView.findViewById(R.id.imageView2);
        setCloudBackground();
        return rootView;
    }

    private void setCloudBackground() {
        Picasso.with(getActivity())
                .load(R.drawable.clouds_parlx_bg1)
                .fit()
                .centerInside()
                .into(cloudBackground);
    }
}
