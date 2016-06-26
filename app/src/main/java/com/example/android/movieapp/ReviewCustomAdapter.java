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
public class ReviewCustomAdapter extends ArrayAdapter<ReviewDataModel> {

    private final String LOG_TAG = ReviewCustomAdapter.class.getSimpleName();

    public ReviewCustomAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_listitem_item, parent, false);
        }
        //
        TextView authorTextView = (TextView) convertView.findViewById(R.id.listitem_review_author_item);
        authorTextView.setText(getItem(position).getAuthor());

        TextView contentTextView = (TextView) convertView.findViewById(R.id.listitem_review_content_item);
        contentTextView.setText(getItem(position).getContent());
        //
        return convertView;
    }
}
