package com.ci.systemware.cloudcapture.aSyncTasks;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import com.ci.systemware.cloudcapture.interfaces.PingTaskInterface;
import com.ci.systemware.cloudcapture.supportingClasses.APIQueries;
import com.ci.systemware.cloudcapture.supportingClasses.QueryArguments;

import java.util.concurrent.TimeUnit;

/**
 * Created by adrian.meraz on 10/8/2014.
 */
public class UploadFileTask  extends AsyncTask<String, String, String> implements PingTaskInterface {

    Activity activity;
    Context context;
    EditText description;
    Object file2upload;
    ProgressDialog ringProgressDialog;
    String topicTemplateName;
    SharedPreferences preferences;
    APIQueries apiobj;
    private boolean success;
    Boolean pingResult;
    PingTask pingTask;


    public UploadFileTask(Context context, EditText description, Object file2upload){
        this.activity = (Activity) context;
        this.context = context;
        this.description = description;
        this.file2upload = file2upload;
        pingTask = new PingTask(context);
        pingTask.delegate = this;
        apiobj = new APIQueries(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        topicTemplateName = preferences.getString("camName_preference", null);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    private void setUploadProgressDialog() {
        ringProgressDialog = new ProgressDialog(context);
        ringProgressDialog.setTitle("Performing Action ...");
        ringProgressDialog.setMessage("Uploading file ...");
    }

    @Override
    protected void onPreExecute() {
        setUploadProgressDialog();
        ringProgressDialog.show();
    }

    protected String doInBackground(String... params) {
        if (uploadCheck(description, file2upload)) {
            try {
                new PingTask(context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                        .get(preferences.getInt("lilotimeout_preference", 5000), TimeUnit.MILLISECONDS);//wait or ping task to execute
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (pingResult) {
                Log.d("Message", "CI Login successful and ready to upload file.");
                createTopic();//create a topic instance object
            } else {
                Log.d("Message", "CI Login failed. Unable to load file.");
            }
        }
        return "uploaded";
    }

    protected void onPostExecute(String result) {
        if(result.equals("uploaded")){
            activity.getFragmentManager().popBackStackImmediate();//return to previous fragment in back stack
        }
        ringProgressDialog.dismiss();
    }

    void createTopic(){
        if (topicTemplateName != null) {
            QueryArguments.addArg("tplid," + topicTemplateName);
            QueryArguments.addArg("name," + description.getText().toString());
            QueryArguments.addArg("detail,y");
            QueryArguments.addArg("sid," + preferences.getString("SID",null));
            QueryArguments.addArg(file2upload);
            Log.d("createTopic()", "Value of file2upload: " + file2upload.toString());
            try {
                setSuccess(apiobj.createtopicQuery(QueryArguments.getArgslist()));//get result from createTopocQuery
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ToastMsgTask.notValidTopicTemplateMessage(context);
        }
        ringProgressDialog.dismiss();
    }

    Boolean uploadCheck(EditText description, Object file2upload) {
        if (file2upload == null) {//checks if image taken yet - or if object is valid if not an image
            ToastMsgTask.picNotTakenMessage(context);
            return false;
        }
        if (String.valueOf(description.getText()).isEmpty()) {
            ToastMsgTask.fillFieldMessage(context);
            return false;
        }
        Log.d("uploadCheck()", "upload check passed.");
        return true;//if pic was taken and there is a non-empty description, return true
    }

    public void pingTaskProcessFinish(String output){
        pingResult = Boolean.parseBoolean(output);//this receives result fired from async class of onPostExecute(result) method.

    }
}
