package com.pickth.dddd.smartcoordination.add;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.common.io.ByteStreams;
import com.pickth.dddd.smartcoordination.R;
import com.pickth.dddd.smartcoordination.cloth.ClothesDataManager;
import com.pickth.dddd.smartcoordination.cloth.ClothesItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.jypdev.maskcroplibrary.ImageUtil.exifOrientationToDegrees;

/**
 * Created by HaEun
 */

public class ClothAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerTopBottoms, spinnerLength, spinnerSeason, spinnerColor;
    ArrayList<ColorItem> mColorList;
    ImageView imageView;
    Bitmap bitmap;
    Uri photoUri;
    String photoString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_add);

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

        //ClothesFragment에서 옷의 uri나 byte[]를 가져와 그에 해당하는 GetAzureDataAsyncTask를 생성한다.
        Intent intent = getIntent();
        photoUri = intent.getParcelableExtra("imageUri");
        try{ photoString = photoUri.toString(); }catch (Exception e){}
        String imageFilePath = intent.getStringExtra("imageFilePath");

        Log.d("imageFilePath", imageFilePath + "");

//        앨범에서도 byte[] 대신 uri를 받아오는 것으로 변경
//        byte[] arr = null;
//        arr = intent.getByteArrayExtra("image");

//        if (arr == null){   //카메라로 사진을 찍었을 경우
            imageView = (ImageView) findViewById(R.id.iv_cloth_add);
//            imageView.setImageURI(photoUri);  //이미지가 가로로 회전되어 표시됨

            //ExifInterface라는 클래스를 이용해 이미지가 회전되어있는 각도를 가져와 회전시켜 이미지 뷰에 띄우는 작업
            //https://raon-studio.tistory.com/6 참고
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            try { exif = new ExifInterface(imageFilePath); } catch (IOException e) { }
            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }
//            imageView.setImageBitmap(rotate(bitmap, exifDegree));
            imageView.setImageBitmap(bitmap);

            try {
                //Image Uri를 byte[]로 변경
                InputStream iStream =   getContentResolver().openInputStream(photoUri);
                byte[] inputData = getBytes(iStream);
                new GetAzureDataAsyncTask(getApplicationContext(), spinnerTopBottoms, spinnerLength, spinnerColor, inputData).execute();
            } catch (IOException e) { }

//        }else { //앨범에서 가져온 경우
//            bitmap = BitmapFactory.decodeByteArray(arr, 0, arr.length);
//            imageView = (ImageView) findViewById(R.id.iv_cloth_add);
//            imageView.setImageBitmap(bitmap);
//            new GetAzureDataAsyncTask(getApplicationContext(), spinnerTopBottoms, spinnerLength, spinnerColor, bitmap).execute();
//        }
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
                String color = spinnerColor.getSelectedItem().toString();

                if (photoUri == null){  //앨범에서 가져온 경우
                    new ClothesDataManager(ClothAddActivity.this).addItem(new ClothesItem(photoString, season));
                    Log.d("nnnnn", "photoUri == null");

                }else { //카메라로 찍은 경우
                    // 입력한 값을 파일에 저장하는 부분
                    new ClothesDataManager(ClothAddActivity.this).addItem(new ClothesItem(photoString, season));
                    Log.d("nnnnn", "photoUri != null");
                }

                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static byte[] GetImage(String folder, String fileName)
    {
        try {
            return ByteStreams.toByteArray(ClothAddActivity.class.getResourceAsStream(folder + "/" + fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
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

    //상수를 받아 각도로 변환시켜주는 메소드
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //비트맵을 각도대로 회전시켜 결과를 반환해주는 메소드
    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }catch (Exception e){}
        return bitmap;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
