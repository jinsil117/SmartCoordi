package com.pickth.dddd.smartcoordination.lookbook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.pickth.dddd.smartcoordination.R;

import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class LookbookFragment extends Fragment
{
    RecyclerView imageView; //갤러리에서 선택한 이미지를 보여주기 위한 것
    FloatingActionButton album; //xml에서 albumfab과 연결하기 위한 것

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

        album = (FloatingActionButton) view.findViewById(R.id.albumfab);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { //startActivityForResult를 실행했을 때, 받기를 기대했던 어떠한 값 동작을 정의하는 메소드
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                try {
                    // 선택한 이미지에서 비트맵 생성
                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();
                    // 이미지 표시
                    //imageView.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
