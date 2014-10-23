package com.ci.systemware.cloudcapture.aSyncTasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.interfaces.ReadAppConfigTaskInterface;
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
import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/20/2014.
 */
public class ReadAppConfigTask extends AsyncTask<String, String, String>{
    public ReadAppConfigTaskInterface delegate = null;
    Context context;
    SharedPreferences preferences;
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;

    public ReadAppConfigTask(Context context){
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
    Boolean isReadAppSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser(context);
        int rc = Integer.parseInt(xobj.getElementText("rc", xmlResponse));//get the return codes
        int xrc = Integer.parseInt(xobj.getElementText("xrc", xmlResponse));
        int xsrc = Integer.parseInt(xobj.getElementText("xsrc", xmlResponse));
        Log.d("isListAppSuccessful()","value of rc, xrc, xsrc: " + rc + "," + xrc + "," + xsrc);
        return (rc==0&&xrc==0&&xsrc==0);//if return codes are 0 return true, else false
    }

    @Override
    protected void onPreExecute() {
    }

    protected String doInBackground(String... params) {
        ArrayList<Object> argList = new ArrayList<Object>();
        argList.add("act,readappconfig");
        argList.add("appfile," + params[0]);//passed in through args
        argList.add("sid," + preferences.getString("SID", null));
        Boolean isSuccess;
        HttpEntity entity;
        String response = null;
        try {
            entity = MultiPartEntityBuilder.mebBuilder(argList);
            response = buildMPEAndExecute(entity);
            isSuccess = isReadAppSuccessful(response);
            Log.d("ReadAppConfigTask.doInBackground()","value of isSuccess: " + isSuccess);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
        }
        return response;
    }

    protected void onPostExecute(String result) {
        Log.d("ReadAppConfigTask.onPostExecute()","value of result: " + result);
        delegate.readAppConfigTaskProcessFinish(result);
    }
}
