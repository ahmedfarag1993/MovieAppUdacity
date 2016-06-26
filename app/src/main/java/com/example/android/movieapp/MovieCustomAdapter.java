package com.example.android.movieapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by farag on 6/22/2016.
 */
public class MovieCustomAdapter extends ArrayAdapter<MovieDataModel> {

    private final String LOG_TAG = MovieCustomAdapter.class.getSimpleName();

    public MovieCustomAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieDataModel movieDataModel=getItem(position);
        //
        if( convertView == null ){
            //We must create a View:
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_imageview_item, parent, false);
        }
        //Here we can do changes to the convertView, such as set a text on a TextView
        //or an image on an ImageView.
        ImageView imageView=(ImageView) convertView.findViewById(R.id.gridview_imageview_item_id);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/"+movieDataModel.getPoster_path()).into(imageView);
        Log.v(LOG_TAG,"http://image.tmdb.org/t/p/w185/"+movieDataModel.getPoster_path());
        return convertView;
    }
}
