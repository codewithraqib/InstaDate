package com.example.raqib.instadate;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserScience2;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserScienceDaily;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TabScience extends Fragment {

    RecyclerView myRecyclerView;
    static public List<NewsItems> ScienceDaily;
    static public List<NewsItems> Science2;
    static  public List<NewsItems> newsItemsList;
    ActionBar actionBar;




    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_recycler_view,container, false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        myRecyclerView.hasFixedSize();

        ScienceDaily = SitesXmlPullParserScienceDaily.getStackSitesFromFile(getActivity().getBaseContext());
        Science2 = SitesXmlPullParserScience2.getStackSitesFromFile(getActivity().getBaseContext());

        ScienceDaily = setTimeZone(ScienceDaily);
        Science2 = setTimeZone(Science2);

//        FOR DIFFERENT CHANNELS
        newsItemsList = new ArrayList<NewsItems>(){
            {

                addAll(Science2);
                addAll(ScienceDaily);
            }
        };

        Collections.sort(newsItemsList, new Comparator<NewsItems>() {

            Date first = null, second = null;

            @Override
            public int compare(NewsItems newsItems, NewsItems t1) {
//
                first = new Date(newsItems.getDate());
                second = new Date(t1.getDate());
                if (first == null || second == null)
                    return 0;
                return second.compareTo(first);
            }
        });

        myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(newsItemsList));

        return view;
    }

    private List<NewsItems> setTimeZone(List<NewsItems> scienceDaily) {


        Date date = null;
        DateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        for(int i = 0 ; i < scienceDaily.size(); i++){

            try {
                date = format.parse(scienceDaily.get(i).getDate());
            } catch (Exception e) {
                Log.e("Exception while parsing", e.toString());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String firstDate = sdf.format(date);
            scienceDaily.get(i).setDate(firstDate);
            scienceDaily.set(i, scienceDaily.get(i));
        }
        return scienceDaily;

    }

}