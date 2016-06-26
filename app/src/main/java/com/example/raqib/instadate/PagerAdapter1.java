package com.example.raqib.instadate;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PagerAdapter1 extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter1(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TabTopStories tab1 = new TabTopStories();
                return tab1;
            case 1:
                TabScience tab2 = new TabScience();
                return tab2;
            case 2:
                TabTechnology tab3 = new TabTechnology();
                return tab3;
            case 3:
                TabSports tab4 = new TabSports();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        Log.e("No Of TABS", String.valueOf(mNumOfTabs));
        return mNumOfTabs;
    }
}