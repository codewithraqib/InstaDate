package com.example.raqib.instadate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserEspnCricinfo;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserWorldSports;

import java.util.ArrayList;
import java.util.List;


public class TabSports extends Fragment {

    RecyclerView myRecyclerView;
    public List<NewsItems> NYT;
    static public List<NewsItems> ESPN;
    static public List<NewsItems> WorldSports;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.general_recycler_view,container,false);
        myRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        myRecyclerView.hasFixedSize();

        ESPN = SitesXmlPullParserEspnCricinfo.getStackSitesFromFile(getActivity().getBaseContext());
        WorldSports = SitesXmlPullParserWorldSports.getStackSitesFromFile(getActivity().getBaseContext());
        List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
            {

                addAll(WorldSports);
                addAll(ESPN);
            }
        };

            myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(newsItemsList));


        return view;
    }


}