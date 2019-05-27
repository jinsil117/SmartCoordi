package com.pickth.dddd.smartcoordination.lookbook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.pickth.dddd.smartcoordination.ChangeImage;
import com.pickth.dddd.smartcoordination.DBHelper;
import com.pickth.dddd.smartcoordination.R;
import com.pickth.dddd.smartcoordination.history.CalendarAdapter;

import java.util.ArrayList;

public class ChooseData extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private Context context;
    int state;
    DataAdapter adapter;
    SQLiteDatabase db;
    DBHelper dbHelper;
    dataItem dataItem;
    ArrayList<dataItem> DI;
    GridView gv;

    CheckBox checkBox;

    ChangeImage changeImage = new ChangeImage();
    public ChooseData(Context context){
        this.context = context;
        state=0; //1=상의. 2=하의
        dbHelper = new DBHelper(context);
    }

    public void callFunction(){
        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.fragment_lookbook_data);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final Button top = (Button) dlg.findViewById(R.id.top);
        final Button bt = (Button) dlg.findViewById(R.id.bt);
        gv = (GridView) dlg.findViewById(R.id.lookgv);
        checkBox = (CheckBox) dlg.findViewById(R.id.cb);
        final Button choose = (Button) dlg.findViewById(R.id.choose);
        final Button cancel = (Button) dlg.findViewById(R.id.cancel);

//        top.setOnClickListener(this);
//        bt.setOnClickListener(this);
        gv.setOnItemClickListener(this);
//        choose.setOnClickListener(this);
//        cancel.setOnClickListener(this);

//        if(state==1){
//            top.setEnabled(false);
//            bt.setEnabled(true);
//        }else if(state == 2) {
//            top.setEnabled(true);
//            bt.setEnabled(false);
//        }else {
//            top.setEnabled(true);
//            bt.setEnabled(true);
//        }

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                state = 1;
                saveTop();
                initLookbookAdapter();

            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                state =2;
               saveBottom();
                initLookbookAdapter();

            }
        });

//        okButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
//                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
//                main_label.setText(message.getText().toString());
//                Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();
//
//                // 커스텀 다이얼로그를 종료한다.
//                dlg.dismiss();
//            }
//        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }

    public void checkState(){

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        dataItem dataItem1 = DI.get(position);
        dataItem1.setChecked(true);
        checkBox.setChecked(true);
    }

    protected void saveTop(){
        db = dbHelper.getReadableDatabase();
        int num;
        DI = new ArrayList<>();
        Cursor numCursor = db.rawQuery("SELECT num FROM clothesTBL WHERE topBottoms= 'Top';",null);

        while (numCursor.moveToNext()){
            num = numCursor.getInt(0);

            Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num = "+num, null);
            if (sizeCursor.moveToNext()) {
                long blobStart = 1; //blob 시작
                long blobLen = 1; //blob 길이
                int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성

                while (blobSize > 0) {
                    blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                    blobSize -= blobLen;

                    Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num = "+num, null);
                    if (blobCursor.moveToNext()) {
                        byte[] barr = blobCursor.getBlob(0);
                        if (barr != null) {
                            System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                        }
                        blobCursor.close();

                        blobStart += blobLen;
                    }

                }
                Bitmap bm = changeImage.getBitmap(bytes);
                dataItem = new dataItem(num,bm);
                DI.add(dataItem);
            }

            sizeCursor.close();
        }
        numCursor.close();
        db.close();
    }

    protected void saveBottom(){
        db = dbHelper.getReadableDatabase();
        int num;
        DI = new ArrayList<>();
        Cursor numCursor = db.rawQuery("SELECT num FROM clothesTBL WHERE topBottoms= 'Bottoms';",null);

        while (numCursor.moveToNext()){
            num = numCursor.getInt(0);

            Cursor sizeCursor = db.rawQuery("SELECT length(img) FROM clothesTBL WHERE num = "+num, null);
            if (sizeCursor.moveToNext()) {
                long blobStart = 1; //blob 시작
                long blobLen = 1; //blob 길이
                int blobSize = sizeCursor.getInt(0); //이미지의 blob 사이즈
                byte[] bytes = blobSize > 0 ? new byte[(int) blobSize] : null; //blob 사이즈의 배열 생성

                while (blobSize > 0) {
                    blobLen = blobSize > 1000000 ? 1000000 : blobSize; //1000000는 cursor 용량 한계치
                    blobSize -= blobLen;

                    Cursor blobCursor = db.rawQuery("SELECT substr(img," + blobStart + "," + blobLen + ") FROM clothesTBL WHERE num = "+num, null);
                    if (blobCursor.moveToNext()) {
                        byte[] barr = blobCursor.getBlob(0);
                        if (barr != null) {
                            System.arraycopy(barr, 0, bytes, (int) blobStart - 1, barr.length);
                        }
                        blobCursor.close();

                        blobStart += blobLen;
                    }

                }
                Bitmap bm = changeImage.getBitmap(bytes);
                dataItem = new dataItem(num,bm);
                DI.add(dataItem);
            }

            sizeCursor.close();
        }
        numCursor.close();
        db.close();
    }

    private void initLookbookAdapter() {
        adapter = new DataAdapter(context, R.layout.fragment_lookbook_data_info,DI);
        gv.setAdapter(adapter);
    }


}
