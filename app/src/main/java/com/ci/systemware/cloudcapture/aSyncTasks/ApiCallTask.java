package com.ci.systemware.cloudcapture.aSyncTasks;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ci.systemware.cloudcapture.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Adrian Meraz on 8/19/2014.
 */
public class ApiCallTask extends AsyncTask<String, Void, String> {

    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost;
    static Context context;

    private static String response;
    private static HttpEntity entity;
    private static int ID = 0;//Task Unique ID

    public static String getResponse() {
        return response;
    }

    public void setResponse(String result) {
        response = result;
    }

    public ApiCallTask(HttpEntity entity, Context context) {
        this.context = context;
        this.entity = entity;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(String... aurl) {
        StringBuilder total = new StringBuilder();
        httppost = new HttpPost(aurl[0]);
        httppost.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            InputStream is = buf.getContent();
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    protected void onPostExecute(String result) {
        setResponse(result);
        Log.d("onPostExecute()", "APITask[" + ID + "].onPostExecute response: " + getResponse());
        ID++;//increment ID number of task
    }
}//end of ReqTask