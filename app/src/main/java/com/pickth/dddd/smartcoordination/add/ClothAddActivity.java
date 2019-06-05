package com.pickth.dddd.smartcoordination.add;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;
import com.pickth.dddd.smartcoordination.cloth.ClothesDataManager;
import com.pickth.dddd.smartcoordination.cloth.ClothesItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by HaEun
 */

public class ClothAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerTopBottoms, spinnerLength, spinnerSeason, spinnerColor;
    ArrayList<ColorItem> mColorList;
    ImageView imageView;
    Uri photoUri;
    String photoString;

    byte[] inputData;

    SQLiteDatabase db;
    DBHelper DBHelper;
    Bitmap bm;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_add);
        DBHelper = new DBHelper(getApplicationContext());

        //상의와 하의 중에 선택하는 스피너 연결
        spinnerTopBottoms = (Spinner) findViewById(R.id.spinner_topBottoms_clothAdd);
        spinnerTopBottoms.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinnerTopBottoms layout
        ArrayAdapter<CharSequence> adapterTopBottoms = ArrayAdapter.createFromResource(this, R.array.array_topBottoms, android.R.layout.simple_spinner_dropdown_item);
        //simple_spinner_dropdown_item or simple_spinner_item
        // Apply the adapterTopBottoms to the spinnerTopBottoms
        spinnerTopBottoms.setAdapter(adapterTopBottoms);

        //옷의 길이를 선택하는 스피너 연결
        spinnerLength = (Spinner)findViewById(R.id.spinner_length_clothAdd);
        spinnerLength.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterLength = ArrayAdapter.createFromResource(this, R.array.array_length, android.R.layout.simple_spinner_dropdown_item);
        spinnerLength.setAdapter(adapterLength);

        //옷의 계절을 선택하는 스피너 연결
        spinnerSeason = (Spinner)findViewById(R.id.spinner_season_clothAdd);
        spinnerSeason.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterSeason = ArrayAdapter.createFromResource(this, R.array.array_season, android.R.layout.simple_spinner_dropdown_item);
        spinnerSeason.setAdapter(adapterSeason);

        //옷의 색을 선택하는 스피너 연결
        //spinnerColor 초기화
        mColorList = new ArrayList<>();
        initList();
        spinnerColor = findViewById(R.id.spinner_color_clothAdd);
        ColorAdapter mAdapter = new ColorAdapter(this, mColorList);
        spinnerColor.setAdapter(mAdapter);
        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ColorItem clickedItem = (ColorItem) parent.getItemAtPosition(position);
                String clickedColorName = clickedItem.getColorName();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        //ClothesFragment에서 옷의 uri를 가져와 GetAzureDataAsyncTask를 생성한다.
        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("imageUri");
        try{ photoString = photoUri.toString(); }catch (Exception e){}
        String imageFilePath = intent.getStringExtra("imageFilePath");

        Log.d("imageFilePath", imageFilePath + "");
        imageView = (ImageView) findViewById(R.id.iv_cloth_add);
        imageView.setImageURI(photoUri);

        try {
            //Image Uri를 byte[]로 변경
            InputStream iStream =   getContentResolver().openInputStream(photoUri);
            inputData = getBytes(iStream);
            new GetAzureDataAsyncTask(getApplicationContext(), spinnerTopBottoms, spinnerLength, spinnerColor, inputData).execute();
        } catch (IOException e) { }
    }

    private void initList() {
        mColorList.add(new ColorItem("brown", this.getResources().getColor(R.color.colorBrown)));
        mColorList.add(new ColorItem("black", this.getResources().getColor(R.color.colorBlack)));
        mColorList.add(new ColorItem("yellow", this.getResources().getColor(R.color.colorYellow)));
        mColorList.add(new ColorItem("pink", this.getResources().getColor(R.color.colorPink)));
        mColorList.add(new ColorItem("green", this.getResources().getColor(R.color.colorGreen)));
        mColorList.add(new ColorItem("sky-blue", this.getResources().getColor(R.color.colorSky_blue)));
        mColorList.add(new ColorItem("gray", this.getResources().getColor(R.color.colorGray)));
        mColorList.add(new ColorItem("white", this.getResources().getColor(R.color.colorWhite)));
        mColorList.add(new ColorItem("navy", this.getResources().getColor(R.color.colorNavy)));
        mColorList.add(new ColorItem("purple", this.getResources().getColor(R.color.colorPurple)));
        mColorList.add(new ColorItem("red", this.getResources().getColor(R.color.colorRed)));
        mColorList.add(new ColorItem("charcoal", this.getResources().getColor(R.color.colorCharcoal)));
        mColorList.add(new ColorItem("blue", this.getResources().getColor(R.color.colorBlue)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //액션바 우측에 더하기 메뉴 생성
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add :
                String topBottoms = spinnerTopBottoms.getSelectedItem().toString();
                String length = spinnerLength.getSelectedItem().toString();
                String season = spinnerSeason.getSelectedItem().toString();
                String color;
                int c = spinnerColor.getSelectedItemPosition(); //brown == 0
                color = whatColor(c);

                // 입력한 값을 DB에 저장하는 부분
                new ClothesDataManager(ClothAddActivity.this).addItem(new ClothesItem(topBottoms, length, season, color, inputData));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public String whatColor(int c){
        switch (c) {
            case 0:
                return "brown";
            case 1:
                return "black";
            case 2:
                return "yellow";
            case 3:
                return "pink";
            case 4:
                return "green";
            case 5:
                return "sky-blue";
            case 6:
                return "gray";
            case 7:
                return "white";
            case 8:
                return "navy";
            case 9:
                return "purple";
            case 10:
                return "red";
            case 11:
                return "charcoal";
            case 12:
                return "blue";
        }
        return "no-way";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}