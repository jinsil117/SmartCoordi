package com.pickth.dddd.smartcoordination;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by User on 2018-12-18.
 */

public class HistoryActivity extends AppCompatActivity {

    String time;
    MaterialCalendarView materialCalendarView;
    private final int CAMERA_CODE=1111;
    private final int GALLERY_CODE=2222;
    final String[] choice = new String []{"카메라","갤러리"}; //대화상자에 표시할 문구
    private String currentPhotoPath;//실제 사진 파일 경로
    String mImageCaptureName;//이미지 이름
    final int REQ_CODE_SELECT_IMAGE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2017, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        materialCalendarView.addDecorators( //달력을 꾸며주는 효과들
                new SundayDecorator(),
                new SaturdayDecorator()
        );

        String[] result = {"2017,03,18","2017,04,18","2017,05,18","2017,06,18"};
        new ApiSimulator(result).executeOnExecutor(Executors.newSingleThreadExecutor());

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() { //날짜 클릭 이벤트
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, final boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "." + Month + "." + Day;

                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();
                //대화상자
                AlertDialog.Builder dlg = new AlertDialog.Builder(HistoryActivity.this);
                dlg.setIcon(R.mipmap.ic_launcher);
                dlg.setItems(choice, new DialogInterface.OnClickListener() { //사진 불러오는 방식
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){ //카메라
                            int permissionCheck = ContextCompat.checkSelfPermission(HistoryActivity.this, Manifest.permission.CAMERA); //권한확인을 위한 변수
                            if(permissionCheck== PackageManager.PERMISSION_DENIED){ // 권한 없음
                                ActivityCompat.requestPermissions(HistoryActivity.this, new String[]{Manifest.permission.CAMERA},0); //카메라 실행
                            }
                            else{// 권한 있음
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent,1);
                            }
                        }else{ //갤러리 선택
                            selectGallery(); //갤러리 실행
                        }
                    }
                });
                dlg.setNegativeButton("취소",null);
                dlg.show();
            }
        });

    }

    private void selectGallery(){ //갤러리에 접근하도록 하는 함수
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,GALLERY_CODE);
    }

    //해당 앱에서 사진을 찍으면 저장할 파일생성
    private File createImageFile1() throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory() + "/path/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageCaptureName = timeStamp + ".png";
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/path/"

                + mImageCaptureName);
        currentPhotoPath = storageDir.getAbsolutePath();

        return storageDir;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_OK){
            switch (requestCode){
                case CAMERA_CODE:
                    String uri = sendPicture(data.getData()); //갤러리에서 가져오기기
                    break;

                case GALLERY_CODE:
                    getPictureForPhoto(); //카메라에서 가져오기
                    break;

                default:
                    break;
            }
        }
    }

    //카메라로 찍은 사진 적용
    private void getPictureForPhoto() {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        //ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }


    private String sendPicture(Uri imgUri){
        String imagePath = getRealPathFromURI(imgUri); // path 경로
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
        // ivImage.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        return imagePath;

    }

    //사진의 회전값 가져오기
    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    //사진을 정방향대로 회전하기
    private Bitmap rotate(Bitmap src, float degree) {

// Matrix 객체 생성
        Matrix matrix = new Matrix();
// 회전 각도 셋팅
        matrix.postRotate(degree);
// 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    //사진의 절대경로 구하기
    private String getRealPathFromURI(Uri contentUri){
        int column_index=0;
        String [] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null,null,null);
        if(cursor.moveToFirst()){
            column_index=cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }
//        sqlDB = dbHelper.getWritableDatabase();
//        sqlDB.execSQL("insert into gallery values ('"
//                +shot_day +"',"
//                +URI+");");
//        sqlDB.close();
        return cursor.getString(column_index);
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case  REQUEST_TAKE_PHOTO:
//                if(requestCode == Activity.RESULT_OK){
//                    try{
//                        Log.i("REQUEST_TAKE_PHOTO","OK");
//                        galleryAddPic();
//
//                       // iv_view.setImageURI(imageUrl);
//                    }catch (Exception e){
//                        Log.e("REQUEST_TAKE_PHOTO", e.toString());
//                    }
//                }else {
//                    Toast.makeText(this, "사진찍기를 취소하였습니다.",Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case REQUEST_TAKE_ALBUM:
//                if(requestCode == Activity.RESULT_OK){
//                    if(data.getData() != null){
//                        try{
//                            File albumFile=null;
//                            albumFile = createImageFile();
//                            photoURI = data.getData();
//                            albumURI = Uri.fromFile(albumFile);
//                            cropImage();
//                        }catch (Exception e){
//                        }
//                    }
//                }
//                break;
//
//            case REQUEST_IMAGE_CROP:
//                if(requestCode == Activity.RESULT_OK){
//                    galleryAddPic();
//                    //iv_view.setImageURI(imageUrl);
//                }
//                break;
//        }
//    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {
        String[] Time_Result;
        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }
        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*특정날짜 달력에 점표시해주는곳*/
            /*월은 0이 1월 년,일은 그대로*/
            //string 문자열인 Time_Result 을 받아와서 ,를 기준으로짜르고 string을 int 로 변환
            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar);
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int dayy = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1,dayy);
            }
            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,HistoryActivity.this));
        }
    }

}