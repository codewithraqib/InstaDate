package com.example.raqib.instadate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {
    Context context;

    private final List<NewsItems> mValues;
    static List<NewsItems> bookmarkedNewsList;     //LIST OF BOOKMARKED FEEDS

    static  int i = 0;
    public static  String title ;
    private static NewsItems bookmarkNewsItems;



    static SharedPreferences bookmarkSharedPreference;
    private static String link;

    MyNewsRecyclerViewAdapter(List<NewsItems> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();


        bookmarkNewsItems = new NewsItems();
        bookmarkedNewsList = new ArrayList<NewsItems>();

        bookmarkSharedPreference = context.getSharedPreferences("com.example.raqib.instadate", MODE_PRIVATE);

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


        //BOOKMARK ONCLICK HANDLING
        holder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookmarkTheFeed(holder,position) ;
            }
        });

        loadImage(holder,position);

    }

    private void bookmarkTheFeed(final ViewHolder holder, final int position) {


        String Data="";

        title = mValues.get(position).getTitle();


//              holder.bookmarkButton.setColorFilter(Color.MAGENTA);



//        bookmarkSharedPreference.edit().putString("title", title).apply();
//
//        String wholeString = bookmarkSharedPreference.getAll().toString();
//
//        int  startString = wholeString.indexOf("title=");
//        int  endString = wholeString.indexOf(',',50);
//        String finalString = wholeString.substring(startString,endString);
//
//        Log.e("Title is ", finalString);
//        List<String> bookmarkedListFinal = new ArrayList<String>();
//        bookmarkedListFinal.add(finalString);
//        Log.e("size is", String.valueOf(bookmarkedListFinal.size()));



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

        }
    }
    private void goToActivity(String linkNews) {
        //Intent intent = new Intent(context, WebViewActivity.class);

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("WebPage Link", linkNews);
        context.startActivity(intent);
    }


}