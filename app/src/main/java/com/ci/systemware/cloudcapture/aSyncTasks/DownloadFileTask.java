package com.ci.systemware.cloudcapture.aSyncTasks;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.R;
import com.ci.systemware.cloudcapture.supportingClasses.FileUtility;
import com.ci.systemware.cloudcapture.supportingClasses.TempFileTracker;
import com.ci.systemware.cloudcapture.supportingClasses.VersionInfo;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by The Bat Cave on 9/25/2014.
 */
public class DownloadFileTask extends AsyncTask<String, String, String> {

    String dirPath;
    String fullFilePathName;
    int versionNumber;
    Activity activity;
    Context context;
    private ProgressDialog mProgressDialog;
    String fragmentChosen;
    SharedPreferences preferences;
    Boolean pdfPref;

    public DownloadFileTask(String dirPath, String fullFilePathName, int versionNumber, Activity activity) {
        this.dirPath = dirPath;
        this.fullFilePathName = fullFilePathName;
        this.versionNumber = versionNumber;
        this.activity = activity;
        this.context = activity;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        pdfPref = preferences.getBoolean("ci_pdf_preference", false);
    }



    void setDialogParms() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading file...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        setDialogParms();
        mProgressDialog.show();
    }

    @Override
    protected String doInBackground(String... args) {//1st arg is a url, 2nd is a flag
        String arg0 = args[0];//grab the arguments passed in
        if (args.length > 1) {//make sure not grabbing an arg that wasn't passed in
            String arg1 = args[1];
            if (arg1 != null) {
                fragmentChosen = arg1;
            }
        }
        if(dirPath.equals(FileUtility.getTempFilePath(context))){//check if the file is to be written to the temp path
            if(TempFileTracker.isTempFileCached(fullFilePathName, versionNumber)) {//if file is already cached, don't download it again
                ToastMsgTask.fileIsCachedMessage(context);
                return "cached";
            }
        }
        int count;
        try {
            URL url;
            Log.d("DownFileTask.doInBackGround()","Value of pdfPref: "+ pdfPref);
            url = new URL(arg0);
            URLConnection conexion = url.openConnection();
            conexion.connect();
            Log.d("DownloadFileTaskTest", "Number of args: " + args.length);
            int lengthOfFile = conexion.getContentLength();
            Log.d("DownloadFileTaskTest", "Length of file: " + lengthOfFile);
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(fullFilePathName);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "downloaded";
    }

    protected void onProgressUpdate(String... progress) {
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("DownloadFileTaskTest.onPostExecute()","Value of result: " + result);
        if(result.equals("cached")){
            //callIP_Fragment();
        }
        else{
            if(fragmentChosen != null){
                if (fragmentChosen.equals("IPFragment")){
                    //callIP_Fragment();
                }
                else{
                    Log.d("DownloadFileTask.onPostExecute","Invalid fragment chosen");
                }
            }
            Log.d("DownloadFileTaskTest", "File " + fullFilePathName + " written");
        }
        mProgressDialog.dismiss();
    }

    /*private void callIP_Fragment() {
        Bundle bundle = new Bundle();
        bundle.putString("retrieve_fileName", TempFileTracker.getTempFilePath(VersionInfo.getVersion()));
        bundle.putString("retrieve_fileFormat", VersionInfo.getFormat());
        bundle.putString("retrieve_versionNumber", String.valueOf(VersionInfo.getVersion()));
        Fragment fragment = new Image_Preview_Fragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
    }*/
}