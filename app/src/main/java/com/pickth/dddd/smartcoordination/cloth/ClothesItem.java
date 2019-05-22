package com.pickth.dddd.smartcoordination.cloth;

public class ClothesItem {
    String mImage;
    String mTopBottoms, mLength, mSeason, mColor;
    Boolean isInLaundry;

    public ClothesItem(String image){
        this.mImage = image;
    }

    public ClothesItem(String image, String season){
        this.mImage = image;
        this.mSeason = season;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmSeason() {
        return mSeason;
    }
}
