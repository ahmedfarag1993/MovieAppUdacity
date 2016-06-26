package com.example.android.movieapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by farag on 6/22/2016.
 */
public class MovieDataModel implements Parcelable {

    private String poster_path;
    private String adult;
    private String overview;
    private String release_date;
    private ArrayList<Integer> genre_ids;
    private String id;
    private String original_title;
    private String original_language;
    private String backdrop_path;
    private String popularity;
    private String vote_count;
    private String video;
    private String vote_average;

    public MovieDataModel() {
    }

    public MovieDataModel(Parcel in) {
        readFromParcel(in);
    }


    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String isAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public ArrayList<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(ArrayList<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeString(adult);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeSerializable(genre_ids);//OR -> dest.writeList(_lqis);
        dest.writeString(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(backdrop_path);
        dest.writeString(popularity);
        dest.writeString(vote_count);
        dest.writeString(video);
        dest.writeString(vote_average);
    }

    public void readFromParcel(Parcel in) {
        this.poster_path = in.readString();
        this.adult = in.readString();
        this.overview = in.readString();
        this.release_date = in.readString();
        /*
         genre_ids = new ArrayList<>();
         in.readList(genre_ids Integer.class.getClassLoader());
         */
        this.genre_ids = (ArrayList<Integer>) in.readSerializable();
        this.id = in.readString();
        this.original_title = in.readString();
        this.original_language = in.readString();
        this.backdrop_path = in.readString();
        this.popularity = in.readString();
        this.vote_count = in.readString();
        this.video = in.readString();
        this.vote_average = in.readString();
    }

    static final Parcelable.Creator<MovieDataModel> CREATOR = new Parcelable.Creator<MovieDataModel>() {

        @Override
        public MovieDataModel createFromParcel(Parcel source) {
            return new MovieDataModel(source);
        }

        @Override
        public MovieDataModel[] newArray(int size) {
            return new MovieDataModel[size];
        }
    };

}
