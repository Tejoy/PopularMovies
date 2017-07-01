package com.nanodegree.tejomai.popularmovies.interfaces;

import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;

/**
 * Created by tejomai on 15/01/17.
 */

public interface MovieDetailsDownloadComplete {

    public void onSuccess(MovieGridItem posterPaths);

    public void onFailure(String message);
}
