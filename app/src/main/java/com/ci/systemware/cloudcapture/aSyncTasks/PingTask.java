package com.ci.systemware.cloudcapture.aSyncTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.interfaces.PingTaskInterface;
import com.ci.systemware.cloudcapture.supportingClasses.MultiPartEntityBuilder;
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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 10/15/2014.
 */
public class PingTask extends AsyncTask<String, String, String> {
    public PingTaskInterface delegate = null;
    Context context;
    SharedPreferences preferences;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

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
    Boolean isPingSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser();
        int rc = Integer.parseInt(xobj.getElementText("rc", xmlResponse));//get the return codes
        int xrc = Integer.parseInt(xobj.getElementText("xrc", xmlResponse));
        int xsrc = Integer.parseInt(xobj.getElementText("xsrc", xmlResponse));
        Log.d("isPingSuccessful()","value of rc, xrc, xsrc: " + rc + "," + xrc + "," + xsrc);
        return (rc==0&&xrc==0&&xsrc==0);//if return codes are 0 return true, else false
    }
    @Override
    protected void onPreExecute() {

    }

    protected String doInBackground(String... params) {
        ArrayList<Object> argList = new ArrayList<Object>();
        argList.add("act,ping");
        argList.add("sid," + preferences.getString("SID", null));
        Boolean isSuccess = false;
        HttpEntity entity;
        String response = null;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
            response = buildMPEAndExecute(entity);
            isSuccess = isPingSuccessful(String.valueOf(response));
            Log.d("ListAppConfigTask.doInBackground()", "value of isSuccess: " + isSuccess);
            isSuccess = isPingSuccessful(response);
            Log.d("PingTask.doInBackground()","value of isSuccess: " + isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
        }
        return String.valueOf(isSuccess);
    }

    protected void onPostExecute(String result) {
        Log.d("PingTask.onPostExecute()","value of isSuccess: " + result);
        delegate.pingTaskProcessFinish(result);
    }
}
