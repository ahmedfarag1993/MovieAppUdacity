package com.example.android.movieapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    TrailerCustomAdapter trailerCustomAdapter;
    ReviewCustomAdapter reviewCustomAdapter;
    //
    MovieDataModel mMovieData;
    //
    TextView release_date;
    TextView overview;
    TextView original_title;
    TextView user_rating;
    ImageView movie_thumbnail;
    Button mark_as_fav_Button;
    //
    ListView reviews_ListView;
    ListView trailers_ListView;
    //
    private final String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesReviewsAndTrailers();
    }

    private void updateMoviesReviewsAndTrailers() {

        if (mMovieData != null) {
            FetchTrailer fetchTrailer = new FetchTrailer();
            fetchTrailer.execute(mMovieData.getId());
            //
            FetchReview fetchReview = new FetchReview();
            fetchReview.execute(mMovieData.getId());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.favorite) {
            //Toast.makeText(this, "Favorite", Toast.LENGTH_LONG).show();
            addOrRemoveFromFavorites();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //
        trailerCustomAdapter = new TrailerCustomAdapter(getContext());
        reviewCustomAdapter = new ReviewCustomAdapter(getContext());
        //
        Intent intent = getActivity().getIntent();//Passed To Activity
        Bundle argumentsIntent = intent.getExtras();//
        //
        if (argumentsIntent != null) {
            boolean twoPane = argumentsIntent.getBoolean("mTwoPane");
            if (twoPane == false) {
                mMovieData = (MovieDataModel) argumentsIntent.getParcelable("com.example.android.movieapp.MovieDataModel");
            }
        }
        //
        Bundle arguments = getArguments();//Passed To Fragment

        if (arguments != null) {
            boolean twoPane = arguments.getBoolean("mTwoPane");
            if (twoPane == true) {
                mMovieData = (MovieDataModel) arguments.getParcelable("com.example.android.movieapp.MovieDataModel");
            }
        }
        //
        View rootView = inflater.inflate(R.layout.detail_fragment, container, false);
        //
        mark_as_fav_Button = (Button) rootView.findViewById(R.id.mark_as_fav);
        mark_as_fav_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addOrRemoveFromFavorites();
            }
        });
        //
        original_title = (TextView) rootView.findViewById(R.id.original_title);
        movie_thumbnail = (ImageView) rootView.findViewById(R.id.movie_thumbnail);
        release_date = (TextView) rootView.findViewById(R.id.release_date);
        overview = (TextView) rootView.findViewById(R.id.overview);
        user_rating = (TextView) rootView.findViewById(R.id.user_rating);
        //
        trailers_ListView = (ListView) rootView.findViewById(R.id.listview_trailers);
        trailers_ListView.setAdapter(trailerCustomAdapter);
        trailers_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TrailerDataModel trailerData = trailerCustomAdapter.getItem(position);
                //
                String video_path = "http://www.youtube.com/watch?v=" + trailerCustomAdapter.getItem(position).getKey();
                Uri uri = Uri.parse(video_path);
                //
                Log.v(LOG_TAG, uri.toString());
                //
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                //
                Toast.makeText(getContext(), "Trailer Selected : " + trailerData.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        //
        //
        reviews_ListView = (ListView) rootView.findViewById(R.id.listview_reviews);
        reviews_ListView.setAdapter(reviewCustomAdapter);
        reviews_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReviewDataModel reviewData = reviewCustomAdapter.getItem(position);
                String url = reviewCustomAdapter.getItem(position).getUrl();
                //
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                //
                Toast.makeText(getContext(), "Review Selected : " + reviewData.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        //

        if (mMovieData != null) {
            original_title.setText(mMovieData.getOriginal_title());
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + mMovieData.getPoster_path()).into(movie_thumbnail);
            release_date.setText(mMovieData.getRelease_date());
            overview.setText(mMovieData.getOverview());
            user_rating.setText(mMovieData.getVote_average() + "/10");
            //
            Cursor cursor = getContext().getContentResolver().query(
                    MovieContract.Favorites.CONTENT_URI.buildUpon().appendPath(mMovieData.getId()).build(),
                    null,
                    null,
                    null,
                    null
            );
            if (cursor.getCount() > 0) {
                mark_as_fav_Button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.set_fav, 0, 0, 0);
            }
            cursor.close();
        } else {
            ((LinearLayout) rootView.findViewById(R.id.detail_fragment_container)).setVisibility(View.INVISIBLE);
        }
        return rootView;
    }


    public long addOrRemoveFromFavorites() {
        long row_id = -1;

        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.Favorites.CONTENT_URI.buildUpon().appendPath(mMovieData.getId()).build(),
                null,
                null,
                null,
                null
        );

        if (cursor.getCount() > 0) {
            getContext().getContentResolver().delete(
                    MovieContract.Favorites.CONTENT_URI,
                    MovieContract.Favorites.COL_MOVIE_ID + " = ?",
                    new String[]{String.valueOf(mMovieData.getId())}
            );
            mark_as_fav_Button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unset_fav, 0, 0, 0);
        } else {
            ContentValues MovieValues = new ContentValues();

            MovieValues.put(MovieContract.Favorites.COL_MOVIE_ID, mMovieData.getId());
            MovieValues.put(MovieContract.Favorites.COL_ORIGINAL_TITLE, mMovieData.getOriginal_title());
            MovieValues.put(MovieContract.Favorites.COL_OVERVIEW, mMovieData.getOverview());
            MovieValues.put(MovieContract.Favorites.COL_MOVIE_POSTER_PATH, mMovieData.getPoster_path());
            MovieValues.put(MovieContract.Favorites.COL_RELEASE_DATE, mMovieData.getRelease_date());
            MovieValues.put(MovieContract.Favorites.COL_VOTE_AVERAGE, mMovieData.getVote_average());

            Uri insertedUri = getContext().getContentResolver().insert(MovieContract.Favorites.CONTENT_URI, MovieValues);
            row_id = ContentUris.parseId(insertedUri);
            //
            mark_as_fav_Button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.set_fav, 0, 0, 0);
        }
        cursor.close();
        return row_id;
    }


    //===================================================================================
    //-----------FetchTrailer-------------------------------------------------------------
    //-----------FetchTrailer-------------------------------------------------------------
    //===================================================================================
    public class FetchTrailer extends AsyncTask<String, Void, ArrayList<TrailerDataModel>> {

        private final String LOG_TAG = FetchTrailer.class.getSimpleName();

        @Override
        protected ArrayList<TrailerDataModel> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                String baseUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "e78b2940f343f66faef64fa2fe876545")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(this.LOG_TAG, "Built URI" + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(this.LOG_TAG, movieJsonStr);
                return getDataFromJson(movieJsonStr);
            } catch (IOException e) {
                Log.e(this.LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        private ArrayList<TrailerDataModel> getDataFromJson(String result) {
            ArrayList<TrailerDataModel> trailers = new ArrayList<TrailerDataModel>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray array = (JSONArray) jsonObject.get("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonTrailerObject = array.getJSONObject(i);
                    //
                    TrailerDataModel trailerData = new TrailerDataModel();
                    //
                    trailerData.setId(jsonTrailerObject.getString("id"));
                    trailerData.setKey(jsonTrailerObject.getString("key"));
                    //
                    trailers.add(trailerData);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            for (TrailerDataModel trailer : trailers) {
                Log.v(LOG_TAG, "Trailer Entry : " + trailer.getId());
            }
            return trailers;
        }

        @Override
        protected void onPostExecute(ArrayList<TrailerDataModel> trailerDataModels) {
            if (trailerDataModels != null) {
                trailerCustomAdapter.clear();
                for (TrailerDataModel trailer : trailerDataModels) {
                    trailerCustomAdapter.add(trailer);
                }
                trailerCustomAdapter.notifyDataSetChanged();
            }
        }
    }

    //===================================================================================
    //-----------FetchReview-------------------------------------------------------------
    //-----------FetchReview-------------------------------------------------------------
    //===================================================================================
    public class FetchReview extends AsyncTask<String, Void, ArrayList<ReviewDataModel>> {

        private final String LOG_TAG = FetchReview.class.getSimpleName();

        @Override
        protected ArrayList<ReviewDataModel> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String reviewJsonStr = null;

            try {

                String baseUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(APPID_PARAM, "e78b2940f343f66faef64fa2fe876545")
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(this.LOG_TAG, "Built URI" + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJsonStr = buffer.toString();
                Log.v(this.LOG_TAG, reviewJsonStr);
                return getDataFromJsonReviews(reviewJsonStr);
            } catch (IOException e) {
                Log.e(this.LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(this.LOG_TAG, "Error closing stream", e);
                    }
                }
            }
        }

        private ArrayList<ReviewDataModel> getDataFromJsonReviews(String result) {
            ArrayList<ReviewDataModel> reviews = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray array = (JSONArray) jsonObject.get("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonMovieObject = array.getJSONObject(i);
                    ReviewDataModel reviewData = new ReviewDataModel();

                    reviewData.setId(jsonMovieObject.getString("id"));
                    reviewData.setAuthor(jsonMovieObject.getString("author"));
                    reviewData.setUrl(jsonMovieObject.getString("url"));
                    reviewData.setContent(jsonMovieObject.getString("content"));

                    reviews.add(reviewData);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            for (ReviewDataModel review : reviews) {
                Log.v(LOG_TAG, "Review Entry : " + review.getId());
            }
            return reviews;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewDataModel> reviewDataModels) {
            if (reviewDataModels != null) {
                reviewCustomAdapter.clear();
                for (ReviewDataModel reviewData : reviewDataModels) {
                    reviewCustomAdapter.add(reviewData);
                }
                reviewCustomAdapter.notifyDataSetChanged();
            }
        }
    }
}
