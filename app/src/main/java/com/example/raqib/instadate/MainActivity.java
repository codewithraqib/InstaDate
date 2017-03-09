package com.example.raqib.instadate;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
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
    DrawerLayout myDrawerLayout;
    static  int i = 0;
    RecyclerView mRecyclerView;
    static boolean searchOpened = false;
    SplashScreenFragment frag;
    FragmentManager manager;
    static boolean firstTimeInONCreate = true;
    boolean []subscribedTabs = new boolean[7];
    static  int unSelectedTab = 0;
    TextView loginText,logoutText;
    ActionBarDrawerToggle mDrawerToggle;


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

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabHost);

        searchListView = (ListView) findViewById(R.id.searchListView);

        // TO SHOW SPLASH FRAGMENT
        frag = new SplashScreenFragment();
        manager = getFragmentManager();


        //SET THE SUBSCRIBED ARRAY TO FALSE INITIALLY
        for( int i = 0; i< subscribedTabs.length; i++){
            subscribedTabs[i] = false;
        }

        final LinearLayout mainNewsRelativeLayout = (LinearLayout) findViewById(R.id.mainNewsRelativeLayout);
        myDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);

        mDrawerToggle = new ActionBarDrawerToggle(this, myDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        myDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        final ViewPager viewPager2 = (ViewPager) findViewById(R.id.pager);
        assert viewPager2 != null;
        viewPager2.setCurrentItem(0);

        //Check For Logged In User To Set UP Drawer Items Accordingly
        loginText = (TextView) findViewById(R.id.drawerLogIn);
        logoutText = (TextView) findViewById(R.id.drawerLogOut);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        try{
            if(user != null){

                loginText.setAlpha(0);
                logoutText.setAlpha(1);
            }

            //LOGGING IN AND LOGGING OUT
//            loginText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
//                }
//            });

//            logoutText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                    if(user == null){
//                        Toast.makeText(MainActivity.this, "You Are Already Logged Out!", Toast.LENGTH_SHORT).show();
//                    }else{
//                        FirebaseAuth.getInstance().signOut();
//                        Toast.makeText(MainActivity.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });

        }catch(NullPointerException e){
            Log.e("Login/Logout", "NullPointerException");
        }



        //CREATE A LIST AF ALL NEWS TO MAKE IT READY FOR SEARCH
        createListOfNews();



        //SETTING THE LIST FOR SEARCH

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listToSearch);
        searchListView.setAdapter(arrayAdapter);


        //MATERIAL SEARCH VIEW IMPLEMENTATION
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener(){

            @Override
            public void onSearchViewShown() {
                searchListView.setVisibility(View.VISIBLE);
                getSupportActionBar().show();
                tabLayout.setVisibility(View.VISIBLE);
                mainNewsRelativeLayout.setVisibility(View.INVISIBLE);
                searchOpened = true;

            }

            @Override
            public void onSearchViewClosed() {

                searchListView = (ListView) findViewById(R.id.searchListView);
                arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,listToSearch);
//                getSupportActionBar().show();
//                tabLayout.setVisibility(View.VISIBLE);
                searchListView.setAdapter(arrayAdapter);
                searchListView.setVisibility(View.INVISIBLE);
                mainNewsRelativeLayout.setVisibility(View.VISIBLE);


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

            getTheCurrentFeeds();
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
        subscribedTabs[0] = true;

        if(Customization.sharedPreferences.getBoolean("scienceFeeds", false)){
            subscribedTabs[1] = true;
            tabLayout.addTab(tabLayout.newTab().setText("Science"));
        }
        if(Customization.sharedPreferences.getBoolean("technologyFeeds", false)){
            subscribedTabs[2] = true;
            tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        }
        if(Customization.sharedPreferences.getBoolean("sportsFeeds", false)){
            subscribedTabs[3] = true;
            tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        }
        if(Customization.sharedPreferences.getBoolean("healthFeeds", false)){
            subscribedTabs[4] = true;
            tabLayout.addTab(tabLayout.newTab().setText("Health"));
        }
        if(Customization.sharedPreferences.getBoolean("internationalFeeds", false)){
            subscribedTabs[4] = true;
            tabLayout.addTab(tabLayout.newTab().setText("International"));
        }
        if(Customization.sharedPreferences.getBoolean("localFeeds", false)){
            subscribedTabs[6] = true;
            tabLayout.addTab(tabLayout.newTab().setText("Local"));
        }
        tabLayout.setTabGravity(TabLayout.MODE_SCROLLABLE);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        assert viewPager != null;
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

//                int tabPositionForward = unSelectedTab + 1;
//                int flag = 0;
//                int tabPositionBackward = unSelectedTab - 1;
//                if(unSelectedTab < tabPositionForward){
////                    int limit = subscribedTabs.length - tabPosition;
//                    for(int i = tabPositionForward; i < subscribedTabs.length ; i++){
//                        if(subscribedTabs[i]){
//                            viewPager.setCurrentItem(i);
//                            break;
//                        }
//
//                    }
//                }else
                viewPager.setCurrentItem(tab.getPosition());
//                viewPager.setCurrentItem(tab.getText().);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                unSelectedTab = tab.getPosition();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mRecyclerView = (RecyclerView)  findViewById(R.id.my_recycler_view);
                mRecyclerView.smoothScrollToPosition(0);

            }
        });

        if(firstTimeInONCreate){
            firstTimeInONCreate = false;
            getTheCurrentFeeds();
        }

    }

    //METHOD TO GET THE CURRENTLY AVAILABLE FEEDS FROM SOURCES
    private void getTheCurrentFeeds() {
        if (isNetworkAvailable()) {
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        } else {
            mySwipeRefreshLayout.setRefreshing(false);

            Snackbar snackbar = Snackbar.make( myDrawerLayout,"No Internet , Can't Connect at the moment ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getTheCurrentFeeds();
                            mySwipeRefreshLayout.setRefreshing(true);
                        }
                    });
            snackbar.show();

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

        searchNewsItems = new ArrayList<>();

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


    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    private boolean isNetworkAvailable() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return false;


