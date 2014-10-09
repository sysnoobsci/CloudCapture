package com.ci.systemware.cloudcapture.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * Created by john.williams on 10/9/2014.
 */
public class CCDatePickerDialog extends DatePickerDialog {
    public CCDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
    }
}
