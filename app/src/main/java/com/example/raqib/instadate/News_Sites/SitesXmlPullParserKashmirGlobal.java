package com.example.raqib.instadate.News_Sites;

import android.content.Context;
import android.util.Log;

import com.example.raqib.instadate.NewsItems;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tabassum on 2/26/2017.
 */

public class SitesXmlPullParserKashmirGlobal {
    static final String KEY_SITE = "item";
    static final String KEY_NAME = "title";
    static final String KEY_LINK = "link";
    static final String KEY_ABOUT = "description";
    static final String KEY_DATE = "pubDate";
    static  int end = 1;
    static String COMMENT="<!--";
    // static int cmnttag=Integer.parseInt(COMMENT);
    public static List<NewsItems> getStackSitesFromFile(Context ctx) {

        // List of StackSites that we will return
        List<NewsItems> newsItems;
        newsItems = new ArrayList<NewsItems>();

        // temp holder for current StackSite while parsing
        NewsItems curNewsItems = null;

        // Temporary Holder for current text value while parsing
        String curText = "";
        String imageAt = "https://pbs.twimg.com/profile_images/477489632762789888/ZKEsjGbl_400x400.png";

        try {
            // Get our factory and PullParser
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();

            // Open up InputStream and Reader of our file.
            FileInputStream fis = ctx.openFileInput("KashmirGlobal.xml");
            Log.e("Global","kashmir"+newsItems.toString());
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
            // xpp.nextToken();

            // get initial eventType
            int eventType = xpp.getEventType();

            //To get the actual location to start parsing from
            boolean actual_work = false;
            //  Log.e("inside eventype gk","now :"+eventType);
            //
            // Loop through pull events until we reach END_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Log.e("End document","default"+XmlPullParser.END_DOCUMENT);
                // Get the current tag
                String tagName = xpp.getName();
//            Log.e("TAG NAME is",tagName);
                //   Log.e("tag name gk","is"+tagName);
                //  Log.e("end document","def:"+XmlPullParser.START_TAG);
                //  Log.e("start tag","defa:"+XmlPullParser.START_TAG);
                // Log.e("text","defa:"+XmlPullParser.TEXT);
                //Log.e("End tag","defa:"+XmlPullParser.END_TAG);
                //  React to different event types appropriately
                switch (eventType)
                {

                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase(KEY_SITE)) {
                            // If we are starting a new <site> block we need
                            //a new StackSite object to represent it
                            curNewsItems = new NewsItems();
                            actual_work = true;
                        }
                        break;
                    case XmlPullParser.COMMENT:
                        actual_work=true;
                        // Log.e("comment","is"+XmlPullParser.COMMENT);
                        break;
                    case XmlPullParser.TEXT:
                        //grab the current text so we can use it in END_TAG event
                        curText = xpp.getText();
                        Log.e("current","text"+curText);
                        break;

                    case XmlPullParser.END_TAG:
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
                            Log.e("LINK IS ",curText);
                            curNewsItems.setLink(curText);
                        } else if (tagName.equalsIgnoreCase(KEY_ABOUT) && actual_work) {
                            // if </description> use setDescription() on curSite
                            // Log.e("DESCRIPTION IS ",curText);
                            final Pattern pattern = Pattern.compile("<p>(.+?)<a");
                            final Matcher matcher = pattern.matcher(curText);
                            matcher.find();
                            System.out.println(matcher.group(1));
                            // Log.e("matcher","1="+matcher.group(1));
                            String des=matcher.group(1);
                            int n= des.length();
                            String descript=des.substring(0,n-9);
                            // final Pattern pattern1 = Pattern.compile("<a>(.+?)</a>");
                            //  final Matcher matcher1 = pattern1.matcher(curText);
                            // matcher1.find();
                            //System.out.println(matcher.group(2));
                            // Log.e("matcjher","2"+matcher1.group(2));
                            curNewsItems.setDescription(descript);
                            //  curNewsItems.setDescription(matcher.group(1));
                            // curNewsItems.setDescription(curText);
                        } else if (tagName.equalsIgnoreCase(KEY_DATE) && actual_work) {
                            // if </image> use setImgUrl() on curSite
                            Log.e("IMAGE DATE IS  : ", curText);

                            curNewsItems.setDate(curText);
                            Log.e("date ", "is" + curText);
                            end = 2;

                            if (end == 2 && curNewsItems != null) {
                                Log.e("newsitem", "empty::" + curNewsItems);
                                curNewsItems.setImgUrl(imageAt);
                            }
                        }
                        break;


                    default:
                        Log.e("Even type","default"+eventType);
                        break;
                }Log.e("switch ended","value"+eventType);


                //   Log.e("incr ","event::"+xpp.getEventType());
                //move on to next iteration

                eventType = xpp.next();
                Log.e("Even type","increased"+eventType);
                // Log.e("Even type","tagname"+xpp.getText());
            }
        } catch (Exception e) {
            Log.e("unexpected","token::"+e);
            e.printStackTrace();
        }
//Log.e("current","is"+newsItems);
        // return the populated list.
        return newsItems;
    }
}
