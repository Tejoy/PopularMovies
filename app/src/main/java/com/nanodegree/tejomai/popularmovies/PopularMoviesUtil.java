package com.nanodegree.tejomai.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by tejomai on 15/01/17.
 */

public class PopularMoviesUtil {

    public final static String PREF_NAME = "movies_preferences";
    public final static String PREF_FILTER = "movies_filter";
    public final static String PREF_FILTER_POPULARITY = "movies_filter_popularity";
    public final static String PREF_FILTER_TOP_RATING = "movies_filter_top_rating";
    public final static String PREF_FILTER_DEFAULT = "movies_filter_popularity";

    public final static String URL_POPULARITY = "movie/popular";
    public final static String URL_TOP_RATING = "movie/top_rated";
    public final static String DEFAULT_API_KEY = "<ENTER YOUR API KEY>";

    public final static String BASE_URL_THUMBNAIL = "http://image.tmdb.org/t/p/";
    public final static String URL_PARAM_SIZE = "w185/";

    public final static String EXTRA_IMAGE_URL = "url";
    public final static String EXTRA_GRID_ITEM = "grid_item";
    public final static String EXTRA_TITLE = "title";
    public final static String EXTRA_VOTE = "vote";
    public final static String EXTRA_RELEASE_DATE = "release_date";
    public final static String EXTRA_OVERVIEW = "overview";

    //check network availability
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}
