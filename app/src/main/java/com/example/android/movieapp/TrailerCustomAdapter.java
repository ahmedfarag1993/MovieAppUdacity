package com.example.android.movieapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by farag on 6/25/2016.
 */
public class TrailerCustomAdapter extends ArrayAdapter<TrailerDataModel> {

    private final String LOG_TAG = TrailerCustomAdapter.class.getSimpleName();

    public TrailerCustomAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_listitem_item, parent, false);
        }
        //
        TextView textView = (TextView) convertView.findViewById(R.id.listitem_item_trailer_num);
        textView.setText("Trailer " + (position + 1));

        return convertView;
    }
}
