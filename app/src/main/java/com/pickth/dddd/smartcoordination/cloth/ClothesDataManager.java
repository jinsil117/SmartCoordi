package com.pickth.dddd.smartcoordination.cloth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.pickth.dddd.smartcoordination.DBHelper;

import java.util.ArrayList;

import static com.pickth.dddd.smartcoordination.cloth.ClothesFragment.rvClothes;

public class ClothesDataManager {  //DB
    private Context mContext;
    private ArrayList<ClothesItem> mItems;

    DBHelper DBHelper;
    SQLiteDatabase db, db2, db3;

    public ClothesDataManager(Context context) {
        mContext = context.getApplicationContext();
        DBHelper = new DBHelper(mContext);
    }

    /**
     * mItems가 비어있으면 파일에서 가져오는 메소드
     * @return
     */
    public ArrayList<ClothesItem> getClothesItems() {
        db = DBHelper.getReadableDatabase();
        mItems = new ArrayList<>(); //저장을 위한 배열
        Cursor cursor = db.rawQuery("SELECT num FROM clothesTBL;", null);
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int num = cursor.getInt(0);
            try { //db에서 옷을 가져옴
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

                        db3 = DBHelper.getReadableDatabase();
                        Cursor blobCursor = db3.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=" + num, null);
                        if (blobCursor.moveToNext()) {
                            byte[] barr = blobCursor.getBlob(0);
                            if (barr != null) {
                                System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                            }
                            blobStart += blobLen;
                        }
                        blobCursor.close();
                        db3.close();
                    }
                    ClothesItem clo = new ClothesItem(bytes); //이미지를 저장시킬 인스턴스 생성
                    mItems.add(clo);
                }
                sizeCursor.close();
                db2.close();
            } catch (Exception e) { }
        }
        cursor.close();
        db.close();
        return mItems;
    }

    /**
     * mItems를 파일에 저장하는 메소드
     * mItems에 아이템을 추가하거나 삭제했을 때 호출한다.
     */
    public void notifyDataSetChanged() {
        ArrayList<ClothesItem> items = getClothesItems();
        ClothesAdapter adapter = new ClothesAdapter();
        //adapter에 item 추가하기
        for(ClothesItem item: items)
            adapter.addItem(item);
        adapter.notifyDataSetChanged();
        // recycler view 설정
        rvClothes.setAdapter(adapter);
//        rvClothes.setLayoutManager(new GridLayoutManager(mContext, 3));
    }

    /**
     * 아이템을 추가하는 메소드
     * @param item
     */
    public void addItem(ClothesItem item) {
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
            db.close();
        } catch (Exception e) { }
        notifyDataSetChanged();
    }

    /**
     * 아이템을 삭제하는 메소드
     * @param position
     */
    public void removeItem(int position) {
        db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL LIMIT " + position +",1;", null);
        cursor.moveToNext();
        int num = cursor.getInt(0);
        db = DBHelper.getWritableDatabase();
        //이미지를 db에서 삭제
        SQLiteStatement p = db.compileStatement("DELETE FROM clothesTBL WHERE num = " + num);
        p.execute();
        cursor.close();
        db.close();
        notifyDataSetChanged();
    }

    /**
     * 선택한 아이템을 가져오는 메소드
     * @param position
     */
    public ClothesItem getItem(int position) {
        ClothesItem item = new ClothesItem();
        db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL LIMIT " + position +",1;", null);
        cursor.moveToNext();
        int num = cursor.getInt(0);
        item.setmTopBottoms(cursor.getString(1));
        item.setmLength(cursor.getString(2));
        item.setmSeason(cursor.getString(3));
        item.setmColor(cursor.getString(4));
        cursor.close();
        db.close();
        return item;
    }
}
