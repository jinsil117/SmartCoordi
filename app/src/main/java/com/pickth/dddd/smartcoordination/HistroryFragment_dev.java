package com.pickth.dddd.smartcoordination;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistroryFragment_dev extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView tv;
    private Button beforeBTN,afterBTN;
    private GridView gv;
    private Calendar curCar,mCal;
    private ArrayList<DayInfo> DayList;
    private CalendarAdapter adapter;
    int Year, Month;
    int width,height;
    DisplayMetrics dm;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history,container,false);
        tv = (TextView)view.findViewById(R.id.tv); //year, month
        beforeBTN = (Button)view.findViewById(R.id.before_btn); //last month
        afterBTN = (Button)view.findViewById(R.id.after_btn); //next month
        gv = (GridView)view.findViewById(R.id.gv);

        beforeBTN.setOnClickListener(this);
        afterBTN.setOnClickListener(this);
        gv.setOnItemClickListener(this);

        DayList = new ArrayList<DayInfo>();

        curCar = Calendar.getInstance();
        Year = curCar.get(Calendar.YEAR);
        Month = curCar.get(Calendar.MONTH);


        dm = getResources().getDisplayMetrics();
        //dm = getApplicationContext().getResources().getDisplayMetrics();
        width = dm.widthPixels; //get display width
        height = dm.heightPixels; //get display height
        mCal = Calendar.getInstance();
        getCalendar();
        return view;

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        mCal = Calendar.getInstance();
//        getCalendar();
//    }

    //create calendar
    private void getCalendar()
    {
        int firstDay;
        int endDay;

        DayList.clear();
        mCal.set(Year,Month,1); //set this month
        firstDay = mCal.get(Calendar.DAY_OF_WEEK); //get first day of this month
        endDay = mCal.getActualMaximum(Calendar.DAY_OF_MONTH); //get end day of this month

        // 캘린더 타이틀(년월 표시)을 세팅한다.
        tv.setText(mCal.get(Calendar.YEAR) + "년 "
                + (mCal.get(Calendar.MONTH) + 1) + "월");

        DayInfo day;

        for(int i=0; i<firstDay-1; i++) //1일 전 까지는 공백으로 처리
        {
            day = new DayInfo();
            day.setDay("");
            DayList.add(day);
        }
        for(int i=1; i <= endDay; i++)
        {
            day = new DayInfo();
            day.setDay(Integer.toString(i));
            DayList.add(day);
        }
        initCalendarAdapter();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.before_btn:
                if(Month == 0){
                    Year -=1 ;
                    Month = 11;
                }
                else Month --;
                getCalendar();
                break;
            case R.id.after_btn:
                if(Month == 11){
                    Year +=1;
                    Month = 0;
                }
                else Month ++;
                getCalendar();
                break;

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DayInfo dayInfo = DayList.get(position);
        if (dayInfo.getDay().equals("")) {

        } else {
            final List<String> ListItems = new ArrayList<>();
            ListItems.add("사진촬영");
            ListItems.add("갤러리");
            ListItems.add("사진삭제");
            final CharSequence[] items = ListItems.toArray(new String[ListItems.size()]);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(Year + "년 " + (Month + 1) + "월 " + dayInfo.getDay() + "일");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int pos) {
                    String selectedText = items[pos].toString();
                    if (selectedText.equals("사진촬영")) {
                    } else if (selectedText.equals("갤러리")) {
                    } else {

                    }
                }
            });
            builder.show();
        }
    }

    //어댑터 초기화 메서드
    private void initCalendarAdapter()
    {
        adapter = new CalendarAdapter(getActivity(), R.layout.fragment_history_day, DayList,width,height);
        gv.setAdapter(adapter);
    }
}
