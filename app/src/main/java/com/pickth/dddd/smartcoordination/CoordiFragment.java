package com.pickth.dddd.smartcoordination;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pickth.dddd.smartcoordination.cloth.ClothesItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by parkjinsil on 2019-05-22.
 */

public class CoordiFragment extends Fragment implements View.OnClickListener {

    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private FloatingActionButton refresh; //새로고침을 위한 변수선언
    DBHelper DBHelper;
    SQLiteDatabase db;

    // GPSTracker class
    private  GpsInfo gps;

    @Bind(R.id.tem)
    TextView tem;
    int tmp;
    int num = 1;
//    @Bind(R.id.getWeatherBtn)
//    Button getWeatherBtn;

    ImageView top_clothes; //상의코디출력
    ImageView bottom_cloths; //하의코디출력

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coordi,container,false);
        ButterKnife.bind(this.getActivity());
        DBHelper = new DBHelper(getContext());

        //새로고침을 위한 버튼
        refresh = (FloatingActionButton)view.findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        tem = (TextView) view.findViewById(R.id.tem);
        setGetWeatherBtn();
//        getWeatherBtn = (Button) view.findViewById(R.id.getWeatherBtn);

        //상의,하의 코디 추천을 위한 이미지뷰
        top_clothes = (ImageView)view.findViewById(R.id.top_clothes);
        bottom_cloths = (ImageView)view.findViewById(R.id.bottom_clothes);

        //화면터치시 온도 받아오는 리스너
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setGetWeatherBtn();
                        CoordiRecommend();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        setGetWeatherBtn();
                        CoordiRecommend();
                        break;
                    case MotionEvent.ACTION_UP:
                        setGetWeatherBtn();
                        CoordiRecommend();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
