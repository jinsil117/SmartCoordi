package com.pickth.dddd.smartcoordination.cloth;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.pickth.dddd.smartcoordination.R;
import com.pickth.dddd.smartcoordination.add.ClothAddActivity;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static android.app.Activity.RESULT_OK;

public class ClothesFragment extends Fragment implements View.OnClickListener{

    RecyclerView rvClothes;
    ClothesAdapter mAdapter;
    ClothesDataManager manager;
    ArrayList<ClothesItem> items = new ArrayList<>();

    private Animation fab_open,fab_close; //fab을 활성화 및 비활성화에 따른 Animation
    private Boolean isFabOpen = false; //처음 +버튼의 fab을 클릭할 경우 fab1과 fab2를 visible
    private FloatingActionButton fab, fab1, fab2; //fragment_clothes.xml에서 만든 fab을 이용하기 위한 선언
    private static final int PICK_FROM_CAMERA = 2; //사진을 촬영하고 찍힌 이미지를 이미지뷰에
    private static final int PICK_FROM_ALBUM = 1; //앨범에서 사진을 고르고 이미지를 이미지뷰에

    private String imageFilePath;
    private Uri photoUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes_he,container,false);

        fab_open =  AnimationUtils.loadAnimation(getContext(),R.anim.fab_open); //+버튼 클릭 시 갤러리 접근 fab과 카메라 접근 fab이 보여짐
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close); // 갤러리 접근 fab과 카메라 접근 fab이 안보여짐

        fab = (FloatingActionButton)view.findViewById(R.id.fab); //fragment_clothes.xml의 FloatingActionButto인 +버튼과 연결
        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1); //fragment_clothes.xml의 FloatingActionButto인 +버튼 클릭시 보이는 fab1(갤러리크롭)과 연결
        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2); //fragment_clothes.xml의 FloatingActionButto인 +버튼 클릭시 보이는 fab2(카메라크롭)과 연결

        fab.setOnClickListener(this); //167줄인 onClick(View v)의 메소드를 통해 View 객체를 받아오는 것
        fab1.setOnClickListener(this); //167줄인 onClick(View v)의 메
        fab2.setOnClickListener(this);

        manager = new ClothesDataManager(getContext());
        items = manager.getClothesItems();

        // rvClothes를 연동할 adapter 설정
        mAdapter = new ClothesAdapter();
        mAdapter.setClothesClickListener(new ClothesClickListener() {
            @Override
            public void onClick(ArrayList<ClothesItem> items) {
            }
        });

        //adapter에 item 추가하기
        for(ClothesItem item: items)
            mAdapter.addItem(item);

        // 새로고침
        mAdapter.notifyDataSetChanged();

        // recycler view 설정
        rvClothes = view.findViewById(R.id.rv_clothes_he);
        rvClothes.setAdapter(mAdapter);
        rvClothes.setLayoutManager(new GridLayoutManager(getContext(), 3));

        return view;
    }

    /**
     * 카메라 사진 촬영
     */
    private void doTakePhotoAction() { //카메라 촬영 후 이미지 가져오는 함수
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }

        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName(), photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
        }
    }

    /**
     * 앨범에서 이미지 선택
     */
    private void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
//        intent.putExtra("crop", "true");
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_CAMERA: {
                Intent intent = new Intent(getContext(),ClothAddActivity.class);
                intent.putExtra("imageUri", photoUri);
                intent.putExtra("imageFilePath", imageFilePath);
                startActivity(intent);
                break;
            }
            case PICK_FROM_ALBUM: {
                Uri uri = data.getData();
                Intent intent = new Intent(getContext(),ClothAddActivity.class);
                intent.putExtra("imageUri", uri);
                imageFilePath = getRealImagePath(uri);
                intent.putExtra("imageFilePath", imageFilePath);
                startActivity(intent);
                break;
            }
        }
    }

    //이미지 파일을 생성하는 메소드
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".png",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onClick(View v) {  //70줄의 setOnClickListener(this)과 관련된 것으로 onClick(View v)의 메소드를 통해 View 객체를 받아오는 것
        int id = v.getId(); // fab, fab1, fab2 이므로 View v의 id값을 가져온다(v.getId())
        switch (id) { //switch문의 변수인 id값에 따라서 함수를 실행할 수 있도록 하는 제어문
            case R.id.fab: //id값이 fab
                anim();
                break;
            case R.id.fab1: //id값이 fab1
                doTakeAlbumAction();
                anim();
                break;
            case R.id.fab2: //id값이 fab2
                doTakePhotoAction();
                anim();
                break;
        }
    }

    public void anim() { //fab 클릭 시 활성화 되는 Animation 함수
        if(isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        }else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    /**
     * URI로 부터 실제 파일 경로를 가져온다.
     * @param uri URI : URI 경로
     * @return String : 실제 파일 경로
     */
    public String getRealImagePath(Uri uri) {
        String res = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        res = cursor.getString(column_index);
        cursor.close();
        return res;
    }

}