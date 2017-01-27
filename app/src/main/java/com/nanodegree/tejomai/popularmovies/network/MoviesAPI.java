package com.nanodegree.tejomai.popularmovies.network;

import com.nanodegree.tejomai.popularmovies.models.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tejomai on 27/01/17.
 */

public interface MoviesAPI {

    @GET("movie/popular")
    Call<JSONResponse> loadPopularGridItemsCall(@Query("api_key") String uri, @Query("language") String language);

    @GET("movie/top_rated")
    Call<JSONResponse> loadRatedGridItemsCall(@Query("api_key") String uri,@Query("language") String language);

}
