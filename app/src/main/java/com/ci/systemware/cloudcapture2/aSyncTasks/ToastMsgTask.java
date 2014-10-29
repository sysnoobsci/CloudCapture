package com.ci.systemware.cloudcapture2.aSyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ci.systemware.cloudcapture2.R;

/**
 * Created by adrian.meraz on 5/29/2014.
 */
// A class that will run Toast messages in the main GUI context
public class ToastMsgTask extends AsyncTask<String, String, String> {
    static Context context;

    public ToastMsgTask(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... params)
    {
        if(params[0]!=null) {
            return params[0];
        }
        else{
            return "noMessage";
        }
    }

    // This is executed in the context of the main GUI thread
    protected void onPostExecute(String result) {
        Toast toast = Toast.makeText(context, result, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void noConnectionMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.noConnectionMessage));
    }

    public static void reportNotValidMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.reportNotValidMessage));
    }

    public static void fillFieldMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.fillFieldMessage));
    }

    public static void isFileUploadStatus(Context context, Boolean flag) {
        String toastMessage;
        if (flag) {
            toastMessage = context.getResources().getString(R.string.fileUploadSuccessMessage);
            Log.d("isFileUploadStatus()", "File was successfully Uploaded.");
        } else {
            toastMessage = context.getResources().getString(R.string.fileUploadFailedMessage);
            Log.d("isFileUploadStatus()", "File upload failed.");
        }
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                toastMessage);
    }

    public static void fileNotWritten(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.fileNotWrittenMessage));
    }

    public static void picNotTakenMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.picNotTakenMessage));
    }

    public static void noProfileSelectedMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.noProfileSelectedMessage));
    }

    public static void notValidTopicTemplateMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.notValidTopicTemplateMessage));
    }

    public static void downloadFileSuccessfulMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.downloadFileSuccessfulMessage));
    }

    public static void invalidFileFormatMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.invalidFileFormatMessage));
    }

    public static void fileIsCachedMessage(Context context) {
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.fileIsCachedMessage));
    }

    public static void isFileOverWritten(Context context, Boolean isOverwrite) {
        String toastMessage;
        if(isOverwrite) {
            toastMessage = context.getResources().getString(R.string.FileOverWrittenMessage);
        }
        else{
            toastMessage = context.getResources().getString(R.string.FileNotOverWrittenMessage);
        }
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                toastMessage);
    }

    public static void CIConnProfileSavedMessage(Context context){
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.CIConnProfileSavedMessage));
    }

    public static void DupCIConnProfileSavedMessage(Context context){
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.DupCIConnProfileSavedMessage));
    }

    public static void isLogoffSuccessMessage(Context context, boolean isSuccess){
        String toastMessage;
        if(isSuccess) {
            toastMessage = context.getResources().getString(R.string.logoffSuccessMessage);
            Log.d("isLogoffSuccessMessage()", "CI Server logoff successful.");
        }
        else{
            toastMessage = context.getResources().getString(R.string.logoffFailedMessage);
            Log.d("isLogoffSuccessMessage()", "CI Server logoff failed.");
        }
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                toastMessage);
    }

    public static void isLogonSuccessMessage(Context context, boolean isSuccess){
        String toastMessage;
        Log.d("isLogonSuccessMessage()","Value of isSuccess: " + isSuccess);
        if(isSuccess) {
            toastMessage = context.getResources().getString(R.string.logonSuccessMessage);
            Log.d("isLogonSuccessMessage()", "CI Server logon successful.");
        }
        else{
            toastMessage = context.getResources().getString(R.string.logonFailedMessage);
            Log.d("isLogonSuccessMessage()", "CI Server logon failed.");
        }
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                toastMessage);
    }

    public static void areSettingsGoodMessage(Context context){
        new ToastMsgTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                context.getResources().getString(R.string.areSettingsGoodMessage));
    }

}