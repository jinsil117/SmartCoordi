package com.pickth.dddd.smartcoordination.cloth;

import android.graphics.Bitmap;

public class ClothesItem {
    String mImage;
    String mTopBottoms, mLength, mSeason, mColor;
    Boolean isInLaundry;

    byte[] mImageByteArr;

    public ClothesItem(byte[] bytes){
        mImageByteArr = bytes;
    }

    public ClothesItem(String image){
        this.mImage = image;
    }

    public ClothesItem(String image, String season){
        this.mImage = image;
        this.mSeason = season;
    }

    public ClothesItem(String topBottoms,String length, String season, String color, byte[] bytes){
        this.mTopBottoms = topBottoms;
        this.mLength = length;
        this.mSeason = season;
        this.mColor = color;
        this.mImageByteArr = bytes;
    }

    public String getmImage() {
        return mImage;
    }

    public String getmSeason() {
        return mSeason;
    }

    public void setByte(byte[] bytes){
        this.mImageByteArr = bytes;
    }
    public byte[] getByte(){
        return mImageByteArr;
    }
}
