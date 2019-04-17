package com.pickth.dddd.smartcoordination;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.pickth.dddd.smartcoordination.add.ClothAddActivity;
import com.pickth.dddd.smartcoordination.cloth.ClothesAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class ClothesFragment extends Fragment implements View.OnClickListener{
    RecyclerView rvCalendar;
    ClothesAdapter mAdapter;
    Button btnVision;

    private RecyclerView.LayoutManager mLayoutManager;
    private Animation fab_open,fab_close; //fab을 활성화 및 비활성화에 따른 Animation
    private Boolean isFabOpen = false; //처음 +버튼의 fab을 클릭할 경우 fab1과 fab2를 visible
    private FloatingActionButton fab, fab1, fab2; //fragment_clothes.xml에서 만든 fab을 이용하기 위한 선언

    private static final int PICK_FROM_CAMERA = 2; //사진을 촬영하고 찍힌 이미지를 이미지뷰에
    private static final int PICK_FROM_ALBUM = 1; //앨범에서 사진을 고르고 이미지를 이미지뷰에
    private static final int CROP_FROM_CAMERA = 0; //이미지를 크롭하는 부분

    private String imageFilePath;

    private Uri photoUri;

    private Uri mImageCaptureUri; //크롭된 이미지에 대한 Uri(Uniform Resource Identifier = 통합 자원 식별자)
    private ImageView mPhotoImageView; //크롭된 이미지를 보여주기 위한 imageView


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_clothes,container,false);

        fab_open =  AnimationUtils.loadAnimation(getContext(),R.anim.fab_open); //+버튼 클릭 시 갤러리 접근 fab과 카메라 접근 fab이 보여짐
        fab_close = AnimationUtils.loadAnimation(getContext(), R.anim.fab_close); // 갤러리 접근 fab과 카메라 접근 fab이 안보여짐

        fab = (FloatingActionButton)view.findViewById(R.id.fab); //fragment_clothes.xml의 FloatingActionButto인 +버튼과 연결
        fab1 = (FloatingActionButton)view.findViewById(R.id.fab1); //fragment_clothes.xml의 FloatingActionButto인 +버튼 클릭시 보이는 fab1(갤러리크롭)과 연결
        fab2 = (FloatingActionButton)view.findViewById(R.id.fab2); //fragment_clothes.xml의 FloatingActionButto인 +버튼 클릭시 보이는 fab2(카메라크롭)과 연결

        fab.setOnClickListener(this); //167줄인 onClick(View v)의 메소드를 통해 View 객체를 받아오는 것
        fab1.setOnClickListener(this); //167줄인 onClick(View v)의 메
        fab2.setOnClickListener(this);

        mPhotoImageView = (ImageView) view.findViewById(R.id.image); //fragment_clothes.xml에서의 imageView와 연결, 갤러리나 카메라로 크롭된 사진을 보여줌

        //vision 테스트하는 버튼
        btnVision = view.findViewById(R.id.btn_frag_clothes_visionTEST);
        btnVision.setOnClickListener(this);

        //옷장 탭
        rvCalendar = view.findViewById(R.id.rv_clothes);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rvCalendar.setLayoutManager(mLayoutManager);
        rvCalendar.scrollToPosition(0);
        mAdapter = new ClothesAdapter();
        rvCalendar.setAdapter(mAdapter);
        rvCalendar.setItemAnimator(new DefaultItemAnimator());

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
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_CAMERA: {
                mPhotoImageView.setImageURI(photoUri);
                break;
            }
            case PICK_FROM_ALBUM: {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    mPhotoImageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }

//            크롭을 없앴다 배경자르기를 여기에 넣는지는 모르겠다_하은
//            case CROP_FROM_CAMERA: {
//                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
//                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
//
//                Intent intent = new Intent("com.android.camera.action.CROP");
//                intent.setDataAndType(mImageCaptureUri, "image/*");
//
//                intent.putExtra("outputX", 200);
//                intent.putExtra("outputY", 200);
//                intent.putExtra("aspectX", 1);
//                intent.putExtra("aspectY", 1);
//                intent.putExtra("scale", true);
//                intent.putExtra("return-data", true);
//                startActivityForResult(intent, CROP_FROM_CAMERA);
//
//                break;
//            }
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

    //임시로 비트맵을 png로 저장하도록 하는 방법 구상중 2019.03.15 여니
//    /*
//
//     * Bitmap을 저장하는 부분
//
//     */
//
//    private void storeCropImage(Bitmap bitmap, String filePath) {
//
//        // SmartCoordiAlbum 폴더를 생성하여 이미지를 저장하는 방식이다.
//
//        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/SmartCoordiAlbum";
//
//        File directory_SmartCoordiAlbum = new File(dirPath);
//
//
//        if(!directory_SmartCoordiAlbum.exists()) // SmartCoordiAlbum 디렉터리에 폴더가 없다면 (새로 이미지를 저장할 경우에 속한다.)
//
//            directory_SmartCoordiAlbum.mkdir();
//
//
//        File copyFile = new File(filePath);
//
//        BufferedOutputStream out = null;
//
//
//        try {
//
//
//            copyFile.createNewFile();
//
//            out = new BufferedOutputStream(new FileOutputStream(copyFile));
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
//
//
//            // sendBroadcast를 통해 Crop된 사진을 앨범에 보이도록 갱신한다.
//
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//
//                    Uri.fromFile(copyFile)));
//
//
//
//            out.flush();
//
//            out.close();
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//
//        }
//
//    }
//
//}
////https://jeongchul.tistory.com/287 [Jeongchul]


    @Override
    public void onClick(View v) {  //70줄의 setOnClickListener(this)과 관련된 것으로 onClick(View v)의 메소드를 통해 View 객체를 받아오는 것
        int id = v.getId(); // fab, fab1, fab2 이므로 View v의 id값을 가져온다(v.getId())
        switch (id) { //switch문의 변수인 id값에 따라서 함수를 실행할 수 있도록 하는 제어문
            case R.id.fab: //id값이 fab
                fab1.startAnimation(fab_open);
                fab2.startAnimation(fab_open);
                //anim();
                break;
            case R.id.fab1: //id값이 fab1
                doTakeAlbumAction();
                fab1.startAnimation(fab_close);
                fab2.startAnimation(fab_close);
                break;
            case R.id.fab2: //id값이 fab2
                doTakePhotoAction();
                //anim();
                break;
            case R.id.btn_frag_clothes_visionTEST:
                Intent intent = new Intent(getContext(),ClothAddActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void anim() { //fab인 +버튼 클릭 시 활성화 되는 Animation 함수
        if(isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
        }else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}