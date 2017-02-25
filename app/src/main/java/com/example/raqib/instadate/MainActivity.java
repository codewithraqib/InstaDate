package com.example.raqib.instadate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserBBCHealth;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserBBCTechnology;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserInternationalNews;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserNYT;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserNYTTechnology;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserRediffSports;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserScience2;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserScienceDaily;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserTheFinancialExpress;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserWorldSports;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;



@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    int uiOptions = 0;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private static final String TAG = "Main Activity";
    private FirebaseAuth mAuth;
    ListView searchListView;
    ArrayAdapter arrayAdapter;
    static List<NewsItems> newsItemsList;
    static List<String> listToSearch;
    static List<NewsItems> searchNewsItems;
    MaterialSearchView searchView;
    static int locationOfSearchedItem = 0;
    boolean doubleBackToExitPressedOnce = false;
    ImageButton shareButton;
    DrawerLayout myDrawerLayout;
    static  int i = 0;
    RecyclerView mRecyclerView;
    ImageView floatingButton;
    static boolean searchOpened = false;
    static boolean itIsTheFirstTimeInMainActivity = true;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ViewPager viewPager2 = (ViewPager) findViewById(R.id.pager);
        assert viewPager2 != null;
        viewPager2.setCurrentItem(0);

        floatingButton = (ImageView) findViewById(R.id.floatingButton);
        floatingButton.setVisibility(View.INVISIBLE);


        createListOfNews();

        final RelativeLayout mainNewsLinearLayout = (RelativeLayout) findViewById(R.id.mainNewsLinearLayout);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);

        //SETTING THE LIST FOR SEARCH
        searchListView = (ListView) findViewById(R.id.searchListView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listToSearch);
        searchListView.setAdapter(arrayAdapter);


        //MATERIAL SEARCH VIEW IMPLEMENTATION
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener(){

            @Override
            public void onSearchViewShown() {
                searchListView.setVisibility(View.VISIBLE);
                mainNewsLinearLayout.setVisibility(View.INVISIBLE);
                searchOpened = true;

            }

            @Override
            public void onSearchViewClosed() {

                searchListView = (ListView) findViewById(R.id.searchListView);
                arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,listToSearch);
                searchListView.setAdapter(arrayAdapter);
                searchListView.setVisibility(View.INVISIBLE);
                mainNewsLinearLayout.setVisibility(View.VISIBLE);


            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //WE DON'T NEED TO USE THIS AS WE USE ON TEXT CHANGE METHOD BELOW
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){

                    String newTextInLowerCase = newText.toLowerCase();
                    List<String> listFound = new ArrayList<String>();
                    for(String item:listToSearch){
                        if(item.toLowerCase().contains(newTextInLowerCase))
                            listFound.add(item);
                    }
                    arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,listFound);
                    searchListView.setAdapter(arrayAdapter);
                }else{
                    //IF SEARCH QUERY DOESN'T MATCH
                    //RETURN DEFAULT
                    arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,listToSearch);
                    searchListView.setAdapter(arrayAdapter);
                }
                return  true;
            }
        });

        mAuth = FirebaseAuth.getInstance();


        //noinspection ConstantConditions
        getSupportActionBar().isHideOnContentScrollEnabled();

        // SWIPE DOWN TO REFRESH IMPLEMENTATION
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshInMain);

        assert mySwipeRefreshLayout != null;
        mySwipeRefreshLayout.setDistanceToTriggerSync(350);
        mySwipeRefreshLayout.stopNestedScroll();

            mySwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
        // METHOD WHICH IS CALLED WHEN THE USER IS ALREADY ON THE TOP AND SWIPES DOWN TO REFRESH THE DATA
        @Override
        public void onRefresh(){
            if (isNetworkAvailable()) {
                SitesDownloadTask download = new SitesDownloadTask();
                download.execute();
            }
            else{
                mySwipeRefreshLayout.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(),"You Don't Have An Active Internet Connection, Please Connect With Internet And Try Again!", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }


    });


        //SETTING LEFT NAVIGATION DRAWER
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        //TO HIDE STATUS BAR AND ACTION BAR
//        View decorView = getWindow().getDecorView();
//        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//      getSupportActionBar().hide();

        //NEW IMPLEMENTATION OF SWIPE TABS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabHost);

        assert tabLayout != null;
        Customization.sharedPreferences = this.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);


        //SETTING ALL THE TABS FOR THE FIRST TIME THEN AFTERWARDS DEPENDS ON USER CHOICE

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {

            // run your one time code
            Customization.sharedPreferences.edit().putBoolean("topFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("scienceFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("technologyFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("sportsFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("healthFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("internationalFeeds", true).apply();
            Customization.sharedPreferences.edit().putBoolean("localFeeds", true).apply();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }


        tabLayout.addTab(tabLayout.newTab().setText("Top National"));
        if(Customization.sharedPreferences.getBoolean("scienceFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Science"));
        if(Customization.sharedPreferences.getBoolean("technologyFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        if(Customization.sharedPreferences.getBoolean("sportsFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        if(Customization.sharedPreferences.getBoolean("healthFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Health"));
        if(Customization.sharedPreferences.getBoolean("localFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("International"));
        tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        assert viewPager != null;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mRecyclerView = (RecyclerView)  findViewById(R.id.my_recycler_view);
                mRecyclerView.smoothScrollToPosition(0);

            }
        });
                //TO HIDE THE APP BAR AND STATUS BAR ON SWIPING UP AND DOWN THE RECYCLER VIEW

        viewPager.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                int dy = i1 - i3;
                if(dy > 5){
                    getSupportActionBar().hide();
                    View decorView = getWindow().getDecorView();
                    uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);

                }else if(dy < - 5) {
                    getSupportActionBar().show();
                    int uiOptionsNormalScreen = 0;
                    View decorView = getWindow().getDecorView();
                    decorView.setSystemUiVisibility(uiOptionsNormalScreen);
                }
            }
        });
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            viewPager.setOnScrollChangeListener(new ViewPager.OnScrollChangeListener() {
//                @Override
//                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                  int dy = scrollY - oldScrollY;
//                    if(dy > 5){
//                        getSupportActionBar().hide();
//                        View decorView = getWindow().getDecorView();
//                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                        decorView.setSystemUiVisibility(uiOptions);
//
//                    }else if(dy < - 5) {
//                        getSupportActionBar().show();
//                        int uiOptionsNormalScreen = 0;
//                        View decorView = getWindow().getDecorView();
//                        decorView.setSystemUiVisibility(uiOptionsNormalScreen);
//                    }
//                }
//            });
//        }



        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"You Don't Have An Active Internet Connection, Please Connect With Internet And Try Again!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Snackbar snackbar = Snackbar.make( myDrawerLayout,"You Don't Have An Active Internet Connection, Please Connect With Internet And Try Again!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKAY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.show();
        }


        try{
            mRecyclerView = (RecyclerView)  findViewById(R.id.my_recycler_view);
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                    if(dy > 80){

                        getSupportActionBar().hide();
//                        View decorView = getWindow().getDecorView();
//                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                        decorView.setSystemUiVisibility(uiOptions);

                    }else if(dy < - 80) {
                        getSupportActionBar().show();
//                        int uiOptionsNormalScreen = 0;
//                        View decorView = getWindow().getDecorView();
//                        decorView.setSystemUiVisibility(uiOptionsNormalScreen);
                    }

                    super.onScrolled(recyclerView, dx, dy);
                }
            });

        }catch (NullPointerException e){
            Log.e("RecyclerViewScrollMain", String.valueOf(e));
//            Toast toast =Toast.makeText(this, "There is Some Error In Scrolling Effect", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
//            toast.show();
        }

    }

    //CREATE THE LIST OF ALL AVAILABLE NEWS AT THE MOMENT
    private void createListOfNews() {

        final List<NewsItems> list1 = SitesXmlPullParserBBCHealth.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list2 = SitesXmlPullParserBBCTechnology.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list3 = SitesXmlPullParserInternationalNews.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list4 = SitesXmlPullParserNYT.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list5 = SitesXmlPullParserNYTTechnology.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list6 = SitesXmlPullParserRediffSports.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list7 = SitesXmlPullParserScience2.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list8 = SitesXmlPullParserScienceDaily.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list9 = SitesXmlPullParserTheFinancialExpress.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list10 = SitesXmlPullParserWorldSports.getStackSitesFromFile(getBaseContext());

        newsItemsList = new ArrayList<NewsItems>(){
            {
                addAll(list1);
                addAll(list2);
                addAll(list3);
                addAll(list4);
                addAll(list5);
                addAll(list6);
                addAll(list7);
                addAll(list8);
                addAll(list9);
                addAll(list10);
            }
        };


        listToSearch = new ArrayList<>();

        for(int i =0; i< newsItemsList.size(); i++){
            listToSearch.add(i,newsItemsList.get(i).getTitle());
        }

//        //SUCCESSFULLY GOT THE LIST
//        for(int i =0; i< listToSearch.size(); i++){
//            Log.e("Title of Items "+ i, String.valueOf(listToSearch.get(i)));
//        }

    }


    //MENU OPTIONS OVERRIDDEN
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    public void goForSearchInNewsList(MenuItem item) {
        final ListView list = (ListView) findViewById(R.id.searchListView);
        searchOpened = true;

        //WANT TO HIDE THE LIST WHEN THE BACK BUTTON IS PRESSED: BELOW LOGIC IS NOT WORKING
        searchView.setMenuItem(item);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("indexOfSearchResult ", " " + position);
                int flag =0;

                String stringToCheckInList = list.getItemAtPosition(position).toString();

                Log.e("Outside for loop ", stringToCheckInList);

                for(int i =0; i<newsItemsList.size(); i++){
                    if(stringToCheckInList.equalsIgnoreCase(newsItemsList.get(i).getTitle())){
                        locationOfSearchedItem = i;
                        Log.e("stringToCheckInList ", stringToCheckInList);
                        Log.e("newsItemsList Title  ", newsItemsList.get(i).getTitle());
                        flag = 1;

                    }
                    if (flag == 1)
                    break;

                }
                Log.e("Title is  ", String.valueOf(list.getItemAtPosition(position)));
                populateTheSearchResult(locationOfSearchedItem);

            }
        });
    }


    private void populateTheSearchResult(int searchResultPosition){

        NewsItems curNewsItems = new NewsItems();

        searchNewsItems = new ArrayList<NewsItems>();

        String searchTitle = newsItemsList.get(searchResultPosition).getTitle();
        String searchDescription = newsItemsList.get(searchResultPosition).getDescription();
        String searchLink = newsItemsList.get(searchResultPosition).getLink();
        String searchImageUrl = newsItemsList.get(searchResultPosition).getImgUrl();
        String searchDate = newsItemsList.get(searchResultPosition).getDate();

        curNewsItems.setTitle(searchTitle);
        curNewsItems.setDescription(searchDescription);
        curNewsItems.setLink(searchLink);
        curNewsItems.setImgUrl(searchImageUrl);
        curNewsItems.setDate(searchDate);
        searchNewsItems.add(curNewsItems);

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("position", searchResultPosition);
        startActivity(intent);

    }

    public void RegisterUser(MenuItem item) {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }


    //EXIT THE APP USING MENU OPTION
    public void closeApp(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do You want to exit :( ?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.this.finish();
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


    private void displayNews() {
        // thinking
    }

    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    //BACK PRESS
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed(); return;
        }
        this.doubleBackToExitPressedOnce = true;

        final ListView list = (ListView) findViewById(R.id.searchListView);
        RelativeLayout mainNewsLinearLayout = (RelativeLayout) findViewById(R.id.mainNewsLinearLayout);

        if(searchOpened){
            list.setVisibility(View.INVISIBLE);
            mainNewsLinearLayout.setVisibility(View.VISIBLE);
            searchView.closeSearch();
            searchOpened = false;
        }
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                    doubleBackToExitPressedOnce=false;
            }
            }, 1000);

    }


    //NAVIGATION DRAWER
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_top_feeds) {

            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            assert viewPager != null;
            viewPager.setCurrentItem(0);
            Toast.makeText(getApplicationContext(), "We Are In Top Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_science) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            assert viewPager != null;
            viewPager.setCurrentItem(1);
            Toast.makeText(getApplicationContext(), "We Are In Science Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_national) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            assert viewPager != null;
            viewPager.setCurrentItem(5);
            Toast.makeText(getApplicationContext(), "We Are In National Feeds Drawer", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_international) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            assert viewPager != null;
            viewPager.setCurrentItem(2);
            Toast.makeText(getApplicationContext(), "We Are In International Feeds Drawer", Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_health) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            assert viewPager != null;
            viewPager.setCurrentItem(4);
            Toast.makeText(getApplicationContext(), "We Are In Health Feeds Drawer", Toast.LENGTH_LONG).show();

        }else if (id == R.id.nav_sports) {
            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myDrawerLayout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void goToLoginActivity(View view) {

        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }


    public void goToCustomizationActivity(MenuItem item) {
        startActivity(new Intent(MainActivity.this, Customization.class));
        this.finish();
    }
    public void openRightDrawer(MenuItem item) {

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);

        if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            mDrawerLayout.openDrawer(GravityCompat.END);


    }

    //TO REFRESH THE NEWS FEED
//    public void refreshFeed(MenuItem item) {
//        if (isNetworkAvailable()) {
//            SitesDownloadTask download = new SitesDownloadTask();
//            download.execute();
//        }
//        else{
//            Toast toast = Toast.makeText(getApplicationContext(),"You Don't Have An Active Internet Connection, Please Connect With Internet And Try Again!", Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER,0,0);
//            toast.show();
//        }
//    }


    //SIMPLE LOGOUT API
    public void logOutCurrentUser(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Toast.makeText(MainActivity.this, "You Are Already Logged Out!", Toast.LENGTH_SHORT).show();
        }else{
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(MainActivity.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
        }

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

                Downloader.DownloadFromUrl("http://feeds.bbci.co.uk/news/technology/rss.xml", openFileOutput("BBCTechnology.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://feeds.bbci.co.uk/news/health/rss.xml", openFileOutput("BBCHealth.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://rss.nytimes.com/services/xml/rss/nyt/Technology.xml", openFileOutput("NYTTechnology.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.rediff.com/rss/sportsrss.xml", openFileOutput("RediffSports.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.tribuneindia.com/rss/feed.aspx?cat_id=5", openFileOutput("TribuneKashmir.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.bing.com/news/search?q=Kashmir&qs=n&form=QBNT&pq=Kashmir&sc=0-0&sp=-1&sk=&format=RSS", openFileOutput("BingKashmir.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/feed/", openFileOutput("TFE.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/industry/tech/feed/", openFileOutput("TFETech.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("https://rss.sciencedaily.com/computers_math.xml", openFileOutput("ScienceDaily.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/health/feed/", openFileOutput("HealthService.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/sports/feed/", openFileOutput("WorldSports.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/science/feed/", openFileOutput("Science2.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.oneindia.com/rss/news-international-fb.xml", openFileOutput("InternationalNews.xml", Context.MODE_PRIVATE));

            } catch (FileNotFoundException e) {
                Log.e("ERROR at DoInBackground", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mySwipeRefreshLayout.setRefreshing(false);
            Snackbar snackbar = Snackbar.make( myDrawerLayout,"Your Feeds Has Been Successfully Updated!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OKAY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

            floatingButton.setVisibility(View.VISIBLE);
            floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingButton.setVisibility(View.INVISIBLE);
                try{
                    mRecyclerView = (RecyclerView)  findViewById(R.id.my_recycler_view);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView
                            .getLayoutManager();

                    layoutManager.setSmoothScrollbarEnabled(true);
//                    layoutManager.scrollToPositionWithOffset(0, 0);
                    mRecyclerView.smoothScrollToPosition(0);

//                    mRecyclerView.setOnScrollListener();

                }catch (NullPointerException NPE){
                    Log.e("At PostExecuteOFMain", String.valueOf(NPE));
                }
            }
        });


//            mRecyclerView = (RecyclerView)  findViewById(R.id.my_recycler_view);
//            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                    if(dy > 20){
//
//                        getSupportActionBar().hide();
////                        View decorView = getWindow().getDecorView();
////                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
////                        decorView.setSystemUiVisibility(uiOptions);
//
//                    }else if(dy < - 20) {
//                        getSupportActionBar().show();
////                        int uiOptionsNormalScreen = 0;
////                        View decorView = getWindow().getDecorView();
////                        decorView.setSystemUiVisibility(uiOptionsNormalScreen);
//                    }
//
//                    super.onScrolled(recyclerView, dx, dy);
//                }
//            });
        }
    }

}
