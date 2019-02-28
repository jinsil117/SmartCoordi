package com.pickth.dddd.smartcoordination;

import java.util.ArrayList;

public class ClothesItem {
    public String title;
    public ArrayList<String> images;

    public ClothesItem(String title, ArrayList<String> images){
        this.title = title;
        this.images = images;
    }

    public ClothesItem(String title){
        this.title = title;
    }
}
