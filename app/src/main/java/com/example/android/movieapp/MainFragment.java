package com.example.android.movieapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.movieapp.data.MovieContract;

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
 * Created by farag on 6/22/2016.
 */
public class MainFragment extends Fragment {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    static MovieCustomAdapter mMovieAdapter;

    private final String LOG_TAG = MainFragment.class.getSimpleName();

    public MainFragment() {}

    public interface Callback {
        public void onItemSelected(MovieDataModel movieData);
    }

    public void updateMovies() {
        FetchMovies fetchMovies = new FetchMovies();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_PopularValue));
        if (sortType.equals("favorite")) {
            Cursor cursor = getContext().getContentResolver().query(
                    MovieContract.Favorites.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            ArrayList<MovieDataModel> data = new ArrayList<>();

            int idIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_MOVIE_ID);
            int originalTitleIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_ORIGINAL_TITLE);
            int overviewIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_OVERVIEW);
            int posterPathIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_MOVIE_POSTER_PATH);
            int releaseDateIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_RELEASE_DATE);
            int voteAverageIndex = cursor.getColumnIndex(MovieContract.Favorites.COL_VOTE_AVERAGE);

            while (cursor.moveToNext()) {
                MovieDataModel movieData = new MovieDataModel();
                //
                movieData.setId(cursor.getString(idIndex));
                movieData.setOriginal_title(cursor.getString(originalTitleIndex));
                movieData.setOverview(cursor.getString(overviewIndex));
                movieData.setPoster_path(cursor.getString(posterPathIndex));
                movieData.setRelease_date(cursor.getString(releaseDateIndex));
                movieData.setVote_average(cursor.getString(voteAverageIndex));
                //
                data.add(movieData);
            }

            mMovieAdapter.clear();
            for (MovieDataModel movieDataa : data) {
                mMovieAdapter.add(movieDataa);
            }
            mMovieAdapter.notifyDataSetChanged();
            cursor.close();
            //
            DetailFragment detailFragment = new DetailFragment();
            FragmentManager fragmentManager=getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.DetailActivity_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            fetchMovies.execute(sortType);
            //
            DetailFragment detailFragment = new DetailFragment();
            FragmentManager fragmentManager=getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.DetailActivity_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        }
        //Toast.makeText(getContext(),"Update Movies",Toast.LENGTH_LONG);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateMovies();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.referesh) {
            updateMovies();
            //Toast.makeText(getContext(),"Referesh",Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMovieAdapter = new MovieCustomAdapter(getActivity());
        //
        View rootView = inflater.inflate(R.layout.main_fragment_gridview, container, false);
        //
        GridView gridView = (GridView) rootView.findViewById(R.id.fragment_main_gridview_id);
        gridView.setAdapter(mMovieAdapter);
        //
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieDataModel movieData = mMovieAdapter.getItem(position);
                //
                /*Intent detailIntent = new Intent(getContext(), DetailActivity.class);
                detailIntent.putExtra("com.example.android.movieapp.MovieDataModel", movieData);
                startActivity(detailIntent);*/
                //
                ((Callback) getActivity()).onItemSelected(movieData);
                //Toast.makeText(getContext(), "Movie Selected" + movieData.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        //
        return rootView;
    }

    public class FetchMovies extends AsyncTask<String, Void, ArrayList<MovieDataModel>> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        @Override
        protected ArrayList<MovieDataModel> doInBackground(String... params) {

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

                String baseUrl = "http://api.themoviedb.org/3/movie/" + params[0] + "?";
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

        private ArrayList<MovieDataModel> getDataFromJson(String result) {
            ArrayList<MovieDataModel> movieFinalData = new ArrayList<MovieDataModel>();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray array = (JSONArray) jsonObject.get("results");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonMovieObject = array.getJSONObject(i);
                    MovieDataModel movieData = new MovieDataModel();

                    movieData.setAdult(jsonMovieObject.getString("adult"));
                    movieData.setBackdrop_path(jsonMovieObject.getString("backdrop_path"));
                    movieData.setId(jsonMovieObject.getString("id"));
                    movieData.setOriginal_language(jsonMovieObject.getString("original_language"));
                    movieData.setOriginal_title(jsonMovieObject.getString("original_title"));
                    movieData.setPopularity(jsonMovieObject.getString("popularity"));
                    movieData.setVote_count(jsonMovieObject.getString("vote_count"));
                    movieData.setVideo(jsonMovieObject.getString("video"));
                    movieData.setOverview(jsonMovieObject.getString("overview"));
                    movieData.setRelease_date(jsonMovieObject.getString("release_date"));
                    movieData.setPoster_path(jsonMovieObject.getString("poster_path"));
                    movieData.setVote_average(jsonMovieObject.getString("vote_average"));

                    movieFinalData.add(movieData);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            for (MovieDataModel movie : movieFinalData) {
                Log.v(LOG_TAG, "Movie Entry : http://image.tmdb.org/t/p/w185/" + movie.getPoster_path());
            }
            return movieFinalData;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieDataModel> movieDataModels) {
            if (movieDataModels != null) {
                mMovieAdapter.clear();
                for (MovieDataModel movie : movieDataModels) {
                    mMovieAdapter.add(movie);
                }
                mMovieAdapter.notifyDataSetChanged();
                // New data is back from the server.  Hooray!
            }
        }
    }
}

