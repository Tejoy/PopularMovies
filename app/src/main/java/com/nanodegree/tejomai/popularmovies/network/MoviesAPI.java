package com.nanodegree.tejomai.popularmovies.network;

import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.models.MovieGridJSONResponse;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewsJSONResponse;
import com.nanodegree.tejomai.popularmovies.models.MovieVideosJSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by tejomai on 27/01/17.
 */

public interface MoviesAPI {

    @GET("movie/{url_key}")
    Call<MovieGridJSONResponse> loadGridItemsCall(@Path("url_key") String url_key, @Query("api_key") String api_key, @Query("language") String language);

    @GET("movie/{id}/videos")
    Call<MovieVideosJSONResponse> loadVideosCall(@Path("id") String id, @Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    Call<MovieReviewsJSONResponse> loadReviewsCall(@Path("id") String id, @Query("api_key") String api_key);

    @GET("movie/{id}")
    Call<MovieGridItem> loadMovieDetailCall(@Path("id") String id, @Query("api_key") String api_key);

}
