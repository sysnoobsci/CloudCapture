package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by john.williams on 8/26/2014.
 */
public class FileUtility {
    //static String root = Environment.getExternalStorageDirectory().toString();
    static ArrayList<File> dirArray = new ArrayList<File>();

    public static String getRootPath(Context context) {
        return context.getFilesDir().toString();
    }

    public static String getSyswareFilePath(Context context) {
        return context.getFilesDir().toString();
    }

    public static String getImageFilePath(Context context) {
        return String.valueOf(context.getFilesDir()) + "/Images/";
    }

    public static String getPDFFilePath(Context context) {
        return String.valueOf(context.getFilesDir()) + "/PDF/";
    }

    public static String getTxtFilePath(Context context) {
        return String.valueOf(context.getFilesDir()) + "/TXT/";
    }

    public static String getTempFilePath(Context context) {
        return String.valueOf(context.getFilesDir()) + "/Temp/";
    }

    public static String getCAMTemplateXMLTempFilePath(Context context) {
        return String.valueOf(context.getFilesDir()) + "/Temp/CAMTemplates";
    }

    public static Boolean doesFileExist(String fullFilePath){
        File file = new File(fullFilePath);
        return file.exists();
    }

    public static void directoryCheck(Context context){
        dirArray.add(new File(getImageFilePath(context)));
        dirArray.add(new File(getPDFFilePath(context)));
        dirArray.add(new File(getTxtFilePath(context)));
        dirArray.add(new File(getTempFilePath(context)));
        dirArray.add(new File(getCAMTemplateXMLTempFilePath(context)));
        for(File path : dirArray){
            if(!path.exists()){//if dir doesn't exist, create it
                path.mkdir();
                Log.d("directoryCheck()","Directory: " + path.getPath() + " created.");
            }
        }
        dirArray.clear();
    }

    public static void writeToFile(String data,String dir, String fileName) {
        File myFile = new File(dir,fileName);
        try {
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            Log.d("writeToFile()","File " + dir + fileName + " written.");
        }
        catch (IOException e) {
            Log.e("writeToFile()", "File write failed: " + myFile.getAbsolutePath());
            e.printStackTrace();
        }
    }


    public static String readFromFile(String path,Context context) {
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(path);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = String.valueOf(stringBuilder);
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    public static String chooseDownloadFilePath(String versionFormat,Context context) {
        String fp;
        if (versionFormat.equals("PDF")) {
            fp = getPDFFilePath(context);
        } else if (versionFormat.equals("XML") || versionFormat.equals("TXT") || versionFormat.equals("ASC")) {
            fp = getTxtFilePath(context);
        } else {
            fp = getImageFilePath(context);
        }
        return fp;
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
}