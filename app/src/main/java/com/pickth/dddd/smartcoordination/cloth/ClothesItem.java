package com.pickth.dddd.smartcoordination.cloth;

import android.net.Uri;

public class ClothesItem {
    public Uri mImage;
    String mTopBottoms, mLength, mSeason, mColor;
    Boolean isInLaundry;

    public ClothesItem(Uri image){
        this.mImage = image;
    }
}
