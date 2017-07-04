package com.nanodegree.tejomai.popularmovies.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by tejomai on 04/07/17.
 */

public class FavoritesContentProvider extends ContentProvider{

    private static final String BASE_PATH = "favorites";
    private static final int FAV = 10;
    private static final String AUTHORITY = "com.nanodegree.tejomai.popularmovies.favorites.contentprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, FAV);
    }

    private DBHelper helper;


    @Override
    public boolean onCreate() {
        helper = new DBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = helper.getWritableDatabase();
        checkColumns(projection);

        Cursor cr = null;
        int uriType = sURIMatcher.match(uri);

        if(uriType == FAV){
            builder.setTables(FavoritesTable.TABLE_NAME);
            cr = builder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
            cr.setNotificationUri(getContext().getContentResolver(),uri);
        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cr;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long id = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        if(uriType == FAV){
            id = db.insert(FavoritesTable.TABLE_NAME,null,contentValues);
            getContext().getContentResolver().notifyChange(uri,null);
        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        if(uriType == FAV){
            rowsDeleted = db.delete(FavoritesTable.TABLE_NAME,selection,selectionArgs);

        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int rowsUpdated = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        int uriType = sURIMatcher.match(uri);
        if(uriType == FAV){
            rowsUpdated = db.update(FavoritesTable.TABLE_NAME,contentValues,selection,selectionArgs);

        }else{
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    private void checkColumns(String[] projection){
        String[] available = { FavoritesTable.TABLE_NAME_COLUMN_ID, FavoritesTable.TABLE_NAME_COLUMN_OVERVIEW, FavoritesTable.TABLE_NAME_COLUMN_RELEASE, FavoritesTable.TABLE_NAME_COLUMN_TITLE,FavoritesTable.TABLE_NAME_COLUMN_URL,FavoritesTable.TABLE_NAME_COLUMN_VOTE };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }


}
