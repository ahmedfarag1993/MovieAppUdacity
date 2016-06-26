package com.example.android.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by farag on 6/24/2016.
 */
public class MovieProvider extends ContentProvider {

    static final int FAVORITES = 1;
    static final int FAVORITES_WITH_ID = 2;
    //
    MovieDBHelper movieDBHelper;
    //
    UriMatcher mUriMatcher = buildUriMatcher();

    //
    UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //
        final String authority = MovieContract.CONTENT_AUTHORITY;
        //
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/*", FAVORITES_WITH_ID);

        return matcher;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case FAVORITES:
                return MovieContract.Favorites.CONTENT_TYPE;
            case FAVORITES_WITH_ID:
                return MovieContract.Favorites.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    private Cursor getFavorites(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return movieDBHelper.getReadableDatabase().query(
                MovieContract.Favorites.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoritesByID(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        long _id = ContentUris.parseId(uri);
        return movieDBHelper.getReadableDatabase().query(
                MovieContract.Favorites.TABLE_NAME,
                projection,
                MovieContract.Favorites.COL_MOVIE_ID + " = ?",
                new String[]{String.valueOf(_id)},
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (mUriMatcher.match(uri)) {
            case FAVORITES:
                retCursor = getFavorites(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case FAVORITES_WITH_ID:
                retCursor = getFavoritesByID(uri, projection, selection, selectionArgs, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;

        switch (mUriMatcher.match(uri)) {
            case FAVORITES:
                long _id = movieDBHelper.getWritableDatabase().insert(MovieContract.Favorites.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.Favorites.buildFavoritesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted; // Number of rows effected

        switch (mUriMatcher.match(uri)) {
            case FAVORITES:
                rowsDeleted = movieDBHelper.getReadableDatabase().delete(MovieContract.Favorites.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated;

        switch (mUriMatcher.match(uri)) {
            case FAVORITES:
                rowsUpdated = movieDBHelper.getWritableDatabase().update(MovieContract.Favorites.TABLE_NAME, values, selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
