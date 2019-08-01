package com.example.android.popularmoviesstage2.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmoviesstage2.Database.DBServiceTasks;
import com.example.android.popularmoviesstage2.Database.FavoritesUtilities;
import com.example.android.popularmoviesstage2.Database.ImagesDBUtilities;
import com.example.android.popularmoviesstage2.Database.MovieDBContract;
import com.example.android.popularmoviesstage2.Models.Movie;
import com.example.android.popularmoviesstage2.Models.RectangleImageView;
import com.example.android.popularmoviesstage2.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MovieViewHolder> {


    private int mNumberOfItems;
    private Context mContext;
    private ArrayList<Movie> mMoviesArray;
    private final MovieAdapterOnClickHandler mClickHandler;

    private String mSearchCriteria;

    private int mMoviePosterViewId;

    public MovieRecyclerViewAdapter(ArrayList<Movie> moviesArray, int numberOfItems,
                                    MovieAdapterOnClickHandler movieAdapterOnClickHandler, Context context,
                                    String searchCriteria) {
        mNumberOfItems = numberOfItems;
        mContext = context;
        mClickHandler = movieAdapterOnClickHandler;
        mSearchCriteria = searchCriteria;
        mMoviesArray = moviesArray;
    }

    @Override
    public MovieRecyclerViewAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        int layoutIdItem = R.layout.movie_item;
        boolean shouldAttachToParentImmediately = false;

        LinearLayout view = (LinearLayout) layoutInflater.inflate(layoutIdItem, parent, shouldAttachToParentImmediately);

        RectangleImageView movieView = createRectangularImageView();
        view.addView(movieView, 0);

        return new MovieViewHolder(view);
    }

    private RectangleImageView createRectangularImageView() {

        RectangleImageView movieView = new RectangleImageView(mContext);
        movieView.setAdjustViewBounds(true);
        movieView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMoviePosterViewId = View.generateViewId();
            movieView.setId(mMoviePosterViewId);
        } else {
            mMoviePosterViewId = R.id.posterImageView;
            movieView.setId(R.id.posterImageView);
        }

        return movieView;
    }

    @Override
    public void onBindViewHolder(MovieRecyclerViewAdapter.MovieViewHolder holder, int position) {

        Movie movie = mMoviesArray.get(position);
        movie.setIsMovieFavorite(determineIfMovieIsFavorite(movie));


        if (!mSearchCriteria.equals("Favorites")) {
            String posterPath = movie.getMoviePosterPath();
            loadMoviePoster(posterPath, holder.mMoviePoster);
        } else {
            String posterPath = DBServiceTasks.getImagePathFromDB(mContext,
                    DBServiceTasks.buildMovieSelectedDBUri(movie),
                    MovieDBContract.FavoriteMoviesEntry.COLUMN_NAME_DATABASE_POSTER_PATH);

            Bitmap poster = ImagesDBUtilities.loadImageFromStorage(posterPath,
                    Integer.toString(movie.getMovieId()),
                    FavoritesUtilities.IMAGE_TYPE_POSTER,
                    -1);

            holder.mMoviePoster.setImageBitmap(poster);
        }

        holder.setOnClickListener(holder.mMoviePoster, movie);

        holder.mMovieTitleView.setText(movie.getMovieTitle());
        holder.mMovieRatingView.setText(Double.toString(movie.getMovieVoteAverage()));

        determineForAdultsLogo(holder, movie.getIsMovieForAdults());
        determineMainPosterFavoriteLogo(holder, movie, movie.getIsMovieFavorite());
    }

    private void determineMainPosterFavoriteLogo(MovieRecyclerViewAdapter.MovieViewHolder holder, Movie movie, boolean isFavorite) {
        if (isFavorite) {
            holder.mMovieIsFavoriteView.setImageResource(R.drawable.heart_pressed_white);
            movie.setIsMovieFavorite(true);
        } else {
            holder.mMovieIsFavoriteView.setImageResource(R.drawable.heart_not_pressed_thin);
            movie.setIsMovieFavorite(false);
        }
    }

    private boolean determineIfMovieIsFavorite(Movie movie) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        if (sharedPreferences.contains(FavoritesUtilities.SHARED_PREFERENCES_FAVORITES_STRING)) {
            return sharedPreferences.getStringSet(FavoritesUtilities.SHARED_PREFERENCES_FAVORITES_STRING, null)
                    .contains(Integer.toString(movie.getMovieId()));
        } else {
            return FavoritesUtilities.checkIfMovieIsFavorite(mContext, Integer.toString(movie.getMovieId()));
        }
    }



    @Override
    public int getItemCount() {
        return mNumberOfItems;
    }

    private void determineForAdultsLogo(MovieRecyclerViewAdapter.MovieViewHolder holder, boolean isForAdults) {
        if (isForAdults) {
            holder.mMovieIsForAdultsView.setImageResource(R.drawable.for_adults);
        } else {
            holder.mMovieIsForAdultsView.setImageResource(R.drawable.for_children);
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        private RectangleImageView mMoviePoster;

        private TextView mMovieTitleView;
        private TextView mMovieRatingView;
        private ImageView mMovieIsForAdultsView;
        private ImageView mMovieIsFavoriteView;

        private MovieViewHolder(View itemView) {
            super(itemView);

            mMoviePoster = itemView.findViewById(mMoviePosterViewId);
            mMovieTitleView = itemView.findViewById(R.id.movie_item_title);
            mMovieRatingView = itemView.findViewById(R.id.movie_item_rating);
            mMovieIsForAdultsView = itemView.findViewById(R.id.movie_item_adults);
            mMovieIsFavoriteView = itemView.findViewById(R.id.movie_item_favorite);
        }

        private void setOnClickListener(RectangleImageView movieView, Movie movie) {
            final Movie movieFinal = movie;
            movieView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickHandler.onClick(movieFinal);
                }
            });
        }
    }

    public static int calculateColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int gridItemWidth = context.getResources().getInteger(R.integer.mainPosterLayoutWidthInt);
        int columns = (int) (dpWidth / gridItemWidth);
        return columns;
    }

    private void loadMoviePoster(String posterPath, RectangleImageView movieView) {

        if (posterPath != null) {
            Picasso.with(mContext)
                    .load(posterPath)
                    .placeholder(R.drawable.placeholder)
                    .fit()
                    .error(R.drawable.error)
                    .into(movieView);
        }
    }

    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }
}
