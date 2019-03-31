package com.pickth.dddd.smartcoordination;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ColorAdapter extends ArrayAdapter<ColorItem> {
    Context mContext;

    public ColorAdapter(Context context, ArrayList<ColorItem> colorList){
        super(context, 0, colorList);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
//            convertView = LayoutInflater.from(mContext).inflate( R.layout.spinner_color, parent, false);
//            convertView = LayoutInflater.from(getContext()).inflate( R.layout.spinner_color, null);

            LayoutInflater li = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            convertView = li.inflate(R.layout.spinner_color, null);
//            Toast.makeText(getContext(), convertView.getBackground().toString(), Toast.LENGTH_LONG).show();
        }

        View colorView = convertView.findViewById(R.id.view_color);
        TextView tvColor = convertView.findViewById(R.id.tv_color);

        ColorItem currentItem = getItem(position);

        if (currentItem != null) {
            colorView.setBackgroundColor(currentItem.getColor());
            tvColor.setText(currentItem.getColorName());
        }

        return convertView;
    }
}
