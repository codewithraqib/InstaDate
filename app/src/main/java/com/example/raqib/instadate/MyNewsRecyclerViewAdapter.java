package com.example.raqib.instadate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {
Context context;

    private final List<NewsItems> mValues;
    static public List<NewsItems> bookmarkedNewsList;
    NewsItems bookmarkNewsItems;
    int sizeOfBookmarkedNewsList;
    private static SharedPreferences sharedPreferences;
    static String link;
    private static String bookmarkHeading;
    private static String bookmarkDetailedNews;
    private static String bookmarkLink;
    private static String bookmarkImageUrl;
    private static String bookmarkNewsDate;
    private static String imageUrl;
    private DisplayImageOptions options;

    MyNewsRecyclerViewAdapter(List<NewsItems> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();


        bookmarkedNewsList = new ArrayList<NewsItems>();
        sharedPreferences = context.getSharedPreferences("com.example.raqib.instadate", Context.MODE_PRIVATE);
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

        if(linkToCheck.toLowerCase().contains(FE.toLowerCase()))
            holder.moreAtLink.setText(R.string.FinancialExpress);
        else if(linkToCheck.toLowerCase().contains(NYT.toLowerCase()))
            holder.moreAtLink.setText(R.string.NewYorkTimes);
        else if(linkToCheck.toLowerCase().contains(OI.toLowerCase()))
            holder.moreAtLink.setText(R.string.OneIndia);
        else if(linkToCheck.toLowerCase().contains(SD.toLowerCase()))
            holder.moreAtLink.setText(R.string.ScienceDaily);



        //MAKE A DATE OBJECT OUT OF STRING
//        String dtStart = "2010-10-15T09:27:37Z";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        try {
//            Date date = format.parse(dtStart);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


        //MORE AT HERE ON CLICK HANDLING
        holder.moreAtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = mValues.get(position).getLink();
                MyNewsRecyclerViewAdapter.this.goToActivity(link);
            }
        });


        //BOOKMARK ONCLICK HANDLING
        holder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.bookmarkButton.setColorFilter(Color.MAGENTA);

                Toast.makeText(context,"Feed Bookmarked!", Toast.LENGTH_SHORT).show();

                bookmarkImageUrl = mValues.get(position).getImgUrl();
                bookmarkHeading = mValues.get(position).getTitle();
                bookmarkDetailedNews = mValues.get(position).getDescription();
                bookmarkLink = mValues.get(position).getLink();
                bookmarkNewsDate = mValues.get(position).getDate();

                MyNewsRecyclerViewAdapter.this.bookmarkCurrentFeed(bookmarkImageUrl,bookmarkHeading,bookmarkDetailedNews,bookmarkLink,bookmarkNewsDate);
            }
        });
        loadImage(holder,position);

    }

    private void loadImage(final ViewHolder holder, int position) {
        imageUrl = mValues.get(position).getImgUrl();
        if(imageUrl == null)
            return;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        //T0 KEEP THE DOWNLOADED IMAGES IN THE CACHE WE ARE IMPLEMENTING THE BELOW CODE
        options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        ImageLoadingListener listener = new ImageLoadingListener(){

            @Override
            public void onLoadingStarted(String arg0, View arg1) {
            }

            @Override
            public void onLoadingCancelled(String arg0, View arg1) {
            }

            @Override
            public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            }
        };

        imageLoader.displayImage(imageUrl, holder.imageView,options, listener);
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
        ImageView imageView;
        ProgressBar progressBar;
        ImageButton bookmarkButton;

        ViewHolder(View view) {

            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.newsTitle);
            description = (TextView) view.findViewById(R.id.newsDetails);
            time = (TextView) view.findViewById(R.id.newsDate);
            moreAt = (TextView) view.findViewById(R.id.moreAtVirtual);
            moreAtLink = (TextView) view.findViewById(R.id.moreAtLink);
            imageView = (ImageView) view.findViewById(R.id.image);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBarNews);
            bookmarkButton = (ImageButton) view.findViewById(R.id.bookmarkButton);

        }
    }
    private void goToActivity(String linkNews) {
        //Intent intent = new Intent(context, WebViewActivity.class);

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("WebPage Link", linkNews);
        context.startActivity(intent);
    }

    private void bookmarkCurrentFeed(String bookmarkImageUrl, String bookmarkHeading, String bookmarkDetailedNews, String bookmarkLink, String bookmarkNewsDate) {

        bookmarkNewsItems = new NewsItems();

        bookmarkNewsItems.setImgUrl(bookmarkImageUrl);
        bookmarkNewsItems.setTitle(bookmarkHeading);
        bookmarkNewsItems.setDescription(bookmarkDetailedNews);
        bookmarkNewsItems.setLink(bookmarkLink);
        bookmarkNewsItems.setDate(bookmarkNewsDate);

//        sizeOfBookmarkedNewsList = bookmarkedNewsList.size();
//        Log.e("BookmarkedNewsListSize1", String.valueOf(sizeOfBookmarkedNewsList));


        bookmarkedNewsList.add(bookmarkNewsItems);
//        bookmarkedNewsList.clear();
//        Log.e("BookmarkedNewsList Size", String.valueOf(sizeOfBookmarkedNewsList));
        Log.e("BOOKMARKED News Details", bookmarkedNewsList.get(0).getTitle());
        Log.e("BOOKMARKED News Details", String.valueOf(bookmarkedNewsList.size()));

//        String []items = new String[5] ;
//        items[0] = bookmarkImageUrl;
//        items[1] = bookmarkHeading;
//        items[2] = bookmarkDetailedNews;
//        items[3] = bookmarkLink;
//        items[4] = bookmarkNewsDate;
////        sharedPreferences.edit().putStringSet("item1",items);
//
        Set<String> bookmarkedList = null;
    }

}
