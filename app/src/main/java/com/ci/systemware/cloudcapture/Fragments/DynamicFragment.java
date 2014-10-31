package com.ci.systemware.cloudcapture.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ci.systemware.cloudcapture.R;

import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/30/2014.
 * Fragment has a dynamical layout that depends upon the uiElements that were extratced from the xml
 * of the templates of the CI CAM
 */
public class DynamicFragment extends Fragment {

    static View rootView;
    Context context;
    RelativeLayout rLayout;
    ArrayList<View>  viewsList = new ArrayList<View>();//holds all the views to be seen in the fragment's layout

    static int id = 0;//unique ID for each view

    public static DynamicFragment newInstance(String templateName, ArrayList<String> camUIElementsList){
        DynamicFragment dFragment = new DynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putString("templateName",templateName);
        bundle.putStringArrayList("camUIElementsList", camUIElementsList);
        dFragment.setArguments(bundle);
        return dFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_dynamic, container, false);
        context = getActivity();
        instantiateViews();
        addViewsToList();
        addViewsToLayout();
        return rootView;
    }

    private void instantiateViews(){
        rLayout = (RelativeLayout)rootView.findViewById(R.id.relLayout);
    }

    private void setLabel(String text){//sets the label (TextView) that will accompany a view
        TextView textView1 = new TextView(context);
        textView1.setId(id);
        textView1.setText(text);
        viewsList.add(textView1);//add TextView to views list
        id++;
    }

    private void addViewsToList(){//takes all the CAM template UI elements and transform them into appropriate Android views
        ArrayList<String> arrList = getArguments().getStringArrayList("camUIElementsList");
        //adding the template name for debugging purposes
        TextView textView0 = new TextView(context);
        textView0.setId(id);
        textView0.setText(getArguments().getString("templateName"));//set the textView to the template name
        viewsList.add(textView0);//add TextView to views list
        id++;
        //end of debugging block
        for(String uiElement: arrList){
            String []uiElementPieces = uiElement.split(",");//[0] is the label, [1] is uiElementType
            if(uiElementPieces[1].equals("spinner")){//if uiElement is a spinner
                setLabel(uiElementPieces[0]);
                Spinner spinner1 = new Spinner(context);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)spinner1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, id-1);//set label to right of textView label
                spinner1.setId(id);
                viewsList.add(spinner1);//add Spinner to views list
                id++;
            }
            else if(uiElementPieces[1].equals("date")){//if uiElement is a date
                setLabel(uiElementPieces[0]);
                DatePicker datePicker1 = new DatePicker(context);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)datePicker1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, id-1);//set label to right of textView label
                datePicker1.setId(id);
                viewsList.add(datePicker1);//add EditText to views list
                id++;
            }
            else if(uiElementPieces[1].equals("editText")){//if uiElement is a textView
                setLabel(uiElementPieces[0]);
                EditText editText1 = new EditText(context);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, id-1);//set label to right of textView label
                editText1.setId(id);
                viewsList.add(editText1);//add EditText to views list
                id++;
            }
            else if(uiElementPieces[1].equals("fileUri")){//if uiElement is a file input field
                setLabel(uiElementPieces[0]);
                EditText editText1 = new EditText(context);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)editText1.getLayoutParams();
                params.addRule(RelativeLayout.RIGHT_OF, id-1);//set label to right of textView label
                editText1.setId(id);
                viewsList.add(editText1);//add EditText to views list
                id++;
            }
            else if(uiElementPieces[1].equals("noViewDisplay")){//if uiElement is a display type field - gets no view
                Log.d("addViewsToList()","UI element " + uiElementPieces[1] + " is a display type. Not shown.");
            }
            else if(uiElementPieces[1].equals("noView")){//if uiElement is a textView - a non-visible field
                Log.d("addViewsToList()","UI element " + uiElementPieces[1] + " is a non-visible type. Not shown.");
            }
            else{//invalid uiElement
                Log.e("addViews", "Invalid view name. Value of uiElement " + uiElement);
            }
        }
    }

    private void addViewsToLayout(){//add all the views to the relativeLayout
        for(View view : viewsList){
            rLayout.addView(view);
        }
    }

}

