package com.example.android.popularmoviesstage2.Models;


import android.content.Context;

public class RectangleImageView extends android.support.v7.widget.AppCompatImageView {

    public RectangleImageView(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        int measuredWidth = getMeasuredWidth();
        Double posterHeight = Math.floor(measuredWidth*1.5);
        setMeasuredDimension(measuredWidth, posterHeight.intValue());
    }
}
