package com.example.android.popularmoviesstage2.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDBHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + MovieDBContract.FavoriteMoviesEntry.TABLE_NAME                           + " (" +
                              MovieDBContract.FavoriteMoviesEntry._ID                                      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_MOVIEDB_ID                   + " INT NOT NULL, "  +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TITLE                        + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_RELEASE_DATE                 + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_POSTER_PATH                  + " TEXT, "          +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_POSTER_PATH         + " TEXT, "          +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_VOTE_AVERAGE                 + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_PLOT                         + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_LANGUAGE                     + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_RUNTIME                      + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_CAST                         + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_REVIEWS_AUTHOR               + " TEXT, "          +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_REVIEWS_TEXT                 + " TEXT, "          +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_IS_FOR_ADULTS                + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_BACKDROP                     + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_BACKDROP_PATH       + " TEXT, "          +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TRAILERS_THUMBNAILS          + " TEXT NOT NULL, " +
                              MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_TRAILERS_THUMBNAILS + " TEXT, "          +
                              " UNIQUE (" + MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_MOVIEDB_ID + ") ON CONFLICT REPLACE"+
                              ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
                MovieDBContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
