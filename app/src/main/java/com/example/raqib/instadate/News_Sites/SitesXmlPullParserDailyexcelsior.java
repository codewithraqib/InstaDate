package com.example.raqib.instadate.News_Sites;

import android.content.Context;
import android.util.Log;

import com.example.raqib.instadate.NewsItems;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Tabassum on 2/26/2017.
 */

public class SitesXmlPullParserDailyexcelsior {
    static final String KEY_SITE = "item";
    static final String KEY_NAME = "title";
    static final String KEY_LINK = "link";
    static final String KEY_ABOUT = "description";
    static final String KEY_DATE = "pubDate";
    static  int end = 1;
    static int depth;


    public static List<NewsItems> getStackSitesFromFile(Context ctx)  {

        // List of StackSites that we will return
        List<NewsItems> newsItems;
        newsItems = new ArrayList<NewsItems>();

        // temp holder for current StackSite while parsing
        NewsItems curNewsItems = null;

        // Temporary Holder for current text value while parsing
        String curText = "";
        String imageAt = "http://www.dailyexcelsior.com/wp-content/uploads/2016/01/de-300x300.png";

        try {
            // Get our factory and PullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // Open up InputStream and Reader of our file.
            FileInputStream fis = ctx.openFileInput("Dailyexcelsior.xml");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
           /* StringBuffer output = new StringBuffer();
            String st;
            while ((st=reader.readLine()) != null) {
                output.append(st);}

                //  String s=convertXmlFileToString("BBCHealth.xml");
            Log.e("XML FILE  of GK IS","THIS::"+output.toString());
            Log.e("XML FILE IS","THIS::"+(output.substring(0,9)));*/
            // point the parser to our file.
            xpp.setInput(reader);

            // get initial eventType
            int eventType = xpp.getEventType();
            Log.e("TAG NAME is","::"+eventType);

            //To get the actual location to start parsing from
            boolean actual_work = false;

            // Loop through pull events until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT) {

                Log.e("TAG NAME is","WHILE");

                // Get the current tag
                String tagName = xpp.getName();
                Log.e("TAG NAME is",":"+tagName);

                // React to different event types appropriately
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(KEY_SITE)) {
                            // If we are starting a new <site> block we need
                            //a new StackSite object to represent it
                            curNewsItems = new NewsItems();
                            Log.e("TAG NAME is","Start_tag"+eventType);
                            actual_work = true;
                        }


                        break;

                    case XmlPullParser.TEXT:
                        //grab the current text so we can use it in END_TAG event
                        curText = xpp.getText();Log.e("TAG NAME is","text"+eventType);
                        break;


                    case XmlPullParser.END_TAG:
                        Log.e("TAG NAME is","end_tag"+eventType);
                        if (tagName.equalsIgnoreCase(KEY_SITE) && actual_work) {
                            // if </item> then we are done with current Site
                            // add it to the list.
                            newsItems.add(curNewsItems);
                        } else if (tagName.equalsIgnoreCase(KEY_NAME) && actual_work) {
                            // if </title> use setTitle() on curSite
                            Log.e("TITLE IS ",curText);
                            curNewsItems.setTitle(curText);

                        } else if (tagName.equalsIgnoreCase(KEY_LINK) && actual_work) {
                            // if </link> use setLink() on curSite
//                            Log.e("LINK IS ",curText);
                            curNewsItems.setLink(curText);
                        } else if (tagName.equalsIgnoreCase(KEY_ABOUT) && actual_work) {
                            // if </description> use setDescription() on curSite
//                            Log.e("DESCRIPTION IS ",curText);
                            // curNewsItems.setDescription(curText);
                            final Pattern pattern = Pattern.compile("<p>(.+?)&#");
                            final Matcher matcher = pattern.matcher(curText);
                            matcher.find();
                            System.out.println(matcher.group(1));
                            Log.e("matcher","1="+matcher.group(1));
                            curNewsItems.setDescription(matcher.group(1));
                        } else if (tagName.equalsIgnoreCase(KEY_DATE) && actual_work) {
                            // if </image> use setImgUrl() on curSite
//                            Log.e("IMAGE URL IS  : ",curText);

                            curNewsItems.setDate(curText);
                            end = 2;
                        }
                        if(end == 2){
                            if (curNewsItems != null) {
                                curNewsItems.setImgUrl(imageAt);
                            }
                        }
                        break;

                    default:
                        Log.e("TAG NAME is","default"+eventType);

                        // skip(xpp);
                        break;
                }
                //move on to next iteration
                // skip(xpp);
                Log.e("EventType is",":"+eventType);
                /*
                if(depth==0&&(eventType==XmlPullParser.TEXT||eventType==XmlPullParser.CDSECT||eventType==XmlPullParser.ENTITY_REF)){

                    throw new NullPointerException();
                }*/
                //    Log.e("valueee","is"+xpp.next());
                eventType = xpp.next();
                Log.e("valueee","next"+eventType);
                // depth=depth+1;
                // Log.e("DEPTH is","VAL"+depth);
            }
        } catch (Exception e) {
            Log.e("exceptiom", "is"+e);
            e.printStackTrace();
        }

        // return the populated list.
        return newsItems;
    }

    private static String convertXmlFileToString(String fis) {
        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            InputStream inputStream = new FileInputStream(new File(fis));
            org.w3c.dom.Document doc = documentBuilderFactory.newDocumentBuilder().parse(inputStream);
            StringWriter stw = new StringWriter();
            Transformer serializer = TransformerFactory.newInstance().newTransformer();
            serializer.transform(new DOMSource(doc), new StreamResult(stw));
            return stw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();

            Log.e("convert","string:::"+e);
        }
        return null;
    }

    private static void skip(XmlPullParser xpp) {
        try {
            Log.e("inside", "skip");
            if (xpp.getEventType() != XmlPullParser.START_TAG) {
                //throw new IllegalStateException();


            }
            int depth = 1;
            while (depth != 0) {
                Log.e("inside", "skip while");
                switch (xpp.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
            Log.e("after ", "skip");
        } catch (Exception e) {
            Log.e("excep in skip", "is" + e);
        }
    }
}
