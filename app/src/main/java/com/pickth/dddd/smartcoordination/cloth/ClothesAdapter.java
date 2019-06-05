package com.pickth.dddd.smartcoordination.cloth;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;

import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ClothesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ClothesClickListener mClickListener;
    ArrayList<ClothesItem> items = new ArrayList<>();

    DBHelper DBHelper;
    SQLiteDatabase db;

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

        ClothesViewHolder(View view) {
            super(view);
        }

        void onBind(final ClothesClickListener listener, int position) {
            DBHelper = new DBHelper(itemView.getContext());
            // 아이템들, 데이터 매니저 초기화
            manager = new ClothesDataManager(itemView.getContext());
            items = manager.getClothesItems();
            try {
                byte[] bytes = items.get(position).getByte();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ivCloth.setImageBitmap(bitmap);
            }catch (Exception e){}

            // 아이템을 눌렀을 때 클릭리스너.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(items);
                    Activity act = (Activity)itemView.getContext();
                    LayoutInflater inflater = act.getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_heart, (ViewGroup)itemView.findViewById(R.id.toast_heart));

                    PrettyDialog pDialog = new PrettyDialog(itemView.getContext());
                    pDialog
//                  .setTitle("PrettyDialog Title")
//                  .setMessage("PrettyDialog Message")
                    .setIcon(R.drawable.pdlg_icon_success)
                    .setIconTint(R.color.colorPurple1)
                    .setIconCallback(new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            Toast toast = new Toast(itemView.getContext());
                            DisplayMetrics display = itemView.getContext().getResources().getDisplayMetrics();
                            int xOffset = (int) (Math.random() * display.widthPixels);
                            int yOffset = (int) (Math.random() * display.heightPixels);
                            toast.setGravity(Gravity.TOP | Gravity.LEFT, xOffset, yOffset);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();
                        }
                    })
                    .addButton(
                            "Information",     // button text
                            R.color.pdlg_color_white,  // button text color
                            R.color.colorPurple2,  // button background color
                            new PrettyDialogCallback() {  // button OnClick listener
                                @Override
                                public void onClick() {
                                    // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                                    ClothesInfoDialog customDialog = new ClothesInfoDialog(itemView.getContext(), manager.getItem(position));

                                    // 커스텀 다이얼로그를 호출한다.
                                    // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                                    customDialog.callFunction();
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .addButton(
                            "Delete",
                            R.color.pdlg_color_white,
                            R.color.colorPurple3,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    manager.removeItem(position);
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .addButton(
                            "Cancel",
                            R.color.pdlg_color_black,
                            R.color.pdlg_color_gray,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .show();
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

        }
    }
}
