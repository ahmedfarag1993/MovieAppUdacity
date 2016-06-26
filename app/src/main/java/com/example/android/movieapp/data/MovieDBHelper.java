package com.example.android.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by farag on 6/24/2016.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "movie.db";
    static final int DB_VERSION = 2;


    public MovieDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + MovieContract.Favorites.TABLE_NAME + " (" +
                MovieContract.Favorites._ID + " INTEGER PRIMARY KEY," +
                MovieContract.Favorites.COL_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.Favorites.COL_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieContract.Favorites.COL_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.Favorites.COL_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Favorites.COL_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Favorites.COL_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.Favorites.TABLE_NAME);
        onCreate(db);
    }

}
