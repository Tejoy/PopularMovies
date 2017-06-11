package com.nanodegree.tejomai.popularmovies.interfaces;

import com.nanodegree.tejomai.popularmovies.models.MovieVideoItem;

import java.util.List;

/**
 * Created by tejomai on 04/05/17.
 */

public interface VideosDownloadComplete {

    public void onSuccess(List<MovieVideoItem> videos);

    public void onFailure(String message);
}
