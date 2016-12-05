package com.example.raqib.instadate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class TabInternational extends Fragment {

    RecyclerView myRecyclerView;
    static public List<NewsItems> OneIndiaInternationalNews;
    static public List<NewsItems> TribuneKashmir;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_recycler_view,container,false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        myRecyclerView.hasFixedSize();

        List<NewsItems> Bookmarks = MyNewsRecyclerViewAdapter.bookmarkedNewsList;

//        BingKashmir = SitesXmlPullParserBingKashmir.getStackSitesFromFile(getActivity().getBaseContext());
//        TribuneKashmir = SitesXmlPullParserTribuneKashmir.getStackSitesFromFile(getActivity().getBaseContext());
        OneIndiaInternationalNews = SitesXmlPullParserInternationalNews.getStackSitesFromFile(getActivity().getBaseContext());
//        List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
//            {
//                addAll(OneIndiaInternationalNews);
//                addAll(TribuneKashmir);
//            }
//        };

            myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(OneIndiaInternationalNews));


        return view;
    }


}