//        getWeatherBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setGetWeatherBtn();
//            }
//        });

        return view;
    }


    //코디추천을 해주는 메소드
    public void CoordiRecommend() {
        tmp = (int)Double.parseDouble(tem.getText().toString());
            if (tmp < 12) { //겨울
                //12도 미만일때 옷 선택하는 DB
                //Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = summer IN (SELECT * From summer WHERE topBottoms = top);", null);
                //Cursor cursor2 = db.rawQuery("SELECT * FROM clothesTBL WHERE season = summer IN (SELECT * From summer WHERE topBottoms = Bottoms);", null);
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = 'winter' AND topBottoms='Top';", null);
                int count = cursor.getCount();
                int random = (int) (Math.random() * count);
                Log.d("cccc",count+" count, "+random+" ran");

                try { //db에서 옷을 가져옴

                    DBHelper = new DBHelper(getContext());
                    db = DBHelper.getReadableDatabase();

                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE season = 'winter' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=2;", null);

                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ")  FROM clothesTBL WHERE season = 'winter' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=2", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        top_clothes.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                } catch (Exception e) {
                }

                SQLiteDatabase db2 = DBHelper.getReadableDatabase();
                Cursor cursor2 = db2.rawQuery("SELECT img FROM clothesTBL WHERE season = 'winter' AND topBottoms='Bottoms';", null);
                int count2 = cursor2.getCount();
                int random2 = (int) (Math.random() * count2);
                try { //db에서 옷을 가져옴
                    Cursor sizeCursor = db2.rawQuery("SELECT length(img)  FROM clothesTBL WHERE season = 'winter' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                    Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=6", null);
                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE season = 'winter' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=6", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bottom_cloths.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                    db2.close();
                } catch (Exception e) {
                }
            } else if (12 <= tmp && tmp <=16 ) {
                //12도이상 16도이하일때 옷 선택하는 DB
                //가을
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = 'fall' AND topBottoms='Top';", null);
                int count = cursor.getCount();
                int random = (int) (Math.random() * count);
                Log.d("cccc",count+" count, "+random+" ran");

                try { //db에서 옷을 가져옴

                    DBHelper = new DBHelper(getContext());
                    db = DBHelper.getReadableDatabase();

                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE season = 'fall' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=2;", null);

                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ")  FROM clothesTBL WHERE season = 'fall' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=2", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        top_clothes.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                } catch (Exception e) {

                }
                SQLiteDatabase db2 = DBHelper.getReadableDatabase();
                Cursor cursor2 = db2.rawQuery("SELECT img FROM clothesTBL WHERE season = 'fall' AND topBottoms='Bottoms';", null);
                int count2 = cursor2.getCount();
                int random2 = (int) (Math.random() * count2);
                try { //db에서 옷을 가져옴
                    Cursor sizeCursor = db2.rawQuery("SELECT length(img)  FROM clothesTBL WHERE season = 'fall' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                    Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=6", null);
                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE season = 'fall' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=6", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bottom_cloths.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                    db2.close();
                } catch (Exception e) {

                }
            } else if (17 < tmp && tmp <= 23) {
                //16도초과 22도이하일때 옷 선택하는 DB
                //봄
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = 'spring' AND topBottoms='Top';", null);
                int count = cursor.getCount();
                int random = (int) (Math.random() * count);
                Log.d("cccc",count+" count, "+random+" ran");

                try { //db에서 옷을 가져옴

                    DBHelper = new DBHelper(getContext());
                    db = DBHelper.getReadableDatabase();

                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE season = 'spring' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=2;", null);

                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ")  FROM clothesTBL WHERE season = 'spring' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=2", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        top_clothes.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                } catch (Exception e) {

                }
                SQLiteDatabase db2 = DBHelper.getReadableDatabase();
                Cursor cursor2 = db2.rawQuery("SELECT img FROM clothesTBL WHERE season = 'spring' AND topBottoms='Bottoms';", null);
                int count2 = cursor2.getCount();
                int random2 = (int) (Math.random() * count2);
                try { //db에서 옷을 가져옴
                    Cursor sizeCursor = db2.rawQuery("SELECT length(img)  FROM clothesTBL WHERE season = 'spring' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                    Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=6", null);
                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE season = 'spring' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=6", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bottom_cloths.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                    db2.close();
                } catch (Exception e) {

                }
            } else if (23 < tmp ) {
                //123도초과 일때 옷 선택하는 DB
                //여름
                SQLiteDatabase db = DBHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top';", null);
                int count = cursor.getCount();
                int random = (int) (Math.random() * count);
                Log.d("cccc",count+" count, "+random+" ran");

                try { //db에서 옷을 가져옴

                    DBHelper = new DBHelper(getContext());
                    db = DBHelper.getReadableDatabase();

                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=2;", null);

                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ")  FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top' LIMIT " + random + ",1;", null);
//                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=2", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        top_clothes.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                } catch (Exception e) {

                }
                SQLiteDatabase db2 = DBHelper.getReadableDatabase();
                Cursor cursor2 = db2.rawQuery("SELECT img FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms';", null);
                int count2 = cursor2.getCount();
                int random2 = (int) (Math.random() * count2);
                try { //db에서 옷을 가져옴
                    Cursor sizeCursor = db2.rawQuery("SELECT length(img)  FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                    Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=6", null);
                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                        long blobStart = 1; //blob 시작
                        long blobLen = 1; //blob 길이
                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
                        while (blobSize > 0) {
                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                            blobSize -= blobLen;

                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
//                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=6", null);
                            if (blobCursor.moveToNext()) {
                                byte[] barr = blobCursor.getBlob(0);
                                if (barr != null) {
                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                                }
                                blobStart += blobLen;
                                blobCursor.close();
                            }
                        }
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        bottom_cloths.setImageBitmap(bitmap);
                    }
                    sizeCursor.close();
                    db2.close();
                } catch (Exception e) {

                }
//            } else if (16 < tmp && tmp <= 19) {
//                //16도초과 19도이하일때 옷 선택하는 DB

//            } else if (19 < tmp && tmp <= 20) {
//                //19도초과 22도이하일때 옷 선택하는 DB

