package com.nanodegree.tejomai.popularmovies.interfaces;

import com.nanodegree.tejomai.popularmovies.models.MovieReviewItem;

import java.util.List;

/**
 * Created by tejomai on 04/05/17.
 */

public interface ReviewsDownloadComplete {

    public void onSuccess(List<MovieReviewItem> reviews);

    public void onFailure(String message);
}
