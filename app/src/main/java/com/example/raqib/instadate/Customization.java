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
import android.widget.LinearLayout;
import android.widget.Toast;

public class Customization extends AppCompatActivity {
    static boolean subscription[] = new  boolean[10];
    LinearLayout topFeeds,scienceFeeds,technologyFeeds,sportsFeeds,healthFeeds,localFeeds,internationalFeeds,gadgetsFeeds,trendingFeeds,miscellaneousFeeds;

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
        setContentView(R.layout.customization);

        sharedPreferences = this.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.customizationToolbar);
        setSupportActionBar(toolbar);

        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException e){
            Log.e("SearchActivity Toolbar", "You have got a NULL POINTER EXCEPTION");
        }

        topFeeds = (LinearLayout) findViewById(R.id.imageButtonTopFeeds);
        scienceFeeds = (LinearLayout) findViewById(R.id.imageButtonScience);
        technologyFeeds = (LinearLayout) findViewById(R.id.imageButtonTechnology);
        sportsFeeds = (LinearLayout) findViewById(R.id.imageButtonSports);
        healthFeeds = (LinearLayout) findViewById(R.id.imageButtonHealth);
        localFeeds = (LinearLayout) findViewById(R.id.imageButtonLocal);
        internationalFeeds = (LinearLayout) findViewById(R.id.imageButtonInternationalFeeds);
        gadgetsFeeds = (LinearLayout) findViewById(R.id.imageButtonGadgetsFeeds);
        trendingFeeds = (LinearLayout) findViewById(R.id.imageButtonTrendingFeeds);
        miscellaneousFeeds = (LinearLayout) findViewById(R.id.imageButtonMiscellaneousFeeds);

        checkForSubscribedFeeds();
    }


    private void checkForSubscribedFeeds() {
        if(sharedPreferences.getBoolean("topFeeds", false)) {
//            Log.e("value of Subscription0", String.valueOf(sharedPreferences.getBoolean("topFeeds", false)));
            topFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            Log.e("TopNewsSubscribedValue1", String.valueOf(topNewsSubscribed));
            topNewsSubscribed = true;
            Log.e("TopNewsSubscribedValue2", String.valueOf(topNewsSubscribed));
        }if(sharedPreferences.getBoolean("scienceFeeds", false)) {
            scienceFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            scienceSubscribed = true;
        }if(sharedPreferences.getBoolean("technologyFeeds", false)) {
            technologyFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            technologySubscribed = true;
        }if(sharedPreferences.getBoolean("sportsFeeds", false)) {
            sportsFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sportsSubscribed = true;
        }if(sharedPreferences.getBoolean("healthFeeds", false)) {
            healthFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            healthSubscribed = true;
        }if(sharedPreferences.getBoolean("localFeeds", false)) {
            localFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            localSubscribed = true;
        }if(sharedPreferences.getBoolean("internationalFeeds", false)) {
            internationalFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            internationalSubscribed = true;
        }if(sharedPreferences.getBoolean("gadgetsFeeds", false)) {
            gadgetsFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            gadgetsSubscribed = true;
        }if(sharedPreferences.getBoolean("trendingFeeds", false)) {
            trendingFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            trendingSubscribed = true;
        }if(sharedPreferences.getBoolean("miscellaneousFeeds", false)) {
            miscellaneousFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
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

        Toast toast = Toast.makeText(getApplicationContext(),"Saving Your Experience...", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();

        startActivity(new Intent(Customization.this, MainActivity.class));
        this.finish();
    }

    public void topNewsSubscribed(View view) {
        Log.e("TopNewsSubValueOnClick", String.valueOf(topNewsSubscribed));
        if(!topNewsSubscribed) {
            subscription[0] = true;
//            topFeeds.setColorFilter(Color.MAGENTA);
            topFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("topFeeds", true).apply();
            topNewsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Top News Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TopNewsFeeds", "Subscribed");
        }else if(topNewsSubscribed){
            subscription[0] = false;
            topFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("topFeeds", false).apply();
            topNewsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Top News Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TopNewsFeeds", "UnSubscribed");
        }
    }

    public void scienceSubscribed(View view) {
        if(!scienceSubscribed) {
            subscription[1] = true;
            scienceFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("scienceFeeds", true).apply();
            scienceSubscribed = true;
            Toast.makeText(getApplicationContext(),"Science Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("ScienceFeeds", "Subscribed");
        }else if(scienceSubscribed){
            subscription[1] = false;
            scienceFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("scienceFeeds", false).apply();
            scienceSubscribed = false;
            Toast.makeText(getApplicationContext(),"Science Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("ScienceFeeds", "UnSubscribed");
        }

    }

    public void technologySubscribed(View view) {
        if(!technologySubscribed) {
            subscription[2] = true;
            technologyFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("technologyFeeds", true).apply();
            technologySubscribed = true;
            Toast.makeText(getApplicationContext(),"Technology Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TechnologyFeeds", "Subscribed");
        }else if(technologySubscribed){
            subscription[2] = false;
            technologyFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("technologyFeeds", false).apply();
            technologySubscribed = false;
            Toast.makeText(getApplicationContext(),"Technology Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TechnologyFeeds", "UnSubscribed");
        }
    }

    public void sportsSubscribed(View view) {
        if(!sportsSubscribed) {
            subscription[3] = true;
            sportsFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("sportsFeeds", true).apply();
            sportsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Sports Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("SportsFeeds", "Subscribed");
        }else if(sportsSubscribed){
            subscription[3] = false;
            sportsFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("sportsFeeds", false).apply();
            sportsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Sports Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("SportsFeeds", "UnSubscribed");
        }
    }

    public void healthSubscribed(View view) {
        if(!healthSubscribed) {
            subscription[4] = true;
            healthFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("healthFeeds", true).apply();
            healthSubscribed = true;
            Toast.makeText(getApplicationContext(),"Health Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("HealthFeeds", "Subscribed");
        }else if(healthSubscribed){
            subscription[4] = false;
            healthFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("healthFeeds", false).apply();
            healthSubscribed = false;
            Toast.makeText(getApplicationContext(),"Health Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("HealthFeeds", "UnSubscribed");
        }
    }

    public void localSubscribed(View view) {
        if(!localSubscribed) {
            subscription[5] = true;
            localFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("localFeeds", true).apply();
            localSubscribed = true;
            Toast.makeText(getApplicationContext(),"Local Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("LocalFeeds", "Subscribed");
        }else if(localSubscribed){
            subscription[5] = false;
            localFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("localFeeds", false).apply();
            localSubscribed = false;
            Toast.makeText(getApplicationContext(),"Local Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("LocalFeeds", "UnSubscribed");
        }
    }

    public void internationalSubscribed(View view) {
        if(!internationalSubscribed) {
            subscription[6] = true;
            internationalFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("internationalFeeds", true).apply();
            internationalSubscribed = true;
            Toast.makeText(getApplicationContext(),"International Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("InternationalFeeds", "Subscribed");
        }else if (internationalSubscribed){
            subscription[6] = false;
            internationalFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("internationalFeeds", false).apply();
            internationalSubscribed = false;
            Toast.makeText(getApplicationContext(),"International Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("InternationalFeeds", "UnSubscribed");
        }
    }

    public void gadgetsSubscribed(View view) {
        if(!gadgetsSubscribed){
            subscription[7]= true;
            gadgetsFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("gadgetsFeeds", true).apply();
            gadgetsSubscribed = true;
            Toast.makeText(getApplicationContext(),"Gadgets Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("GadgetsFeeds", "Subscribed");
        }else if(gadgetsSubscribed){
            subscription[7]= false;
            gadgetsFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("gadgetsFeeds", false).apply();
            gadgetsSubscribed = false;
            Toast.makeText(getApplicationContext(),"Gadgets Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("GadgetsFeeds", "UnSubscribed");
        }

    }

    public void trendingSubscribed(View view) {
        if(!trendingSubscribed) {
            subscription[8] = true;
            trendingFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("trendingFeeds", true).apply();
            trendingSubscribed = true;
            Toast.makeText(getApplicationContext(),"Trending Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("TrendingFeeds", "Subscribed");
        }else if(trendingSubscribed){
            subscription[8] = false;
            trendingFeeds.setBackgroundColor(Color.WHITE);
            sharedPreferences.edit().putBoolean("trendingFeeds", false).apply();
            trendingSubscribed = false;
            Toast.makeText(getApplicationContext(),"Trending Feeds UnSubscribed", Toast.LENGTH_SHORT).show();
            Log.e("TrendingFeeds", "UnSubscribed");
        }
    }

    public void miscellaneousSubscribed(View view) {

        if(!miscellaneousSubscribed) {
            subscription[9] = true;
//            miscellaneousFeeds.setColorFilter(Color.MAGENTA);
            miscellaneousFeeds.setBackgroundColor(getResources().getColor(R.color.colorSubscription));
            sharedPreferences.edit().putBoolean("miscellaneousFeeds", true).apply();
            miscellaneousSubscribed = true;
            Toast.makeText(getApplicationContext(),"Miscellaneous Feeds Subscribed", Toast.LENGTH_SHORT).show();
            Log.e("MiscellaneousFeeds", "Subscribed");
        }else if (miscellaneousSubscribed){
            subscription[9] = false;
            miscellaneousFeeds.setBackgroundColor(Color.WHITE);

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
