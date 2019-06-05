package com.pickth.dddd.smartcoordination.cloth;

public class ClothesItem {
    String mTopBottoms, mLength, mSeason, mColor;
    byte[] mImageByteArr;

    public  ClothesItem(){}

    public ClothesItem(byte[] bytes){
        mImageByteArr = bytes;
    }

    public ClothesItem(String topBottoms,String length, String season, String color, byte[] bytes){
        this.mTopBottoms = topBottoms;
        this.mLength = length;
        this.mSeason = season;
        this.mColor = color;
        this.mImageByteArr = bytes;
    }

    public String getmTopBottoms() {
        return mTopBottoms;
    }
    public void setmTopBottoms(String topBottoms){
        this.mTopBottoms = topBottoms;
    }

    public String getmLength() {
        return mLength;
    }
    public void setmLength(String length){
        this.mLength = length;
    }

    public String getmSeason() {
        return mSeason;
    }
    public void setmSeason(String season){
        this.mSeason = season;
    }

    public String getmColor() {
        return mColor;
    }
    public void setmColor(String color){
        this.mColor = color;
    }

    public void setByte(byte[] bytes){
        this.mImageByteArr = bytes;
    }
    public byte[] getByte(){
        return mImageByteArr;
    }
}
