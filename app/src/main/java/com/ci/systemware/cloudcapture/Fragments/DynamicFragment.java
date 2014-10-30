package com.ci.systemware.cloudcapture.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.ci.systemware.cloudcapture.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by adrian.meraz on 10/30/2014.
 */
public class DynamicFragment extends Fragment {

    static View rootView;
    Context context;
    RelativeLayout rLayout;
    ArrayList<View>  viewsList = new ArrayList<View>();//holds all the views to be seen in the fragment's layout
    static int viewId = 0;//unique ID for each view

    public static DynamicFragment newInstance(String templateName, ArrayList<String> arrList){
        DynamicFragment df = new DynamicFragment();

        Bundle bundle = new Bundle();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_dynamic, container, false);
        context = getActivity();
        instantiateViews();
        return rootView;
    }

    private void instantiateViews(){
        rLayout = (RelativeLayout)rootView.findViewById(R.id.relLayout);
    }

    private void addViews(ArrayList<String> arrList){
        for(String uiElement: arrList){
            String []uiElementPieces = uiElement.split(",");//[0] is the label, [1] is uiElementType
            if(uiElementPieces[1].equals("spinner")){//if uiElement is a spinner

                Spinner spinner1 = new Spinner(context);
                spinner1.setId(1);
            }
            else if(uiElementPieces[1].equals("date")){//if uiElement is a date

            }
            else if(uiElementPieces[1].equals("textView")){//if uiElement is a textView

            }
            else if(uiElementPieces[1].equals("fileUri")){//if uiElement is a file input field

            }
            else if(uiElementPieces[1].equals("noViewDisplay")){//if uiElement is a display type field - gets no view

            }
            else if(uiElementPieces[1].equals("noView")){//if uiElement is a textView - a non-visible field

            }
            else{//invalid uiElement
                Log.e("addViews", "Invalid view name. Value of uiElement " + uiElement);
            }
        }
    }


}

