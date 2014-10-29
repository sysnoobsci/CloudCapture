package com.ci.systemware.cloudcapture2.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.res.AssetManager;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteException;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import com.ci.systemware.cloudcapture2.aSyncTasks.ToastMsgTask;

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;

/**
 * Created by The Bat Cave on 7/1/2014.
 */
public class DatabaseTxns extends SQLiteOpenHelper {

    final static int DB_VERSION = 1;
    final static String DB_NAME = "config_table";
    final static String TABLE_NAME = "config_table";
    final static String SCRIPT_NAME = "create.sql";
    final static String CICONFIG_COLUMN_NAME = "Ciprofile";
    Context context;


    public DatabaseTxns(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        // Store the context for later use
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("Message", "DatabaseHandler.oncreate() executed");
        Log.d("Message", "Creating database.");
        executeSQLScript(database, SCRIPT_NAME);
    }

    private void executeSQLScript(SQLiteDatabase database, String script) {
        Log.d("Message", "executeSQLScript() called");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open(script);
            Log.d("Variable", "Value of inputStream: " + inputStream);
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            String[] createScript = outputStream.toString().split(";");
            for (String aCreateScript : createScript) {
                String sqlStatement = aCreateScript.trim();
                // TODO You may want to parse out comments here
                if (sqlStatement.length() > 0) {
                    database.execSQL(sqlStatement + ";");
                    Log.d("Message", "SQL script executed.");
                }
            }
        } catch (IOException e) {
            Log.e("Error", e.toString());
            // TODO Handle Script Failed to Load
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            switch (oldVersion) {
                case 1:
                    executeSQLScript(db, "update_v2.sql");
                case 2:
                    executeSQLScript(db, "update_v3.sql");
            }
        }
    }

    public String[] list_ci_servers() {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        List<String> list = new ArrayList<String>();
        try {
            String listciserversQuery = "SELECT " + CICONFIG_COLUMN_NAME + " FROM " + TABLE_NAME;
            //Log.d("Variable", "listciserversQuery: " + listciserversQuery);
            Cursor cursor = db.rawQuery(listciserversQuery, null);
            while (cursor.moveToNext()) {
                result = result.concat(cursor.getString(cursor.getColumnIndex(CICONFIG_COLUMN_NAME)) + ",");
            }
            list = new ArrayList<String>(Arrays.asList(result.split(",")));
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.d("Error", e.toString());
        }
        //Log.d("Variable", "Value of list_ci_servers() result: " + result);
        String[] ciserversArr = new String[list.size()];
        ciserversArr = list.toArray(ciserversArr);//convert List<String> to String[]
        return ciserversArr;
    }

    public String select_ci_server(String cis) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        try {
            String cisSelectQuery = "SELECT * FROM config_table WHERE " + CICONFIG_COLUMN_NAME + "=" + "'" + cis + "'" + ";";
            Log.d("Variable", "Value of cisSelectQuery: " + cisSelectQuery);
            Cursor cursor = db.rawQuery(cisSelectQuery, null);
            while (cursor.moveToNext()) {
                result = cursor.getString(cursor.getColumnIndex("_id"));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Ciprofile")));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Hostname")));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Domain")));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Portnumber")));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Username")));
                result = result.concat("," + cursor.getString(cursor.getColumnIndex("Password")));
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.d("Error", e.toString());
        }
        Log.d("Variable", "Value of select_ci_server() result: " + result);
        return result;
    }

    public void add_ci_server(ArrayList<String> slist) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Ciprofile", slist.get(0));
        values.put("Hostname", slist.get(1));
        values.put("Domain", slist.get(2));
        values.put("Portnumber", slist.get(3));
        values.put("Username", slist.get(4));
        values.put("Password", slist.get(5));
        try {
            db.insertOrThrow(TABLE_NAME, null, values);
            db.close();
            ToastMsgTask.CIConnProfileSavedMessage(context);
        } catch (SQLiteException e) {
            ToastMsgTask.DupCIConnProfileSavedMessage(context);
            e.printStackTrace();
        }
    }
}