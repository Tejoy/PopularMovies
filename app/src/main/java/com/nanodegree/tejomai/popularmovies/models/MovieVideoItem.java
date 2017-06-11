package com.nanodegree.tejomai.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by tejomai on 03/05/17.
 */

public class MovieVideoItem  implements Parcelable {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("key")
    @Expose
    String key;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("site")
    @Expose
    String site;

    @SerializedName("type")
    @Expose
    String type;

    public MovieVideoItem(String i,String k,String n,String s, String t){
        id = i;
        key = k;
        name = n;
        site = s;
        type = t;
    }

    public MovieVideoItem(Parcel in){
        id = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        type = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeString(type);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){

        @Override
        public MovieVideoItem createFromParcel(Parcel parcel) {
            return new MovieVideoItem(parcel);
        }

        @Override
        public MovieVideoItem[] newArray(int size) {
            return new MovieVideoItem[size];
        }
    };
}
