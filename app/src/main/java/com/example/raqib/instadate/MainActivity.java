package com.example.raqib.instadate;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity{
    public List<NewsItems> NYT;
    public List<NewsItems> ESPN;
    public List<NewsItems> GK;
    public List<NewsItems> TFE;
    public List<NewsItems> TFETech;
    public List<NewsItems> ScienceDaily;
    RecyclerView myRecyclerView;
    LinearLayout myLinearLayout;
    Context context;
    static String link;
    int position = MyNewsRecyclerViewAdapter.pos;
//    ViewPagerAdapter adapter;
    ViewPager pager;
    static boolean scroll_down;
    ActionBar actionBar;
    int uiOptions = 0;



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

        //TO HIDE STATUS BAR AND ACTION BAR
        View decorView = getWindow().getDecorView();
        actionBar = getSupportActionBar();
        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        actionBar.hide();

        //NEW IMPLEMENTATION OF SWIPE TABS
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabHost);
        tabLayout.addTab(tabLayout.newTab().setText("Top News"));
        tabLayout.addTab(tabLayout.newTab().setText("Science"));
        tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
               // viewPager.removeAllViews();
                viewPager.removeViewAt(0);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

                myRecyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

                myRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);


                if (scroll_down) {
                    View decorView = getWindow().getDecorView();
                    // Hide the status bar.
                    int uiOptions = 0;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                    }
                    decorView.setSystemUiVisibility(uiOptions);
                }


            }
             @Override
             public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if ( dy > 5 ) {
                    //scroll down
                    scroll_down = true;

                } else if ( dy < -5 ) {
                    //scroll up
                    scroll_down = false;
                }
            }
        });


