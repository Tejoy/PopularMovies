package com.nanodegree.tejomai.popularmovies.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nanodegree.tejomai.popularmovies.models.MovieGridItem;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by tejomai on 10/06/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popularmovies.db";
    private static final String FAVORITES_TABLE_NAME = "favorites";
    private static final String FAVORITES_TABLE_NAME_COLUMN_ID = "id";
    private static final String FAVORITES_TABLE_NAME_COLUMN_URL = "url";
    private static final String FAVORITES_TABLE_NAME_COLUMN_TITLE = "title";
    private static final String FAVORITES_TABLE_NAME_COLUMN_RELEASE = "release";
    private static final String FAVORITES_TABLE_NAME_COLUMN_OVERVIEW = "overview";
    private static final String FAVORITES_TABLE_NAME_COLUMN_VOTE = "votes";
    private static final String SQL_CREATE_QUERY =
            "CREATE TABLE " + FAVORITES_TABLE_NAME + " (" +
                    FAVORITES_TABLE_NAME_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    FAVORITES_TABLE_NAME_COLUMN_URL + " TEXT," +
                    FAVORITES_TABLE_NAME_COLUMN_TITLE + " TEXT," +
                    FAVORITES_TABLE_NAME_COLUMN_RELEASE + " TEXT," +
                    FAVORITES_TABLE_NAME_COLUMN_OVERVIEW + " TEXT," +
                    FAVORITES_TABLE_NAME_COLUMN_VOTE + " TEXT)";

    private static final String SQL_DROP_QUERY =
            "DROP TABLE IF EXISTS " + FAVORITES_TABLE_NAME;


    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_QUERY);
        onCreate(db);
    }

    public boolean insertFavourite(String id, String url, String title, String releaseDate, String vote, String overview){
        Log.i(TAG, "insert favorites " + id+" "+url);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_ID, id);
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_URL, url);
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_TITLE, title);
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_RELEASE, releaseDate);
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_OVERVIEW, overview);
        contentValues.put(FAVORITES_TABLE_NAME_COLUMN_VOTE, vote);
        db.insert(FAVORITES_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean removeFavourite(String id){
        Log.i(TAG, "remove favorites " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FAVORITES_TABLE_NAME,"id = ?",new String[]{id});
        return true;
    }

    public Cursor fetchFavorites(){
        Log.i(TAG, "fetch favorites " );
        String[] selection = { FAVORITES_TABLE_NAME_COLUMN_ID, FAVORITES_TABLE_NAME_COLUMN_URL, FAVORITES_TABLE_NAME_COLUMN_TITLE, FAVORITES_TABLE_NAME_COLUMN_RELEASE, FAVORITES_TABLE_NAME_COLUMN_OVERVIEW, FAVORITES_TABLE_NAME_COLUMN_VOTE };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cr = db.query(true,FAVORITES_TABLE_NAME,selection,null,null,null,null,null,null);
        return cr;
    }

    public List<MovieGridItem> getFavorites(){
        Log.i(TAG, "get favorites " );
        List<MovieGridItem> list= new ArrayList<MovieGridItem>();
        Cursor cr = fetchFavorites();
        MovieGridItem item;
        try {
            if (cr != null && cr.getCount() > 0) {
                cr.moveToFirst();

                do {
                    item = new MovieGridItem();
                    item.setId(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_ID)));
                    item.setPosterPath(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_URL)));
                    item.setOriginal_title(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_TITLE)));
                    item.setRelease_date(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_RELEASE)));
                    item.setOverview(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_OVERVIEW)));
                    item.setVote_average(cr.getString(cr.getColumnIndex(FAVORITES_TABLE_NAME_COLUMN_VOTE)));
                    list.add(item);
                } while (cr.moveToNext());
            }
        }finally {
            if(cr!=null){
                cr.close();
                cr = null;
            }
        }
        Log.i(TAG, "return get favorites " );
        return list;
    }
}
