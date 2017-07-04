package com.nanodegree.tejomai.popularmovies.ui;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.R;
import com.nanodegree.tejomai.popularmovies.adapters.ReviewsRVAdapter;
import com.nanodegree.tejomai.popularmovies.db.FavoritesContentProvider;
import com.nanodegree.tejomai.popularmovies.db.FavoritesTable;
import com.nanodegree.tejomai.popularmovies.interfaces.MovieDetailsDownloadComplete;
import com.nanodegree.tejomai.popularmovies.interfaces.RecyclerViewHeight;
import com.nanodegree.tejomai.popularmovies.interfaces.ReviewsDownloadComplete;
import com.nanodegree.tejomai.popularmovies.interfaces.VideosDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewItem;
import com.nanodegree.tejomai.popularmovies.models.MovieVideoItem;
import com.nanodegree.tejomai.popularmovies.tasks.MovieDetailFetcherAsyncTask;
import com.nanodegree.tejomai.popularmovies.tasks.MovieReviewsFetcherAsyncTask;
import com.nanodegree.tejomai.popularmovies.tasks.MovieVideosFetcherAsyncTask;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.nanodegree.tejomai.popularmovies.R.id.vote;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_ID;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_OVERVIEW;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_RELEASE;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_TITLE;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_URL;
import static com.nanodegree.tejomai.popularmovies.db.FavoritesTable.TABLE_NAME_COLUMN_VOTE;

