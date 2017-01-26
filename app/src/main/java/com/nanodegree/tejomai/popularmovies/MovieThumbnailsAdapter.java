package com.nanodegree.tejomai.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 15/01/17.
 */

public class MovieThumbnailsAdapter extends BaseAdapter{

    private final String TAG = "MovieThumbnailsAdapter";

    Context mContext;
    List<MovieGridItem> items = new ArrayList<MovieGridItem>();
    LayoutInflater mInflater;

    public void addItem(MovieGridItem item){
        items.add(item);
    }

    public void addItems(List<MovieGridItem> data){
        items.addAll(data);
    }

    public void clearItems(){
        items.clear();
    }


    public MovieThumbnailsAdapter(Context context,List<MovieGridItem> data){
        mContext = context;
        if(data ==null)
            items = new ArrayList<MovieGridItem>();
        else
            items = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public MovieGridItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        Log.i(TAG,"get view called "+position);

        ViewHolder viewHolder;
        View view = convertView;

        ImageView image;

        if (view == null) {
            view = new ImageView(mContext);
            view = mInflater.inflate(R.layout.grid_layout_item, viewGroup, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.picture);

            view.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        final String url = getUrl(position);
        final MovieGridItem item = getItem(position);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName(mContext, MovieDetailActivity.class.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PopularMoviesUtil.EXTRA_IMAGE_URL,url );
                intent.putExtra(PopularMoviesUtil.EXTRA_OVERVIEW,item.getOverview());
                intent.putExtra(PopularMoviesUtil.EXTRA_RELEASE_DATE,item.getRelease_date());
                intent.putExtra(PopularMoviesUtil.EXTRA_TITLE,item.getOriginal_title());
                intent.putExtra(PopularMoviesUtil.EXTRA_VOTE,item.getVote_average());
                mContext.startActivity(intent);
            }

        });
        //Picasso library used for image fetching and loading
        Picasso.with(mContext).load(url).into(viewHolder.imageView);

        return view;
    }

    private String getUrl(int position){
        StringBuilder builder = new StringBuilder();
        builder.append(PopularMoviesUtil.BASE_URL_THUMBNAIL).append(PopularMoviesUtil.URL_PARAM_SIZE).append(getItem(position).getPosterPath());
        Log.i(TAG,"URL "+builder.toString());
        return builder.toString();
    }

    static class ViewHolder{
        ImageView imageView;

    }
}
