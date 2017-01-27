package com.nanodegree.tejomai.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tejomai on 17/01/17.
 */

public class MovieGridItem implements Parcelable {

    @SerializedName("poster_path")
    @Expose
    String posterPath;

    @SerializedName("original_title")
    @Expose
    String original_title;

    @SerializedName("overview")
    @Expose
    String overview;

    @SerializedName("vote_average")
    @Expose
    String vote_average;

    @SerializedName("release_date")
    @Expose
    String release_date;

    public MovieGridItem(String pp,String ot,String o,String va,String rd){
        posterPath = pp;
        original_title = ot;
        overview = o;
        vote_average = va;
        release_date = rd;
    }

    public MovieGridItem(Parcel in){
        posterPath = in.readString();
        original_title = in.readString();
        overview = in.readString();
        vote_average = in.readString();
        release_date = in.readString();
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

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(posterPath);
        parcel.writeString(original_title);
        parcel.writeString(overview);
        parcel.writeString(vote_average);
        parcel.writeString(release_date);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public MovieGridItem createFromParcel(Parcel parcel) {
            return new MovieGridItem(parcel);
        }

        @Override
        public MovieGridItem[] newArray(int size) {
            return new MovieGridItem[size];
        }
    };
}
