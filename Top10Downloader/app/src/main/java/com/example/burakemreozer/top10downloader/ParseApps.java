package com.example.burakemreozer.top10downloader;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by burakemreozer on 24.01.2018.
 */

public class ParseApps {
    private static final String TAG = "ParseApps";
    private ArrayList<FeedEntry> applications;

    public ParseApps() {
        this.applications = new ArrayList<>();

    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData, MainActivity.Outline outline){
        boolean status = true;
        FeedEntry currentRecord = null;

        boolean inEntry = false;
        boolean isTitle = false;
        boolean isUpdated = false;

        String textValue= "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        //Log.d(TAG, "parse: Starting tag for " + tagName);
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentRecord = new FeedEntry();
                        } else if("title".equalsIgnoreCase(tagName) && !inEntry){
                            isTitle = true;
                        } else if("updated".equalsIgnoreCase(tagName) && !inEntry){
                            isUpdated = true;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(isTitle){
                            if("title".equalsIgnoreCase(tagName) && !inEntry){
                                outline.setTitle(textValue);
                                Log.d(TAG, "parse: TITLE = " + textValue);
                            }
                        }

                        if(isUpdated){
                            if("updated".equalsIgnoreCase(tagName) && !inEntry){
                                outline.setDate(textValue);
                                Log.d(TAG, "parse: TITLE = " + textValue);
                            }
                        }
                        //Log.d(TAG, "parse: Ending tag for " + tagName);
                        if(inEntry){
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if("name".equalsIgnoreCase(tagName)){
                                currentRecord.setName(textValue);
                            } else if("artist".equalsIgnoreCase(tagName)){
                                currentRecord.setArtist(textValue);
                            } else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentRecord.setReleaseDate(textValue);
                            } else if("summary".equalsIgnoreCase(tagName)){
                                currentRecord.setSummary(textValue);
                            } else if("image".equalsIgnoreCase(tagName)){
                                currentRecord.setImageURL(textValue);
                            }
                        }
                        break;

                    default:
                        // Nothing else to do.
                }
                eventType = xpp.next();
            }

//            for(FeedEntry app:applications){
//                Log.d(TAG, "*********************");
//                Log.d(TAG, app.toString());
//            }

        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }

        return status;
    }

}
