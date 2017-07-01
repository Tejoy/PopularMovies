package com.nanodegree.tejomai.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.interfaces.MovieDetailsDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.AsyncTaskItem;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.network.MoviesAPI;

import java.io.IOException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.nanodegree.tejomai.popularmovies.PopularMoviesUtil.BASE_URL;

/**
 * Created by tejomai on 15/01/17.
 * This task is used to fetch the movie details like title, release date and vote average when there is internet connectivity
 */

public class MovieDetailFetcherAsyncTask extends AsyncTask<String,Void,AsyncTaskItem<MovieGridItem>>{

    private final String TAG = "DataFetcherAsyncTask";

    private MovieDetailsDownloadComplete movieDetailsDownloadComplete;

    public void setMovieDetailsDownloadComplete(MovieDetailsDownloadComplete movieDetailsDownloadComplete) {
        this.movieDetailsDownloadComplete = movieDetailsDownloadComplete;
    }

    @Override
    protected AsyncTaskItem<MovieGridItem> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        if (params[0].equals(PopularMoviesUtil.DEFAULT_API_KEY)) {
            Log.e(TAG, "Please set the API key!!");
            return null;
        }

        Log.i(TAG, "logging - " + BASE_URL + " " + params[0] + " " + params[1] + " " + params[2]);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
        String type = params[0];
        Call<MovieGridItem> gridCall = null;

        if (type.equals(PopularMoviesUtil.PREF_FILTER_MOVIE_DETAIL)) {
            gridCall = moviesAPI.loadMovieDetailCall(params[1], params[2]);
        }

        if (gridCall == null) {
            return new AsyncTaskItem<MovieGridItem>();
        }
        try {
            Response<MovieGridItem> response = gridCall.execute();
            MovieGridItem item = response.body();
            return new AsyncTaskItem<MovieGridItem>(item);

        } catch (UnknownHostException e) {
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<MovieGridItem>(e);
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<MovieGridItem>(e);
        }catch (Exception e){
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<MovieGridItem>(e);
        }

    }

    @Override
    protected void onPostExecute(AsyncTaskItem<MovieGridItem> result) {
        super.onPostExecute(result);
        if(result.getResult()!=null){
            movieDetailsDownloadComplete.onSuccess(result.getResult());
        }else if(result.getError()!=null){
            movieDetailsDownloadComplete.onFailure(result.getError().getMessage());
            Log.e(TAG,result.getError().getStackTrace().toString());
        }else{
            movieDetailsDownloadComplete.onFailure(null);
        }
    }
}
