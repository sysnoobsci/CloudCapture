package com.ci.systemware.cloudcapture2.supportingClasses;

/**
 * Created by adrian.meraz on 6/24/2014.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

    public static String getCAMTemplateIDs(String xmlResponse) throws XmlPullParserException, IOException {//pass in a camid, and get template names from appconfig
        String camid =  preferences.getString("camid",null);
        if(TextUtils.isEmpty(xmlResponse)){
            Log.d("getCAMTemplateIDs()", "xmlResponse empty or null");
            return "xmlResponseIsEmpty";
        }
        else {
            xmlResponse = xmlResponse.replace("<![CDATA[", "").replace("]]>", "").replace("]]", "");//remove all the CDATA tags so XML can be parsed properly
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
                    if (matcher != null && matcher.equals(camid)) {//if the tag name contains the camid, get the template name
                        String templateName = xpp.getAttributeValue(null,"label");
                        if(templateName.contains(TEMPLATE_PREFIX)) {//if template name contains the right prefix, add it
                            Log.d("getCAMTemplateIDs()", "templateName value: " + templateName);
                            tagText.append(templateName).append(",");
                        }
                    }
                }//end of XmlPullParser.START_TAG event
                eventType = xpp.next();
            }//end of xmlString
            String trimmedComma = tagText.toString().substring(0, tagText.toString().length() - 1);//trim commas off of the end
            return trimmedComma;
        }
    }

    public static void readXMLAndMapViews(String templateFullFileName) throws XmlPullParserException, IOException {//pass in a camid, and get template names from appconfig
        String templateFileName = FileUtility.fileNameAndExt(templateFullFileName);
        if(TextUtils.isEmpty(templateFullFileName)){
            Log.d("readXMLAndMapViews()", "templateFullFileName empty or null");
        }
        else {
            Log.d("readXMLAndMapViews()","Beginning read of file: " + templateFullFileName);
            String xmlString = FileUtility.readFromFile(templateFullFileName);
            xmlString = xmlString.replace("<![CDATA[", "").replace("]]>", "").replace("]]", "");//remove all the CDATA tags so XML can be parsed properly
            Log.d("readXMLAndMapViews()", "xmlResponse value: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));//get the XML string that was created from parsing the query response
            int eventType = xpp.getEventType();
            //instantiating variables
            int eleCount = 0;
            String matcher;
            String matcher2;
            String fieldLabel = null;
            Boolean isVisible;//flag to see if view is visible
            Boolean isEndTag = false;//flag to see if a certain tag has been hit again
            Boolean isTypeFound = false;
            Boolean isLabelFound = false;
            String startTagName;
            String uiElementType = null;
            ArrayList<String> viewsList = new ArrayList<String>();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG){
                    startTagName = xpp.getName();
                    matcher = xpp.getAttributeValue(null, "name");
                    matcher2 = xpp.getAttributeValue(null, "visible");
                    if (matcher!=null && matcher2!= null && matcher2.equals("1")){
                        isVisible = true;//found a visible ui element
                        Log.d("readXMLAndMapViews()","UI input element found");
                        xpp.next();//advance to next event
                        Log.d("readXMLAndMapViews()","Checking sub tags inside <" + startTagName + ">");
                        while(!isEndTag){//iterate through tags within the tag to make sure element is visible
                            if(xpp.getName() != null && xpp.getName().equals("type") && !isTypeFound){
                                xpp.next();
                                uiElementType = xpp.getText();
                                Log.d("readXMLAndMapViews()", "value of uiElementType: " + uiElementType);
                                isTypeFound = true;
                            }
                            else if(xpp.getName() != null && xpp.getName().equals("label") && !isLabelFound){
                                xpp.next();
                                if(!TextUtils.isEmpty(xpp.getText())) {//don't want the values getting overidden by empty values
                                    fieldLabel = xpp.getText();
                                    Log.d("readXMLAndMapViews()", "value of fieldLabel: " + fieldLabel);
                                }
                                isLabelFound = true;
                            }
                            if(xpp.getAttributeValue(null, "visible")!= null && xpp.getAttributeValue(null, "visible").equals("0")){
                                Log.d("readXMLAndMapViews()","UI element " + uiElementType + " will be non-visible. Not added to visible UI elements");
                                isVisible = false;
                                uiElementType = "";
                            }
                            if(xpp.getName() != null && xpp.getName().equals(startTagName)) {//if the end tag is found, set isEndTag to true
                                Log.d("readXMLAndMapViews()","end tag <" + startTagName + "> found.");
                                isEndTag = true;
                            }
                            xpp.next();
                        }//end of while loop
                        if(!TextUtils.isEmpty(uiElementType)&&isVisible){
                            viewsList.add(fieldLabel + "," + viewChooser(uiElementType));
                            Log.d("readXMLAndMapViews()", "value of viewsList[" + eleCount + "]: " + viewsList.get(eleCount));
                            eleCount++;
                        }
                        fieldLabel = "";
                        uiElementType = "";
                        //reset flags
                        isEndTag = false;
                        isTypeFound = false;
                        isLabelFound = false;
                    }//end of visible UI element being found
                }//end of XmlPullParser.START_TAG event
                eventType = xpp.next();//go to next event
            }//end of xmlstring to be parsed
            Template2LayoutTracker.map.put(templateFileName,viewsList);//add the template file name and its list of visible UI elements
        }//end of else statement
    }


    public static String viewChooser(String uiElementName){//converts the CI UI element to what it would be as an Android view
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
            return "noView";//if suitable Android view isn't found
        }
    }
}
