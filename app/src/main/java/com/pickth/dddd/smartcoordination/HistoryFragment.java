package com.pickth.dddd.smartcoordination;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tv;
    private Button beforeBTN,afterBTN;
    private GridView gv;
    private Calendar curCar,mCal;
    private ArrayList<DayInfo> DayList;
    private CalendarAdapter adapter;
    int Year, Month;
    String Day;
    int width,height;
    int Position;
    DisplayMetrics dm;
    private static final int PICK_FROM_CAMERA = 0; //사진을 촬영하고 찍힌 이미지를 처리하는 부분
    private Uri mImageCaptureUri; //크롭된 이미지에 대한 Uri(Uniform Resource Identifier = 통합 자원 식별자)
    DBHelper DBHelper;
    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        DBHelper = new DBHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_history,container,false);
        tv = (TextView)view.findViewById(R.id.tv); //year and month
        beforeBTN = (Button)view.findViewById(R.id.before_btn); //last month
        afterBTN = (Button)view.findViewById(R.id.after_btn); //next month
        gv = (GridView)view.findViewById(R.id.gv);

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
    private void getCalendar() //달력 계산 함수
    {
        int firstDay; //원하는 달의 첫번째 요일의 숫자를 저장시키기 위함
        int endDay; //원하는 달의 마지막 날짜

        DayList.clear(); //기존에 저장되어있던 날짜들을 삭제
        mCal.set(Year,Month,1); //원하는 달의 연도, 달, 날짜 셋팅
        firstDay = mCal.get(Calendar.DAY_OF_WEEK); //첫 번째 날의 요일 얻기 (이 함수는 일=0, 월=1, 화=2, 수=3, 목=4, 금=5, 토=6 로 출력됨)
        endDay = mCal.getActualMaximum(Calendar.DAY_OF_MONTH); //마지막 날짜 얻기

        // 캘린더 타이틀(년월 표시)을 세팅한다.
        tv.setText(mCal.get(Calendar.YEAR) + "년 "
                + (mCal.get(Calendar.MONTH) + 1) + "월"); //month는 0월부터 시작하기 때문에 +1 해줘야 함

        DayInfo day; //날짜와 이미지를 저장시킬 인스턴스 호출
        for(int i=0; i<firstDay-1; i++) //1일이 시작하기 전까지
        {
            day = new DayInfo(); //인스턴스 생성
            day.setDay(""); //공백으로 처리
            DayList.add(day); //날짜 배열에 저장
        }
        for(int i=1; i <= endDay; i++) //1일부터 말일까지
        {
            day = new DayInfo(); //인스턴스 생성
            day.setDay(Integer.toString(i)); //날짜 저장
            DayList.add(day);
        }
        initCalendarAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap imgg = BitmapFactory.decodeStream(in);
                    byte[] image = getBytes(imgg);
                    db = DBHelper.getWritableDatabase();
                    SQLiteStatement p = db.compileStatement("INSERT INTO historyTBL values(?,?,?,?);");
                    p.bindString(1,Integer.toString(Year));
                    p.bindString(2,Integer.toString(Month));
                    p.bindString(3,Day);
                    p.bindBlob(4,image);
                    p.execute();
                    db.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) { //달력 이동
        switch(v.getId())
        {
            case R.id.before_btn: //이전 달 버튼 클릭
                if(Month == 0){ //만약 현재 보고있는 달력이 1월이면
                    Year -=1 ; //연도를 -1
                    Month = 11; //달을 12월달로 셋팅
                }
                else Month --;
                getCalendar();
                break;
            case R.id.after_btn: //다음 달 버튼 클릭
                if(Month == 11){ //만약 현재 보고있는 달력이 12월이면
                    Year +=1; //연도를 +1
                    Month = 0; //달을 1월로 셋팅
                }
                else Month ++;
                getCalendar();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //날짜 선택
        DayInfo dayInfo = DayList.get(position); //사용자가 선택한 날짜 호출
        Day = dayInfo.getDay();
        Position = position;
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
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //사진을 찍기 위하여 설정

                        // 임시로 사용할 파일의 경로를 생성
                        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
                        // mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
                        FileProvider.getUriForFile(getContext(), "com.pickth.dddd.smartcoordination.fileprovider", new File(Environment.getExternalStorageDirectory(), url));
                        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                        //사진촬영

                        //사진인공지능

                        //사진저장

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } else if (selectedText.equals("갤러리")) {
                        try {
                            // 사진선택
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);

                            // 사진을 히스토리에 보여줌
//                            db = DBHelper.getReadableDatabase();
//                            Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM historyTBL WHERE num = ?;", null);
//                            if(sizeCursor.moveToNext()){
//                                long blobStart = 1;
//                                long blobLen = 1;
//                                int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
//                                byte[] bytes = blobSize > 0? new byte[(int)blobSize] : null; //blob 사이즈의 배열 생성
//
//                                while(blobSize > 0){
//                                    blobLen = blobSize > 1000000? 1000000 : blobSize; //1000000는 cursor 용량 한계치
//                                    blobSize -= blobLen;
//
//                                    Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM historyTBL;", null);
//                                    if(blobCursor.moveToNext()){
//                                        byte [] barr = blobCursor.getBlob(0);
//                                        if(barr != null){
//                                            System.arraycopy(barr,0,bytes,(int)blobStart-1, barr.length);
//                                        }
//                                        blobCursor.close();
//
//                                        blobStart += blobLen;
//                                    }
//
////                            if(bytes != null){
////                                //로드된 byte 저장하기
////                            }
//
//
//                                }
//                                bm = getImage(bytes);
//                            }
//                            sizeCursor.close();
////                    Cursor cursor = db.rawQuery("SELECT img FROM clothesTBL;", null);
////                    while (cursor.moveToLast()) {
////                        blob  = cursor.getBlob(0);
////
////                    }
////                    cursor.close();
//
//                            //dayInfo.img.setImageBitmap(bm);
//
//                            db.close();
                        }catch (Exception e){

                        }
                    } else {

                    }
                }
            });
            builder.show();
        }
    }

    public static byte[] getBytes(Bitmap bitmap) { //bitmap을 byte(blob)로 변환
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        return data;
    }

    public static Bitmap getImage(byte[] image) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        return bitmap;
    }

    //어댑터 초기화 메서드
    private void initCalendarAdapter()
    {
        adapter = new CalendarAdapter(getActivity(), R.layout.fragment_history_day, DayList,width,height);
        gv.setAdapter(adapter);
    }

}
