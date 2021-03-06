package com.nanodegree.tejomai.popularmovies.interfaces;

import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;

import java.util.List;

/**
 * Created by tejomai on 15/01/17.
 */

public interface DataDownloadComplete {

    public void onSuccess(List<MovieGridItem> posterPaths);

    public void onFailure(String message);
}
