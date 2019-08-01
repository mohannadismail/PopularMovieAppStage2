package com.example.android.popularmoviesstage2.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TrailerThumbnail implements Parcelable {

    private String thumbnailKey;
    private String thumbnailPath;

    public TrailerThumbnail(String thumbnailPath, String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
        this.thumbnailPath = thumbnailPath;
    }

    private TrailerThumbnail(Parcel in) {
        thumbnailKey = in.readString();
        thumbnailPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getThumbnailTag() {
        return thumbnailKey;
    }
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailTag(String thumbnailTag) {
        this.thumbnailKey = thumbnailTag;
    }
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(thumbnailKey);
        parcel.writeString(thumbnailPath);
    }

    public static Parcelable.Creator<TrailerThumbnail> CREATOR = new Parcelable.Creator<TrailerThumbnail>() {

        @Override
        public TrailerThumbnail createFromParcel(Parcel parcel) {
            return new TrailerThumbnail(parcel);
        }

        @Override
        public TrailerThumbnail[] newArray(int i) {
            return new TrailerThumbnail[i];
        }
    };


}
