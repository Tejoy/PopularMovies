package com.nanodegree.tejomai.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tejomai on 03/05/17.
 */

public class MovieReviewItem  implements Parcelable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("author")
    @Expose
    String author;

    @SerializedName("content")
    @Expose
    String content;

    @SerializedName("url")
    @Expose
    String url;

    public MovieReviewItem(String i,String a,String c,String u){
        id = i;
        author = a;
        content = c;
        url = u;
    }

    public MovieReviewItem(Parcel in){
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public MovieReviewItem createFromParcel(Parcel parcel) {
            return new MovieReviewItem(parcel);
        }

        @Override
        public MovieReviewItem[] newArray(int size) {
            return new MovieReviewItem[size];
        }
    };

}
