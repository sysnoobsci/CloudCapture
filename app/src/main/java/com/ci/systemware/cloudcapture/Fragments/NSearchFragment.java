package com.ci.systemware.cloudcapture.fragments;


import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ci.systemware.cloudcapture.R;

import java.util.Calendar;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class NSearchFragment extends Fragment{
    static View rootView;
    private Spinner bUnit;
    private Spinner group;
    private Spinner docType;
    private Spinner createdBy;
    private TextView calendarFrom;
    private TextView calendarTo;
    private Calendar c;

    public NSearchFragment(){


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_nsearch, container, false);
        intializeVariables();
        setFromOnClickListener();
        setToOnClickListener();
        return rootView;
    }



    public void setFromOnClickListener(){
        calendarFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), fromDateListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }

    public void setToOnClickListener(){
        calendarFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), toDateListener,
                        c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }


    private DatePickerDialog.OnDateSetListener fromDateListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            if(selectedYear > c.get(Calendar.YEAR) && selectedMonth > c.get(Calendar.MONTH) && selectedDay > c.get(Calendar.DAY_OF_MONTH)){
                Toast.makeText(getActivity(), "No versions have been created in the future...", Toast.LENGTH_SHORT).show();
            }else {
                calendarFrom.setText(Integer.toString(selectedMonth + 1) + '-' + Integer.toString(selectedDay) + '-' + Integer.toString(selectedYear));
            }
        }
    };
    private DatePickerDialog.OnDateSetListener toDateListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            if(selectedYear > c.get(Calendar.YEAR) && selectedMonth > c.get(Calendar.MONTH) && selectedDay > c.get(Calendar.DAY_OF_MONTH)){
                Toast.makeText(getActivity(), "No versions have been created in the future...", Toast.LENGTH_SHORT).show();
            }else {
                calendarTo.setText(Integer.toString(selectedMonth + 1) + '-' + Integer.toString(selectedDay) + '-' + Integer.toString(selectedYear));
            }
        }
    };


    private void intializeVariables() {
        bUnit = (Spinner)rootView.findViewById(R.id.bu_spinner);
        group = (Spinner)rootView.findViewById(R.id.g_spinner);
        docType = (Spinner)rootView.findViewById(R.id.dt_spinner);
        createdBy = (Spinner)rootView.findViewById(R.id.cb_spinner);
        calendarFrom = (TextView)rootView.findViewById(R.id.cd_from);
        calendarTo = (TextView)rootView.findViewById(R.id.cd_to);
        c = Calendar.getInstance();
    }
}
