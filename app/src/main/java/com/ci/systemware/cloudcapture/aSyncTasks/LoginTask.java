package com.ci.systemware.cloudcapture.aSyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.R;
import com.ci.systemware.cloudcapture.fragments.HomeFragment;
import com.ci.systemware.cloudcapture.interfaces.LoginTaskInterface;
import com.ci.systemware.cloudcapture.supportingClasses.MultiPartEntityBuilder;
import com.ci.systemware.cloudcapture.supportingClasses.ParseSessionInfo;
import com.ci.systemware.cloudcapture.supportingClasses.XMLParser;

import org.apache.http.HttpEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by The Bat Cave on 10/14/2014.
 */
public class LoginTask extends AsyncTask<String, String, String>{
    public LoginTaskInterface listener;
    Context context;
    Activity activity;
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;

    public LoginTask(Context context,LoginTaskInterface listener){
        this.context = context;
        this.listener = listener;
        activity = (Activity) context;
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
        Log.d("targetCIQuery()","value of targetCIQuery: " + targetCIQuery);
        return targetCIQuery;
    }

    //action return code check
    Boolean isLoginSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser();
        int rc = Integer.parseInt(xobj.getElementText("rc", xmlResponse));//get the return codes
        int xrc = Integer.parseInt(xobj.getElementText("xrc", xmlResponse));
        int xsrc = Integer.parseInt(xobj.getElementText("xsrc", xmlResponse));
        Log.d("isLoginSuccessful()","value of rc, xrc, xsrc: " + rc + "," + xrc + "," + xsrc);
        return (rc==0&&xrc==0&&xsrc==0);//if return codes are 0 return true, else false
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
        argList.add("password," + preferences.getString("password", null));
        Boolean isSuccess = false;
        HttpEntity entity;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
            new ApiCallTask(entity, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetCIQuery())
                    .get(preferences.getInt("lilotimeout_preference", 5000), TimeUnit.MILLISECONDS);
            Log.d("LoginTask.doInBackground()", "ApiCallTask.getResponse() value: " + ApiCallTask.getResponse());
            isSuccess = isLoginSuccessful(ApiCallTask.getResponse());
            Log.d("LoginTask.doInBackground()","value of isSuccess: " + isSuccess);
        }catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
        }
        if (isSuccess) {//if the ping is successful(i.e. user logged in), set SID
            String SID = ParseSessionInfo.parseSID(ApiCallTask.getResponse());
            String permissions = ParseSessionInfo.parsePermission(ApiCallTask.getResponse());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("SID", SID);
            editor.putString("permission", permissions);
            editor.apply();//commit the SID change and store them in a background thread
        }
        return String.valueOf(isSuccess);
    }

    protected void onPostExecute(String result) {
        Log.d("LoginTask.onPostExecute()","value of isSuccess: " + result);
        ToastMsgTask.isLogonSuccessMessage(context,Boolean.valueOf(result));
        if(Boolean.valueOf(result)){
            callHomeFragment();//if login is successful, call the home fragment
        }
        listener.loginTaskProcessFinish(result);
        ringProgressDialog.dismiss();
    }

    private void callHomeFragment() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
