package com.example.android.popularmoviesstage2.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieReview implements Parcelable {

    private String reviewText;
    private String reviewAuthor;

    public MovieReview(String reviewAuthor, String reviewText) {
        this.reviewText = reviewText;
        this.reviewAuthor = reviewAuthor;
    }

    private MovieReview(Parcel in) {
        reviewText = in.readString();
        reviewAuthor = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(reviewText);
        parcel.writeString(reviewAuthor);
    }

    public static Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {

        @Override
        public MovieReview createFromParcel(Parcel parcel) {
            return new MovieReview(parcel);
        }

        @Override
        public MovieReview[] newArray(int i) {
            return new MovieReview[i];
        }
    };

    public String getReviewText() { return reviewText; }
    public String getReviewAuthor() { return reviewAuthor; }

    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public void setReviewAuthor(String reviewAuthor) { this.reviewAuthor = reviewAuthor; }
}
