package com.nanodegree.tejomai.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 15/01/17.
 */

public class MovieDataFetcherAsyncTask extends AsyncTask<String,Void,AsyncTaskItem<String>>{

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
    protected AsyncTaskItem<String> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        if(params[0].equals(PopularMoviesUtil.DEFAULT_API_KEY)){
            Log.e(TAG,"Please set the API key!!");
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        AsyncTaskItem<String> result = null;

        try {
            URL url = new URL(buildURL(params[0],params[1],params[2]).toString());

            // Create the request and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                jsonStr = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                jsonStr = null;
            }
            jsonStr = buffer.toString();
            Log.i(TAG," "+jsonStr);
            result = new AsyncTaskItem<String>(jsonStr);
        } catch (IOException e) {
            Log.e("ForecastFragment", "Error ", e);
            // If the code didn't successfully get movies data, there's no point in attempting
            // to parse it.
            jsonStr = null;
            result = new AsyncTaskItem<String>(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("ForecastFragment", "Error closing stream", e);
                }
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(AsyncTaskItem<String> result) {
        super.onPostExecute(result);
        if(result!=null){
            dataDownloadCompete.onSuccess(parseResponseData(result.getResult()));
        }else{
            dataDownloadCompete.onFailure(result.getError().getMessage());
            Log.e(TAG,result.getError().getStackTrace().toString());
        }
    }

    //parse the required data from the received response and return in a list of MovieGridItem objects
    private List<MovieGridItem> parseResponseData(String jsonStr){
        List<MovieGridItem> results = new ArrayList<>();

        MovieGridItem gridItem;

        if(jsonStr == null){
            return results;
        }

        JSONObject innerObj;
        try {
            JSONObject obj = new JSONObject(jsonStr);
            JSONArray list = obj.getJSONArray("results");

            if(list == null){
                return results;
            }

            for(int i=0;i<list.length();++i){
                innerObj = list.getJSONObject(i);
                gridItem = new MovieGridItem(innerObj.getString("poster_path"),innerObj.getString("original_title"),innerObj.getString("overview"),innerObj.getInt("vote_average"),innerObj.getString("release_date"));
                results.add(gridItem);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}
