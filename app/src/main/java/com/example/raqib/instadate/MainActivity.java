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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity{
    static String link;
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
        assert tabLayout != null;
        tabLayout.addTab(tabLayout.newTab().setText("Top News"));
        tabLayout.addTab(tabLayout.newTab().setText("Science"));
        tabLayout.addTab(tabLayout.newTab().setText("Technology"));
        tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        tabLayout.addTab(tabLayout.newTab().setText("Health"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        PagerAdapter1 adapter = new PagerAdapter1(getSupportFragmentManager(), tabLayout.getTabCount());

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
//        displayNews();

        if (isNetworkAvailable()) {
            Log.i("StackSites", "starting download Task");
            SitesDownloadTask download = new SitesDownloadTask();
            download.execute();
        } else {
            displayNews();
        }
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


    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {

//        ProgressDialog pdl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                Downloader.DownloadFromUrl("http://www.hsj.co.uk/XmlServers/navsectionRSS.aspx?navsectioncode=20703", openFileOutput("HealthService.xml", Context.MODE_PRIVATE));
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

        }

    }

}
