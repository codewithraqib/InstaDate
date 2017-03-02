package com.example.raqib.instadate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {

    Context context;

    private final List<NewsItems> mValues;

    static  int i = 0;
    static SharedPreferences sharedPreferencesBookmark;
    public static String title;
    private static String link;
    private static String bookmarkHeading;
    private static String bookmarkDetailedNews;
    private static String bookmarkLink;
    private static String bookmarkImageUrl;
    private static String bookmarkNewsDate;
    private static List<NewsItems> bookmarkedNewsList;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    MyNewsRecyclerViewAdapter(List<NewsItems> items) {
        mValues = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.title.setText(mValues.get(position).getTitle());

        holder.description.setText(mValues.get(position).getDescription());

        holder.time.setText(mValues.get(position).getDate());



        //SETTING THE NAME OF THE WEBSITE AT THE "MORE AT LINK"

        String linkToCheck = mValues.get(position).getLink();
        String FE = "www.financialexpress.com";
        String NYT = "www.nytimes.com";
        String OI = "www.oneindia.com";
        String SD = "www.sciencedaily.com";
        String BBC = "www.bbc.co.uk";
        String Rediff = "www.rediff.com";
        String KG = "kashmirglobal.com";
        String DE = "www.dailyexcelsior.com";

        if(linkToCheck.toLowerCase().contains(FE.toLowerCase()))
            holder.moreAtLink.setText(R.string.FinancialExpress);
        else if(linkToCheck.toLowerCase().contains(NYT.toLowerCase()))
            holder.moreAtLink.setText(R.string.NewYorkTimes);
        else if(linkToCheck.toLowerCase().contains(OI.toLowerCase()))
            holder.moreAtLink.setText(R.string.OneIndia);
        else if(linkToCheck.toLowerCase().contains(SD.toLowerCase()))
            holder.moreAtLink.setText(R.string.ScienceDaily);
        else if(linkToCheck.toLowerCase().contains(BBC.toLowerCase()))
            holder.moreAtLink.setText(R.string.BBC);
        else if(linkToCheck.toLowerCase().contains(Rediff.toLowerCase()))
            holder.moreAtLink.setText(R.string.Rediff);
        else if (linkToCheck.toLowerCase().contains(KG.toLowerCase()))
            holder.moreAtLink.setText(R.string.kashmirglobal_com);
        else if (linkToCheck.toLowerCase().contains(DE.toLowerCase()))
            holder.moreAtLink.setText(R.string.dailyexcelsior);



        //MORE AT HERE ON CLICK HANDLING
        holder.moreAtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = mValues.get(position).getLink();
                MyNewsRecyclerViewAdapter.this.goToActivity(link);
            }
        });

        //BOOKMARK ON CLICKING
        holder.bookmarkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Log.e("POSITION","is"+ String.valueOf(position));
                holder.bookmarkButton.setColorFilter(Color.BLUE);

                bookmarkImageUrl = mValues.get(position).getImgUrl();
                bookmarkHeading = mValues.get(position).getTitle();
                // holder.description.setTextColor(Color.CYAN);
                bookmarkDetailedNews = mValues.get(position).getDescription();
                bookmarkLink = mValues.get(position).getLink();
                bookmarkNewsDate = mValues.get(position).getDate();


                MyNewsRecyclerViewAdapter.this.bookmarkCurrentFeed(bookmarkImageUrl, bookmarkHeading, bookmarkDetailedNews, bookmarkLink, bookmarkNewsDate, position);
                // holder.bookmarkButton.setColorFilter(Color.BLUE);
                Toast.makeText(context, "Feed Bookmarked!", Toast.LENGTH_SHORT).show();
            }


        });

        //SHARE SCREENSHOT ONCLICK HANDLING
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareWith(holder,position) ;
            }
        });

        loadImage(holder,position);

    }

    private void bookmarkCurrentFeed(String bookmarkImageUrl, String bookmarkHeading, String bookmarkDetailedNews, String bookmarkLink, String bookmarkNewsDate, int pos) {
//BM_count+=1;

//bt.clearColorFilter();
//if(!BM_Subscribed){
        sharedPreferencesBookmark = context.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);
        // Log.e("sixeof","bm:::::"+sharedPreferencesBookmark.getInt("sizeOfBM",1));
        int val;
        if (sharedPreferencesBookmark.getInt("sizeOfBM", 0) == 0) {
//            Log.e("sixeof", "bmlist::::" + sharedPreferencesBookmark.getInt("sizeOfBM", 0));
            sharedPreferencesBookmark.edit().putInt("sizeOfBM", 1).apply();
            val = sharedPreferencesBookmark.getInt("sizeOfBM", 1);
//            Log.e("value", "is" + val);
        } else {
            val = sharedPreferencesBookmark.getInt("sizeOfBM", 1);
//            Log.e("value", "is" + val);
            sharedPreferencesBookmark.edit().putInt("sizeOfBM", ++val).apply();

//            Log.e("value", "is" + val);
        }
        try {
            NewsItems bookmarkNewsItems = new NewsItems();

            bookmarkNewsItems.setImgUrl(bookmarkImageUrl);
            bookmarkNewsItems.setTitle(bookmarkHeading);
            bookmarkNewsItems.setDescription(bookmarkDetailedNews);
            bookmarkNewsItems.setLink(bookmarkLink);
            bookmarkNewsItems.setDate(bookmarkNewsDate);
            bookmarkedNewsList.add(bookmarkNewsItems);}catch (Exception e){Log.e("after news created","to"+e);}
        sharedPreferencesBookmark.edit().putString("BMImageUrl" + String.valueOf(val), bookmarkImageUrl).apply();
        sharedPreferencesBookmark.edit().putString("BMHeading" + String.valueOf(val), bookmarkHeading).apply();
        Log.e("BMHEADING", "is:::" + sharedPreferencesBookmark.getString("BMHeading" + String.valueOf(val), null));
        sharedPreferencesBookmark.edit().putString("BMDetailedNews" + String.valueOf(val), bookmarkDetailedNews).apply();
        sharedPreferencesBookmark.edit().putString("BMLink" + String.valueOf(val), bookmarkLink).apply();
        sharedPreferencesBookmark.edit().putString("BMNewsDate" + String.valueOf(val), bookmarkNewsDate).apply();
        Log.e(sharedPreferencesBookmark.getString("BMImageUrl" + String.valueOf(val), null), "STRING BOOKMARK");

        sharedPreferencesBookmark.edit().apply();
        //Toast.makeText(getApplicationContext(),"Top News Feeds Subscribed", Toast.LENGTH_SHORT).show();
        Log.e("feed is", "Subscribed");
    }


    // RETRIEVES AN IMAGE SPECIFIED BY THE URL, DISPLAYS IT IN THE UI.

    private void loadImage(final ViewHolder holder, int position) {
        String imageUrl = mValues.get(position).getImgUrl();
        if(imageUrl == null)
            return;

        ImageLoader imageLoader = VolleySingleton.getInstance(context)
                .getImageLoader();

        imageLoader.get(imageUrl, ImageLoader.getImageListener(holder.imageView,
                R.drawable.main_image, android.R.drawable.ic_dialog_alert));

        holder.imageView.setImageUrl(imageUrl, imageLoader);


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        customfonts.MyTextViewTitle title;
        customfonts.MyTextViewDescription description;
        TextView time;
        TextView moreAt;
        TextView moreAtLink;
        NetworkImageView imageView;
        ImageButton bookmarkButton;
        ImageButton shareButton;
        View screenShotView ;


        ViewHolder(View view) {

            super(view);
            mView = view;
            title = (customfonts.MyTextViewTitle) view.findViewById(R.id.newsTitle);
            description = (customfonts.MyTextViewDescription) view.findViewById(R.id.newsDetails);
            time = (TextView) view.findViewById(R.id.newsDate);
            moreAt = (TextView) view.findViewById(R.id.moreAtVirtual);
            moreAtLink = (TextView) view.findViewById(R.id.moreAtLink);
            imageView = (NetworkImageView) view.findViewById(R.id.image);
            bookmarkButton = (ImageButton) view.findViewById(R.id.bookmarkButton);
            shareButton = (ImageButton) view.findViewById(R.id.shareButton);
            screenShotView = view.findViewById(R.id.card_view);

        }
    }
    private void goToActivity(String linkNews) {
        //Intent intent = new Intent(context, WebViewActivity.class);

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("WebPage Link", linkNews);
        context.startActivity(intent);
    }



    // BELOW ONCLICK METHODS FROM CARD VIEW ITEM
    private void shareWith(ViewHolder holder, int position) {
//        verifyStoragePermissions(this);

        Bitmap myBitmap;

        assert holder.screenShotView != null;
        holder.screenShotView.setDrawingCacheEnabled(true);
        myBitmap = Bitmap.createBitmap(holder.screenShotView.getDrawingCache());
        holder.screenShotView.setDrawingCacheEnabled(false);


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

        PackageManager packageManager = context.getPackageManager();
        List activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            Log.e("ON SHARE ", String.valueOf(uri));
            context.startActivity(Intent.createChooser(intent, "Share via.."));
        }
    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
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