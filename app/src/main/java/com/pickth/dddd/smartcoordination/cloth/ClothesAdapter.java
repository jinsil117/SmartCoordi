package com.pickth.dddd.smartcoordination.cloth;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.pickth.dddd.smartcoordination.R;

import java.util.ArrayList;

public class ClothesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ClothesClickListener mClickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        Log.d("Adddd", "onCreate");
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cloth_thumbnail, parent, false);
        return new ClothesViewHolder(itemView);
    }

    /**
     * 리턴된 뷰 홀더에 데이터를 입력시킨다.(바인딩)
     * @param holder onCreateViewHolder에서 만들어진 뷰 홀더
     * @param position 몇 번째 아이템인지
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("Adddd", "onBindV");

        // 뷰 홀더에 바인딩하는 부분
        ((ClothesViewHolder) holder).onBind(mClickListener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     *
     * @param listener ClothesFragment_HE 에서 생성한 클릭리스너를 받아오는 부분
     */
    public void setClothesClickListener(ClothesClickListener listener) {
        mClickListener = listener;
    }

    class ClothesViewHolder extends RecyclerView.ViewHolder {
        ClothesDataManager manager;
        ArrayList<ClothesItem> items;
        ImageView ivCloth = itemView.findViewById(R.id.iv_cloth_thumbnail);

        ClothesViewHolder(View view) {
            super(view);
        }

        void onBind(final ClothesClickListener listener) {
            Log.d("Adddd", "onBind");
            // 아이템들, 데이터 메니저 초기화
            manager = new ClothesDataManager(itemView.getContext());
            items = manager.getClothesItems();

            // recycler view로 아이템 리스트 뿌려주기
            ClothesListAdapterThumbnail adapter = new ClothesListAdapterThumbnail(0);
            RecyclerView rvClothes = itemView.findViewById(R.id.rv_clothes_he);
            rvClothes.setAdapter(adapter);
            rvClothes.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayout.VERTICAL,false));

            //adapte에 item 추가하기
            for(ClothesItem item: items)
                adapter.addItem(item);

            // 새로고침
            adapter.notifyDataSetChanged();

            // 아이템을 눌렀을 때 클릭리스너. CalendarActivity에서 만든 클릭리스너를 CalendarAdapter에 넘겨서 여기서 사용한다.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(items);
                }
            });
        }

        void onBind(ClothesItem item, int positoin) {
            ivCloth.setImageURI(item.mImage);
        }
    }
}
