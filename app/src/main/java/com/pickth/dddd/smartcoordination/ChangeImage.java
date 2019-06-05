package com.pickth.dddd.smartcoordination;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import android.net.Uri;

public class ChangeImage extends AppCompatActivity {
//
//    SQLiteDatabase db;
//    DBHelper dbHelper;
//    String query;
//    int questionMark;
//    public ImageSave(String query){
//        this.query = query;
//        dbHelper = new DBHelper(this);
//    }
//
//    public void createBitmap(Intent data){
//        // 선택한 이미지에서 비트맵 생성
//        try {
//            InputStream in = getContentResolver().openInputStream(data.getData());
//            Bitmap imgg = BitmapFactory.decodeStream(in);
//            byte[] image = getBytes(imgg);
//            db = dbHelper.getWritableDatabase();
//            SQLiteStatement p = db.compileStatement("INSERT INTO historyTBL values(?,?);");
//            p.bindBlob(2, image);
//            p.execute();
//            db.close();
//            in.close();
//        }catch (IOException e){
//
//        }
//    }

    public ChangeImage(){

    }
    public static byte[] getBytes(Bitmap bitmap) { //bitmap을 byte(blob)로 변환. db에 저장할 때 사용
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }

    public static Bitmap getBitmap(byte[] image) { //byte형식을 bitmap으로 변환
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        return bitmap;
    }

    public static Bitmap getBitmap(Activity activity, Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);

        }catch (Exception e){}
        return bitmap;
    }
}
