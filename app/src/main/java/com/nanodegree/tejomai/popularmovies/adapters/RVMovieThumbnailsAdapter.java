package com.nanodegree.tejomai.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.ui.MovieDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 27/01/17.
 */

public class RVMovieThumbnailsAdapter extends RecyclerView.Adapter<RVMovieThumbnailsAdapter.ViewHolder> {

    private final String TAG = "RVMovieAdapter";

    Context mContext;
    List<MovieGridItem> items = new ArrayList<MovieGridItem>();
    LayoutInflater mInflater;


    public RVMovieThumbnailsAdapter(Context context, List<MovieGridItem> data){
        mContext = context;
        if(data ==null)
            items = new ArrayList<MovieGridItem>();
        else
            items = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.grid_layout_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieGridItem item = items.get(position);
        String url = PopularMoviesUtil.getUrl(item);
        Picasso.with(mContext).load(url).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void clearItems(){
        items.clear();
    }

    public void addItems(List<MovieGridItem> posterPaths){
        items.addAll(posterPaths);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.picture);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            MovieGridItem item = items.get(position);
            if(position != RecyclerView.NO_POSITION){

                SharedPreferences prefs = mContext.getSharedPreferences(PopularMoviesUtil.PREF_NAME,Context.MODE_PRIVATE);
                String type = prefs.getString(PopularMoviesUtil.PREF_FILTER,PopularMoviesUtil.PREF_FILTER_DEFAULT);
                boolean isFavGridItem = type.equals(PopularMoviesUtil.PREF_FILTER_FAVOURITE);
                Intent intent = new Intent();
                intent.setClassName(mContext, MovieDetailActivity.class.getName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(PopularMoviesUtil.EXTRA_GRID_ITEM,item);
                intent.putExtra(PopularMoviesUtil.ACTION_FAV_FILTER_DETAIL,isFavGridItem);
                mContext.startActivity(intent);
            }
        }
    }
}
