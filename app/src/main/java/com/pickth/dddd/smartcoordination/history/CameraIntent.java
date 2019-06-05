package com.pickth.dddd.smartcoordination.history;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.pickth.dddd.smartcoordination.ChangeImage;
import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;

public class CameraIntent extends AppCompatActivity {

    Uri photoUri;
    String photoString;

    DBHelper dbHelper;
    SQLiteDatabase db;
    Integer savePk;
    String year,month,day;
    ImageView imageView;

    Bitmap currBit;

    FloatingActionButton fab;
    ChangeImage changeImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_camera);
        dbHelper = new DBHelper(getApplicationContext());
        changeImage = new ChangeImage();

        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("imageUri");
        try {
            photoString = photoUri.toString();
        } catch (Exception e) {
        }
        String imageFilePath = intent.getStringExtra("imageFilePath");
        year = intent.getStringExtra("year");
        month = intent.getStringExtra("month");
        day = intent.getStringExtra("day");
        savePk =intent.getIntExtra("pk",0);
        Bitmap bitmap = changeImage.getBitmap(this, photoUri);

        currBit = rotateImage(bitmap,90);

        imageView = (ImageView)findViewById(R.id.camera_img);
        imageView.setImageBitmap(currBit);
    }

    public boolean onCreateOptionsMenu(Menu menu) { //액션바 우측에 더하기 메뉴 생성
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add :
                saveDB();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveDB(){
        Bitmap bm = changeImage.getBitmap(this,photoUri);
        byte[] bytes = ChangeImage.getBytes(bm);

        try{
            db = dbHelper.getWritableDatabase();
            SQLiteStatement p = db.compileStatement("INSERT INTO historyTBL values(?,?,?,?,?);");
            p.bindLong(1, savePk);
            p.bindString(2, year);
            p.bindString(3, month); //12월이면 11로 저장됨
            p.bindString(4, day);
            p.bindBlob(5, bytes);
            p.execute();

            p.close();
            db.close();
        }catch (Exception e){}

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
