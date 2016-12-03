package com.example.raqib.instadate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static String link;
//    ActionBar actionBar;
    int uiOptions = 0;
    public static final String APP_ID = "B325D49E-27BD-C5D6-FF3F-46457273B900";  // DON'T CHANGE IT EITHER YOU WILL LOST THE CONNECTIVITY WITH THE APP SERVER
    public static final String SECRET_KEY = "E7D442EA-E108-4F9E-FF28-E010A8EB1700";  // DON'T CHANGE IT EITHER YOU WILL LOST THE CONNECTIVITY WITH THE APP SERVER
    public static final String VERSION = "v1";
    TextView setNameInDrawer, setEmailInDrawer;
    SwipeRefreshLayout mySwipeRefreshLayout;


    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //noinspection ConstantConditions
        getSupportActionBar().isHideOnContentScrollEnabled();

//        startActivity(new Intent(this, GeneralNavigationTab.class));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view) ;
        Log.e("RecyclerView in Main", String.valueOf(recyclerView));


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
            Log.e("Checking OnRefresh", "inside on Refresh");
            if (isNetworkAvailable()) {
                Log.e("Checking OnRefresh", "inside IF ");
                SitesDownloadTask download = new SitesDownloadTask();
                download.execute();
            }
            else{
                mySwipeRefreshLayout.setRefreshing(false);
                Log.e("Checking OnRefresh", "inside ELSE");
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

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        Backendless.initApp(this, APP_ID, SECRET_KEY, VERSION);


        //SETTING NAME AND EMAIL IN DRAWER
        setNameInDrawer = (TextView) findViewById(R.id.drawerLogIn);
        setEmailInDrawer = (TextView) findViewById(R.id.drawerUserEmail);

        if(Backendless.UserService.CurrentUser() != null){
            setNameInDrawer.setText(String.valueOf( Backendless.UserService.CurrentUser().getProperty("name")));
            setEmailInDrawer.setText(String.valueOf( Backendless.UserService.CurrentUser().getEmail()));
            Log.e("NameInDrawer",String.valueOf( Backendless.UserService.CurrentUser().getProperty("name")));

        }

        //TO HIDE STATUS BAR AND ACTION BAR
//        View decorView = getWindow().getDecorView();
//        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//      getSupportActionBar().hide();

        //NEW IMPLEMENTATION OF SWIPE TABS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabHost);

        assert tabLayout != null;
        Customization.sharedPreferences = this.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);

        tabLayout.addTab(tabLayout.newTab().setText("All News"));
        if(Customization.sharedPreferences.getBoolean("scienceFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Science"));
        if(Customization.sharedPreferences.getBoolean("technologyFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        if(Customization.sharedPreferences.getBoolean("sportsFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        if(Customization.sharedPreferences.getBoolean("healthFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Health"));
        if(Customization.sharedPreferences.getBoolean("localFeeds", false))
        tabLayout.addTab(tabLayout.newTab().setText("Kashmir"));
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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
//         LayoutInflater.from(getApplicationContext())
//                .inflate(R.layout.right_drawer_layout, getParent(), false);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.searchInMain).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        Log.e("SearchView In Menu", String.valueOf(searchManager));
        Log.e("SearchInfoInMain", String.valueOf(searchManager.getSearchableInfo(getComponentName())));
//        searchView.setSubmitButtonEnabled(true);


//        if(onSearchRequested()){
//           CharSequence query = searchView.getQuery();
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_SEARCH);
//            intent.putExtra("Search Query",searchView.getQuery());
//            startActivity(new Intent(this, SearchResultsActivity.class).putExtra("Query Is",query));
//
//        }
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, SearchResultsActivity.class).putExtra("Query Is",query));
//            }
//        });

        return true;
    }

    public void RegisterUser(MenuItem item) {
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
    }


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



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.myLinearLayout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


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
        View v1 = getWindow().getDecorView().getRootView();

        //TO GET THE PARTICULAR SCREEN TO DISPLAY IN THE SCREENSHOT
//        View v2 = findViewById(R.id.mainScreen);
        v1.setDrawingCacheEnabled(true);
        myBitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        try {
            Date now = new Date();
            android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";
            File imageFile = new File(mPath);
            verifyStoragePermissions(MainActivity.this);

            //TO SUPPORT THE NON ROOTED DEVICES
            Process sh = Runtime.getRuntime().exec("su", null, null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p "+"/sdcard/"+mPath).getBytes("ASCII"));

            //CREATE THE FILE ON EXTERNAL STORAGE
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bytes.toByteArray());
            fos.flush();
            fos.close();
            shareScreenshot(imageFile);
        } catch (Exception e) {
            Log.e("ERROR IS ", String.valueOf(e));
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
     *                 If the app does not has permission then the user will be prompted to grant permissions to use External Storage
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
