package com.pickth.dddd.smartcoordination.cloth;

import java.util.ArrayList;

public interface ClothesClickListener {

    /**
     * 옷 아이템을 눌렀을 때 호출할 메소드
     * @param items 해당 날짜의 스케쥴 리스트들
     */
    void onClick(ArrayList<ClothesItem> items);
}
