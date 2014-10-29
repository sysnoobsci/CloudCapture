package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ci.systemware.cloudcapture.R;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class SeeBUFragment extends Fragment {
    static View rootView;
    Button createdDateButton;
    Button modifiedDateButton;
    Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_seebu, container, false);
        instantiateViews();
        createdButtonListener();
        modifiedButtonListener();
        searchButtonListener();
        return rootView;
    }

    private void instantiateViews(){
        createdDateButton = (Button) rootView.findViewById(R.id.createdButton);
        modifiedDateButton = (Button) rootView.findViewById(R.id.modifiedButton);
        searchButton = (Button) rootView.findViewById(R.id.searchButton);
    }

    private void createdButtonListener() {
        createdDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SeeBUFragment", "createdDateButton clicked");
            }
        });
    }

    private void modifiedButtonListener() {
        modifiedDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SeeBUFragment", "modifiedDateButton clicked");
            }
        });
    }

    private void searchButtonListener() {
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SeeBUFragment", "searchButton clicked");
            }
        });
    }

}