public class MovieDetailActivity extends AppCompatActivity implements RecyclerViewHeight {

    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";
    private static final String TRAILER = "Trailer";
    private int taskCount2 = 2;
    private int taskCount1 = 1;
    final String TAG = "MovieDetailActivity";
    private final String PARAM_LANGUAGE = "en-US";
    private final String key_instance_movie_item = "movieItem";
    ProgressBar review_download, trailer_progress;
    ImageView thumbnail_img, favourite_star;
    ImageView trailer_play, trailer_share;
    TextView tv_title, tv_vote, tv_overview, tv_release_date, title_reviews, trailer;
    RecyclerView reviewList;
    Toolbar toolbar;
    ProgressDialog progressDialog = null;
    private String key;
    private ReviewsRVAdapter adapter;
    private SharedPreferences sharedPreferences;
    private CollapsingToolbarLayout layout;
    private AppBarLayout appBarLayout;
    private MovieGridItem gridItem;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.coordinator_layout);

        layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        layout.setTitleEnabled(true);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        favourite_star = (ImageView) findViewById(R.id.star);
        trailer_progress = (ProgressBar) findViewById(R.id.trailer_progress);
        review_download = (ProgressBar) findViewById(R.id.reviews_progress);
        thumbnail_img = (ImageView) findViewById(R.id.thumbnail_image);
        trailer = (TextView) findViewById(R.id.trailer);
        trailer_play = (ImageView) findViewById(R.id.trailer_play);
        trailer_share = (ImageView) findViewById(R.id.trailer_share);
        tv_title = (TextView) findViewById(R.id.title);
        tv_overview = (TextView) findViewById(R.id.overview);
        tv_release_date = (TextView) findViewById(R.id.release_date);
        tv_vote = (TextView) findViewById(vote);
        title_reviews = (TextView) findViewById(R.id.title_reviews);
        reviewList = (RecyclerView) findViewById(R.id.reviews_list);

        Intent intent = getIntent();
        if (intent != null) {
            boolean isFavFilterDetails = intent.getBooleanExtra(PopularMoviesUtil.ACTION_FAV_FILTER_DETAIL, false);
            if (!isFavFilterDetails && !PopularMoviesUtil.isNetworkAvailable(this)) {
                showAlertMessage(getString(R.string.no_network));
                return;
            }
            //restore state if present
            if (savedInstanceState != null) {
                gridItem = (MovieGridItem) savedInstanceState.get(key_instance_movie_item);
                updateMovieDetails(gridItem);
            } else {
                gridItem = intent.getParcelableExtra(PopularMoviesUtil.EXTRA_GRID_ITEM);
                //update the base layout of the page
                updateMovieDetails(gridItem);
                if (isFavFilterDetails) {
                    //fetch movie details to update the vote average to latest
                    fetchMovieDetails(gridItem);
                }
            }
            fetchReviewsAndVideos(gridItem);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(key_instance_movie_item, gridItem);
    }

    private void updateMovieDetails(final MovieGridItem item) {

        tv_title.setText(item.getOriginal_title());
        tv_overview.setText(item.getOverview());
        tv_release_date.setText(item.getRelease_date());
        tv_vote.setText(item.getVote_average());
        sharedPreferences = getFavouritesPref();
        boolean isFavMovie = sharedPreferences.getBoolean(item.getId(), false);
        updateFavStarState(isFavMovie);

        favourite_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sharedPreferences == null) {
                    sharedPreferences = getFavouritesPref();
                }
                boolean isFav = sharedPreferences.getBoolean(item.getId(), false);
                if (isFav) {
                    deleteFavoriteItem(item.getId());

                } else {
                    insertFavoriteItem(item.getId(), item.getPosterPath(), item.getOriginal_title(), item.getRelease_date(), item.getVote_average(), item.getOverview());
                }
                sharedPreferences.edit().putBoolean(item.getId(), !isFav).commit();
                updateFavStarState(!isFav);
            }
        });

        trailer.setVisibility(View.VISIBLE);
        trailer_progress.setVisibility(View.VISIBLE);

        adapter = new ReviewsRVAdapter(getBaseContext(), null, this);
        reviewList.setAdapter(adapter);
        reviewList.setLayoutManager(new LinearLayoutManager(this));

        String url = PopularMoviesUtil.getUrl(item);
        Picasso.with(getBaseContext()).load(url).into(thumbnail_img);
        layout.setTitle(" ");
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    layout.setTitle(item.getOriginal_title());
                    tv_title.setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    layout.setTitle(" ");
                    isShow = false;
                    tv_title.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void insertFavoriteItem(String id, String url, String title, String releaseDate, String vote, String overview){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TABLE_NAME_COLUMN_ID, id);
        contentValues.put(TABLE_NAME_COLUMN_URL, url);
        contentValues.put(TABLE_NAME_COLUMN_TITLE, title);
        contentValues.put(TABLE_NAME_COLUMN_RELEASE, releaseDate);
        contentValues.put(TABLE_NAME_COLUMN_OVERVIEW, overview);
        contentValues.put(TABLE_NAME_COLUMN_VOTE, vote);
        getContentResolver().insert(FavoritesContentProvider.CONTENT_URI,contentValues);
    }

    private void deleteFavoriteItem(String id){
        String where_clause = FavoritesTable.TABLE_NAME_COLUMN_ID+" = ?";
        getContentResolver().delete(FavoritesContentProvider.CONTENT_URI,where_clause,new String[]{id});
    }

    private void updateFavStarState(boolean isFav) {
        if (isFav) {
            favourite_star.setImageResource(R.drawable.star_selected);
        } else {
            favourite_star.setImageResource(R.drawable.star_not_selected);
        }
    }

    private SharedPreferences getFavouritesPref() {
        return getSharedPreferences(PopularMoviesUtil.FAV_MOVIE_PREF, MODE_PRIVATE);
    }

    private void updateReviewTrailer(MovieGridItem item) {
        taskCount2--;
        if (taskCount2 > 0) {
            return;
        }
        trailer_progress.setVisibility(View.GONE);
        if (item.getTrailers() != null && item.getTrailers().size() > 0 && ((key = getTrailerKey(item)) != null)) {
            trailer_play.setVisibility(View.VISIBLE);
            trailer_share.setVisibility(View.VISIBLE);
            trailer.setText(R.string.trailer);
        } else {
            trailer_play.setVisibility(View.GONE);
            trailer_share.setVisibility(View.GONE);
            trailer.setText(R.string.no_trailer);
        }

        trailer_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key != null) {
                    Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + key));
                    youtubeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(youtubeIntent);
                }
            }
        });

        trailer_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (key != null) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("video/*");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(YOUTUBE_BASE_URL + key));
                    startActivity(Intent.createChooser(share, "Share Trailer"));
                }
            }
        });

        List<MovieReviewItem> reviews = item.getReviews();
        if (reviews != null && reviews.size() > 0) {

            adapter.addItems(reviews);
            adapter.notifyDataSetChanged();
        } else {
            title_reviews.setText(R.string.no_review);
        }
        review_download.setVisibility(View.GONE);
    }

    private String getTrailerKey(MovieGridItem item) {
        if (item.getTrailers().size() > 0) {
            for (MovieVideoItem video : item.getTrailers()) {
                if (video.getType().equals(TRAILER)) {
                    return video.getKey();
                }
            }
        }
        return null;
    }


    private void fetchMovieDetails(final MovieGridItem item) {
        taskCount1  =1;
        if (!PopularMoviesUtil.isNetworkAvailable(this)) {
            return;
        }
        showProgressDialog();
        try {
            ExecutorService taskExecutor = Executors.newFixedThreadPool(taskCount1);

            final MovieDetailFetcherAsyncTask detailsFetcherTask = new MovieDetailFetcherAsyncTask();
            detailsFetcherTask.setMovieDetailsDownloadComplete(new MovieDetailTaskCallback());
            detailsFetcherTask.execute(PopularMoviesUtil.PREF_FILTER_MOVIE_DETAIL, item.getId(), PopularMoviesUtil.PARAM_API_KEY, PARAM_LANGUAGE);
        } catch (Exception e) {
            e.printStackTrace();
            updateReviewTrailer(gridItem);
        }
    }

    private void fetchReviewsAndVideos(final MovieGridItem item) {
        taskCount2 = 2;
        gridItem = item;
        if (!PopularMoviesUtil.isNetworkAvailable(this)) {
            taskCount2 = 0;
            updateReviewTrailer(gridItem);
            return;
        }
        try {
            ExecutorService taskExecutor = Executors.newFixedThreadPool(taskCount2);
            final MovieReviewsFetcherAsyncTask reviewFetchTask = new MovieReviewsFetcherAsyncTask();
            final MovieVideosFetcherAsyncTask videosFetchTask = new MovieVideosFetcherAsyncTask();

            reviewFetchTask.setReviewsDownloadComplete(new ReviewsTaskCallback());
            taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    reviewFetchTask.execute(item.getId(), PopularMoviesUtil.PARAM_API_KEY);
                }
            });
            videosFetchTask.setVideosDownloadComplete(new VideosTaskCallback());
            taskExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    videosFetchTask.execute(item.getId(), PopularMoviesUtil.PARAM_API_KEY);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            updateReviewTrailer(gridItem);
        }
    }

    private void showProgressDialog() {
        closeProgressDialog();
        progressDialog = new ProgressDialog(MovieDetailActivity.this);
        progressDialog.setMessage("Fetching data....");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void showAlertMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void setHeight(int height) {
        reviewList.getLayoutParams().height = height;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MovieDetail Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class ReviewsTaskCallback implements ReviewsDownloadComplete {

        @Override
        public void onSuccess(List<MovieReviewItem> reviews) {
            gridItem.setReviews(reviews);
            updateReviewTrailer(gridItem);
        }

        @Override
        public void onFailure(String message) {
            gridItem.setReviews(null);
            updateReviewTrailer(gridItem);
        }
    }

    class VideosTaskCallback implements VideosDownloadComplete {

        @Override
        public void onSuccess(List<MovieVideoItem> videos) {
            gridItem.setTrailers(videos);
            updateReviewTrailer(gridItem);
        }

        @Override
        public void onFailure(String message) {
            gridItem.setTrailers(null);
            updateReviewTrailer(gridItem);
        }
    }

    class MovieDetailTaskCallback implements MovieDetailsDownloadComplete {

        @Override
        public void onSuccess(MovieGridItem item) {
            closeProgressDialog();
            updateMovieDetails(item);
        }

        @Override
        public void onFailure(String message) {
            closeProgressDialog();
        }
    }
}
