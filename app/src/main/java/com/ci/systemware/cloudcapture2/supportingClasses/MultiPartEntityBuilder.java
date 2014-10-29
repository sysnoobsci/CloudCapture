package com.ci.systemware.cloudcapture2.supportingClasses;

import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by The Bat Cave on 10/14/2014.
 */
public class MultiPartEntityBuilder {

    public static HttpEntity mebBuilder(ArrayList<Object> args) throws UnsupportedEncodingException { //build MultiPartEntity after checking type of the args
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Object larg : args) {//check each argument for class type and act accordingly
            if (larg != null) {//make sure arg isn't null
                Log.d("mebBuilder()", "larg.getClass() value: " + larg.getClass());
                if (larg.getClass().equals(String.class)) {//if type of arg is String, do this
                    int i = 0;
                    int j = 1;
                    String[] parts = larg.toString().split(",");
                    while (j < parts.length) {//allows for multiple key-value pairs
                        builder.addPart(parts[i], new StringBody(parts[j]));
                        i += 2;
                        j += 2;
                    }
                }
                if (larg.getClass().equals(File.class)) {//if type of arg is File, do this
                    builder.addPart("file", new FileBody((File) larg));
                }
                if (larg.getClass().getName().startsWith("android.net.Uri$")) {//if type of arg is Uri, do this
                    Uri imageUri = (Uri) larg;
                    File newImage = new File(imageUri.getPath());
                    Log.d("Variable", "imageUri.getPath().toString() value: " + imageUri.getPath());
                    builder.addPart("file", new FileBody(newImage));
                }
            }
        }
        HttpEntity entity = builder.build();
        return entity;
    }
}
