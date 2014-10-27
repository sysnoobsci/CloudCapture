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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


/**
 * Created by The Bat Cave on 10/14/2014.
 */
public class LoginTask extends AsyncTask<String, String, String>{
    public LoginTaskInterface listener;
    Context context;
    Activity activity;
    ProgressDialog ringProgressDialog;
    SharedPreferences preferences;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

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

    private String buildMPEAndExecute(HttpEntity entity){
        StringBuilder queryResponse;
        queryResponse = new StringBuilder();
        try{
            httppost = new HttpPost(targetCIQuery());
            httppost.setEntity(entity);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            InputStream is = buf.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                queryResponse.append(line);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return String.valueOf(queryResponse);
    }

    //action return code check
    Boolean isLoginSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser(context);
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
        String response = null;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
            response = buildMPEAndExecute(entity);
            isSuccess = isLoginSuccessful(response);
            Log.d("LoginTask.doInBackground()","value of isSuccess: " + isSuccess);
            if (isSuccess) {//if the ping is successful(i.e. user logged in), set SID
                String SID = ParseSessionInfo.parseSID(response);
                String permissions = ParseSessionInfo.parsePermission(response);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("SID", SID);
                editor.putString("permission", permissions);
                editor.apply();//commit the SID change and store them in a background thread
            }
        }catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
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
