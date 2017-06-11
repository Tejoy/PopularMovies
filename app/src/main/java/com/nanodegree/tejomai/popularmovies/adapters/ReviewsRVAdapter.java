package com.nanodegree.tejomai.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.interfaces.RecyclerViewHeight;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 04/05/17.
 */

public class ReviewsRVAdapter extends RecyclerView.Adapter<ReviewsRVAdapter.ViewHolder> {

    private final String TAG = "ReviewsRVAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<MovieReviewItem> items = new ArrayList<>();
    private RecyclerViewHeight rvHeight;

    public ReviewsRVAdapter(Context mContext, List<MovieReviewItem> data, RecyclerViewHeight rvHeight) {
        this.mContext = mContext;
        this.rvHeight = rvHeight;
        mInflater = LayoutInflater.from(mContext);
        if(data == null)
            items = new ArrayList<>();
        else
            items = data;

    }


    /*public View getView(int position, View convertView, ViewGroup parent) {

        ReviewsRVAdapter.ViewHolder viewHolder = null;
        LayoutInflater mInflater;

        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.detail_page_review_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);

        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MovieReviewItem item = getItem(position);
        viewHolder.author.setText(item.getAuthor());
        viewHolder.content.setText(item.getContent());

        return convertView;
    }*/

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.detail_page_review_item, parent, false);
        ReviewsRVAdapter.ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MovieReviewItem item = getItem(position);
        holder.author.setText(item.getAuthor());
        holder.content.setText(item.getContent());
        //holder.itemHeight = holder.author.getHeight()+holder.content.getHeight()+40;
        //rvHeight.setHeight(items.size()*30);
    }

    private MovieReviewItem getItem(int position){
        return items.get(position);
    }

    public int getCount(){
        return items.size();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItems(List<MovieReviewItem> list){
        items.clear();
        items.addAll(list);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView author;
        public TextView content;
        public int itemHeight;
        //public Button moreButton;
        int position;

        public ViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.author);
            content = (TextView) itemView.findViewById(R.id.content);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
            MovieReviewItem item = items.get(position);
            String url = item.getUrl();
            if(url !=null){

                Intent reviewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                reviewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(reviewIntent);
            }
        }
    }
}
