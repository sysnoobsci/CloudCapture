package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 6/24/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by adrian.meraz on 5/16/2014.
 */
public class XMLParser {
    private static String xmlVals;
    private static String xmlResponse;
    StringBuilder total = new StringBuilder();
    private final static String EMPTY_STRING = "";
    private final static String TEMPLATE_PREFIX = "gensrch";
    ArrayList<String> textTag = new ArrayList<String>();
    static SharedPreferences preferences;

    public XMLParser(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);;
    }

    public static String getxmlVals() {
        return xmlVals;
    }

    public void setxmlVals(String xmlstring) {
        this.xmlVals = xmlstring;
    }

    public ArrayList<String> getTextTag() {
        return textTag;
    }

    public void setTextTag(ArrayList<String> textTag) {
        this.textTag = textTag;
    }

    public String parseXMLfunc(String xmlResponse) throws XmlPullParserException, IOException{
        String xresp = xmlResponse;
        clearXMLString();//clear the String before adding a new XMLString
        clearXMLTags();
        ArrayList<String> listOfTextTags = new ArrayList<String>();//a list contain all the text inside XML tags
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader (xresp) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.TEXT) {
                listOfTextTags.add(xpp.getText());
                total.append(xpp.getText()).append(",");
            }
            eventType = xpp.next();
        }
        setTextTag(listOfTextTags);
        setxmlVals(total.toString());
        Log.d("XML", "Contents of CSV XML Response: " + getxmlVals());
        return total.toString();//return parsed contents of XML
    }

    public void clearXMLString(){
        setxmlVals(EMPTY_STRING);
    }

    public void clearXMLTags(){
        textTag.clear();
    }

    public static String getElementText(String tag, String xmlResponse) throws XmlPullParserException, IOException {//pass in a tag, and get the tag contents
        if (TextUtils.isEmpty(tag)) {//if nothing is being searched for, return all the xml results
            Log.d("getElementText()", "No tag being searched for.");
            return "tagNotValid";
        }
        else if(TextUtils.isEmpty(xmlResponse)){
            Log.d("getElementText()", "xmlResponse empty or null");
            return "xmlResponseIsEmpty";
        }
        else {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlResponse));//get the XML string that was created from parsing the query response
            int eventType = xpp.getEventType();
            StringBuilder tagText = new StringBuilder();
            String matcher = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    matcher = xpp.getName();
                }
                else if (eventType == XmlPullParser.TEXT) {
                    if (matcher.equals(tag)) {//if the tag name matches what you're searching for, append the contents
                        tagText.append(xpp.getText()).append(",");
                    }
                }
                eventType = xpp.next();
            }
            String trimmedComma = tagText.toString().substring(0, tagText.toString().length() - 1);//trim commas off of the end
            return trimmedComma;
        }
    }

    public static String getCAMTemplateIDs(String camid, String xmlResponse) throws XmlPullParserException, IOException {//pass in a camid, and get template names from appconfig
        if (TextUtils.isEmpty(camid)) {//if nothing is being searched for, return all the xml results
            Log.d("getCAMTemplateIDs()", "No CAM ID being searched for.");
            return "camIDNotValid";
        }
        else if(TextUtils.isEmpty(xmlResponse)){
            Log.d("getCAMTemplateIDs()", "xmlResponse empty or null");
            return "xmlResponseIsEmpty";
        }
        else {
            xmlResponse = xmlResponse.replace("<![CDATA[", "").replace("]]>", "");//remove all the CDATA tags so XML can be parsed properly
            Log.d("getCAMTemplateIDs()", "xmlResponse value: " + xmlResponse);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlResponse));//get the XML string that was created from parsing the query response
            int eventType = xpp.getEventType();
            StringBuilder tagText = new StringBuilder();
            String matcher;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    matcher = xpp.getAttributeValue(null, "camid");
                    Log.d("getCAMTemplateIDs()", "Value of matcher: " + matcher);
                    String camidpref = preferences.getString("camid",null);
                    if (matcher != null && matcher.equals(camidpref)) {//if the tag name contains the camid, get the template name
                        String templateName = xpp.getAttributeValue(null, "label");
                        if(templateName.contains(TEMPLATE_PREFIX)) {//if template name contains the right prefix, add it
                            Log.d("getCAMTemplateIDs()", "templateName value: " + templateName);
                            tagText.append(templateName).append(",");
                        }
                    }
                }//end of XmlPullParser.START_TAG event
                eventType = xpp.next();
            }
            String trimmedComma = tagText.toString().substring(0, tagText.toString().length() - 1);//trim commas off of the end
            return trimmedComma;
        }
    }

    public static String readXMLAndSetFragmentLayout(String templateXMLFileName,Context context) throws XmlPullParserException, IOException {//pass in a camid, and get template names from appconfig
        if(TextUtils.isEmpty(templateXMLFileName)){
            Log.d("readXMLAndSetFragmentLayout()", "templateXMLFileName empty or null");
            return "templateXMLFileNameIsEmpty";
        }
        else {
            String xmlString = FileUtility.readFromFile(templateXMLFileName,context);
            xmlString = xmlString.replace("<![CDATA[", "").replace("]]>", "");//remove all the CDATA tags so XML can be parsed properly
            Log.d("getCAMTemplateIDs()", "xmlResponse value: " + xmlString);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));//get the XML string that was created from parsing the query response
            int eventType = xpp.getEventType();
            StringBuilder tagText = new StringBuilder();
            String matcher;
            String matcher2;
            Boolean isVisible = true;//flag to see if view is visible
            String startTagName;
            ArrayList<View> viewsList = new ArrayList<View>();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    matcher = xpp.getAttributeValue(null, "input");
                    matcher2 = xpp.getAttributeValue(null, "visible");
                    startTagName = xpp.getName();
                    Log.d("getCAMTemplateIDs()", "Value of matcher: " + matcher);
                    Log.d("getCAMTemplateIDs()", "Value of matcher: " + matcher2);
                    if (!TextUtils.isEmpty(matcher) && !TextUtils.isEmpty(matcher2) && String.valueOf(matcher2) == "1" ) {//if the view is visible in the CI UI, include it
                        while(xpp.getName() != startTagName){//keep going through the tag to make sure there aren't visible="0" strings
                            if(xpp.getAttributeValue(null, "visible") == "0"){
                                isVisible = false;
                            }
                            xpp.next();
                        }
                        if(isVisible){//if view is deemed visible, find out what view (CI UI element) is used in CI

                        }
                    }

                }//end of XmlPullParser.START_TAG event
                eventType = xpp.next();
            }
            String trimmedComma = tagText.toString().substring(0, tagText.toString().length() - 1);//trim commas off of the end
            return trimmedComma;
        }
    }

    public String viewChooser(String uiElementName){
        if(uiElementName.equals("combo")){
            return "spinner";
        }
        else if(uiElementName.equals("date")){
            return "date";
        }
        else if(uiElementName.equals("text")){
            return "textView";
        }
        else{
            return "noView";//if sutiable Android  view isn't found
        }
    }
}