//        //ALTERNATE WAY  TO CHECK INTERNET CONNECTION
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    //BACK PRESS
    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed(); return;
        }
        this.doubleBackToExitPressedOnce = true;

        final ListView list = (ListView) findViewById(R.id.searchListView);
        LinearLayout mainNewsLinearLayout = (LinearLayout) findViewById(R.id.mainNewsRelativeLayout);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        if (id == R.id.nav_top_feeds) {


            assert viewPager != null;
            viewPager.setCurrentItem(0);

        } else if (id == R.id.nav_science) {
            assert viewPager != null;
            viewPager.setCurrentItem(1);

        } else if (id == R.id.nav_technology) {
            assert viewPager != null;
            viewPager.setCurrentItem(2);

        }else if (id == R.id.nav_sports) {
            assert viewPager != null;
            viewPager.setCurrentItem(3);

        }else if (id == R.id.nav_health) {
            assert viewPager != null;
            viewPager.setCurrentItem(4);

        }else if (id == R.id.nav_international) {
            assert viewPager != null;
            viewPager.setCurrentItem(5);
        }else if (id == R.id.nav_local) {
            assert viewPager != null;
            viewPager.setCurrentItem(6);
        } else if (id == R.id.nav_bookmarked_feeds) {
            startActivity(new Intent(getApplicationContext(), BookmarksActivity.class));

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            Snackbar snackbar = Snackbar.make( myDrawerLayout,"You'r not Signed In, SignIn to Customize ", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Sign In", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    });
            snackbar.show();
            myDrawerLayout.closeDrawers();

        }else{
            startActivity(new Intent(MainActivity.this, Customization.class));
        }
    }

    public void aboutActivity(MenuItem item) {
        startActivity(new Intent(MainActivity.this, About.class));

    }

    public void openRightDrawer(MenuItem item) {

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.myDrawerLayout);

        if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            mDrawerLayout.openDrawer(GravityCompat.END);


    }



    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {

//            if(firstTimeInONCreate){
//                manager.beginTransaction().add(R.id.myDrawerLayout,frag,"splashScreenFragment").commit();
//            }
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

                Downloader.DownloadFromUrl("http://www.financialexpress.com/feed/", openFileOutput("TFE.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/industry/tech/feed/", openFileOutput("TFETech.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("https://rss.sciencedaily.com/computers_math.xml", openFileOutput("ScienceDaily.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/health/feed/", openFileOutput("HealthService.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/sports/feed/", openFileOutput("WorldSports.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/lifestyle/science/feed/", openFileOutput("Science2.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.oneindia.com/rss/news-international-fb.xml", openFileOutput("InternationalNews.xml", Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://kashmirglobal.com/feed",openFileOutput("KashmirGlobal.xml",Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.kashmirlife.net/feed/",openFileOutput("KashmirLife.xml",Context.MODE_PRIVATE));

                Downloader.DownloadFromUrl("http://www.tribuneindia.com/rss/feed.aspx?cat_id=5",openFileOutput("TribuneLocal.xml",Context.MODE_PRIVATE));

            } catch (FileNotFoundException e) {
                Log.e("ERROR at DoInBackground", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mySwipeRefreshLayout.setRefreshing(false);

            Toast toast =  Toast.makeText(MainActivity.this, " Feeds Successfully Synced!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();

//            manager.beginTransaction().remove(frag).commit();

        }
    }

}
