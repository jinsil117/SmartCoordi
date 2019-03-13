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
        sb = new StringBuffer();
        sb.append("create table clothesTBL (");
        sb.append("idx_ integer primary key autoincrement,");
        sb.append("category text,");
        sb.append("length text,");
        sb.append("season text,");
        sb.append("color text");
        sb.append("img blob");
        db.execSQL(sb.toString());

        sb = new StringBuffer();
        sb.append("create table shoesTBL (");
        sb.append("idx_ integer primary key autoincrement,");
        sb.append("category text,");
        sb.append("season text,");
        sb.append("color text");
        sb.append("img blob");
        db.execSQL(sb.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists shoesTBL");
        onCreate(sqLiteDatabase);
    }
}
