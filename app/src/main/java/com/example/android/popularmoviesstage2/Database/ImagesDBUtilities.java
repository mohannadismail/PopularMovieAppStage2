package com.example.android.popularmoviesstage2.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.example.android.popularmoviesstage2.Activities.DetailsActivity;
import com.example.android.popularmoviesstage2.GeneralUtilities.LoaderUtilities;
import com.example.android.popularmoviesstage2.Models.Movie;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.android.popularmoviesstage2.Database.FavoritesUtilities.CHARACTER_TO_SEPARATE_THUMBNAILS;
import static com.example.android.popularmoviesstage2.Database.FavoritesUtilities.CHARACTER_TO_SEPARATE_THUMBNAIL_TAG;
import static com.example.android.popularmoviesstage2.Database.FavoritesUtilities.IMAGE_TYPE_BACKDROP;
import static com.example.android.popularmoviesstage2.Database.FavoritesUtilities.IMAGE_TYPE_POSTER;
import static com.example.android.popularmoviesstage2.Database.FavoritesUtilities.IMAGE_TYPE_TRAILER_THUMBNAIL;

public class ImagesDBUtilities {

    private static final String DIRECTORY_POSTERS = "postersDir";
    private static final String DIRECTORY_BACKDROPS = "backdropsDir";
    private static final String DIRECTORY_THUMBNAILS = "thumbnailDir";

    public static void saveAllMovieImages(Context context, Movie movieSelected) {

        saveMovieImage(FavoritesUtilities.IMAGE_TYPE_POSTER,
                context,
                movieSelected,
                movieSelected.getMoviePosterPath());

        saveMovieImage(FavoritesUtilities.IMAGE_TYPE_BACKDROP,
                context,
                movieSelected,
                DetailsActivity.createFullBackdropPath(context, movieSelected.getMovieBackdropPath()));

        saveMovieThumbnails(context, movieSelected);
    }

    private static void saveMovieThumbnails(Context context, Movie movieSelected) {

        for (int i = 0; i < movieSelected.getMovieTrailersThumbnails().size(); i++) {

            Bitmap bitmapTrailer = getImageBitmapFromPicasso(context,
                    movieSelected.getMovieTrailersThumbnails().get(i).getThumbnailPath());

            ImagesDBUtilities.saveImageToInternalStorage(bitmapTrailer,
                    Integer.toString(movieSelected.getMovieId()),
                    FavoritesUtilities.IMAGE_TYPE_TRAILER_THUMBNAIL,
                    context,
                    movieSelected,
                    i);
        }
    }

    private static void saveMovieImage(String imageType, Context context, Movie movieSelected, String loadPath) {
        Bitmap bitmap = getImageBitmapFromPicasso(context, loadPath);

        ImagesDBUtilities.saveImageToInternalStorage(bitmap,
                Integer.toString(movieSelected.getMovieId()),
                imageType,
                context,
                movieSelected,
                -1);
    }

