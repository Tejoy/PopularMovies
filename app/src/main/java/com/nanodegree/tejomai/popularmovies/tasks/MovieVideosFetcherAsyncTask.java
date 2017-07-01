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
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nanodegree.tejomai.popularmovies.PopularMoviesUtil.BASE_URL;

/**
 * Created by tejomai on 04/05/17.
 */

public class MovieVideosFetcherAsyncTask extends AsyncTask<String, Void, AsyncTaskItem<List<MovieVideoItem>>> {

    private final String TAG = "MovieVideosAsyncTask";
    VideosDownloadComplete videosDownloadCompete = null;

    public void setVideosDownloadComplete(VideosDownloadComplete videosDownloadCompete) {
        this.videosDownloadCompete = videosDownloadCompete;
    }

    @Override
    protected AsyncTaskItem<List<MovieVideoItem>> doInBackground(String... params) {
        {
            if (params.length == 0 || params[1].equals(PopularMoviesUtil.DEFAULT_API_KEY)) {
                return new AsyncTaskItem<List<MovieVideoItem>>();
            }
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
            Call<MovieVideosJSONResponse> videosCall = null;
            videosCall = moviesAPI.loadVideosCall(params[0], params[1]);

            if (videosCall == null) {
                return new AsyncTaskItem<List<MovieVideoItem>>();
            }
            try {
                Response<MovieVideosJSONResponse> response = videosCall.execute();
                List<MovieVideoItem> dataList = response.body().getDataList();
                if (response != null && response.body() != null) {
                    return new AsyncTaskItem<List<MovieVideoItem>>(dataList);
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "UnknownHostException " + e.getMessage());
                e.printStackTrace();
                return new AsyncTaskItem<List<MovieVideoItem>>(e);
            } catch (IOException e) {
                Log.e(TAG, "" + e.getMessage());
                e.printStackTrace();
                return new AsyncTaskItem<List<MovieVideoItem>>(e);
            } catch (Exception e) {
                Log.e(TAG, "" + e.getMessage());
                e.printStackTrace();
                return new AsyncTaskItem<List<MovieVideoItem>>(e);
            }
            return new AsyncTaskItem<List<MovieVideoItem>>();
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskItem<List<MovieVideoItem>> result) {
        super.onPostExecute(result);
        if (result.getResult() != null) {
            videosDownloadCompete.onSuccess(result.getResult());
        } else if (result.getError() != null) {
            videosDownloadCompete.onFailure(result.getError().getMessage());
            Log.e(TAG, result.getError().getStackTrace().toString());
        } else {
            videosDownloadCompete.onFailure(null);
        }
    }

}
