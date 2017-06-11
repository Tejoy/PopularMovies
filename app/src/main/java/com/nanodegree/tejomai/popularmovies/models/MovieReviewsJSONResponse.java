package com.nanodegree.tejomai.popularmovies.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tejomai on 04/05/17.
 */

public class MovieReviewsJSONResponse {

    @SerializedName("results")
    @Expose
    private List<MovieReviewItem> dataList = new ArrayList<MovieReviewItem>();

    public List<MovieReviewItem> getDataList() {
        return dataList;
    }
}
