package com.pickth.dddd.smartcoordination.cloth;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pickth.dddd.smartcoordination.R;

import java.util.ArrayList;

public class ClothesListAdapterThumbnail extends RecyclerView.Adapter<ClothesListAdapterThumbnail.ClothesListViewHolder> {
    // 0이면 홈화면, 아니면 리스트 화면
    private int type = 0;
    ArrayList<ClothesItem> items = new ArrayList<>();

    public ClothesListAdapterThumbnail(int type) {
        this.type = type;
    }

    @Override
    public ClothesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cloth_thumbnail, parent, false);
        Log.d("Adddd", "onCreateViewHolder");
        return new ClothesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClothesListViewHolder holder, int position) {
        Log.d("Adddd", "onBindViewHolder");
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(ClothesItem item) {
        items.add(item);
    }

    class ClothesListViewHolder extends RecyclerView.ViewHolder {
        ClothesListViewHolder(View view) {
            super(view);
        }

        void onBind(ClothesItem item) {
            ImageView iv = itemView.findViewById(R.id.iv_cloth_thumbnail);
            iv.setImageURI(item.mImage);
        }
    }
}
