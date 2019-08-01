package com.example.android.popularmoviesstage2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmoviesstage2.Models.Movie;
import com.example.android.popularmoviesstage2.Models.MovieReview;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.example.android.popularmoviesstage2.Database.ImagesDBUtilities.deleteImageFromStorage;
import static com.example.android.popularmoviesstage2.GeneralUtilities.LoaderUtilities.getStringFromCursor;

public class DBServiceTasks {

    public static final String ACTION_INSERT_FAVORITE = "insert-favorite-movie";
    public static final String ACTION_REMOVE_FAVORITE = "remove-favorite-movie";

    public static final String CHARACTER_SEPARATING_REVIEWS_AUTHORS = ", ";
    public static final String CHARACTER_SEPARATING_REVIEWS_TEXT = "===>";

    public static final String CHARACTER_SEPARATING_CAST_MEMBERS = ", ";


    public static void executeTask(Context context, String action, Movie movieSelected) {

        switch (action) {
            case ACTION_INSERT_FAVORITE:
                insertMovieToFavoritesDB(context, movieSelected);
                break;
            case ACTION_REMOVE_FAVORITE:
                removeMovieFromFavoritesDB(context, movieSelected);
                break;
        }
    }

    private static void insertMovieToFavoritesDB(Context context, Movie movieSelected) {

        ContentValues cv = createMovieContentValuesForDB(movieSelected);
        Uri uri = buildMovieSelectedDBUri(movieSelected);

        Uri insertResult = context.getContentResolver().insert(uri, cv);

        if (insertResult != null) {
            ImagesDBUtilities.saveAllMovieImages(context, movieSelected);
        } else {
            Log.v(TAG, "Movie couldn't be inserted successfully");
        }
    }

    private static void removeMovieFromFavoritesDB(Context context, Movie movieSelected) {

        Uri uri = MovieDBContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(movieSelected.getMovieId()))
                .build();

        boolean posterDeleted = deleteImageFromStorage(context,
                getImagePathFromDB(context, uri, MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_POSTER_PATH),
                Integer.toString(movieSelected.getMovieId()),
                FavoritesUtilities.IMAGE_TYPE_POSTER,
                -1);

        boolean backdropDeleted = deleteImageFromStorage(context,
                getImagePathFromDB(context, uri, MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_BACKDROP_PATH),
                Integer.toString(movieSelected.getMovieId()),
                FavoritesUtilities.IMAGE_TYPE_BACKDROP,
                -1);

        boolean thumbnailsDeleted = ImagesDBUtilities.deleteThumbnailsFromStorage(context, Integer.toString(movieSelected.getMovieId()));

        int numDeleted = context.getContentResolver().delete(uri, "movieDBId=?", new String[]{"id"});

        if (numDeleted == 1) {
            movieSelected.setIsMovieFavorite(false);
        }
    }

    private static ContentValues createMovieContentValuesForDB(Movie movieSelected) {

        ContentValues cv = new ContentValues();

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_MOVIEDB_ID, Integer.toString(movieSelected.getMovieId()));

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TITLE, movieSelected.getMovieTitle());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_RELEASE_DATE, movieSelected.getMovieReleaseDate());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_VOTE_AVERAGE, movieSelected.getMovieVoteAverage());

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_PLOT, movieSelected.getMoviePlot());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_LANGUAGE, movieSelected.getMovieLanguage());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_RUNTIME, movieSelected.getMovieRuntime());

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_CAST, movieSelected.getMovieCast().toString());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_IS_FOR_ADULTS, movieSelected.getIsMovieFavorite());

        String[] formattedReviews = formattingReviewsForDB(movieSelected.getMovieReviews());
        String formattedAuthors = formattedReviews[0];
        String formattedReviewsText = formattedReviews[1];

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_REVIEWS_AUTHOR, formattedAuthors);
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_REVIEWS_TEXT, formattedReviewsText);

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_POSTER_PATH, movieSelected.getMoviePosterPath());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_BACKDROP_PATH, "");

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_BACKDROP, movieSelected.getMovieBackdropPath());
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_BACKDROP_PATH, "");

        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TRAILERS_THUMBNAILS, "");
        cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_TRAILERS_THUMBNAILS, "");

        return cv;
    }

    public static String getImagePathFromDB(Context context, Uri uri, String columnName) {
        Cursor imagePathCursor = context.getContentResolver().query(uri,
                new String[]{columnName},
                null,
                null,
                MovieDBContract.FavoriteMoviesEntry._ID);

        imagePathCursor.moveToFirst();
        return getStringFromCursor(imagePathCursor, columnName);
    }

    public static Uri buildMovieSelectedDBUri(Movie movieSelected) {
        return MovieDBContract.FavoriteMoviesEntry.CONTENT_URI.buildUpon()
                .appendPath(Integer.toString(movieSelected.getMovieId()))
                .build();
    }

    private static String[] formattingReviewsForDB(ArrayList<MovieReview> movieReviews) {

        String reviewsAuthor = "";
        String reviewsText = "";

        for (MovieReview review : movieReviews) {
            reviewsAuthor += review.getReviewAuthor() + CHARACTER_SEPARATING_REVIEWS_AUTHORS;
            reviewsText += review.getReviewText() + CHARACTER_SEPARATING_REVIEWS_TEXT;
        }

        String[] reviewsData = new String[2];
        reviewsData[0] = reviewsAuthor;
        reviewsData[1] = reviewsText;

        return reviewsData;
    }
}
