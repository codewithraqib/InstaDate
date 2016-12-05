package com.example.raqib.instadate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.List;

public class GeneralNavigationTab extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    RecyclerView recyclerView;
    public List<NewsItems> BookmarkedFeeds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_navigation_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInGeneralNavigationTab);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.general_navigation_tab_recycler_view);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

//        BookmarkedFeeds = SitesXmlPullParserWorldSports.getStackSitesFromFile(this);
//        BookmarkedFeeds = MyNewsRecyclerViewAdapter.bookmarkedNewsList;

        recyclerView.setAdapter(new RecyclerAdapter());

        //SETTING LEFT NAVIGATION DRAWER TOGGLE BUTTON
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myGeneralNavigationLinearLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_in_general_tab);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        NavigationView navigationViewRight = (NavigationView) findViewById(R.id.nav_view_in_general_tab_right);
        assert navigationViewRight != null;
        navigationViewRight.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myGeneralNavigationLinearLayout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //MENU ITEM ON CLICK LISTENERS
    public void RegisterUser(MenuItem item) {
        startActivity(new Intent(GeneralNavigationTab.this, RegisterActivity.class));
    }

    public void goToLoginActivity(View view) {

        startActivity(new Intent(GeneralNavigationTab.this, LoginActivity.class));
    }

    public void goToCustomizationActivity(MenuItem item) {
        startActivity(new Intent(GeneralNavigationTab.this, Customization.class));
        this.finish();
    }

    public void refreshFeed(MenuItem item) {
        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"You Don't Have An Active Internet Connection, Please Coneect With Internet And Try Again!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }
    public void closeApp(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(GeneralNavigationTab.this);
        builder.setMessage("Do You want to exit :( ?");
        builder.setCancelable(false);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GeneralNavigationTab.this.finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {

//        ProgressDialog pdl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //DOWNLOAD THE FILES FROM INTERNET
        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                Downloader.DownloadFromUrl("http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml", openFileOutput("NYTNews.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.espncricinfo.com/rss/content/story/feeds/6.xml", openFileOutput("EspnCricinfo.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.tribuneindia.com/rss/feed.aspx?cat_id=5", openFileOutput("TribuneKashmir.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.bing.com/news/search?q=Kashmir&qs=n&form=QBNT&pq=Kashmir&sc=0-0&sp=-1&sk=&format=RSS", openFileOutput("BingKashmir.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/feed/", openFileOutput("TFE.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/industry/tech/feed/", openFileOutput("TFETech.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("https://rss.sciencedaily.com/computers_math.xml", openFileOutput("ScienceDaily.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/health/feed/", openFileOutput("HealthService.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/sports/feed/", openFileOutput("WorldSports.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/science/feed/", openFileOutput("Science2.xml", Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                Log.e("ERROR at DoInBackground", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast toast = Toast.makeText(GeneralNavigationTab.this, "Your Feeds Has Been Successfully Updated!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

        }

    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_feeds) {

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(0);
            Toast.makeText(getApplicationContext(), "We Are In Top Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_science) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(1);
            Toast.makeText(getApplicationContext(), "We Are In Science Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_national) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(5);
            Toast.makeText(getApplicationContext(), "We Are In National Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_international) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(2);
            Toast.makeText(getApplicationContext(), "We Are In International Feeds Drawer", Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_health) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(4);
            Toast.makeText(getApplicationContext(), "We Are In Health Feeds Drawer", Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_sports) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pagerInGeneralNavigationTab);
            assert viewPager != null;
            viewPager.setCurrentItem(3);
            Toast.makeText(getApplicationContext(), "We Are In Sports Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_bookmarked_feeds) {
            startActivity(new Intent(getApplicationContext(), GeneralNavigationTab.class));
            Toast.makeText(getApplicationContext(), "We Are In Sports Feeds Drawer", Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_share) {

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.playstore.com/InstaDate");
            sendIntent.setType("text/plain");

            //TO GET A LIST OF APPS THAT CAN HANDLE THE PARTICULAR INTENT
            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            if(isIntentSafe) {
                startActivity(sendIntent);
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myGeneralNavigationLinearLayout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
