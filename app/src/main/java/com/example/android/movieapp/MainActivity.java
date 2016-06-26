package com.example.android.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback{

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (findViewById(R.id.DetailActivity_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.DetailActivity_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.settings) {
            //Toast.makeText(this,"Settings",Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(MovieDataModel movieData) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable("com.example.android.movieapp.MovieDataModel", movieData);
            args.putBoolean("mTwoPane", mTwoPane);
            //Toast.makeText(this,"on Item Selected True two Pane : "+movieData.getOriginal_title(),Toast.LENGTH_LONG).show();//Work OK
            //
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.DetailActivity_container, detailFragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            //Toast.makeText(this,"on Item Selected",Toast.LENGTH_LONG).show();
            Intent detailIntent = new Intent(this, DetailActivity.class);
            detailIntent.putExtra("com.example.android.movieapp.MovieDataModel", movieData);
            detailIntent.putExtra("mTwoPane",mTwoPane);
            startActivity(detailIntent);
        }
    }
}
