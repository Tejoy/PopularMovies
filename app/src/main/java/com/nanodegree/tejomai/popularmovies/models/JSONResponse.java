package com.nanodegree.tejomai.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 27/01/17.
 */


public class JSONResponse {

    String TAG = "JSONResponse";

    @SerializedName("results")
    @Expose
    private List<MovieGridItem> dataList = new ArrayList<MovieGridItem>();

    public List<MovieGridItem> getDataList() {
        return dataList;
    }

}
