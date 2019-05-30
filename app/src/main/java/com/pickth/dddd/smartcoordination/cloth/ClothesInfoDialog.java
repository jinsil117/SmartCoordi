package com.pickth.dddd.smartcoordination.cloth;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.pickth.dddd.smartcoordination.R;

public class ClothesInfoDialog {
    private Context mContext;
    ClothesItem mItem;

    public ClothesInfoDialog(Context context, ClothesItem item) {
        this.mContext = context;
        this.mItem = item;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(mContext);

//        // 액티비티의 타이틀바를 숨긴다.
//        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_cloth_information);

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final TextView topBottoms = (TextView) dlg.findViewById(R.id.tv_cloth_info_topBottoms);
        final TextView length = (TextView) dlg.findViewById(R.id.tv_cloth_info_length);
        final TextView season = (TextView) dlg.findViewById(R.id.tv_cloth_info_season);
        final TextView color = (TextView) dlg.findViewById(R.id.tv_cloth_info_color);

        topBottoms.setText(mItem.getmTopBottoms());
        length.setText(mItem.getmLength());
        season.setText(mItem.getmSeason());
        color.setText(mItem.getmColor());

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
    }
}
