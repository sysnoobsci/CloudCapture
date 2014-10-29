package com.ci.systemware.cloudcapture2.supportingClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture2.aSyncTasks.ApiCallTask;
import com.ci.systemware.cloudcapture2.aSyncTasks.ToastMsgTask;

import org.apache.http.HttpEntity;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by adrian.meraz on 6/27/2014.
 */
public class APIQueries {
    private static Context context;
    private static Boolean actionresult = false;
    private static int action_timeout = 5000;//default values in milliseconds of timeouts
    private static int lilo_timeout = 5000;
    private static int upload_timeout = 30000;
    SharedPreferences preferences;

    public APIQueries(Context context) {
        setContext(context);
        setTimeouts();//set timeouts whenever APIQueries object is instantiated
    }

    private static Boolean getActionresult() {
        return actionresult;
    }

    private static void setActionresult(Boolean result) {
        actionresult = result;
    }

    Context getContext() {
        return context;
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setTimeouts() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        action_timeout = Integer.parseInt(preferences.getString("actiontimeout_preference", String.valueOf(action_timeout))) * 1000;
        lilo_timeout = Integer.parseInt(preferences.getString("lilotimeout_preference", String.valueOf(lilo_timeout))) * 1000;
        upload_timeout = Integer.parseInt(preferences.getString("uploadtimeout_preference", String.valueOf(upload_timeout))) * 1000;
        Log.d("setTimeouts()", "action_timeout in seconds: " + (double) action_timeout / 1000);
        Log.d("setTimeouts()", "lilo_timeout in seconds: " + (double) lilo_timeout / 1000);
        Log.d("setTimeouts()", "upload_timeout in seconds: " + (double) upload_timeout / 1000);
    }

    void resetResult() {
        setActionresult(false);
    }

    String targetCIQuery() {
        String targetCIQuery = "http://" + preferences.getString("hostname",null) + "." +
                preferences.getString("domain", null) + ":" + preferences.getString("portnumber", null) + "/ci";
        return targetCIQuery;
    }

    //createtopic
    public Boolean createtopicQuery(ArrayList<Object> args) throws IOException, XmlPullParserException, InterruptedException, ExecutionException {
        Boolean uploadSuccess;
        ArrayList<Object> actionargs = args;
        actionargs.add("act,createtopic");
        HttpEntity entity = MultiPartEntityBuilder.mebBuilder(actionargs);
        ApiCallTask apitaskobj = new ApiCallTask(entity);
        try {
            apitaskobj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,targetCIQuery())
                    .get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMsgTask.noConnectionMessage(getContext());
        }
        Log.d("Variable", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XMLParser xobj = new XMLParser(context);
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        ToastMsgTask.isFileUploadStatus(getContext(), getActionresult());
        uploadSuccess = getActionresult();
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
        return uploadSuccess;
    }