    private static Bitmap getImageBitmapFromPicasso(Context context, String path) {

        Bitmap bitmap = null;

        try {
            bitmap = Picasso.with(context)
                    .load(path)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static File createFileDirectory(Context context, String imageType) {

        ContextWrapper cw = new ContextWrapper(context);

        File directory = null;

        switch (imageType) {
            case IMAGE_TYPE_POSTER:
                directory = cw.getDir(DIRECTORY_POSTERS, Context.MODE_PRIVATE);
                break;
            case IMAGE_TYPE_BACKDROP:
                directory = cw.getDir(DIRECTORY_BACKDROPS, Context.MODE_PRIVATE);
                break;
            case IMAGE_TYPE_TRAILER_THUMBNAIL:
                directory = cw.getDir(DIRECTORY_THUMBNAILS, Context.MODE_PRIVATE);
                break;
            default:
                break;
        }

        return directory;
    }

    public static String saveImageToInternalStorage(Bitmap bitmapImage, String movieDBId,
                                                    String imageType, Context context, Movie movieSelected,
                                                    int thumbnailIndex) {

        File directory = createFileDirectory(context, imageType);

        if (directory != null) {
            File bitmapPath = createBitmapPath(directory, imageType, movieDBId, thumbnailIndex);
            createBitmapOutputStream(bitmapImage, bitmapPath);

            saveImagePathToDatabase(context, directory.getAbsolutePath(), imageType, movieSelected, thumbnailIndex);
        }

        return directory.getAbsolutePath();
    }

    private static File createBitmapPath(File directory, String imageType, String movieDBId, int thumbnailIndex) {
        File bitmapPath;

        if (imageType.equals(IMAGE_TYPE_TRAILER_THUMBNAIL)) {
            bitmapPath = new File(directory, movieDBId + thumbnailIndex + ".jpg");
        } else {
            bitmapPath = new File(directory, movieDBId + ".jpg");
        }

        return bitmapPath;
    }

    private static void createBitmapOutputStream(Bitmap bitmapImage, File bitmapPath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(bitmapPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ContentValues generateContentValuesForImagePath(Context context, String imageInternalPath,
                                                                   String imageType, Movie movieSelected,
                                                                   int thumbnailIndex, Uri uri) {
        ContentValues cv = new ContentValues();

        switch (imageType) {
            case IMAGE_TYPE_POSTER:
                cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_POSTER_PATH, imageInternalPath);
                break;
            case IMAGE_TYPE_BACKDROP:
                cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_BACKDROP_PATH, imageInternalPath);
                break;
            case IMAGE_TYPE_TRAILER_THUMBNAIL:

                String newInternetThumbnails = formatInternetTrailersForDB(context, movieSelected, uri, thumbnailIndex);

                String newDatabaseThumbnailsString = formatThumbnailsForDB(context,
                        imageInternalPath,
                        movieSelected,
                        uri,
                        thumbnailIndex);

                cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_TRAILERS_THUMBNAILS,
                        newDatabaseThumbnailsString);

                cv.put(MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TRAILERS_THUMBNAILS,
                        newInternetThumbnails);

                break;
        }

        return cv;
    }

    public static void saveImagePathToDatabase(Context context, String imageInternalPath,
                                               String imageType, Movie movieSelected,
                                               int thumbnailIndex) {
        Uri uri = DBServiceTasks.buildMovieSelectedDBUri(movieSelected);

        ContentValues cv = generateContentValuesForImagePath(context,
                imageInternalPath, imageType, movieSelected, thumbnailIndex, uri);

        context.getContentResolver().update(uri, cv, null, null);
    }

    private static String createNewInternetThumbnails(String previousThumbnails, String trailerPath, String trailerKey) {
        return previousThumbnails +
                trailerPath +
                CHARACTER_TO_SEPARATE_THUMBNAIL_TAG +
                trailerKey +
                CHARACTER_TO_SEPARATE_THUMBNAILS;
    }

    private static String formatThumbnailsForDB(Context context, String imageInternalPath, Movie movieSelected,
                                                Uri uri, int thumbnailIndex) {

        Cursor previousThumbnails = context.getContentResolver().query(uri,
                new String[]{MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_TRAILERS_THUMBNAILS},
                null,
                null,
                null);

        if (previousThumbnails.moveToFirst()) {
            String previousString = LoaderUtilities.getStringFromCursor(previousThumbnails,
                    MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_TRAILERS_THUMBNAILS);

            previousThumbnails.close();

            return createNewThumbnails(previousString, imageInternalPath, movieSelected, thumbnailIndex);
        } else {
            return null;
        }
    }

    private static String createNewThumbnails(String previousString, String imageInternalPath,
                                              Movie movieSelected, int thumbnailIndex) {
        String newThumbnails = "";

        newThumbnails += previousString +
                imageInternalPath +
                CHARACTER_TO_SEPARATE_THUMBNAIL_TAG +
                movieSelected.getMovieTrailersThumbnails().get(thumbnailIndex).getThumbnailTag() +
                CHARACTER_TO_SEPARATE_THUMBNAILS;

        return newThumbnails;
    }

    private static String formatInternetTrailersForDB(Context context, Movie movieSelected,
                                                      Uri uri, int thumbnailIndex) {
        Cursor previousInternetThumbnails = context.getContentResolver().query(uri,
                new String[]{MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TRAILERS_THUMBNAILS},
                null,
                null,
                null);

        if (previousInternetThumbnails.moveToFirst()) {
            String previousString = LoaderUtilities.getStringFromCursor(previousInternetThumbnails,
                    MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_TRAILERS_THUMBNAILS);

            previousInternetThumbnails.close();

            return createNewInternetThumbnails(previousString,
                    movieSelected.getMovieTrailersThumbnails().get(thumbnailIndex).getThumbnailPath(),
                    movieSelected.getMovieTrailersThumbnails().get(thumbnailIndex).getThumbnailTag());
        } else {
            return null;
        }
    }

    public static Bitmap loadImageFromStorage(String path, String movieDBId, String imageType, int thumbnailIndex) {

        Bitmap bitmap = null;

        try {
            File f;

            if (imageType.equals(FavoritesUtilities.IMAGE_TYPE_TRAILER_THUMBNAIL)) {
                f = new File(path, movieDBId + thumbnailIndex + ".jpg");
            } else {
                f = new File(path, movieDBId + ".jpg");
            }

            bitmap = BitmapFactory.decodeStream(new FileInputStream(f));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static boolean deleteImageFromStorage(Context context, String path, String movieDBId,
                                                 String imageType, int thumbnailIndex) {

        File file;

        switch (imageType) {
            case FavoritesUtilities.IMAGE_TYPE_BACKDROP:
            case FavoritesUtilities.IMAGE_TYPE_POSTER:
                file = new File(path, movieDBId + ".jpg");
                break;
            case FavoritesUtilities.IMAGE_TYPE_TRAILER_THUMBNAIL:
                file = new File(path, movieDBId + thumbnailIndex + ".jpg");
                break;
            default:
                throw new UnsupportedOperationException("Unknown image type: " + imageType);
        }

        return file.delete();
    }

    public static Bitmap loadImageFromDatabase(Context context, Movie movieSelected, String databaseColumnName, String imageType) {

        String[] projection = {
                databaseColumnName
        };

        Cursor pathCursor = context.getContentResolver().query(
                DetailsActivity.mMovieSelectedUri,
                projection,
                null,
                null,
                MovieDBContract.FavoriteMoviesEntry._ID);

        pathCursor.moveToFirst();

        String imagePath = pathCursor.getString(pathCursor.getColumnIndex(databaseColumnName));

        pathCursor.close();

        return ImagesDBUtilities.loadImageFromStorage(
                imagePath,
                Integer.toString(movieSelected.getMovieId()),
                imageType,
                -1);
    }

    public static boolean deleteThumbnailsFromStorage(Context context, String movieDBId) {

        boolean allRemoved = true;

        String[] trailersArray = DetailsActivity.queryTrailersArray(context);

        for (int i = 0; i < trailersArray.length; i++) {

            String trailerPath = trailersArray[i].split(FavoritesUtilities.CHARACTER_TO_SEPARATE_THUMBNAIL_TAG)[0];

            boolean removed = ImagesDBUtilities.deleteImageFromStorage(context,
                    trailerPath,
                    movieDBId,
                    FavoritesUtilities.IMAGE_TYPE_TRAILER_THUMBNAIL,
                    i);

            if (!removed) {
                allRemoved = false;
            }
        }
        return allRemoved;
    }


}
