package com.example.raqib.instadate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.raqib.instadate.News_Sites.SitesXmlPullParserBBCHealth;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserBBCTechnology;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserNYT;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserNYTTechnology;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserRediffSports;
import com.example.raqib.instadate.News_Sites.SitesXmlPullParserTheFinancialExpress;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Resumed...");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#263238"));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));

        createListOfNews();




        final LinearLayout mainNewsLinearLayout = (LinearLayout) findViewById(R.id.mainNewsLinearLayout);

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
                        if(item.toLowerCase().contains(newText))
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myLinearLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);


        //SETTING RIGHT NAVIGATION DRAWER
//        DrawerLayout rightDrawer = (DrawerLayout) findViewById(R.id.myLinearLayout2);
//        ActionBarDrawerToggle rightToggle = new ActionBarDrawerToggle(
//                this, rightDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        assert rightDrawer != null;
//        rightDrawer.setDrawerListener(rightToggle);
//        rightToggle.syncState();
//
//        NavigationView rightNavigationView = (NavigationView) findViewById(R.id.right_nav_view);
//        assert rightNavigationView != null;
//        rightNavigationView.setNavigationItemSelectedListener(this);

        //SETTING BACKENDLESS




        //SETTING NAME AND EMAIL IN DRAWER
        // UserTokenStorageFactory is available in the com.backendless.persistence.local package


        //TO HIDE STATUS BAR AND ACTION BAR
//        View decorView = getWindow().getDecorView();
//        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//      getSupportActionBar().hide();

        //NEW IMPLEMENTATION OF SWIPE TABS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabHost);

        assert tabLayout != null;
        Customization.sharedPreferences = this.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);

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
            }
        });
                //TO HIDE THE APP BAR AND STATUS BAR ON SWIPING UP AND DOWN THE RECYCLER VIEW
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewPager.setOnScrollChangeListener(new ViewPager.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                  int dy = scrollY - oldScrollY;
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
        }
