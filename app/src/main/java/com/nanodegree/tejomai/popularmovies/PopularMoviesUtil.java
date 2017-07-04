package com.nanodegree.tejomai.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;

/**
 * Created by tejomai on 15/01/17.
 */

public class PopularMoviesUtil {

    public final static String PREF_NAME = "movies_preferences";
    public final static String PREF_FILTER = "movies_filter";
    public final static String PREF_FILTER_POPULARITY = "movies_filter_popularity";
    public final static String PREF_FILTER_TOP_RATING = "movies_filter_top_rating";
    public final static String PREF_FILTER_MOVIE_DETAIL = "movies_filter_movie_detail";
    public final static String PREF_FILTER_FAVOURITE = "movies_filter_favourite";
    public final static String PREF_FILTER_DEFAULT = "movies_filter_popularity";
    public final static String URL_KEY_POPULAR = "popular";
    public final static String URL_KEY_TOP_RATED = "top_rated";


    public final static String DEFAULT_API_KEY = "<ENTER YOUR API KEY>";

    public final static String PARAM_API_KEY = BuildConfig.POPULAR_MOVIES_API_KEY;
    public final static String BASE_URL_THUMBNAIL = "http://image.tmdb.org/t/p/";
    public final static String BASE_URL = "http://api.themoviedb.org/3/";
    public final static String URL_PARAM_SIZE = "w185/";

    public final static String EXTRA_GRID_ITEM = "grid_item";
    public static final String FAV_MOVIE_PREF = "favourite_movie_pref";
    public static final String ACTION_FAV_FILTER_DETAIL = "favourite_filter_detail";

    //check network availability
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getUrl(MovieGridItem item){
        StringBuilder builder = new StringBuilder();
        builder.append(PopularMoviesUtil.BASE_URL_THUMBNAIL).append(PopularMoviesUtil.URL_PARAM_SIZE).append(item.getPosterPath());
        return builder.toString();
    }


}
