package com.pickth.dddd.smartcoordination.lookbook;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;
import com.pickth.dddd.smartcoordination.history.CalendarAdapter;
import com.pickth.dddd.smartcoordination.history.DayInfo;

import java.util.ArrayList;

public class DataAdapter extends BaseAdapter {

    DBHelper dbHelper;
    SQLiteDatabase db;
    private Context mContext;
    private int mResource;
    private LayoutInflater mLiInflater;
    ArrayList<dataItem> DI;
    int num=0;

    public DataAdapter(Context context,int textResource, ArrayList<dataItem> DI)
    {
        dbHelper = new DBHelper(mContext);
        this.mContext = context;
        this.mResource = textResource;
        this.DI = DI;
        this.mLiInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return DI.size();
    }
    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub

        return DI.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        dataItem dataItem = DI.get(position);
        Bitmap bm = dataItem.getBm();
        LookViewHolde dayViewHolder = new LookViewHolde();
        if(convertView == null)
        {
            convertView = mLiInflater.inflate(mResource, null);

            dayViewHolder.img = (ImageView) convertView.findViewById(R.id.data_info_iv);

            convertView.setTag(dayViewHolder);
        }
        else
        {
            dayViewHolder = (DataAdapter.LookViewHolde) convertView.getTag();
        }

        if(bm != null) {
            dayViewHolder.img.setImageBitmap(bm);
        }

        return convertView;
    }

    public class LookViewHolde
    {
        public ImageView img;

    }


}
