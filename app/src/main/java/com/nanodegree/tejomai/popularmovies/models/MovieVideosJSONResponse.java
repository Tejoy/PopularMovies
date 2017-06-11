package com.nanodegree.tejomai.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 04/05/17.
 */

public class MovieVideosJSONResponse {

    @SerializedName("results")
    @Expose
    private List<MovieVideoItem> dataList = new ArrayList<MovieVideoItem>();

    public List<MovieVideoItem> getDataList() {
        return dataList;
    }
}
