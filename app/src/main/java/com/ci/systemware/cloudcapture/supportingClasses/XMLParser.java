package com.ci.systemware.cloudcapture.supportingClasses;

/**
 * Created by adrian.meraz on 6/24/2014.
 */

import android.text.TextUtils;
import android.util.Log;

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
    private static final int MAX_STRING_LENGTH = 60;//max length to look backwards from given index

    private final static String EMPTY_STRING = "";

    ArrayList<String> textTag = new ArrayList<String>();

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
                    //Log.d("xpp", "xpp.getName() value: " + xpp.getName());
                    matcher = xpp.getName();
                } else if (eventType == XmlPullParser.TEXT) {
                    //Log.d("matcher", "matcher value: " + matcher);
                    if (matcher.equals(tag)) {//if the tag name matches what you're searching for, append the contents
                        //Log.d("xpp","xpp.getText() value: " + xpp.getText());
                        tagText.append(xpp.getText()).append(",");
                        matcher = "";//clear out the String again
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
            xmlResponse = xmlResponse.replace("<![CDATA[", "");//remove all the CDATA tags so XML can be parsed properly
            xmlResponse = xmlResponse.replace("]]>", "");
            Log.d("getCAMTemplateIDs()", "xmlResponse value: " + xmlResponse);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlResponse));//get the XML string that was created from parsing the query response
            int eventType = xpp.getEventType();
            StringBuilder tagText = new StringBuilder();
            String matcher;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    //xpp.require(XmlPullParser.START_TAG, null, xpp.getName());
                    final String text = xpp.getName();
                    //xpp.require(XmlPullParser.END_TAG, null, xpp.getName());
                    Log.d("getCAMTemplateIDs()", "Value of text: " + text);
                    Log.d("getCAMTemplateIDs()", "xpp.getName() value: " + xpp.getName());
                    matcher = text;
                    if (matcher.contains(camid)) {//if the tag name contains the camid, get the template name
                        Log.d("getCAMTemplateIDs()", "matcher value: " + matcher);
                        int endIndex = matcher.indexOf("camid='" + camid + "'") - 2;
                        int startIndex = endIndex - MAX_STRING_LENGTH;
                        String tempString = matcher.substring(startIndex, endIndex);
                        String targetString = "appfile label='";
                        int tempStartIndex = tempString.indexOf(targetString) + tempString.length();
                        String templateName = matcher.substring(tempStartIndex, endIndex);
                        Log.d("getCAMTemplateIDs()", "templateName value: " + templateName);
                        //Log.d("xpp","xpp.getText() value: " + xpp.getText());
                        tagText.append(templateName).append(",");
                        matcher = "";//clear out the String again
                    }
                }      //end of XmlPullParser.START_TAG event
                eventType = xpp.next();
            }
            String trimmedComma = tagText.toString().substring(0, tagText.toString().length() - 1);//trim commas off of the end
            return trimmedComma;
        }
    }
}
