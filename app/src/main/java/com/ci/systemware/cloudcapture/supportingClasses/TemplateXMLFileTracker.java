package com.ci.systemware.cloudcapture.supportingClasses;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by adrian.meraz on 10/24/2014.
 */
public class TemplateXMLFileTracker {
    static ArrayList<String> templateFileInfo = new ArrayList<String>();

    public static void addTemplateFileToList(String filePath) {//add temp files and versions
        templateFileInfo.add(filePath);//version number is the key, filepath is the value
        Log.d("addTemplateFileToList()", "Template XML File: " + filePath + " added to the list");
    }

    public static void clearTemplateXMLFiles(Context context){
        File dir = new File(FileUtility.getCAMTemplateXMLTempFilePath(context));
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                new File(dir, aChildren).delete();
                Log.d("clearTemplateXMLFiles()", "Template XML File " + aChildren + " has been deleted.");
            }
        }
    }

}
