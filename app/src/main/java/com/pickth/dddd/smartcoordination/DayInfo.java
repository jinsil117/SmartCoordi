package com.pickth.dddd.smartcoordination;


import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class DayInfo {
    String day; //날짜
    Bitmap bm;
    int historyNum;
    ImageView img;

    public DayInfo() {
        bm = null;
    }

    public void setBm(Bitmap bm){
        this.bm = bm;
    }

    public Bitmap getBm(){
        return bm;
    }
    public void setHistoryNum(int historyNum){
        this.historyNum = historyNum;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public int getHistoryNum(){
        return historyNum;
    }
}
