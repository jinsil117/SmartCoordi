package com.pickth.dddd.smartcoordination.cloth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pickth.dddd.smartcoordination.ChangeImage;
import com.pickth.dddd.smartcoordination.DBHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ClothesDataManager {  //저장
    private Context mContext;
    private ArrayList<ClothesItem> mItems;

    ChangeImage changeImage;
    DBHelper DBHelper;
    SQLiteDatabase db, db2, db3;
    int i = 0;

    public ClothesDataManager(Context context) {
        mContext = context.getApplicationContext();
        DBHelper = new DBHelper(mContext);
    }

//    /**
//     * mItems가 비어있으면 파일에서 가져오는 메소드
//     * @return
//     */
//    public ArrayList<ClothesItem> getClothesItems() {
//        if(mItems.size() == 0) {
//            String json = mContext
//                    .getSharedPreferences("cloth_item", Context.MODE_PRIVATE)
//                    .getString("clothes", "");
//
//            if(json == "") return mItems;
//
//            Type type = new TypeToken<ArrayList<ClothesItem>>() {}.getType();
////            Type type = TypeToken.getParameterized(ArrayList.class, ClothesItem.class).getType();
//            mItems = new Gson().fromJson(json, type);
//
//        }
//        return mItems;
//    }
    /**
     * mItems가 비어있으면 파일에서 가져오는 메소드
     * @return
     */
    public ArrayList<ClothesItem> getClothesItems() {
        db = DBHelper.getReadableDatabase();
        mItems = new ArrayList<>(); //저장을 위한 배열
        Cursor cursor = db.rawQuery("SELECT num FROM clothesTBL;", null);
        if (i != cursor.getCount()) {
            cursor.moveToNext();
            int num = cursor.getInt(0);
            Log.d("sqlll", "num " + num);
            try { //db에서 옷을 가져옴
                DBHelper = new DBHelper(mContext);
                db2 = DBHelper.getReadableDatabase();
                Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=" + num, null);
                if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                    long blobStart = 1; //blob 시작
                    long blobLen = 1; //blob 길이
                    int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                    byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                    while (blobSize > 0) {
                        blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                        blobSize -= blobLen;

                        DBHelper = new DBHelper(mContext);
                        db3 = DBHelper.getReadableDatabase();
                        Cursor blobCursor = db3.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL;", null);
                        if (blobCursor.moveToNext()) {
                            byte[] barr = blobCursor.getBlob(0);
                            if (barr != null) {
                                System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                            }
                            blobStart += blobLen;
                            blobCursor.close();
                            db3.close();
                        }
                    }
                    ClothesItem clo = new ClothesItem(bytes); //이미지를 저장시킬 인스턴스 생성
                    mItems.add(clo);
                }
                sizeCursor.close();
                db2.close();
            } catch (Exception e) { }
            cursor.close();
            db.close();
            i++;
        }
        Log.d("sqlll", "mItems.size() " + mItems.size());
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
     * 아이템을 추가하는 메소드
     * @param item
     */
    public void addItem(ClothesItem item) {
        //        getClothesItems().add(item);

        // 입력한 값을 db에 저장하는 부분
        try {
            db = DBHelper.getWritableDatabase();
            //이미지 정보를 db에 저장
            SQLiteStatement p = db.compileStatement("INSERT INTO clothesTBL values(?,?,?,?,?,?);");
            p.bindString(2, item.mTopBottoms);
            p.bindString(3, item.mLength);
            p.bindString(4, item.mSeason);
            p.bindString(5, item.mColor);
            p.bindBlob(6, item.mImageByteArr);
            p.execute();

            Log.d("sqlll", "insert");

            db.close();

//            selectedDay.img.setImageBitmap(rotateBitmap);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("sqlll", e.getMessage());
        }
        notifyDataSetChanged();
    }

    /**
     * 아이템을 삭제하는 메소드
     * @param item
     */
    public void removeItem(ClothesItem item) {
//        getClothesItems().remove(item);
        mItems.remove(item);
        Log.d("rrrrr", "remove");
        notifyDataSetChanged();
    }
}
