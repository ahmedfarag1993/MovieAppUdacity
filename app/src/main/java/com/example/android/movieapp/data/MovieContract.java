package com.example.android.movieapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by farag on 6/24/2016.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.movieapp.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_FAVORITE = "favorites";

    public static final class Favorites implements BaseColumns{

        public static final String TABLE_NAME="favorites";
        //
        public static final String COL_MOVIE_ID="movie_id";
        public static final String COL_MOVIE_POSTER_PATH="poster_path";
        public static final String COL_OVERVIEW="overview";
        public static final String COL_RELEASE_DATE="release_date";
        public static final String COL_VOTE_AVERAGE="vote_average";
        public static final String COL_ORIGINAL_TITLE="original_title";
        //
        public static final Uri CONTENT_URI =BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();
        //
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;
        //
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FAVORITE;

        public static Uri buildFavoritesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }
}
