//package com.pickth.dddd.smartcoordination;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.v4.content.FileProvider;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.text.Layout;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
//public class HistoryActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener
//{
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    static final int REQUEST_TAKE_PHOTO = 1;
//    private String imageFilePath;
//    private String photoUri;
//    private Calendar curCar,mCal;
//    private TextView tv;
//    private Button beforeBTN, afterBTN, testbtn;
//    private GridView gv;
//    private ArrayList<DayInfo> DayList;
//    private CalendarAdapter adapter;
//    int Year, Month;
//    int width,height;
//    DisplayMetrics dm;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_history);
//        tv = (TextView)findViewById(R.id.tv); //year, month
//        beforeBTN = (Button)findViewById(R.id.before_btn); //last month
//        afterBTN = (Button)findViewById(R.id.after_btn); //next month
//        testbtn = (Button)findViewById(R.id.testbtn);
//        gv = (GridView)findViewById(R.id.gv);
//
//        beforeBTN.setOnClickListener(this);
//        afterBTN.setOnClickListener(this);
//        testbtn.setOnClickListener(this);
//        gv.setOnItemClickListener(this);
//
//        DayList = new ArrayList<DayInfo>();
//
//        curCar = Calendar.getInstance();
//        Year = curCar.get(Calendar.YEAR);
//        Month = curCar.get(Calendar.MONTH);
//
//
//        dm = getApplicationContext().getResources().getDisplayMetrics();
//        width = dm.widthPixels; //get display width
//        height = dm.heightPixels; //get display height
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mCal = Calendar.getInstance();
//        getCalendar();
//    }
//
//    //create calendar
//    private void getCalendar()
//    {
//        int firstDay;
//        int endDay;
//
//        DayList.clear();
//        mCal.set(Year,Month,1); //set this month
//        firstDay = mCal.get(Calendar.DAY_OF_WEEK); //get first day of this month
//        endDay = mCal.getActualMaximum(Calendar.DAY_OF_MONTH); //get end day of this month
//
//        // 캘린더 타이틀(년월 표시)을 세팅한다.
//        tv.setText(mCal.get(Calendar.YEAR) + "년 "
//                + (mCal.get(Calendar.MONTH) + 1) + "월");
//
//        DayInfo day;
//
//        for(int i=0; i<firstDay-1; i++) //1일 전 까지는 공백으로 처리
//        {
//            day = new DayInfo();
//            day.setDay("");
//            DayList.add(day);
//        }
//        for(int i=1; i <= endDay; i++)
//        {
//            day = new DayInfo();
//            day.setDay(Integer.toString(i));
//            DayList.add(day);
//        }
//        initCalendarAdapter();
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View v, int position, long arg3)
//    {
//        DayInfo dayInfo = DayList.get(position);
//        final List<String> ListItems = new ArrayList<>();
//        ListItems.add("사진촬영");
//        ListItems.add("갤러리");
//        ListItems.add("사진삭제");
//        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(Year+"년 "+Month+1+"일 "+dayInfo.getDay()+"일");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int pos) {
//                String selectedText = items[pos].toString();
//                if(selectedText.equals("사진촬영")){
//                }
//                else if(selectedText.equals("갤러리")){
//                }
//                else {
//
//                }
//            }
//        });
//        builder.show();
//    }
//
//    //클릭 메서드
//    public void onClick(View v)
//    {
//        switch(v.getId())
//        {
//            case R.id.before_btn:
//                if(Month == 0){
//                    Year -=1 ;
//                    Month = 11;
//                }
//                else Month --;
//                getCalendar();
//                break;
//            case R.id.after_btn:
//                if(Month == 11){
//                    Year +=1;
//                    Month = 0;
//                }
//                else Month ++;
//                getCalendar();
//                break;
//            case R.id.testbtn:
//                Toast.makeText(this,"안녕?",Toast.LENGTH_SHORT).show();
//
//        }
//    }
//
//    //사진촬영 https://developer.android.com/training/camera/photobasics#java
//
//    //갤러리
//
//
//    //어댑터 초기화 메서드
//    private void initCalendarAdapter()
//    {
//        adapter = new CalendarAdapter(this, R.layout.fragment_history_day, DayList,width,height);
//        gv.setAdapter(adapter);
//    }
//}
