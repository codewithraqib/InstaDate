package com.example.raqib.instadate;

import android.content.Context;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SitesXmlPullParserBingKashmir {

    static final String KEY_SITE = "item";
    static final String KEY_NAME = "title";
    static final String KEY_LINK = "link";
    static final String KEY_ABOUT = "description";
    static final String KEY_IMAGE_URL = "News:Image";
    static final String KEY_DATE = "pubDate";


    public static List<NewsItems> getStackSitesFromFile(Context ctx) {

        // List of StackSites that we will return
        List<NewsItems> newsItems;
        newsItems = new ArrayList<NewsItems>();

        // temp holder for current StackSite while parsing
        NewsItems curNewsItems = null;

        // Temporary Holder for current text value while parsing
        String curText = "";

        try {
            // Get our factory and PullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // Open up InputStream and Reader of our file.
            FileInputStream fis = ctx.openFileInput("BingKashmir.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

            // point the parser to our file.
            xpp.setInput(reader);

            // get initial eventType
            int eventType = xpp.getEventType();

            //To get the actual location to start parsing from
            boolean actual_work = false;

            // Loop through pull events until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT) {

                // Get the current tag
                String tagName = xpp.getName();
//                Log.e("TAG NAME is",tagName);

                // React to different event types appropriately
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(KEY_SITE)) {
                            // If we are starting a new <news> block we need
                            //a new NewsItems object to represent it
                                actual_work = true;
                                curNewsItems = new NewsItems();
                        }

                        break;

                    case XmlPullParser.TEXT:
                        //grab the current text so we can use it in END_TAG event
                        curText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagName.equalsIgnoreCase(KEY_SITE) && actual_work) {
                            // if </item> then we are done with current Site
                            // add it to the list.
                            newsItems.add(curNewsItems);
                        } else if (tagName.equalsIgnoreCase(KEY_NAME) && actual_work) {
                            // if </title> use setTitle() on curSite
//                            Log.e("TITLE IS ",curText);
                            curNewsItems.setTitle(curText);

                        } else if (tagName.equalsIgnoreCase(KEY_LINK) && actual_work) {
                            // if </link> use setLink() on curSite
//                            Log.e("LINK IS ",curText);
                            curNewsItems.setLink(curText);
                        } else if (tagName.equalsIgnoreCase(KEY_ABOUT) && actual_work) {
                            // if </description> use setDescription() on curSite
//                            Log.e("DESCRIPTION IS ",curText);
                            curNewsItems.setDescription(curText);
                        }
                            else if (tagName.equalsIgnoreCase(KEY_DATE) && actual_work) {

                            curNewsItems.setDate(curText);
                        }
                        else if (tagName.equalsIgnoreCase(KEY_IMAGE_URL) && actual_work) {
//                            Log.e("IMAGE URL IS  : ",curText);
                            curNewsItems.setImgUrl(curText);
                        }
                        break;

                    default:
                        break;
                }
                //move on to next iteration
                eventType = xpp.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return the populated list.
        return newsItems;
    }
}
