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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 10/20/2014.
 */
public class ReadAppConfigTask extends AsyncTask<String, String, String>{
    public ReadAppConfigTaskInterface delegate = null;
    Context context;
    SharedPreferences preferences;
    ProgressDialog ringProgressDialog;

    public ReadAppConfigTask(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void setReadAppConfigProgressDialog() {
        ringProgressDialog = new ProgressDialog(context);
        ringProgressDialog.setTitle("Read CI config");
        ringProgressDialog.setMessage("Reading app config files ...");
    }

    String targetCIQuery() {
        String targetCIQuery = "http://" + preferences.getString("hostname",null) + "." +
                preferences.getString("domain", null) + ":" + preferences.getString("portnumber", null) + "/ci";
        Log.d("targetCIQuery()", "value of targetCIQuery: " + targetCIQuery);
        return targetCIQuery;
    }

    //action return code check
    Boolean isReadAppSuccessful(String xmlResponse) throws Exception{
        XMLParser xobj = new XMLParser();
        int rc = Integer.parseInt(xobj.findTagText("rc",xmlResponse));//get the return codes
        int xrc = Integer.parseInt(xobj.findTagText("xrc",xmlResponse));
        int xsrc = Integer.parseInt(xobj.findTagText("xsrc",xmlResponse));
        Log.d("isReadAppSuccessful()","value of rc, xrc, xsrc: " + rc + "," + xrc + "," + xsrc);
        return (rc==0&&xrc==0&&xsrc==0);//if return codes are 0 return true, else false
    }

    @Override
    protected void onPreExecute() {
        setReadAppConfigProgressDialog();
        ringProgressDialog.show();
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
            ApiCallTask apitaskobj = new ApiCallTask(entity,context);
            apitaskobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, targetCIQuery())
                    .get(preferences.getInt("actiontimeout_preference", 30000), TimeUnit.MILLISECONDS);
            isSuccess = isReadAppSuccessful(apitaskobj.getResponse());
            Log.d("ReadAppConfigTask.doInBackground()","value of isSuccess: " + isSuccess);
            response = apitaskobj.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
            ToastMsgTask.noConnectionMessage(context);
        }
        return response;
    }

    protected void onPostExecute(String result) {
        Log.d("ReadAppConfigTask.onPostExecute()","value of result: " + result);
        ringProgressDialog.dismiss();
        //delegate.readAppConfigTaskProcessFinish(result);
    }
}
