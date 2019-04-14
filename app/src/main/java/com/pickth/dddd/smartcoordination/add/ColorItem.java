package com.pickth.dddd.smartcoordination.add;

public class ColorItem {
    private String mColorName;
    private int mColor;

    public ColorItem(String colorName, int color){
        mColorName = colorName;
        mColor = color;
    }

    public String getColorName(){
        return mColorName;
    }

    public int getColor(){
        return mColor;
    }
}
