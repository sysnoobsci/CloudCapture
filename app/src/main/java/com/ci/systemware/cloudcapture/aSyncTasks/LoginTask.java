package com.ci.systemware.cloudcapture.aSyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.supportingClasses.APIQueries;
import com.ci.systemware.cloudcapture.supportingClasses.MultiPartEntityBuilder;
import com.ci.systemware.cloudcapture.supportingClasses.ParseSessionInfo;
import com.ci.systemware.cloudcapture.supportingClasses.QueryArguments;
import com.ci.systemware.cloudcapture.supportingClasses.XMLParser;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by The Bat Cave on 10/14/2014.
 */
public class LoginTask extends AsyncTask<String, String, String> {

    Context context;
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;
    Boolean logonStatus = false;

    public LoginTask(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void setUploadProgressDialog() {
        ringProgressDialog = new ProgressDialog(context);
        ringProgressDialog.setTitle("Performing Login");
        ringProgressDialog.setMessage("Logging in ...");
    }

    String targetCIQuery() {
        String targetCIQuery = "http://" + preferences.getString("hostname",null) + "." +
                preferences.getString("domain", null) + ":" + preferences.getString("portnumber", null) + "/ci";
        return targetCIQuery;
    }

    //action return code check
    void isLoginSuccessful(ArrayList<String> larray) {
        XMLParser xobj = new XMLParser(xmlResponse);
        String rc = xobj.findTagText("path");//get the path name
        String rc = xobj.findTagText("path");//get the path name
        String rc = xobj.findTagText("path");//get the path name
    }

    @Override
    protected void onPreExecute() {
        setUploadProgressDialog();
        ringProgressDialog.show();
    }

    protected String doInBackground(String... params) {//need to figure out how to put login query in this task
        ArrayList<Object> argList = new ArrayList<Object>();
        argList.add("act,logon");
        argList.add("user," + preferences.getString("username", null));
        argList.add("password," + preferences.getString("password",null));

        HttpEntity entity = null;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        APITask apitaskobj = new APITask(entity,context);
        try {
            //apitaskobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetCIQuery())
            //          .get(lilo_timeout, TimeUnit.MILLISECONDS);
            apitaskobj.execute(targetCIQuery())
                    .get(lilo_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMsgTask.noConnectionMessage(context);
        }
        Log.d("logonQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XMLParser xobj = new XMLParser(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        Boolean logonStatus = getActionresult();
        if (logonStatus) {//if the ping is successful(i.e. user logged in)
            String SID = ParseSessionInfo.parseSID(apitaskobj.getResponse());
            Log.d("logonQuery()", "CI Server logon successful.");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("SID", String.valueOf(SID));
            editor.apply();//commit the SID change and store them in a background thread
        } else {
            Log.d("logonQuery()", "CI Server logon failed.");
        }
        resetResult();//reset action result after checking it
        return "done";
    }

    protected void onPostExecute(String result) {
        ToastMsgTask.isLogonSuccessMessage(context,logonStatus);
        ringProgressDialog.dismiss();
    }

}
