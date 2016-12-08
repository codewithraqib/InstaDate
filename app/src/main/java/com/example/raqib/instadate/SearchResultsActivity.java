package com.example.raqib.instadate;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

public class SearchResultsActivity extends Activity {
    DatabaseTableForSearch db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        db = new DatabaseTableForSearch(this);

        Log.e("Hello", "Search Is Working1");
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e("Hello", "Search Is Working2");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.e("Hello", "Search Is Working3");

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Cursor c = db.getWordMatches(query, null);
            Log.e("Hello", String.valueOf(c));
            Log.e("Hello", "Search Is Working4");
            //use the query to search your data somehow
        }
    }
}
