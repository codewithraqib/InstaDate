package com.example.raqib.instadate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class Customization extends AppCompatActivity {
    static boolean subscription[] = new  boolean[10];
    ImageButton topFeeds,scienceFeeds,technologyFeeds,sportsFeeds,healthFeeds,localFeeds,internationalFeeds,gadgetsFeeds,trendingFeeds,miscellaneousFeeds;

    static SharedPreferences sharedPreferences;
    static boolean topNewsSubscribed = false;
    static boolean scienceSubscribed = false;
    static boolean technologySubscribed = false;
    static boolean sportsSubscribed = false;
    static boolean healthSubscribed = false;
    static boolean localSubscribed = false;
    static boolean internationalSubscribed = false;
    static boolean gadgetsSubscribed = false;
    static boolean trendingSubscribed = false;
    static boolean miscellaneousSubscribed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);

        sharedPreferences = this.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.customizationToolbar);
        setSupportActionBar(toolbar);

        topFeeds = (ImageButton) findViewById(R.id.imageButtonTopFeeds);
        scienceFeeds = (ImageButton) findViewById(R.id.imageButtonScience);
        technologyFeeds = (ImageButton) findViewById(R.id.imageButtonTechnology);
        sportsFeeds = (ImageButton) findViewById(R.id.imageButtonSports);
        healthFeeds = (ImageButton) findViewById(R.id.imageButtonHealth);
        localFeeds = (ImageButton) findViewById(R.id.imageButtonLocal);
        internationalFeeds = (ImageButton) findViewById(R.id.imageButtonInternationalFeeds);
        gadgetsFeeds = (ImageButton) findViewById(R.id.imageButtonGadgetsFeeds);
        trendingFeeds = (ImageButton) findViewById(R.id.imageButtonTrendingFeeds);
        miscellaneousFeeds = (ImageButton) findViewById(R.id.imageButtonMiscellaneousFeeds);

        checkForSubscribedFeeds();
    }

    private void checkForSubscribedFeeds() {
        if(sharedPreferences.getBoolean("topFeeds", false)) {
//            Log.e("value of Subscription0", String.valueOf(sharedPreferences.getBoolean("topFeeds", false)));
            topFeeds.setColorFilter(Color.MAGENTA);
            Log.e("TopNewsSubscribedValue1", String.valueOf(topNewsSubscribed));
            topNewsSubscribed = true;
            Log.e("TopNewsSubscribedValue2", String.valueOf(topNewsSubscribed));
        }if(sharedPreferences.getBoolean("scienceFeeds", false)) {
            scienceFeeds.setColorFilter(Color.MAGENTA);
            scienceSubscribed = true;
        }if(sharedPreferences.getBoolean("technologyFeeds", false)) {
            technologyFeeds.setColorFilter(Color.MAGENTA);
            technologySubscribed = true;
        }if(sharedPreferences.getBoolean("sportsFeeds", false)) {
            sportsFeeds.setColorFilter(Color.MAGENTA);
            sportsSubscribed = true;
        }if(sharedPreferences.getBoolean("healthFeeds", false)) {
            healthFeeds.setColorFilter(Color.MAGENTA);
            healthSubscribed = true;
        }if(sharedPreferences.getBoolean("localFeeds", false)) {
            localFeeds.setColorFilter(Color.MAGENTA);
            localSubscribed = true;
        }if(sharedPreferences.getBoolean("internationalFeeds", false)) {
            internationalFeeds.setColorFilter(Color.MAGENTA);
            internationalSubscribed = true;
        }if(sharedPreferences.getBoolean("gadgetsFeeds", false)) {
            gadgetsFeeds.setColorFilter(Color.MAGENTA);
            gadgetsSubscribed = true;
        }if(sharedPreferences.getBoolean("trendingFeeds", false)) {
            trendingFeeds.setColorFilter(Color.MAGENTA);
            trendingSubscribed = true;
        }if(sharedPreferences.getBoolean("miscellaneousFeeds", false)) {
            miscellaneousFeeds.setColorFilter(Color.MAGENTA);
            miscellaneousSubscribed = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customization_menu, menu);
        return true;
    }

    public void storeInDatabase(MenuItem item) {

        Toast toast = Toast.makeText(getApplicationContext(),"Saving Your Experience...", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        startActivity(new Intent(Customization.this, MainActivity.class));
        this.finish();
    }

    public void topNewsSubscribed(View view) {
        Log.e("TopNewsSubValueOnClick", String.valueOf(topNewsSubscribed));
        if(!topNewsSubscribed) {
            subscription[0] = true;
            topFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("topFeeds", true).apply();
            topNewsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Top News Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TopNewsFeeds", "Subscribed");
        }else if(topNewsSubscribed){
            subscription[0] = false;
            topFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("topFeeds", false).apply();
            topNewsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Top News Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TopNewsFeeds", "UnSubscribed");
        }
    }

    public void scienceSubscribed(View view) {
        if(!scienceSubscribed) {
            subscription[1] = true;
            scienceFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("scienceFeeds", true).apply();
            scienceSubscribed = true;
            Toast.makeText(getApplicationContext(),"Science Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("ScienceFeeds", "Subscribed");
        }else if(scienceSubscribed){
            subscription[1] = false;
            scienceFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("scienceFeeds", false).apply();
            scienceSubscribed = false;
            Toast.makeText(getApplicationContext(),"Science Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("ScienceFeeds", "UnSubscribed");
        }

    }

    public void technologySubscribed(View view) {
        if(!technologySubscribed) {
            subscription[2] = true;
            technologyFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("technologyFeeds", true).apply();
            technologySubscribed = true;
            Toast.makeText(getApplicationContext(),"Technology Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TechnologyFeeds", "Subscribed");
        }else if(technologySubscribed){
            subscription[2] = false;
            technologyFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("technologyFeeds", false).apply();
            technologySubscribed = false;
            Toast.makeText(getApplicationContext(),"Technology Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TechnologyFeeds", "UnSubscribed");
        }
    }

    public void sportsSubscribed(View view) {
        if(!sportsSubscribed) {
            subscription[3] = true;
            sportsFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("sportsFeeds", true).apply();
            sportsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Sports Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("SportsFeeds", "Subscribed");
        }else if(sportsSubscribed){
            subscription[3] = false;
            sportsFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("sportsFeeds", false).apply();
            sportsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Sports Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("SportsFeeds", "UnSubscribed");
        }
    }

    public void healthSubscribed(View view) {
        if(!healthSubscribed) {
            subscription[4] = true;
            healthFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("healthFeeds", true).apply();
            healthSubscribed = true;
            Toast.makeText(getApplicationContext(),"Health Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("HealthFeeds", "Subscribed");
        }else if(healthSubscribed){
            subscription[4] = false;
            healthFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("healthFeeds", false).apply();
            healthSubscribed = false;
            Toast.makeText(getApplicationContext(),"Health Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("HealthFeeds", "UnSubscribed");
        }
    }

    public void localSubscribed(View view) {
        if(!localSubscribed) {
            subscription[5] = true;
            localFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("localFeeds", true).apply();
            localSubscribed = true;
            Toast.makeText(getApplicationContext(),"Local Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("LocalFeeds", "Subscribed");
        }else if(localSubscribed){
            subscription[5] = false;
            localFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("localFeeds", false).apply();
            localSubscribed = false;
            Toast.makeText(getApplicationContext(),"Local Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("LocalFeeds", "UnSubscribed");
        }
    }

    public void internationalSubscribed(View view) {
        if(!internationalSubscribed) {
            subscription[6] = true;
            internationalFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("internationalFeeds", true).apply();
            internationalSubscribed = true;
            Toast.makeText(getApplicationContext(),"International Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("InternationalFeeds", "Subscribed");
        }else if (internationalSubscribed){
            subscription[6] = false;
            internationalFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("internationalFeeds", false).apply();
            internationalSubscribed = false;
            Toast.makeText(getApplicationContext(),"International Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("InternationalFeeds", "UnSubscribed");
        }
    }

    public void gadgetsSubscribed(View view) {
        if(!gadgetsSubscribed){
            subscription[7]= true;
            gadgetsFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("gadgetsFeeds", true).apply();
            gadgetsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Gadgets Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("GadgetsFeeds", "Subscribed");
        }else if(gadgetsSubscribed){
            subscription[7]= false;
            gadgetsFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("gadgetsFeeds", false).apply();
            gadgetsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Gadgets Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("GadgetsFeeds", "UnSubscribed");
        }

    }

    public void trendingSubscribed(View view) {
        if(!trendingSubscribed) {
            subscription[8] = true;
            trendingFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("trendingFeeds", true).apply();
            trendingSubscribed = true;
            Toast.makeText(getApplicationContext(),"Trending Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TrendingFeeds", "Subscribed");
        }else if(trendingSubscribed){
            subscription[8] = false;
            trendingFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("trendingFeeds", false).apply();
            trendingSubscribed = false;
            Toast.makeText(getApplicationContext(),"Trending Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TrendingFeeds", "UnSubscribed");
        }
    }

    public void miscellaneousSubscribed(View view) {
        if(!miscellaneousSubscribed) {
            subscription[9] = true;
            miscellaneousFeeds.setColorFilter(Color.MAGENTA);
            sharedPreferences.edit().putBoolean("miscellaneousFeeds", true).apply();
            miscellaneousSubscribed = true;
            Toast.makeText(getApplicationContext(),"Miscellaneous Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("MiscellaneousFeeds", "Subscribed");
        }else if (miscellaneousSubscribed){
            subscription[9] = false;
            miscellaneousFeeds.setColorFilter(Color.BLACK);
            sharedPreferences.edit().putBoolean("miscellaneousFeeds", false).apply();
            miscellaneousSubscribed = false;
            Toast.makeText(getApplicationContext(),"Miscellaneous Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("MiscellaneousFeeds", "UnSubscribed");
        }
    }

    // IF THE USER PRESSED THE CUSTOMIZATION MENU ITEM AND THEN SUBSCRIBED 0 TO N NUMBER OF FEEDS BUT
    // NOT ACTUALLY PRESSED THE + ICON TO SAVE THE SUBSCRIBED FEEDS IN CASE SUBSCRIBED SOME FEEDS, RATHER
    // PRESSED THE BACK BUTTON AND THE BELOW METHOD IS IMPLEMENTED TO GET THE MAIN ACTIVITY BACK
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // CHECK IF THE KEY EVENT WAS THE BACK BUTTON
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            this.finish();
            Log.e("BACK PRESSED", "Successfully");
            return true;
        }

        // SYSTEM BEHAVIOR (PROBABLY EXIT THE ACTIVITY)
        return super.onKeyDown(keyCode, event);
    }
}
