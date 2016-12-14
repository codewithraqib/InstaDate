package com.example.raqib.instadate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserBBCHealth;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserHealth;

import java.util.ArrayList;
import java.util.List;

public class TabHealth extends Fragment {

    RecyclerView myRecyclerView;
    static public List<NewsItems> HealthService;
    static public List<NewsItems> BBCHealth;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.general_recycler_view,container, false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        myRecyclerView.hasFixedSize();

        HealthService = SitesXmlPullParserHealth.getStackSitesFromFile(getActivity().getBaseContext());
        BBCHealth = SitesXmlPullParserBBCHealth.getStackSitesFromFile(getActivity().getBaseContext());
        //FOR DIFFERENT CHANNELS
        List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
            {
                addAll(BBCHealth);
                addAll(HealthService);
            }
        };

        myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(newsItemsList));
        return view;
    }

}