    //listversion
    public String listversionQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {
        ArrayList<Object> actionargs = args;
        actionargs.add("act,listversion");
        HttpEntity entity = MultiPartEntityBuilder.mebBuilder(actionargs);
        ApiCallTask apitaskobj = new ApiCallTask(entity);
        try {
            apitaskobj.execute(targetCIQuery())
                    .get(action_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMsgTask.noConnectionMessage(getContext());
        }
        Log.d("listversionQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XMLParser xobj = new XMLParser(context);
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        if (getActionresult()) {//if the ping is successful(i.e. user logged in)
            Log.d("listversionQuery()", "CI Server listversion successful.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return apitaskobj.getResponse();//return the good results
        } else {
            ToastMsgTask.reportNotValidMessage(getContext());
            Log.d("listversionQuery()", "CI Server listversion failed.");
            resetResult();//reset action result after checking it
            QueryArguments.clearList();//clear argslist after query
            return null;
        }
    }

    //logoff
    public Boolean logoffQuery(ArrayList<Object> args) throws ExecutionException,
            InterruptedException, IOException, XmlPullParserException {

        ArrayList<Object> actionargs = args;
        actionargs.add("act,logoff");
        HttpEntity entity = MultiPartEntityBuilder.mebBuilder(actionargs);
        ApiCallTask apitaskobj = new ApiCallTask(entity);
        try {
            apitaskobj.execute(targetCIQuery())
                    .get(lilo_timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException te) {
            ToastMsgTask.noConnectionMessage(getContext());
        }
        Log.d("logoffQuery()", "apitaskobj.getResponse() value: " + apitaskobj.getResponse());
        XMLParser xobj = new XMLParser(context);
        xobj.parseXMLfunc(apitaskobj.getResponse());
        isActionSuccessful(xobj.getTextTag());
        Boolean logoffStatus = getActionresult();
        ToastMsgTask.isLogoffSuccessMessage(context, logoffStatus);
        resetResult();//reset action result after checking it
        QueryArguments.clearList();//clear argslist after query
        return logoffStatus;
    }

    //retrieve
    public String retrieveQuery(String tid) {//retrieves resources from content server
        String retrieveQuery = targetCIQuery() + "?act=retrieve&tid=" + tid + "&sid=" + preferences.getString("SID",null);
        return retrieveQuery;
    }

    public ArrayList<String> getVersionInfo(String xmlResponse) throws IOException, XmlPullParserException {
        XMLParser xobj = new XMLParser(context);
        ArrayList<String> versionInfo = new ArrayList<String>();
        String path = xobj.getElementText("path", xmlResponse);//get the path name
        String xid = xobj.getElementText("xid", xmlResponse);//get the xid
        String dsids = xobj.getElementText("dsid", xmlResponse);//get the DSIDs
        String cts = xobj.getElementText("cts", xmlResponse);//get the DSIDs
        String bytes = xobj.getElementText("bytes", xmlResponse);//get the bytes
        String fmt = xobj.getElementText("fmt", xmlResponse);//get the format
        String ver = xobj.getElementText("v", xmlResponse);//get the version number
        String[] pathsarr = path.split(",");//arrays should all be the same size
        String[] xidarr = xid.split(",");
        String[] dsidsarr = dsids.split(",");
        String[] ctsarr = cts.split(",");
        String[] bytesarr = bytes.split(",");
        String[] fmtarr = fmt.split(",");
        String[] verarr = ver.split(",");

        for (int i = 0; i < dsidsarr.length; i++) {
            //Log.d("getVersionInfo()", "version " + verarr[i] + " attributes: " + sbuild.toString()); //just for debug purposes
            versionInfo.add(dsidsarr[i] + "," + ctsarr[i] + "," + bytesarr[i] + "," + fmtarr[i] + ","
                    + verarr[i] + "," + "V~" + xidarr[i] + "~" + dsidsarr[i] + "~" + pathsarr[i] + "~"
                    + verarr[i]);
        }
        return versionInfo;
    }

    public static ArrayList<String> getMetadata(ArrayList<String> lvers, String selection) {
        int choose = -1;//selects the version info to grab
        if (selection.equals("DSID")) {//dsid of report version
            choose = 0;
        } else if (selection.equals("CTS")) {//timestamp when report version was created
            choose = 1;
        } else if (selection.equals("BYTES")) {//size in bytes of report version
            choose = 2;
        } else if (selection.equals("FMT")) {//format of report version
            choose = 3;
        } else if (selection.equals("VER")) {//version number of report version
            choose = 4;
        } else if (selection.equals("TID")) {//topic instance id of report version
            choose = 5;
        }
        if (choose == -1) {
            Log.d("getMetadata()", "Error. Invalid Metadata Tag.");
        }
        ArrayList<String> vers = new ArrayList<String>();
        for (String v : lvers) {
            String[] pieces = v.split(",");
            vers.add(pieces[choose]);//0=dsid,1=cts,2=bytes,3=fmt,4=ver,5=tid
        }
        return vers;
    }

    //action return code check
    void isActionSuccessful(ArrayList<String> larray) {
        if (larray.size() == 0) {//if the array is of size 0, nothing was returned from the ciserver
            Log.d("isActionSuccessful()", "Nothing returned from CI server.");
            setActionresult(false);
        } else {
            if ((larray.get(0).equals("0") || larray.get(0).equals("7")) && larray.get(1).equals("0") && larray.get(2).equals("0")) {
                setActionresult(true);
            } else {
                setActionresult(false);
            }
        }
    }


}