//        myViewPager.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (scroll_down) {
//                    actionBar.hide();
//                } else {
//                    actionBar.show();
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 5) {
//                    //scroll down
//                    scroll_down = true;
//
//                } else if (dy < -5) {
//                    //scroll up
//                    scroll_down = false;
//                }
//            }
//        });

//        displayNews();

        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        } else {
            displayNews();
        }
    }

    //CREATE THE LIST OF ALL AVAILABLE NEWS AT THE MOMENT
    private void createListOfNews() {

        final List<NewsItems> list1 = SitesXmlPullParserBBCHealth.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list2 = SitesXmlPullParserBBCTechnology.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list3 = SitesXmlPullParserNYT.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list4 = SitesXmlPullParserNYTTechnology.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list5 = SitesXmlPullParserRediffSports.getStackSitesFromFile(getBaseContext());
        final List<NewsItems> list6 = SitesXmlPullParserTheFinancialExpress.getStackSitesFromFile(getBaseContext());

        newsItemsList = new ArrayList<NewsItems>(){
            {
                addAll(list1);
                addAll(list2);
                addAll(list3);
                addAll(list4);
                addAll(list5);
                addAll(list6);
            }
        };

        //SUCCESSFULLY GOT THE LIST
//        for(int i =0; i< newsItemsList.size(); i++){
//            Log.e("Title of Item "+ i, String.valueOf(newsItemsList.get(i).getTitle()));
//
//        }

        listToSearch = new ArrayList<>();

        for(int i =0; i< newsItemsList.size(); i++){
            listToSearch.add(i,newsItemsList.get(i).getTitle());
        }

        //SUCCESSFULLY GOT THE LIST
        for(int i =0; i< listToSearch.size(); i++){
            Log.e("Title of Items "+ i, String.valueOf(listToSearch.get(i)));
        }

    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
//    }

    //MENU OPTIONS OVERRIDDEN
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.options_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.searchInMain);
//        searchView.setMenuItem(menuItem);
        return true;
    }

    public void goForSearchInNewsList(MenuItem item) {
        final ListView list = (ListView) findViewById(R.id.searchListView);
        LinearLayout mainNewsLinearLayout = (LinearLayout) findViewById(R.id.mainNewsLinearLayout);


        //WANT TO HIDE THE LIST WHEN THE BACK BUTTON IS PRESSED: BELOW LOGIC IS NOT WORKING
//        boolean isSearchOpen = searchView.isSearchOpen();
//        if(!isSearchOpen)
//            list.setVisibility(View.INVISIBLE);
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
//        Log.e("SearchTitle is ",searchTitle);
//        Log.e("SearchDescription is ",searchDescription);
//        Log.e("SearchLink is ",searchLink);
//        Log.e("SearchImageUrl is ",searchImageUrl);
//        Log.e("SearchDate is ",searchDate);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myLinearLayout);
        ListView list = (ListView) findViewById(R.id.searchListView);
        LinearLayout mainNewsLinearLayout = (LinearLayout) findViewById(R.id.mainNewsLinearLayout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        //CODE FOR GOING BACK FROM SEARCH VIEW
        int listVisibility = list.getVisibility();
        int mainNewsLinearLayoutVisibility = mainNewsLinearLayout.getVisibility();

        if(listVisibility > 0 && mainNewsLinearLayoutVisibility ==0){
            list.setVisibility(View.INVISIBLE);
            mainNewsLinearLayout.setVisibility(View.VISIBLE);
        }
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myLinearLayout);
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

    //TO REFRESH THE NEWS FEED
    public void refreshFeed(MenuItem item) {
        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),"You Don't Have An Active Internet Connection, Please Connect With Internet And Try Again!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }


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
            Toast toast = Toast.makeText(MainActivity.this, "Your Feeds Has Been Successfully Updated!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();

        }

    }

    // BELOW CODE IS IMPLEMENTED TO FREEZE THE SINGLE BACK BUTTON PRESS TO EXIT THE APP AND
    // TO MAKE THE APP TO EXIT ON LONG BACK BUTTON PRESS ONLY.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myLinearLayout);

            /**
             * Retrieve the repeat count of the event.  For both key up and key down
             * events, this is the number of times the key has repeated with the first
             * down starting at 0 and counting up from there.  For multiple key
             * events, this is the number of down/up pairs that have occurred.
             *
             * @return The number of times the key has repeated.
             */

            if ((event.getRepeatCount() != 0)) {
                this.finish();
                Log.e("BACK PRESSED IN MAIN 2C", "Successfully");
            }else {
                assert drawer != null;
                if (drawer.isDrawerOpen(GravityCompat.START) && (event.getRepeatCount() == 0)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    // THEN OBVIOUSLY THE KEY EVENT WAS THE BACK BUTTON AND THE DRAWER IS CLOSED
                    Toast toast = Toast.makeText(getApplicationContext(), "Long Press Back Button To Exit The App", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CLIP_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }



        // SYSTEM BEHAVIOR (PROBABLY EXIT THE ACTIVITY WITH A SINGLE TIME BACK BUTTON PRESSED)
//        return super.onKeyDown(keyCode, event);

        return true;
    }



    // BELOW ONCLICK METHODS FROM CARD VIEW ITEM
    public void shareWith(View view) {

        Bitmap myBitmap;

        //TO GET THE WHOLE SCREEN TO DISPLAY IN THE SCREENSHOT
//        View v1 = getWindow().getDecorView().getRootView();

        //TO GET THE PARTICULAR SCREEN(only news chunk) TO DISPLAY IN THE SCREENSHOT
        View v2 = findViewById(R.id.wholeNewsChunk);

        assert v2 != null;
        v2.setDrawingCacheEnabled(true);
        myBitmap = Bitmap.createBitmap(v2.getDrawingCache());
        v2.setDrawingCacheEnabled(false);


        final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        String fileName = now + ".jpg";
        File dir = new File(dirPath);
        if(!dir.exists())
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        File imageFile = new File(dirPath, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(imageFile);
            myBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            shareScreenshot(imageFile);
            imageFile.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void shareScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            Log.e("ON SHARE ", String.valueOf(uri));
            startActivity(Intent.createChooser(intent, "Share via.."));
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * @param activity *
     * If the app does not has permission then the user will be prompted to grant permissions to use External Storage
     */

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {

            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }


}
