package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ci.systemware.cloudcapture.R;

/**
 * Created by adrian.meraz on 10/16/2014.
 */
public class NoCamSettingsFragment extends Fragment {
    static View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_nocamsettings, container, false);
        return rootView;
    }
}
