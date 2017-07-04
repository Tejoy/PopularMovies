package com.nanodegree.tejomai.popularmovies.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by tejomai on 04/07/17.
 */

public class FavoritesTable {

    public static final String TABLE_NAME = "favorites";
    public static final String TABLE_NAME_COLUMN_ID = "id";
    public static final String TABLE_NAME_COLUMN_URL = "url";
    public static final String TABLE_NAME_COLUMN_TITLE = "title";
    public static final String TABLE_NAME_COLUMN_RELEASE = "release";
    public static final String TABLE_NAME_COLUMN_OVERVIEW = "overview";
    public static final String TABLE_NAME_COLUMN_VOTE = "votes";

    public static final String[] PROJECTION = {FavoritesTable.TABLE_NAME_COLUMN_ID, FavoritesTable.TABLE_NAME_COLUMN_URL,FavoritesTable.TABLE_NAME_COLUMN_TITLE,FavoritesTable.TABLE_NAME_COLUMN_RELEASE, FavoritesTable.TABLE_NAME_COLUMN_VOTE, FavoritesTable.TABLE_NAME_COLUMN_OVERVIEW};

    private static final String SQL_CREATE_QUERY =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    TABLE_NAME_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    TABLE_NAME_COLUMN_URL + " TEXT," +
                    TABLE_NAME_COLUMN_TITLE + " TEXT," +
                    TABLE_NAME_COLUMN_RELEASE + " TEXT," +
                    TABLE_NAME_COLUMN_OVERVIEW + " TEXT," +
                    TABLE_NAME_COLUMN_VOTE + " TEXT)";

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_QUERY);
    }

}
