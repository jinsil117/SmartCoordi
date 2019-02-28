package com.pickth.dddd.smartcoordination;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

public class ClothesListAdapterTitle extends RecyclerView.Adapter<ClothesListAdapterTitle.CalendarListViewHolder> {
    // 0이면 홈화면, 아니면 리스트 화면
    private int type = 0;
    ArrayList<ClothesItem> items = new ArrayList<>();

    public ClothesListAdapterTitle(int type) {
        this.type = type;
    }

    @Override
    public CalendarListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cloth, parent, false);
        return new CalendarListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CalendarListViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ClothesItem item) {
        items.add(item);
    }

    class CalendarListViewHolder extends RecyclerView.ViewHolder {
        CalendarListViewHolder(View view) {
            super(view);
        }

        void onBind(ClothesItem item) {
            TextView tvTitle = itemView.findViewById(R.id.tv_cloth);

            tvTitle.setText(item.title);
        }
    }
}
