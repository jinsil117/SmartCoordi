package com.pickth.dddd.smartcoordination;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.Context;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClothesDataManager {  //메모 저장
    private Context mContext;
    private ArrayList<ClothesItem> mItems = new ArrayList<>();

    public ClothesDataManager(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * mItems가 비어있으면 파일에서 가져오는 메소드
     * @return
     */
    public ArrayList<ClothesItem> getClothesItems() {
        if(mItems.size() == 0) {
            String json = mContext
                    .getSharedPreferences("cloth_item", Context.MODE_PRIVATE)
                    .getString("clothes", "");

            if(json == "") return mItems;

            Type type = new TypeToken<ArrayList<ClothesItem>>() {}.getType();
            mItems = new Gson().fromJson(json, type);
        }

        return mItems;
    }

    /**
     * mItems를 파일에 저장하는 메소드
     * mItems에 아이템을 추가하거나 삭제했을 때 호출한다.
     */
    public void notifyDataSetChanged() {
        mContext.getSharedPreferences("cloth_item", Context.MODE_PRIVATE)
                .edit()
                .putString("clothes", new Gson().toJson(mItems).toString())
                .apply();
    }

    /**
     * 캘린더 아이템을 추가하는 메소드
     * @param item
     */
    public void addItem(ClothesItem item) {
        getClothesItems().add(item);
        notifyDataSetChanged();
    }
}
