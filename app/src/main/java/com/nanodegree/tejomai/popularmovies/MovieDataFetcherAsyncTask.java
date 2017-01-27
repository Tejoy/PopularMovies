package com.nanodegree.tejomai.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.models.AsyncTaskItem;
import com.nanodegree.tejomai.popularmovies.models.JSONResponse;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.network.MoviesAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tejomai on 15/01/17.
 */

public class MovieDataFetcherAsyncTask extends AsyncTask<String,Void,AsyncTaskItem<List<MovieGridItem>>>{

    private final String TAG = "MovieDataFetcher";

    public void setDataDownloadCompete(DataDownloadComplete dataDownloadCompete) {
        this.dataDownloadCompete = dataDownloadCompete;
    }

    DataDownloadComplete dataDownloadCompete = null;
    private final String BASE_URL = "http://api.themoviedb.org/3/";

    private final String key_api_key = "api_key";
    private final String key_language = "language";
    private final String key_page = "page";
    private final String key_region = "region";

    //build url based on sort type
    private Uri buildURL(String type,String api_key, String language){
        Uri.Builder builder = Uri.parse(BASE_URL).buildUpon();
        if(type.equals(PopularMoviesUtil.PREF_FILTER_POPULARITY)){
            builder.appendEncodedPath(PopularMoviesUtil.URL_POPULARITY);
        }else if(type.equals(PopularMoviesUtil.PREF_FILTER_TOP_RATING)){
            builder.appendEncodedPath(PopularMoviesUtil.URL_TOP_RATING);
        }
        builder.appendQueryParameter(key_api_key, api_key);
        builder.appendQueryParameter(key_language,language);
        return builder.build();
    }

    @Override
    protected AsyncTaskItem<List<MovieGridItem>> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        if(params[0].equals(PopularMoviesUtil.DEFAULT_API_KEY)){
            Log.e(TAG,"Please set the API key!!");
            return null;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
        String type = params[0];
        Call<JSONResponse> call = null;

        if(type.equals(PopularMoviesUtil.PREF_FILTER_POPULARITY)){
            call = moviesAPI.loadPopularGridItemsCall(params[1],params[2]);
        }else if(type.equals(PopularMoviesUtil.PREF_FILTER_TOP_RATING)){
            call = moviesAPI.loadRatedGridItemsCall(params[1],params[2]);
        }

        if(call == null) {
            return new AsyncTaskItem<List<MovieGridItem>>();
        }
            try {
                Response<JSONResponse> response = call.execute();
                List<MovieGridItem> dataList = response.body().getDataList();
                return new AsyncTaskItem<List<MovieGridItem>>(dataList);

            } catch (IOException e) {
                e.printStackTrace();
                return new AsyncTaskItem<List<MovieGridItem>>();
            }

    }

    @Override
    protected void onPostExecute(AsyncTaskItem<List<MovieGridItem>> result) {
        super.onPostExecute(result);
        if(result!=null){
            dataDownloadCompete.onSuccess(result.getResult());
        }else{
            dataDownloadCompete.onFailure(result.getError().getMessage());
            Log.e(TAG,result.getError().getStackTrace().toString());
        }
    }
}
