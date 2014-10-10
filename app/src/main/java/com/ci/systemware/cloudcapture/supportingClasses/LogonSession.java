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

public class LogonSession {

    static Context context;

    public LogonSession(Context context) {
        context = this.context;
    }

    private static String hostname;
    private static String domain;
    private static int portnumber;
    private static String username;
    private static String password;
    private static String sid;//session id
    private static String jsid;//jsession id
    final static int SIZE_OF_TARGET_SID = 40;//size of session ID
    final static int SIZE_OF_TARGET_JSID = 32;//size of session ID
    static APIQueries apiobj;

    public static String getHostname() {
        return hostname;
    }

    public static void setHostname(String hostname) {
        LogonSession.hostname = hostname;
    }

    public static String getDomain() {
        return domain;
    }

    public static void setDomain(String domain) {
        LogonSession.domain = domain;
    }

    public static int getPortnumber() {
        return portnumber;
    }

    public static void setPortnumber(int portnumber) {
        LogonSession.portnumber = portnumber;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        LogonSession.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        LogonSession.password = password;
    }

    public static String getSid() {
        return sid;
    }

    public static String getJSid() {
        return jsid;
    }

    public static void setSid(String result) {
        Log.d("Variable", "result value: " + result);
        String target = "session sid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the sid plus the "target" string size
        LogonSession.sid = result.substring(b, c);
        Log.d("Sid", sid);
    }

    public static void setJSid(String result) {
        Log.d("Variable", "result value: " + result);
        String target = "jsessionid=\"";
        int a = result.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_JSID + target.length();//47 is the size of the sid plus the "target" string size
        LogonSession.jsid = result.substring(b, c);
        Log.d("JSid", jsid);
    }

    public static void setCiLoginInfo() {//takes the info from the fields and sends it in the loginQuery
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        DatabaseTxns dbh = new DatabaseTxns(context);
        String ciserver = preferences.getString("list_preference_ci_servers", null);
        String ciserverResult = dbh.select_ci_server(ciserver);
        String[] parms = ciserverResult.split(",");
        try {
            setHostname(parms[2]);
            setDomain(parms[3]);
            setPortnumber(Integer.parseInt(parms[4]));
            setUsername(parms[5]);
            setPassword(parms[6]);
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
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
