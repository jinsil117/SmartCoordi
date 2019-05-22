package com.pickth.dddd.smartcoordination.lookbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.pickth.dddd.smartcoordination.R;

import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class LookbookFragment extends Fragment
{
    RecyclerView imageView; //갤러리에서 선택한 이미지를 보여주기 위한 것
    FloatingActionButton album; //xml에서 albumfab과 연결하기 위한 것
    DisplayMetrics dm;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lookbook,container,false);


        //룩북 탭
        imageView = view.findViewById(R.id.iv_view);


        ChooseData chooseData = new ChooseData(getContext());
        WindowManager.LayoutParams wm = chooseData.getWindow().getAttributes();  //다이얼로그의 높이 너비 설정하기위해

        dm = getResources().getDisplayMetrics(); //디스플레이
        int width = dm.widthPixels; //get display width
        int height = dm.heightPixels; //get display height
        wm.width = width / 2;  //화면 너비의 절반
        wm.height = height / 2;  //화면 높이의 절반
        wm.copyFrom(chooseData.getWindow().getAttributes());  //여기서 설정한값을 그대로 다이얼로그에 넣겠다는의미
        album = (FloatingActionButton) view.findViewById(R.id.albumfab);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseData.callFunction();
            }
        });

        return view;
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) { //startActivityForResult를 실행했을 때, 받기를 기대했던 어떠한 값 동작을 정의하는 메소드
//        if (requestCode == 1) {
//            // Make sure the request was successful
//            if (resultCode == RESULT_OK) {
//                try {
//                    // 선택한 이미지에서 비트맵 생성
//                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    in.close();
//                    // 이미지 표시
//                    //imageView.setImageBitmap(img);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
