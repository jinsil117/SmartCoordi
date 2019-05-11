package com.pickth.dddd.smartcoordination;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

public class HistoryFragment_dev extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tv;
    private Button beforeBTN, afterBTN;
    private GridView gv;
    private Calendar curCar, mCal;
    private ArrayList<DayInfo> DayList;
    private CalendarAdapter adapter;
    int Year, Month;
    String Day;
    int width, height;
    DisplayMetrics dm;
    DBHelper DBHelper;
    SQLiteDatabase db;
    int position;
    ChangeImage changeImage;
    Bitmap bm;
    private int roadPk; //DB에서 pk를 불러오기 위한 변수: 년도+월+일
    private int savePk; //DB에 pk를 저장하기 위한 변수
    DayInfo selectedDay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        DBHelper = new DBHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        tv = (TextView) view.findViewById(R.id.tv); //year and month
        beforeBTN = (Button) view.findViewById(R.id.before_btn); //last month
        afterBTN = (Button) view.findViewById(R.id.after_btn); //next month
        gv = (GridView) view.findViewById(R.id.gv);

        beforeBTN.setOnClickListener(this);
        afterBTN.setOnClickListener(this);
        gv.setOnItemClickListener(this);

        DayList = new ArrayList<DayInfo>(); //날짜 저장을 위한 배열

        curCar = Calendar.getInstance(); //현재달력을 생성하기 위한 달력
        Year = curCar.get(Calendar.YEAR); //현재 년도
        Month = curCar.get(Calendar.MONTH); //현재 월


        dm = getResources().getDisplayMetrics(); //디스플레이
        //dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels; //get display width
        height = dm.heightPixels; //get display height

        mCal = Calendar.getInstance(); //현재 달의 달력이 아닌 다른 달의 달력을 생성하기 위한 달력
        getCalendar();

        return view;
    }

    //create calendar
    private void getCalendar() //달력 생성 함수
    {
        int firstDay; //원하는 달의 첫번째 요일의 숫자를 저장시키기 위함
        int endDay; //원하는 달의 마지막 날짜

        DayList.clear(); //기존에 저장되어있던 날짜들을 삭제
        mCal.set(Year, Month, 1); //원하는 달의 연도, 달, 날짜 셋팅
        firstDay = mCal.get(Calendar.DAY_OF_WEEK); //첫 번째 날의 요일 얻기 (이 함수는 일=0, 월=1, 화=2, 수=3, 목=4, 금=5, 토=6 로 출력됨)
        endDay = mCal.getActualMaximum(Calendar.DAY_OF_MONTH); //마지막 날짜 얻기

        // 캘린더 타이틀(년월 표시)을 세팅한다.
        tv.setText(mCal.get(Calendar.YEAR) + "년 "
                + (mCal.get(Calendar.MONTH) + 1) + "월"); //month는 0월부터 시작하기 때문에 +1 해줘야 함

        DayInfo day; //날짜와 이미지를 저장시킬 인스턴스 호출
        for (int i = 0; i < firstDay - 1; i++) //1일이 시작하기 전까지
        {
            day = new DayInfo(); //인스턴스 생성
            day.setDay(""); //공백으로 처리
            DayList.add(day); //날짜 배열에 저장
        }
        for (int i = 1; i <= endDay; i++) //1일부터 말일까지
        {
            try { //db에서 해당 년, 월, 일자에 이미지가 저장되어 있는지 확인하고, 그 이미지를 가져옴
                db = DBHelper.getReadableDatabase();
                day = new DayInfo(); //인스턴스 생성

                //년,월,일을 합쳐 하나의 키값으로 생성
                String historyNum = ""+Year+""+Month+""+i;
                roadPk = Integer.parseInt(historyNum);

                Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM historyTBL WHERE num=" + roadPk, null);
                if (sizeCursor.moveToNext()) { //byte -> Bitmap 변환. cursor로 db에 저장되어있는 bitmap을 한번에 불러올 수 없기 때문에 나눠서 불러와 다른 변수에 저장하는 식으로 해야 함
                    long blobStart = 1; //blob 시작
                    long blobLen = 1; //blob 길이
                    int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                    byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성

                    while (blobSize > 0) {
                        blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                        blobSize -= blobLen;

                        Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM historyTBL WHERE num = " + roadPk, null);
                        if (blobCursor.moveToNext()) {
                            byte[] barr = blobCursor.getBlob(0);
                            if (barr != null) {
                                System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                            }
                            blobCursor.close();

                            blobStart += blobLen;
                        }

                    }
                    Bitmap bm = changeImage.getBitmap(bytes);
                    day.setBm(bm);
                }
                sizeCursor.close();
                db.close();

                //날짜, 이미지 저장
                day.setDay(Integer.toString(i));
                day.setHistoryNum(roadPk);
                DayList.add(day);
            }catch (Exception e) {
            }
        }
        initCalendarAdapter();
    }

    @Override
    public void onClick(View v) { //달력 이동
        switch (v.getId()) {
            case R.id.before_btn: //이전 달 버튼 클릭
                if (Month == 0) { //만약 현재 보고있는 달력이 1월이면
                    Year -= 1; //연도를 -1
                    Month = 11; //달을 12월달로 셋팅
                } else Month--;
                getCalendar();
                break;
            case R.id.after_btn: //다음 달 버튼 클릭
                if (Month == 11) { //만약 현재 보고있는 달력이 12월이면
                    Year += 1; //연도를 +1
                    Month = 0; //달을 1월로 셋팅
                } else Month++;
                getCalendar();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //날짜 선택
        DayInfo dayInfo = DayList.get(position); //사용자가 선택한 날짜 호출
        selectedDay = DayList.get(position);
        selectedDay.img = (ImageView)view.findViewById(R.id.day_img);
        Day = dayInfo.getDay();
        this.position = position;
        if (Day.equals("")) { //만약 공백인 날짜를 선택하면
            //아무것도 뜨지 않음
        } else {
            final List<String> ListItems = new ArrayList<>(); //날짜 선택시 뜨는 팝업창의 메뉴 리스트들
            ListItems.add("사진촬영");
            ListItems.add("갤러리");
            ListItems.add("사진삭제");
            final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(Year + "년 " + (Month + 1) + "월 " + Day + "일");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int pos) {
                    String selectedText = items[pos].toString();
                    if (selectedText.equals("사진촬영")) {

                    } else if (selectedText.equals("갤러리")) {
                        try {
                            // 갤러리로 이동
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                            bm = null;
                        } catch (Exception e) {

                        }
                    } else { //사진삭제
                        int key = dayInfo.getHistoryNum();
                        db = DBHelper.getWritableDatabase();
                        db.execSQL("DELETE FROM historyTBL WHERE num = " + key);
                        db.close();

                        selectedDay.img.setImageBitmap(null);
                    }
                }
            });
            builder.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) { //갤러리에서 선택한 이미지 db에 저장하기
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    db = DBHelper.getWritableDatabase();
                    String historyNum1 = ""+Year+""+Month+""+Day;
                    savePk = Integer.parseInt(historyNum1);
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData()); //이미지를 불러온다

                    Matrix rotateMatrix = new Matrix();
                    rotateMatrix.postRotate(90);

                    bm = BitmapFactory.decodeStream(in); //이미지를 Bitmap 변수에 저장
                    Bitmap rotateBitmap = Bitmap.createBitmap(bm,0,0,bm.getWidth(),bm.getHeight(),rotateMatrix,false);
                    byte[] bytes = changeImage.getBytes(rotateBitmap); //Bitmap 형식을 byte형식으로 변환 및 저장

                    Cursor checkCursor = db.rawQuery("SELECT img FROM historyTBL WHERE num ="+savePk,null);
                    if (checkCursor.moveToNext()){ //해당 날짜에 이미 사진이 존재
                        SQLiteStatement p = db.compileStatement("UPDATE historyTBL SET img=? WHERE num = "+savePk);
                        p.bindBlob(1,bytes);
                        p.execute();
                    }
                    else {
                        //이미지 정보를 db에 저장
                        SQLiteStatement p = db.compileStatement("INSERT INTO historyTBL values(?,?,?,?,?);");
                        p.bindLong(1, savePk);
                        p.bindString(2, Integer.toString(Year));
                        p.bindString(3, Integer.toString(Month)); //12월이면 11로 저장됨
                        p.bindString(4, Day);
                        p.bindBlob(5, bytes);
                        p.execute();
                    }
                    db.close();
                    in.close();

                    selectedDay.img.setImageBitmap(rotateBitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //어댑터 초기화 메서드
    private void initCalendarAdapter() {
        adapter = new CalendarAdapter(getActivity(), R.layout.fragment_history_day, DayList, width, height);
        gv.setAdapter(adapter);
    }

//public synchronized static int GetExifOrientation(Bitmap bitmap);
//{
//       int degree = 0;
//           ExifInterface exif = null;
//
//          try
//            {
//                 exif = new ExifInterface(String.valueOf(bm));
//               }
//           catch (IOException e)
//          {
//                  Log.e(TAG, "cannot read exif");
//          e.printStackTrace();
//             }
//
//         if (exif != null)
//            {
//                 int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
//
//                   if (orientation != -1)
//                        {
//                           // We only recognize a subset of orientation tag values.
//                            switch(orientation)
//                            {
//                                  case ExifInterface.ORIENTATION_ROTATE_90:
//                                           degree = 90;
//                                            break;
//
//                                    case ExifInterface.ORIENTATION_ROTATE_180:
//                                            degree = 180;
//                                            break;
//
//                                    case ExifInterface.ORIENTATION_ROTATE_270:
//                                            degree = 270;
//                                            break;
//                                }
//                        }
//        }
//            return degree;
//        }
//
//    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
//{
//            if ( degrees != 0 && bitmap != null )
//                {
//                   Matrix m = new Matrix();
//                  m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
//                   try
//                   {
//                          Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//                            if (bitmap != b2)
//                            {
//                                bitmap.recycle();
//                                  bitmap = b2;
//                           }
//                    }
//                 catch (OutOfMemoryError ex)
//                   {
//                          // We have no memory to rotate. Return the original bitmap.
//                  }
//             }
//
//         return bitmap;
//      }

}