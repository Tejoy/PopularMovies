package com.nanodegree.tejomai.popularmovies;

/**
 * Created by tejomai on 17/01/17.
 */

public class MovieGridItem {
    
    String posterPath;
    String original_title;
    String overview;
    int vote_average;
    String release_date;

    public MovieGridItem(String pp,String ot,String o,int va,String rd){
        posterPath = pp;
        original_title = ot;
        overview = o;
        vote_average = va;
        release_date = rd;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getVote_average() {
        return vote_average;
    }

    public void setVote_average(int vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

}
