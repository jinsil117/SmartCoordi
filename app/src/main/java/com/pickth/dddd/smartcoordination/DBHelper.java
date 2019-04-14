package com.pickth.dddd.smartcoordination;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    StringBuffer sb;

    public DBHelper(Context context){
        super(context, "SmartDB", null,1);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE clothesTBL(num INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT, length TEXT, season TEXT, color TEXT, img BLOB)"); //옷 정보 저장 테이블
        db.execSQL("CREATE TABLE shoesTBL(num INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT, length TEXT, season TEXT, color TEXT, img BLOB)"); //신발 정보 저장 테이블
        db.execSQL("CREATE TABLE historyTBL (num INTEGER PRIMARY KEY, year TEXT, month TEXT, day TEXT, img BLOB);"); //히스토리 사진 저장 테이블
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists shoesTBL");
        onCreate(sqLiteDatabase);
    }
}
