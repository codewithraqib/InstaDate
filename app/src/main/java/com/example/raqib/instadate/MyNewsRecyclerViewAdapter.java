package com.example.raqib.instadate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
    public static  String title ;
    private static String link;

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

        String dateToFormat = mValues.get(position).getDate();

        holder.time.setText(mValues.get(position).getDate());



        //SETTING THE NAME OF THE WEBSITE AT THE "MORE AT LINK"

        String linkToCheck = mValues.get(position).getLink();
        String FE = "www.financialexpress.com";
        String NYT = "www.nytimes.com";
        String OI = "www.oneindia.com";
        String SD = "www.sciencedaily.com";
        String BBC = "www.bbc.co.uk";
        String Rediff = "www.rediff.com";

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



        //MORE AT HERE ON CLICK HANDLING
        holder.moreAtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = mValues.get(position).getLink();
                MyNewsRecyclerViewAdapter.this.goToActivity(link);
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

        TextView title;
        TextView description;
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
            title = (TextView) view.findViewById(R.id.newsTitle);
            description = (TextView) view.findViewById(R.id.newsDetails);
            time = (TextView) view.findViewById(R.id.newsDate);
            moreAt = (TextView) view.findViewById(R.id.moreAtVirtual);
            moreAtLink = (TextView) view.findViewById(R.id.moreAtLink);
            imageView = (NetworkImageView) view.findViewById(R.id.image);
            bookmarkButton = (ImageButton) view.findViewById(R.id.bookmarkButton);
            shareButton = (ImageButton) view.findViewById(R.id.shareButton);
            screenShotView = view.findViewById(R.id.wholeNewsChunk);

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