package com.nanodegree.tejomai.popularmovies.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.adapters.ReviewsRVAdapter;
import com.nanodegree.tejomai.popularmovies.db.DBHelper;
import com.nanodegree.tejomai.popularmovies.interfaces.RecyclerViewHeight;
import com.nanodegree.tejomai.popularmovies.interfaces.ReviewsDownloadComplete;
import com.nanodegree.tejomai.popularmovies.interfaces.VideosDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewItem;
import com.nanodegree.tejomai.popularmovies.models.MovieVideoItem;
import com.nanodegree.tejomai.popularmovies.tasks.MovieReviewsFetcherAsyncTask;
import com.nanodegree.tejomai.popularmovies.tasks.MovieVideosFetcherAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MovieDetailActivity extends AppCompatActivity implements RecyclerViewHeight{

    final String TAG = "MovieDetailActivity";
    ProgressBar trailer_download,review_download;
    ImageView thumbnail_img, trailer_img, favourite_star;
    Button trailer_play_status;
    TextView tv_title,tv_vote,tv_overview,tv_release_date,title_reviews;
    RecyclerView reviewList;
    Toolbar toolbar;
    MovieGridItem item;
    private static long WAIT_INTERVAL = 10;
    ProgressDialog progressDialog = null;
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String TRAILER = "Trailer";
    private static int taskCount = 0;
    private String key;
    private ReviewsRVAdapter adapter;
    private SharedPreferences sharedPreferences;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.coordinator_layout);

        dbHelper = new DBHelper(getBaseContext());
        final CollapsingToolbarLayout layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        layout.setTitleEnabled(true);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        favourite_star = (ImageView) findViewById(R.id.star);
        trailer_download = (ProgressBar) findViewById(R.id.trailer_progress);
        review_download = (ProgressBar) findViewById(R.id.reviews_progress);
        thumbnail_img = (ImageView) findViewById(R.id.thumbnail_image);
        trailer_img = (ImageView) findViewById(R.id.trailer_image);
        trailer_play_status = (Button) findViewById(R.id.trailer_play_image);
        tv_title = (TextView) findViewById(R.id.title);
        tv_overview = (TextView) findViewById(R.id.overview);
        tv_release_date = (TextView) findViewById(R.id.release_date);
        tv_vote = (TextView) findViewById(R.id.vote);
        title_reviews = (TextView) findViewById(R.id.title_reviews);
        reviewList = (RecyclerView) findViewById(R.id.reviews_list);

        String url;

        Intent intent = getIntent();
        if(intent!=null){
            //retrieve data from intent and set in the layout
            item = intent.getParcelableExtra(PopularMoviesUtil.EXTRA_GRID_ITEM);
            url = intent.getStringExtra(PopularMoviesUtil.EXTRA_IMAGE_URL);

            tv_title.setText(item.getOriginal_title());
            tv_overview.setText(item.getOverview());
            tv_release_date.setText(item.getRelease_date());
            tv_vote.setText(item.getVote_average());
            sharedPreferences = getFavouritesPref();
            boolean isFav = sharedPreferences.getBoolean(item.getId(),false);
            updateFavStarState(isFav);

            favourite_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(sharedPreferences == null) {
                        sharedPreferences = getFavouritesPref();
                    }
                    boolean isFav = sharedPreferences.getBoolean(item.getId(),false);
                    if(isFav){
                        dbHelper.removeFavourite(item.getId());
                    }else{
                        dbHelper.insertFavourite(item.getId(),item.getPosterPath(),item.getOriginal_title(),item.getRelease_date(),item.getVote_average());
                    }
                    sharedPreferences.edit().putBoolean(item.getId(),!isFav).commit();
                    updateFavStarState(!isFav);
                }
            });

            adapter = new ReviewsRVAdapter(getBaseContext(),null, this);
            reviewList.setAdapter(adapter);
            reviewList.setLayoutManager(new LinearLayoutManager(this));

            Picasso.with(getBaseContext()).load(url).into(thumbnail_img);
            Picasso.with(getBaseContext()).load(url).into(trailer_img);
            layout.setTitle(" ");
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(scrollRange == -1){
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if(scrollRange+verticalOffset == 0){
                        layout.setTitle(item.getOriginal_title());
                        tv_title.setVisibility(View.GONE);
                        isShow = true;
                    }else if (isShow){
                        layout.setTitle(" ");
                        isShow = false;
                        tv_title.setVisibility(View.VISIBLE);
                    }
                }
            });

            fetchReviewsAndVideos();

        }
    }

    private void updateFavStarState(boolean isFav){
        if(isFav){
            favourite_star.setImageResource(R.drawable.star_selected);
        }else{
            favourite_star.setImageResource(R.drawable.star_not_selected);
        }
    }

    private SharedPreferences getFavouritesPref(){
        return getSharedPreferences(PopularMoviesUtil.FAV_MOVIE_PREF,MODE_PRIVATE);
    }

    private void updateView(){
        taskCount--;
        if(taskCount>0){
            return;
        }
        trailer_download.setVisibility(View.GONE);
        trailer_play_status.setVisibility(View.VISIBLE);
        if(item.getTrailers()!=null && item.getTrailers().size()>0 && ((key = getTrailerKey(item)) !=null)){
            trailer_play_status.setText(R.string.play_trailer);
        }else{

            trailer_play_status.setText(R.string.no_trailer);
        }

        trailer_play_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(key !=null) {
                    Log.i(TAG, "youtube url " + YOUTUBE_BASE_URL + key);
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + key));
                    youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(youtubeIntent);

                }else{
                    Toast.makeText(getBaseContext(),"No trailer available",Toast.LENGTH_SHORT).show();
                }

            }
        });

        List<MovieReviewItem> reviews = item.getReviews();
        if(reviews!=null && reviews.size()>0){

            adapter.addItems(reviews);
            adapter.notifyDataSetChanged();
            //setListViewHeightBasedOnChildren(reviewList);
        }else{
            title_reviews.setText(R.string.no_review);
        }
        review_download.setVisibility(View.GONE);
    }

    /*public static void setListViewHeightBasedOnChildren(RecyclerView recyclerView) {
        ReviewsRVAdapter rvAdapter = (ReviewsRVAdapter) recyclerView.getAdapter();
        if (rvAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < rvAdapter.getCount(); i++) {
            view = rvAdapter.getView(i, view, recyclerView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, WindowManager.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = totalHeight + (recyclerView.getDividerHeight() * (rvAdapter.getCount() - 1));
        recyclerView.setLayoutParams(params);
    }*/

    private String getTrailerKey(MovieGridItem item){
        if(item.getTrailers().size()>0){
            for(MovieVideoItem video : item.getTrailers()){
                if(video.getType().equals(TRAILER)){
                    return video.getKey();
                }
            }
        }
        return null;
    }

    private void fetchReviewsAndVideos(){
        if(!PopularMoviesUtil.isNetworkAvailable(this)){
            return;
        }
        taskCount = 2;
        //showProgressDialog();
Log.i(TAG,"start tasks");
        ExecutorService taskExecutor = Executors.newFixedThreadPool(2);

        final MovieReviewsFetcherAsyncTask reviewFetchTask = new MovieReviewsFetcherAsyncTask();
        reviewFetchTask.setReviewsDownloadComplete(new ReviewsTaskCallback());
        //reviewFetchTask.execute(PopularMoviesUtil.PARAM_API_KEY,item.getId());
        //reviewFetchTask.executeOnExecutor(taskExecutor,"reviewTask");

        Future reviewsfuture = taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                reviewFetchTask.execute(item.getId(),PopularMoviesUtil.PARAM_API_KEY);
            }
        });

        final MovieVideosFetcherAsyncTask videosFetchTask = new MovieVideosFetcherAsyncTask();
        videosFetchTask.setVideosDownloadComplete(new VideosTaskCallback());
        //videosFetchTask.execute(PopularMoviesUtil.PARAM_API_KEY,item.getId());
        //videosFetchTask.executeOnExecutor(taskExecutor,"videoTask");

        Future videosfuture =  taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                videosFetchTask.execute(item.getId(),PopularMoviesUtil.PARAM_API_KEY);
            }
        });
       // reviewsfuture.get();
       // videosfuture.get();

        //closeProgressDialog();
        Log.i(TAG,"ended tasks");


    }

    private void showProgressDialog(){
        closeProgressDialog();
        progressDialog = new ProgressDialog(MovieDetailActivity.this);
        progressDialog.setMessage("Fetching data....");
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog !=null && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }


    }

    @Override
    public void setHeight(int height) {
        reviewList.getLayoutParams().height = height;
    }

    class ReviewsTaskCallback implements ReviewsDownloadComplete{

        @Override
        public void onSuccess(List<MovieReviewItem> reviews) {
            Log.i(TAG,"reviews success "+reviews.size());
            item.setReviews(reviews);
            updateView();
        }

        @Override
        public void onFailure(String message) {
            Log.i(TAG,"reviews failure "+message);
            item.setReviews(null);
            updateView();
        }
    }

    class VideosTaskCallback implements VideosDownloadComplete{

        @Override
        public void onSuccess(List<MovieVideoItem> videos) {
            Log.i(TAG,"videos success "+videos.size());
            item.setTrailers(videos);
            updateView();
        }

        @Override
        public void onFailure(String message) {

            Log.i(TAG,"videos failure "+message);
            item.setTrailers(null);
            updateView();
        }
    }
}
