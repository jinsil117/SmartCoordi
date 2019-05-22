package com.pickth.dddd.smartcoordination.cloth;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickth.dddd.smartcoordination.R;

import java.util.ArrayList;

public class ClothesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ClothesClickListener mClickListener;
    ArrayList<ClothesItem> items = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cloth_thumbnail, parent, false);
        return new ClothesViewHolder(itemView);
    }

    /**
     * 리턴된 뷰 홀더에 데이터를 입력시킨다.(바인딩)
     * @param holder onCreateViewHolder에서 만들어진 뷰 홀더
     * @param position 몇 번째 아이템인지
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 뷰 홀더에 바인딩하는 부분
        ((ClothesViewHolder) holder).onBind(mClickListener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Object getItem(int position)
    {
        return items.get(position);
    }

    public void setImage(int position, byte[] bytes){
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void addItem(ClothesItem item) {
        items.add(item);
    }

    /**
     *
     * @param listener ClothesFragment 에서 생성한 클릭리스너를 받아오는 부분
     */
    public void setClothesClickListener(ClothesClickListener listener) {
        mClickListener = listener;
    }

    class ClothesViewHolder extends RecyclerView.ViewHolder {
        ClothesDataManager manager;
        ImageView ivCloth = itemView.findViewById(R.id.iv_cloth_thumbnail);
        TextView tvSeason = itemView.findViewById(R.id.tv_cloth_thumbnail);

        ClothesViewHolder(View view) {
            super(view);
        }

        void onBind(final ClothesClickListener listener, int position) {
            // 아이템들, 데이터 매니저 초기화
            manager = new ClothesDataManager(itemView.getContext());
            items = manager.getClothesItems();
            try {
                byte[] bytes = items.get(position).getByte();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivCloth.setImageBitmap(bitmap);
                tvSeason.setText(items.get(position).getmSeason());
            }catch (Exception e){}


            // 아이템을 눌렀을 때 클릭리스너.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(items);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    manager.removeItem(items.get(position));
                    onBindViewHolder(ClothesViewHolder.this,position);
                    return false;
                }
            });

        }
    }
}
