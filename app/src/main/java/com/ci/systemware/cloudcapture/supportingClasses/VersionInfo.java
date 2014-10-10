package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
public class VersionInfo {//class stores information about a single version of a report

    static String dsid;
    static String capture_timestamp;
    static int bytes;
    static String format;
    static int version;

    public static String getDsid() {
        return dsid;
    }

    public static void setDsid(String dsid) {
        VersionInfo.dsid = dsid;
    }

    public static String getCapture_timestamp() {
        return capture_timestamp;
    }

    public static void setCapture_timestamp(String capture_timestamp) {
        VersionInfo.capture_timestamp = capture_timestamp;
    }

    public static int getBytes() {
        return bytes;
    }

    public static void setBytes(int bytes) {
        VersionInfo.bytes = bytes;
    }

    public static String getFormat() {
        return format;
    }

    public static void setFormat(String format) {
        VersionInfo.format = format;
    }

    public static int getVersion() {
        return version;
    }

    public static void setVersion(int version) {
        VersionInfo.version = version;
    }
}