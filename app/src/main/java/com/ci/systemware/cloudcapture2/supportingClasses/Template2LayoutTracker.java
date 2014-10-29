package com.ci.systemware.cloudcapture2.supportingClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adrian.meraz on 10/28/2014.
 * This class keeps track of the fragment layout elements that are associated
 * with the template UI elements from a CAM.
 */
public class Template2LayoutTracker {
    static Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();//form is key = template name ; value = list of labels + UI elements

    ArrayList<String> searchTemplateNames = new ArrayList<String>();
    ArrayList<String> createTemplateNames = new ArrayList<String>();
    ArrayList<String> updateTemplateNames = new ArrayList<String>();

    ArrayList<String> getSearchTemplateNames(){//returns sorted arraylist
        for (String key : map.keySet()) {
            if(key.contains("gensrch_search")){
                searchTemplateNames.add(key);
            }
        }
        Collections.sort(searchTemplateNames);
        return searchTemplateNames;
    }
    public ArrayList<String> getCreateTemplateNames(){//returns sorted arraylist
        for (String key : map.keySet()) {
            if(key.contains("gensrch_create")){
                createTemplateNames.add(key);
            }
        }
        Collections.sort(createTemplateNames);
        return createTemplateNames;
    }
    public ArrayList<String> getUpdateTemplateNames(){//returns sorted arraylist
        for (String key : map.keySet()) {
            if(key.contains("gensrch_update")){
                updateTemplateNames.add(key);
            }
        }
        Collections.sort(updateTemplateNames);
        return updateTemplateNames;
    }

    public ArrayList<String> getTemplateUIElements(String templateName){//pass in a template name and get the array list containing UI element label and type
        return map.get(templateName);
    }
}
