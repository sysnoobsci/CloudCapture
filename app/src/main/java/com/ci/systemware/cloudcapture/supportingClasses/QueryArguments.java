package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 10/10/2014.
 */
import java.util.ArrayList;

/**
 * Created by adrian.meraz on 9/8/2014.
 */
public class QueryArguments {
    static private ArrayList<Object> argslist = new ArrayList<Object>();

    public static ArrayList<Object> getArgslist() {
        return argslist;
    }

    public static void addArg(Object arg) {
        argslist.add(arg);
    }

    static void clearList() {
        argslist.clear();
    }
}