//            } else if (16 < tmp && tmp < 27) {
//                //22도초과 27도미만일때 옷 선택하는 DB
//                //Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = summer IN (SELECT * From summer WHERE topBottoms = top);", null);
//                //Cursor cursor2 = db.rawQuery("SELECT * FROM clothesTBL WHERE season = summer IN (SELECT * From summer WHERE topBottoms = Bottoms);", null);
//                SQLiteDatabase db = DBHelper.getReadableDatabase();
//                Cursor cursor = db.rawQuery("SELECT * FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top';", null);
//                int count = cursor.getCount();
//                int random = (int) (Math.random() * count);
//                Log.d("cccc",count+" count, "+random+" ran");
//
//                try { //db에서 옷을 가져옴
//
//                    DBHelper = new DBHelper(getContext());
//                    db = DBHelper.getReadableDatabase();
//
//                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top' LIMIT " + random + ",1;", null);
////                    Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=2;", null);
//
//                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
//                        long blobStart = 1; //blob 시작
//                        long blobLen = 1; //blob 길이
//                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
//                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
//                        while (blobSize > 0) {
//                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
//                            blobSize -= blobLen;
//
//                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ")  FROM clothesTBL WHERE season = 'summer' AND topBottoms='Top' LIMIT " + random + ",1;", null);
////                            Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=2", null);
//                            if (blobCursor.moveToNext()) {
//                                byte[] barr = blobCursor.getBlob(0);
//                                if (barr != null) {
//                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
//                                }
//                                blobStart += blobLen;
//                                blobCursor.close();
//                            }
//                        }
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        top_clothes.setImageBitmap(bitmap);
//                    }
//                    sizeCursor.close();
//                } catch (Exception e) {
//                }
//
//                SQLiteDatabase db2 = DBHelper.getReadableDatabase();
//                Cursor cursor2 = db2.rawQuery("SELECT img FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms';", null);
//                int count2 = cursor2.getCount();
//                int random2 = (int) (Math.random() * count2);
//                try { //db에서 옷을 가져옴
//                    Cursor sizeCursor = db2.rawQuery("SELECT length(img)  FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
////                    Cursor sizeCursor = db2.rawQuery("SELECT length(img) FROM clothesTBL WHERE num=6", null);
//                    if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
//                        long blobStart = 1; //blob 시작
//                        long blobLen = 1; //blob 길이
//                        int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
//                        byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성
//                        while (blobSize > 0) {
//                            blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
//                            blobSize -= blobLen;
//
//                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE season = 'summer' AND topBottoms='Bottoms' LIMIT " + random2 + ",1;", null);
////                            Cursor blobCursor = db2.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num=6", null);
//                            if (blobCursor.moveToNext()) {
//                                byte[] barr = blobCursor.getBlob(0);
//                                if (barr != null) {
//                                    System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
//                                }
//                                blobStart += blobLen;
//                                blobCursor.close();
//                            }
//                        }
//                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        bottom_cloths.setImageBitmap(bitmap);
//                    }
//                    sizeCursor.close();
//                    db2.close();
//                } catch (Exception e) {
//                }
//
//            } else if (tmp >= 27) {
//                //27도 이상일때 옷 선택하는 DB
            }
    }

    //새로고침 버튼 클릭 시 발생되는 함수
    @Override
    public void onClick(View v) {
        CoordiRecommend();
    }

    public void setGetWeatherBtn() {
        // 권한 요청을 해야 함
        if (!isPermission) {
            callPermission();
            return;
        }
        gps = new GpsInfo(this.getActivity());
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
        }
        else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
        callPermission();  // 권한 요청을 해야 함

        Retrofit client = new Retrofit.Builder().baseUrl("http://api.openweathermap.org").addConverterFactory(GsonConverterFactory.create()).build();

        ApiInterface service = client.create(ApiInterface.class);
        Call<CoordiRepo> call = service.repo("684b98e21b4f35b7d52abe9ff6279349", Double.valueOf(gps.getLatitude()), Double.valueOf(gps.getLongitude()));
        call.enqueue(new Callback<CoordiRepo>() {
            @Override
            public void onResponse(Call<CoordiRepo> call, Response<CoordiRepo> response) {
                if (response.isSuccessful()) {
                    CoordiRepo repo = response.body();
                    tem.setText(String.valueOf(String.format("%.2f",((repo.getMain().getTemp())-(273.15)))));
                } else {
                }
            }

            @Override
            public void onFailure(Call<CoordiRepo> call, Throwable t) {
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }
    // 위치 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }
}
