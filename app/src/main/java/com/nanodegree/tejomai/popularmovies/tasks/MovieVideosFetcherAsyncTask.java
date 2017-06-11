package com.nanodegree.tejomai.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.interfaces.VideosDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.AsyncTaskItem;
import com.nanodegree.tejomai.popularmovies.models.MovieVideoItem;
import com.nanodegree.tejomai.popularmovies.models.MovieVideosJSONResponse;
import com.nanodegree.tejomai.popularmovies.network.MoviesAPI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nanodegree.tejomai.popularmovies.PopularMoviesUtil.BASE_URL;

/**
 * Created by tejomai on 04/05/17.
 */

public class MovieVideosFetcherAsyncTask   extends AsyncTask<String,Void,AsyncTaskItem<List<MovieVideoItem>>> {

    private final String TAG = "MovieVideosAsyncTask";

    public void setVideosDownloadComplete(VideosDownloadComplete videosDownloadCompete) {
        this.videosDownloadCompete = videosDownloadCompete;
    }

    VideosDownloadComplete videosDownloadCompete = null;


    @Override
    protected AsyncTaskItem<List<MovieVideoItem>> doInBackground(String... params) {
        {

            if (params.length == 0) {
                return null;
            }

            if (params[1].equals(PopularMoviesUtil.DEFAULT_API_KEY)) {
                Log.e(TAG, "Please set the API key!!");
                return null;
            }

            Log.i(TAG, "url " + params[0]);

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);

            Call<MovieVideosJSONResponse> videosCall = null;

            videosCall = moviesAPI.loadVideosCall(params[0],params[1]);

            if (videosCall == null) {
                return new AsyncTaskItem<List<MovieVideoItem>>();
            }
            try {
                Response<MovieVideosJSONResponse> response = videosCall.execute();
                List<MovieVideoItem> dataList = response.body().getDataList();
                if(response!=null && response.body()!=null) {
                    return new AsyncTaskItem<List<MovieVideoItem>>(dataList);
                }
            } catch (IOException e) {
                Log.e(TAG, "" + e.getMessage());
                e.printStackTrace();
            }
            return new AsyncTaskItem<List<MovieVideoItem>>();
        }
    }


        @Override
        protected void onPostExecute(AsyncTaskItem<List<MovieVideoItem>> result) {
            super.onPostExecute(result);
            if(result.getResult()!=null){
                videosDownloadCompete.onSuccess(result.getResult());
            }else if(result.getError()!=null){
                videosDownloadCompete.onFailure(result.getError().getMessage());
                Log.e(TAG,result.getError().getStackTrace().toString());
            }else{
                videosDownloadCompete.onFailure(null);
            }
        }

}
