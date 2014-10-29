package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by adrian.meraz on 5/16/2014.
 */

public class ParseSessionInfo {

    static Context context;
    static SharedPreferences preferences;

    public ParseSessionInfo(Context context) {
        context = this.context;
    }

    final static int SIZE_OF_TARGET_SID = 40;//size of session ID
    final static int SIZE_OF_TARGET_JSID = 32;//size of session ID

    public static String parseSID(String xmlstring) {//pass in the xml response string to parse out SID
        String SID;
        Log.d("Variable", "xmlstring value: " + xmlstring);
        String target = "session sid=\"";
        int a = xmlstring.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_SID + target.length();//54 is the size of the SID plus the "target" string size
        SID = xmlstring.substring(b, c);
        Log.d("Value of SID ", SID);
        return SID;//returns the session ID
    }

    public static String parseJSID(String xmlstring) {
        String JSID;
        Log.d("Variable", "xmlstring value: " + xmlstring);
        String target = "jsessionid=\"";
        int a = xmlstring.indexOf(target);
        int b = a + target.length();
        int c = a + SIZE_OF_TARGET_JSID + target.length();//47 is the size of the SID plus the "target" string size
        JSID = xmlstring.substring(b, c);
        Log.d("Value of JSID ", JSID);
        return JSID;//returns the session ID
    }

    public static String parsePermission(String xmlstring) {
        String target1 = "permission id=\"";
        int index1 = xmlstring.indexOf(target1);
        int start = index1 + target1.length();
        String permissionID = xmlstring.substring(start,start+7);
        permissionID = permissionID.replaceAll("\\D+","");//strips out everything except digits
        Log.d("parsePermission()","value of permission: " + permissionID);
        return permissionID;
    }


    protected static Boolean isValidSID(Context context) {//checks if there is a valid SID
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String SID = preferences.getString("SID",null);
        return (SID != null && !SID.isEmpty());
    }


}
