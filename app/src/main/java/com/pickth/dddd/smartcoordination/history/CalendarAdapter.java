package com.pickth.dddd.smartcoordination.history;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;

import java.util.ArrayList;

public class CalendarAdapter extends BaseAdapter
{
    private ArrayList<DayInfo> mDayList; //날짜 저장 배열
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;
    int width, height;
    DBHelper dbHelper;
    SQLiteDatabase db;
    /**
     * Adpater 생성자
     *
     * @param context
     *            컨텍스트
     * @param textResource
     *            레이아웃 리소스
     * @param dayList
     *            날짜정보가 들어있는 리스트
     * @param width
     *            display 가로 길이
     * @param height
     *            display 세로 길이
     */
    public CalendarAdapter(Context context, int textResource, ArrayList<DayInfo> dayList, int width, int height)
    {
        dbHelper = new DBHelper(mContext);
        this.mContext = context;
        this.mDayList = dayList;
        this.mResource = textResource;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mDayList.size();
    }
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return mDayList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    public void setImage(int position, byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DayInfo day = mDayList.get(position);
        Bitmap bm = day.getBm();
        DayViewHolde dayViewHolder = new DayViewHolde(); //날짜, 이미지

        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);

            if(position % 7 == 6)
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP()+getRestCellWidthDP(), getCellHeightDP()));
            }
            else
            {
                convertView.setLayoutParams(new GridView.LayoutParams(getCellWidthDP(), getCellHeightDP()));
            }


            dayViewHolder.img = (ImageView) convertView.findViewById(R.id.day_img);
            dayViewHolder.tvDay = (TextView) convertView.findViewById(R.id.day_tv);

            convertView.setTag(dayViewHolder);
        }
        else
        {
            dayViewHolder = (DayViewHolde) convertView.getTag();
        }

        if(day.getBm() != null) {
            Bitmap bit = rotateImage(bm,90);
            dayViewHolder.img.setImageBitmap(bit);
        }

        if(day != null)
        {
            dayViewHolder.tvDay.setText(day.getDay()); //DayInfo에 저장되어있던 날짜 불러오기


            if(position % 7 == 0) //일요일
            {
                dayViewHolder.tvDay.setTextColor(Color.RED);
            }
            else if(position % 7 == 6) //토요일
            {
                dayViewHolder.tvDay.setTextColor(Color.BLUE);
            }
            else //평일
            {
                dayViewHolder.tvDay.setTextColor(Color.BLACK);
            }

        }

        return convertView;
    }

    public class DayViewHolde
    {
        public ImageView img;
        public TextView tvDay;

    }

    private int getCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellWidth = width/7;

        return cellWidth;
    }

    private int getRestCellWidthDP()
    {
//      int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellWidth = width%7;

        return cellWidth;
    }

    private int getCellHeightDP()
    {
//      int height = mContext.getResources().getDisplayMetrics().widthPixels;
        int cellHeight = height/6;

        return cellHeight;
    }

    public static Bitmap rotateImage(Bitmap source, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),matrix,true);
    }
}