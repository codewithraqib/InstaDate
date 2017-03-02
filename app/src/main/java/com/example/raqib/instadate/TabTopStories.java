package com.example.raqib.instadate;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserNYT;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserTheFinancialExpress;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class TabTopStories extends Fragment {

    RecyclerView myRecyclerView;
    static public List<NewsItems> NYT;
    static public List<NewsItems> TFE;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_recycler_view,container,false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        myRecyclerView.hasFixedSize();

        NYT = SitesXmlPullParserNYT.getStackSitesFromFile(getActivity().getBaseContext());
        TFE = SitesXmlPullParserTheFinancialExpress.getStackSitesFromFile(getActivity().getBaseContext());

        TFE = setTimeZone(TFE);
        NYT = setTimeZone(NYT);


        List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
            {

                addAll(TFE);
                addAll(NYT);

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

    private List<NewsItems> setTimeZone(List<NewsItems> listReference) {


        Date date = null;
        DateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        for(int i = 0 ; i < listReference.size(); i++){

            try {
                date = format.parse(listReference.get(i).getDate());
            } catch (Exception e) {
                Log.e("Exception while parsing", e.toString());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd, MMM yyyy HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
            String firstDate = sdf.format(date);
            listReference.get(i).setDate(firstDate);
            listReference.set(i, listReference.get(i));
        }
        return listReference;

    }


}