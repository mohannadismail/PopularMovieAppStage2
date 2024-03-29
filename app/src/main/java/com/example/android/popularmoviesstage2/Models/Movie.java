package com.example.android.popularmoviesstage2.Models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Movie implements Parcelable {

    private int movieId;

    private String movieTitle;
    private String movieReleaseDate;
    private String moviePosterPath;
    private String movieDatabasePosterPath = null;

    private double movieVoteAverage;
    private String moviePlot;
    private String movieLanguage;

    private double movieRuntime;
    private ArrayList<String> movieCast;
    private ArrayList<MovieReview> movieReviews;

    private boolean isMoviewForAdults;
    private String movieBackdropPath;
    private String movieDatabaseBackdropPath = null;

    private ArrayList<TrailerThumbnail> movieTrailersThumbnails = new ArrayList<TrailerThumbnail>();

    private boolean isFavorite = false;

    public Movie(int movieId, String movieTitle, String movieReleaseDate, String moviePoster,
                 double movieVoteAverage, String moviePlot, String movieLanguage, double movieRuntime,
                 ArrayList<String> movieCast, ArrayList<MovieReview> movieReviews, boolean isMoviewForAdults,
                 String movieBackdropPath, ArrayList<TrailerThumbnail> movieTrailersThumbnails) {

        this.movieId = movieId;

        this.movieTitle = movieTitle;
        this.movieReleaseDate = movieReleaseDate;
        this.moviePosterPath = moviePoster;

        this.movieVoteAverage = movieVoteAverage;
        this.moviePlot = moviePlot;
        this.movieLanguage = movieLanguage;

        this.movieRuntime = movieRuntime;
        this.movieCast = movieCast;
        this.movieReviews = movieReviews;

        this.isMoviewForAdults = isMoviewForAdults;
        this.movieBackdropPath = movieBackdropPath;
        this.movieTrailersThumbnails = movieTrailersThumbnails;
    }


    private Movie(Parcel in) {

        movieId = in.readInt();

        movieTitle = in.readString();
        movieReleaseDate = in.readString();
        moviePosterPath = in.readString();
        movieDatabasePosterPath = in.readString();

        movieRuntime = in.readDouble();
        movieCast = in.createStringArrayList();
        movieReviews = in.createTypedArrayList(MovieReview.CREATOR);

        movieVoteAverage = in.readDouble();
        moviePlot = in.readString();
        movieLanguage = in.readString();

        isMoviewForAdults = in.readInt() == 1;
        movieBackdropPath = in.readString();
        movieDatabaseBackdropPath = in.readString();
        movieTrailersThumbnails = in.createTypedArrayList(TrailerThumbnail.CREATOR);

        isFavorite = in.readInt() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeInt(movieId);

        parcel.writeString(movieTitle);
        parcel.writeString(movieReleaseDate);
        parcel.writeString(moviePosterPath);
        parcel.writeString(movieDatabasePosterPath);

        parcel.writeDouble(movieRuntime);
        parcel.writeStringList(movieCast);
        parcel.writeTypedList(movieReviews);

        parcel.writeDouble(movieVoteAverage);
        parcel.writeString(moviePlot);
        parcel.writeString(movieLanguage);

        parcel.writeInt(isMoviewForAdults ? 1 : 0);
        parcel.writeString(movieBackdropPath);
        parcel.writeString(movieDatabaseBackdropPath);
        parcel.writeTypedList(movieTrailersThumbnails);

        parcel.writeInt(isFavorite ? 1 : 0);
    }

    public static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    public int getMovieId() {
        return this.movieId;
    }
    public String getMovieTitle() {
        return this.movieTitle;
    }
    public String getMovieReleaseDate() {
        return this.movieReleaseDate;
    }
    public String getMoviePosterPath() {
        return this.moviePosterPath;
    }
    public String getMovieDatabasePosterPath() { return this.movieDatabasePosterPath; }
    public double getMovieVoteAverage() {
        return this.movieVoteAverage;
    }
    public String getMoviePlot() {
        return this.moviePlot;
    }
    public String getMovieLanguage() {
        return this.movieLanguage;
    }
    public boolean getIsMovieForAdults() {
        return this.isMoviewForAdults;
    }
    public double getMovieRuntime() {
        return this.movieRuntime;
    }
    public ArrayList<String> getMovieCast() {
        return this.movieCast;
    }
    public ArrayList<MovieReview> getMovieReviews() {
        return this.movieReviews;
    }
    public String getMovieBackdropPath() {
        return this.movieBackdropPath;
    }
    public String getMovieDatabaseBackdropPath() { return this.movieDatabaseBackdropPath; }
    public boolean getIsMovieFavorite() {
        return this.isFavorite;
    }
    public ArrayList<TrailerThumbnail> getMovieTrailersThumbnails() {
        return this.movieTrailersThumbnails;
    }

    public void setMovieId(int id) { this.movieId = movieId; }
    public void setMovieTitle(String title) {
        this.movieTitle = title;
    }
    public void setMovieReleaseDate(String releaseDate) {
        this.movieReleaseDate = releaseDate;
    }
    public void setMoviePosterPath(String posterPath) {
        this.moviePosterPath = posterPath;
    }
    public void setMovieDatabasePosterPath(String posterDatabasePath) { this.movieDatabaseBackdropPath = posterDatabasePath; }
    public void setMovieLanguage(String language) {
        this.movieLanguage = language;
    }
    public void setMovieVoteAverage(double voteAverage) {
        this.movieVoteAverage = voteAverage;
    }
    public void setMoviePlot(String plot) {
        this.moviePlot = plot;
    }
    public void setMovieRuntime(double runtime) {
        this.movieRuntime = runtime;
    }
    public void setMovieCast(ArrayList<String> cast) {
        this.movieCast = cast;
    }
    public void setMovieReviews(ArrayList<MovieReview> movieReviews) {
        this.movieReviews = movieReviews;
    }

    public void setIsMovieFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public void setMovieTrailersThumbnails(ArrayList<TrailerThumbnail> trailersThumbnails) {
        this.movieTrailersThumbnails = trailersThumbnails;
    }

    public void setIsMovieForAdults(boolean isMovieForAdults) {
        this.isMoviewForAdults = isMovieForAdults;
    }

    public void setMovieBackdropPath(String movieBackdropPath) {
        this.movieBackdropPath = movieBackdropPath;
    }

    public void setMovieDatabaseBackdropPath(String movieDatabaseBackdropPath) {
        this.movieDatabaseBackdropPath = movieDatabaseBackdropPath;
    }


}
