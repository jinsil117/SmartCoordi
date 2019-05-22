package com.pickth.dddd.smartcoordination.lookbook;

import android.graphics.Bitmap;

public class dataItem {

    protected int num;
    protected Bitmap bm;
    protected boolean checked;

    public dataItem(int num, Bitmap bm){
        this.num = num;
        this.bm = bm;
        checked = false;
    }

    protected void setChecked(boolean checked){
        this.checked = checked;
    }

    protected boolean getChecked(){
        return checked;
    }

    protected int getNum(){
        return num;
    }

    protected Bitmap getBm(){
        return bm;
    }
}
