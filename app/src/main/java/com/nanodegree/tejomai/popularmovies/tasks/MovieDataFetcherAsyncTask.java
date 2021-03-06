package com.nanodegree.tejomai.popularmovies.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.PopularMoviesUtil;
import com.nanodegree.tejomai.popularmovies.interfaces.DataDownloadComplete;
import com.nanodegree.tejomai.popularmovies.models.AsyncTaskItem;
import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;
import com.nanodegree.tejomai.popularmovies.models.MovieGridJSONResponse;
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
 * Created by tejomai on 15/01/17.
 * This task fetches the movies by sort order, that are to be displayed on main page
 */

public class MovieDataFetcherAsyncTask extends AsyncTask<String, Void, AsyncTaskItem<List<MovieGridItem>>> {

    private final String TAG = "DataFetcherAsyncTask";
    private DataDownloadComplete dataDownloadCompete = null;

    public void setDataDownloadCompete(DataDownloadComplete dataDownloadCompete) {
        this.dataDownloadCompete = dataDownloadCompete;
    }

    @Override
    protected AsyncTaskItem<List<MovieGridItem>> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        if (params[0].equals(PopularMoviesUtil.DEFAULT_API_KEY)) {
            return null;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        MoviesAPI moviesAPI = retrofit.create(MoviesAPI.class);
        String type = params[0];
        Call<MovieGridJSONResponse> gridCall = moviesAPI.loadGridItemsCall(params[3], params[1], params[2]);

        if (gridCall == null) {
            return new AsyncTaskItem<>();
        }
        try {
            Response<MovieGridJSONResponse> response = gridCall.execute();
            List<MovieGridItem> dataList = response.body().getDataList();
            return new AsyncTaskItem<>(dataList);

        } catch (UnknownHostException e) {
            Log.e(TAG, "UnknownHostException " + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<>(e);
        } catch (IOException e) {
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<>(e);
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
            e.printStackTrace();
            return new AsyncTaskItem<>(e);
        }

    }

    @Override
    protected void onPostExecute(AsyncTaskItem<List<MovieGridItem>> result) {
        super.onPostExecute(result);
        if (result.getResult() != null) {
            dataDownloadCompete.onSuccess(result.getResult());
        } else if (result.getError() != null) {
            dataDownloadCompete.onFailure(result.getError().getMessage());
            Log.e(TAG, result.getError().getStackTrace().toString());
        } else {
            dataDownloadCompete.onFailure(null);
        }
    }
}
