package com.example.raqib.instadate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder> {
Context context;

    private final List<NewsItems> mValues;
    static int pos;
    static String link;
    static String imageUrl;
    DisplayImageOptions options;
    ImageLoader imageLoader;

    public MyNewsRecyclerViewAdapter(List<NewsItems> items) {
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

            holder.title.setText(mValues.get(position).getName());
            holder.description.setText(mValues.get(position).getDescription());
            String dateToFormat = mValues.get(position).getDate();

            holder.time.setText(mValues.get(position).getDate());


        //MAKE A DATE OBJECT OUT OF STRING
//        String dtStart = "2010-10-15T09:27:37Z";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        try {
//            Date date = format.parse(dtStart);
//            System.out.println(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        holder.moreAtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                link = mValues.get(position).getLink();
                MyNewsRecyclerViewAdapter.this.goToActivity(link);
            }
        });
        loadImage(holder,position);

    }

    private void loadImage(final ViewHolder holder, int position) {
        imageUrl = mValues.get(position).getImgUrl();
        if(imageUrl == null)
            return;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader = ImageLoader.getInstance();    //COMMENTED FOR TESTING
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        TextView title;
        TextView description;
        TextView time;
        TextView moreAt;
        TextView moreAtLink;
        ImageView imageView;
        ProgressBar progressBar;

        public ViewHolder(View view) {

            super(view);
            mView = view;
            title = (TextView) view.findViewById(R.id.newsTitle);
            description = (TextView) view.findViewById(R.id.news);
            time = (TextView) view.findViewById(R.id.newsDate);
            moreAt = (TextView) view.findViewById(R.id.moreAtVirtual);
            moreAtLink = (TextView) view.findViewById(R.id.moreAtLink);
            imageView = (ImageView) view.findViewById(R.id.image);
            progressBar = (ProgressBar)view.findViewById(R.id.progressBarNews);

        }
    }
    private void goToActivity(String linkNews) {
        //Intent intent = new Intent(context, WebViewActivity.class);

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("WebPage Link", linkNews);
        context.startActivity(intent);

    }
}
