package com.pickth.dddd.smartcoordination.history;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.dddd.smartcoordination.ChangeImage;
import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment_dev extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int PICK_FROM_CAMERA = 2;
    private Uri photoUri;
    private String imageFilePath;
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
    com.pickth.dddd.smartcoordination.DBHelper DBHelper;
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
            PrettyDialog pDialog = new PrettyDialog(gv.getContext());
            pDialog
                    .setTitle(Year + "년 " + (Month + 1) + "월 " + Day + "일")
                    .setIcon(R.drawable.pdlg_icon_success)
                    .setIconTint(R.color.colorPurple1)
                    .setIconCallback(new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            pDialog.dismiss();
                        }
                    })
                    .addButton(
                            "camera",     // button text
                            R.color.pdlg_color_white,  // button text color
                            R.color.colorPurple2,  // button background color
                            new PrettyDialogCallback() {  // button OnClick listener
                                @Override
                                public void onClick() {
                                    doTakePhotoAction();
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .addButton(
                            "album",
                            R.color.pdlg_color_white,
                            R.color.colorPurple3,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    Intent intent = new Intent();
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(intent, 1);
                                    bm = null;
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .addButton(
                            "delete",
                            R.color.pdlg_color_black,
                            R.color.pdlg_color_gray,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    int key = dayInfo.getHistoryNum();
                                    db = DBHelper.getWritableDatabase();
                                    db.execSQL("DELETE FROM historyTBL WHERE num = " + key);
                                    db.close();

                                    selectedDay.img.setImageBitmap(null);
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .show();
        }
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        String historyNum1 = ""+Year+""+Month+""+Day;
        savePk = Integer.parseInt(historyNum1);

        switch (requestCode) {
            case PICK_FROM_CAMERA: {
                Bitmap bitmap = changeImage.getBitmap(this.getActivity(),photoUri);
                byte[] bytes1 = changeImage.getBytes(bitmap);

                selectedDay.setBm(bitmap);

                Intent intent = new Intent(getContext(),CameraIntent.class);
                intent.putExtra("imageUri", photoUri);
                intent.putExtra("imageFilePath", imageFilePath);
                intent.putExtra("pk",savePk);
                intent.putExtra("year",String.valueOf(Year));
                intent.putExtra("month",String.valueOf(Month));
                intent.putExtra("day",String.valueOf(Day));
                startActivity(intent);

                Bitmap bit = rotateImage(bitmap,90);
                selectedDay.img.setImageBitmap(bit);
                break;
            }
            case 1: {
                try {
                    db = DBHelper.getWritableDatabase();

                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData()); //이미지를 불러온다

                    bm = BitmapFactory.decodeStream(in); //이미지를 Bitmap 변수에 저장
                    byte[] bytes = changeImage.getBytes(bm); //Bitmap 형식을 byte형식으로 변환 및 저장

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

                    Bitmap bit = rotateImage(bm,90);
                    selectedDay.img.setImageBitmap(bit);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }

    }

    //어댑터 초기화 메서드
    private void initCalendarAdapter() {
        adapter = new CalendarAdapter(getActivity(), R.layout.fragment_history_day, DayList, width, height);
        gv.setAdapter(adapter);
    }

    private void doTakePhotoAction() { //카메라 촬영 후 이미지 가져오는 함수
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".png",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }

}