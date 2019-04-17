package com.pickth.dddd.smartcoordination.cloth;

import android.net.Uri;
import android.util.Log;

public class ClothesItem {
    public Uri mImage;
    String mTopBottoms, mLength, mSeason, mColor;
    Boolean isInLaundry;

    public ClothesItem(Uri image){
        this.mImage = image;
        Log.d("mmmm", "image");
    }

    public ClothesItem(Uri image, String season){
        this.mImage = image;
        this.mSeason = season;
        Log.d("mmmm", "image + season");
    }
}
