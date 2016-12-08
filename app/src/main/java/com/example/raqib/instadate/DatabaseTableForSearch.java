package com.example.raqib.instadate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  Created by RaQib on 16-07-2016.
 */
class DatabaseTableForSearch {

    private static final String TAG = "DictionaryDatabase";

    //The columns we'll include in the news table

    private static final String IMAGE_URL = "IMAGE_URL";
    private static final String NEWS_TITLE = "NEWS_TITLE";
    private static final String NEWS_DESCRIPTION = "NEWS_DESCRIPTION";
    private static final String NEWS_LINK = "NEWS_LINK";
    private static final String NEWS_DATE = "NEWS_DATE";

    private static final String DATABASE_NAME = "NEWSDATA";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    DatabaseTableForSearch(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private SQLiteDatabase mDatabase;

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" +
                        IMAGE_URL + ", " +
                        NEWS_TITLE + ", " +
                        NEWS_DESCRIPTION +", " +
                        NEWS_LINK + ","+
                        NEWS_DATE + ")";

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            loadDataToDatabase();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }


        private void loadDataToDatabase() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        actualContentLoading();
                    } catch (IOException e) {
                        Log.e("LoadingDictionaryThread", String.valueOf(e));
                    }
                }
            }).start();
        }

        void actualContentLoading() throws IOException {

            final List<NewsItems> HealthService = TabHealth.HealthService;
//            final List<NewsItems> BingKashmir = TabKashmir.BingKashmir;
            final List<NewsItems> TribuneKashmir = TabInternational.TribuneKashmir;
            final List<NewsItems> ScienceDaily = TabScience.ScienceDaily;
            final List<NewsItems> Science2 = TabScience.Science2;
            final List<NewsItems> WorldSports = TabSports.WorldSports;
            final List<NewsItems> ESPN = TabSports.WorldSports;
            final List<NewsItems> TFETech = TabTechnology.TFETech;
            final List<NewsItems> NYT = TabTopStories.NYT;
            final List<NewsItems> TFE = TabTopStories.TFE;
            List<NewsItems> newsItemsList = new ArrayList<NewsItems>(){
                {
                    addAll(TFE);
                    addAll(NYT);
                    addAll(TFETech);
                    addAll(WorldSports);
                    addAll(ESPN);
                    addAll(ScienceDaily);
                    addAll(Science2);
                    addAll(HealthService);
                    addAll(TribuneKashmir);

                }
            };

            try {
                String image;
                String title;
                String description;
                String link;
                String date;
                for(int i = 0; i<newsItemsList.size(); i++){
                    image = newsItemsList.get(i).getImgUrl();
                    title = newsItemsList.get(i).getTitle();
                    description = newsItemsList.get(i).getDescription();
                    link = newsItemsList.get(i).getLink();
                    date = newsItemsList.get(i).getDate();
                    addWord(image, title, description, link, date);
                }

            } catch(Exception e) {
                Log.e("LoadingDataForSearch", String.valueOf(e));
            }
        }

        long addWord(String image, String title, String description, String link, String date) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(IMAGE_URL, image);
            contentValues.put(NEWS_TITLE, title);
            contentValues.put(NEWS_DESCRIPTION, description);
            contentValues.put(NEWS_LINK, link);
            contentValues.put(NEWS_DATE, date);

            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, contentValues);
        }


    }

    Cursor getWordMatches(String query, String[] columns) {
        String selection = NEWS_TITLE + " MATCH ?";
        String[] selectionArgs = new String[] {query+"*"};

        return query(selection, selectionArgs, columns);
    }

    private Cursor query(String selection, String[] selectionArgs, String[] columns) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
                columns, selection, selectionArgs, null, null, null);

        if (cursor == null) {
            return null;
        } else if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }
}
