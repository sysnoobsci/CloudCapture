package com.ci.systemware.cloudcapture.aSyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.supportingClasses.MultiPartEntityBuilder;
import com.ci.systemware.cloudcapture.supportingClasses.XMLParser;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 10/15/2014.
 */
public class PingTask extends AsyncTask<String, String, String> {
    Context context;
    SharedPreferences preferences;
    Boolean isLoggedIn = false;

    public PingTask(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    String targetCIQuery() {
        String targetCIQuery = "http://" + preferences.getString("hostname",null) + "." +
                preferences.getString("domain", null) + ":" + preferences.getString("portnumber", null) + "/ci";
        Log.d("targetCIQuery()", "value of targetCIQuery: " + targetCIQuery);
        return targetCIQuery;
    }

    //action return code check
    Boolean isPingSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser();
        int rc = Integer.parseInt(xobj.findTagText("rc",xmlResponse));//get the return codes
        int xrc = Integer.parseInt(xobj.findTagText("xrc",xmlResponse));
        int xsrc = Integer.parseInt(xobj.findTagText("xsrc",xmlResponse));
        Log.d("isPingSuccessful()","value of rc, xrc, xsrc: " + rc + "," + xrc + "," + xsrc);
        return (rc==0&&xrc==0&&xsrc==0);//if return codes are 0 return true, else false
    }
    @Override
    protected void onPreExecute() {

    }

    protected String doInBackground(String... params) {//need to figure out how to put login query in this task
        ArrayList<Object> argList = new ArrayList<Object>();
        argList.add("act,ping");
        argList.add("sid," + preferences.getString("SID", null));
        Boolean isSuccess = false;
        HttpEntity entity = null;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ApiCallTask apitaskobj = new ApiCallTask(entity,context);
        try {
            apitaskobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetCIQuery())
                    .get(preferences.getInt("lilotimeout_preference", 5000), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
        }
        Log.d("PingTask.doInBackground()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        try {
            isSuccess = isPingSuccessful(apitaskobj.getResponse());
            Log.d("PingTask.doInBackground()","value of isSuccess: " + isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(isSuccess);
    }

    protected void onPostExecute(String result) {
        Log.d("PingTask.onPostExecute()","value of isSuccess: " + result);
    }
}