//        displayNews();

        if (isNetworkAvailable()) {
            Log.i("StackSites", "starting download Task");
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        } else {
            displayNews();
        }
    }


    //TO  DESTROY THE OLD VIEW AND TO MAKE SPACE NULL FOR THE NEXT TAB VIEW
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (view != null) {
//            ViewGroup parentViewGroup = (ViewGroup) view.getParent();
//            if (parentViewGroup != null) {
//                parentViewGroup.removeAllViews();
//            }
//        }
//    }

    private void displayNews() {

//
//        //TO HIDE AND SHOW THE ACTIONBAR ON SCROLL
//        myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//        myLinearLayout = (LinearLayout) findViewById(R.id.myLinearLayout);
//
//        actionBar = getSupportActionBar();
//
//        myRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//
//                if (scroll_down) {
//                    View decorView = getWindow().getDecorView();
//                    // Hide the status bar.
//                    int uiOptions = 0;
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//                        uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//                    }
//                    decorView.setSystemUiVisibility(uiOptions);
//                    actionBar.hide();
//                } else {
//                    actionBar.show();
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 10) {
//                    //scroll down
//                    scroll_down = true;
//
//                } else if (dy < -10) {
//                    //scroll up
//                    scroll_down = false;
//                }
//            }
//        });
//
//
//        //MATERIAL TAB HOST ON WORK
//        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
//        pager = (ViewPager) this.findViewById(R.id.pager);
//
//        // init view pager
//        adapter = new ViewPagerAdapter(getSupportFragmentManager());
//        pager.setAdapter(adapter);
//
//
//        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                // when user do a swipe the selected tab change
//                tabHost.setSelectedNavigationItem(position);
//
//            }
//        });
//
//        // insert all tabs from pagerAdapter data
//        for (int i = 0; i < adapter.getCount(); i++) {
//            Log.e("ADAPTER COUNT ", String.valueOf(adapter.getCount()));
//            tabHost.addTab(
//                    tabHost.newTab()
//                            .setText(adapter.getPageTitle(i))
//                            .setTabListener(this)
//            );
//        }
    }

    //HELPER METHOD TO DETERMINE WHETHER NETWORK IS AVAILABLE OR NOT
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void shareWith(View view){

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
            Process sh = Runtime.getRuntime().exec("su",null,null);
            OutputStream os = sh.getOutputStream();
            os.write(("/system/bin/screencap -p " + "/sdcard/"+mPath).getBytes("ASCII"));

            //CREATE THE FILE ON EXTERNAL STORAGE
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bytes.toByteArray());
            fos.flush();
            fos.close();
            shareScreenshot(imageFile);
        }catch (Exception e) {
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

        if(isIntentSafe) {
            Log.e("ON SHARE ", String.valueOf(uri));
            startActivity(Intent.createChooser(intent,"Share via.."));
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     * @param activity
     * *
     * If the app does not has permission then the user will be prompted to grant permissions to use External Storage
     *
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



//    @Override
//    public void onTabSelected(MaterialTab tab) {
//        pager.setCurrentItem(tab.getPosition());
//    }
//
//    @Override
//    public void onTabReselected(MaterialTab tab) {
//
//    }
//
//    @Override
//    public void onTabUnselected(MaterialTab tab) {
//
//    }
//
//
//    private class ViewPagerAdapter extends FragmentStatePagerAdapter {
//
//        public ViewPagerAdapter(FragmentManager fm) {
//            super(fm);
//
//        }
//
//        public Fragment getItem(int num) {
//            return new FragmentText();
//        }
//
//        @Override
//        public int getCount() {
//            return 10;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Section " + position;
//        }
//
//    }


    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {

//        ProgressDialog pdl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

////            pdl = ProgressDialog.show(context,"please wait...","fetching data",false,false);
//            myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//            myRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//            myRecyclerView.hasFixedSize();
//
//            NYT = SitesXmlPullParserNYT.getStackSitesFromFile(getApplicationContext());
//            ESPN = SitesXmlPullParserEspnCricinfo.getStackSitesFromFile(getApplicationContext());
////            GK = SitesXmlPullParserGK.getStackSitesFromFile(getApplicationContext());
//            TFE = SitesXmlPullParserTheFinancialExpress.getStackSitesFromFile(getApplicationContext());
//            TFETech = SitesXmlPullParserTheFinancialExpressTech.getStackSitesFromFile(getApplicationContext());
//            ScienceDaily = SitesXmlPullParserScienceDaily.getStackSitesFromFile(getApplicationContext());
//
//            List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
//                {
//                    addAll(TFE);
//                    addAll(NYT);
//                    addAll(ESPN);
////                    addAll(GK);
//                    addAll(TFETech);
//                    addAll(ScienceDaily);
//                }
//            };
//            myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(newsItemsList));
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //Download the file
            try {
                Downloader.DownloadFromUrl("http://www.nytimes.com/services/xml/rss/nyt/HomePage.xml", openFileOutput("NYTNews.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.espncricinfo.com/rss/content/story/feeds/6.xml", openFileOutput("EspnCricinfo.xml", Context.MODE_PRIVATE));
//                Downloader.DownloadFromUrl("http://www.greaterkashmir.com/feed.aspx?cat_id=2", openFileOutput("GK.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/feed/", openFileOutput("TFE.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("http://www.financialexpress.com/section/industry/tech/feed/", openFileOutput("TFETech.xml", Context.MODE_PRIVATE));
                Downloader.DownloadFromUrl("https://rss.sciencedaily.com/computers_math.xml", openFileOutput("ScienceDaily.xml", Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                Log.e("ERROR at DoInBackground", String.valueOf(e));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast toast = Toast.makeText(MainActivity.this,"Your Feeds Has Been Successfully Updated!",Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
            toast.show();

            myRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            myRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            myRecyclerView.hasFixedSize();

            NYT = SitesXmlPullParserNYT.getStackSitesFromFile(getApplicationContext());
            ESPN = SitesXmlPullParserEspnCricinfo.getStackSitesFromFile(getApplicationContext());
//            GK = SitesXmlPullParserGK.getStackSitesFromFile(getApplicationContext());
            TFE = SitesXmlPullParserTheFinancialExpress.getStackSitesFromFile(getApplicationContext());
            TFETech = SitesXmlPullParserTheFinancialExpressTech.getStackSitesFromFile(getApplicationContext());
            ScienceDaily = SitesXmlPullParserScienceDaily.getStackSitesFromFile(getApplicationContext());

            List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
                {
                    addAll(TFE);
                    addAll(NYT);
                    addAll(ESPN);
//                    addAll(GK);
                    addAll(TFETech);
                    addAll(ScienceDaily);
                }
            };
            myRecyclerView.setAdapter(new MyNewsRecyclerViewAdapter(newsItemsList));
        }

    }

}
