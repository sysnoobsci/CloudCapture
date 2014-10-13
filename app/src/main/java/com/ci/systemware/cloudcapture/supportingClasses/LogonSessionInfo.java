package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ci.systemware.cloudcapture.aSyncTasks.ToastMsgTask;

/**
 * Created by adrian.meraz on 5/16/2014.
 */

public class LogonSessionInfo {

    static Context context;

    public LogonSessionInfo(Context context) {
        context = this.context;
    }

    private static String sid;//session id
    private static String jsid;//jsession id
    final static int SIZE_OF_TARGET_SID = 40;//size of session ID
    final static int SIZE_OF_TARGET_JSID = 32;//size of session ID
    static APIQueries apiobj;

    public static String getSid() {
        return sid;
    }

    public static String getJSid() {
        return jsid;
    }

    public static void setSid(String result) {//pass in the xml response string to parse out sid
        Log.d("Variable", "result value: " + result);
        String target = "session sid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the sid plus the "target" string size
        LogonSessionInfo.sid = result.substring(b, c);
        Log.d("Sid", sid);
    }

    public static void setJSid(String result) {
        Log.d("Variable", "result value: " + result);
        String target = "jsessionid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_JSID + target.length();//47 is the size of the sid plus the "target" string size
        LogonSessionInfo.jsid = result.substring(b, c);
        Log.d("JSid", jsid);
    }



    public Boolean tryLogin(Context context) throws Exception {
        this.context = context;
        apiobj = new APIQueries(context);
        Boolean loginResult;
        setCiLoginInfo();
        SharedPreferences connProfile = PreferenceManager.getDefaultSharedPreferences(context);
        //try a ping first, if successful, don't try logging in again
        Boolean pingResult = apiobj.pingQuery();
        if (pingResult) {
            Log.d("tryLogin()", "Logon session already established. Ping Successful.");
            return true;//if ping is successful, return true
        }
        Log.d("tryLogin", "profile chosen: " + connProfile.getString("list_preference_ci_servers", null));
        if (connProfile.getString("list_preference_ci_servers", null) != null) {//check if profile has been chosen
            QueryArguments.addArg("user," + getUsername());
            QueryArguments.addArg("password," + getPassword());
            loginResult = apiobj.logonQuery(QueryArguments.getArgslist());//send login query to CI via asynctask
            Log.d("tryLogin()", "loginResult value: " + loginResult);
            return loginResult;
        } else {
            ToastMsgTask.noProfileSelectedMessage(context);
            return false;
        }
    }

    protected static Boolean doesSidExist() {//checks if there is a valid sid
        return (getSid() != null) && !getSid().isEmpty();
    }
}
