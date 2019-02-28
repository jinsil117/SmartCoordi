package com.pickth.dddd.smartcoordination;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ClothesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ClothesClickListener clickListener;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cloth, parent, false);
        return new ClothesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 날짜 뷰 홀더에 바인딩하는 부분
        ((ClothesViewHolder)holder).onBind(clickListener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ClothesViewHolder extends RecyclerView.ViewHolder {
        ClothesDataManager manager;
        ArrayList<ClothesItem> items;
        ClothesViewHolder(View view) {
            super(view);
        }

        void onBind(final ClothesClickListener listener) {
            // 날짜, 아이템들, 데이터 메니저 초기화
            manager = new ClothesDataManager(itemView.getContext());
            items = manager.getClothesItems();
            TextView tvCloth = itemView.findViewById(R.id.tv_cloth);
            ImageView ivCloth = itemView.findViewById(R.id.iv_cloth);

            // recycler view로 아이템 리스트 뿌려주기
            ClothesListAdapterTitle adapter = new ClothesListAdapterTitle(0);
            RecyclerView rvSchedule = itemView.findViewById(R.id.rv_clothes);
            rvSchedule.setAdapter(adapter);
            rvSchedule.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayout.VERTICAL,false));

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
    }
}
