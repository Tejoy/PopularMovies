package com.nanodegree.tejomai.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.interfaces.ReviewsDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.AsyncTaskItem;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewItem;
import com.nanodegree.tejomai.popularmovies.models.MovieReviewsJSONResponse;
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

public class MovieReviewsFetcherAsyncTask  extends AsyncTask<String,Void,AsyncTaskItem<List<MovieReviewItem>>> {
    private final String TAG = "MovieReviewsAsyncTask";

    public void setReviewsDownloadComplete(ReviewsDownloadComplete reviewsDownloadCompete) {
        this.reviewsDownloadCompete = reviewsDownloadCompete;
    }

    ReviewsDownloadComplete reviewsDownloadCompete = null;

    @Override
    protected AsyncTaskItem<List<MovieReviewItem>> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        if(params[1].equals(PopularMoviesUtil.DEFAULT_API_KEY)){
            Log.e(TAG,"Please set API key!!");
            return null;
        }

        Log.i(TAG,"url "+params[0]);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);

        Call<MovieReviewsJSONResponse> reviewsCall = null;

        reviewsCall = moviesAPI.loadReviewsCall(params[0],params[1]);

        if(reviewsCall == null) {
            return new AsyncTaskItem<List<MovieReviewItem>>();
        }
        try {
            Response<MovieReviewsJSONResponse> response = reviewsCall.execute();
            if(response!=null && response.body()!=null) {
                List<MovieReviewItem> dataList = response.body().getDataList();
                return new AsyncTaskItem<List<MovieReviewItem>>(dataList);
            }
        } catch (IOException e) {
            Log.e(TAG,""+e.getMessage());
            e.printStackTrace();

        }
        return new AsyncTaskItem<List<MovieReviewItem>>();
    }

    @Override
    protected void onPostExecute(AsyncTaskItem<List<MovieReviewItem>> result) {
        super.onPostExecute(result);
        if(result.getResult()!=null){
            reviewsDownloadCompete.onSuccess(result.getResult());
        }else if(result.getError()!=null){
            reviewsDownloadCompete.onFailure(result.getError().getMessage());
            Log.e(TAG,result.getError().getStackTrace().toString());
        }else{
            reviewsDownloadCompete.onFailure(null);
        }
    }
}
