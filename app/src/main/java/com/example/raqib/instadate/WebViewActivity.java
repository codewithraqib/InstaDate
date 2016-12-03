package com.example.raqib.instadate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

public class WebViewActivity extends AppCompatActivity {
    static  String link;
    WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

//        getSupportActionBar().setElevation(0);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarWebView);
        setSupportActionBar(toolbar);

        link = getIntent().getExtras().getString("WebPage Link");
//        Log.e("LINK is ", link);
        myWebView = (WebView) findViewById(R.id.webview);
        try{
            if (myWebView != null) {
                myWebView.loadUrl(link);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                builder.setMessage("There Is A Problem Opening The WebPage");
                builder.setCancelable(true);

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch(Exception e){
            Log.e("Exception WebPage", String.valueOf(e));
        }
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.setKeepScreenOn(true);
        myWebView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                Log.e("DragEvent", String.valueOf(event));
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // CHECK IF THE KEY EVENT WAS THE BACK BUTTON AND IF THERE'S HISTORY
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_FORWARD) && myWebView.canGoForward()) {
            myWebView.goForward();
            return true;
        }
        // IF IT WASN'T THE BACK KEY OR THERE'S NO WEB PAGE HISTORY, BUBBLE UP TO THE DEFAULT
        // SYSTEM BEHAVIOR (PROBABLY EXIT THE ACTIVITY)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_web_activity, menu);
        return true;
    }

    public void openInBrowser(MenuItem item) {
        Uri webPage = Uri.parse(link);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPage);

        //TO GET A LIST OF APPS THAT CAN HANDLE THE PARTICULAR INTENT
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(webIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe) {
            startActivity(webIntent);
        }

    }

    public void shareLinkOutside(MenuItem item) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, link);
        sendIntent.setType("text/plain");

        //TO GET A LIST OF APPS THAT CAN HANDLE THE PARTICULAR INTENT
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe) {
            startActivity(sendIntent);
        }
    }
}