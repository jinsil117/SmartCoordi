package com.pickth.dddd.smartcoordination.add;

import android.widget.Spinner;

public class SetSpinner {
    String mTagName;
    Spinner mSpinnerTopBottoms, mSpinnerLength, mSpinnerColor;

    public SetSpinner(String tagName, Spinner spinnerTopBottoms, Spinner spinnerLength, Spinner spinnerColor) {
        this.mTagName = tagName;
        this.mSpinnerTopBottoms = spinnerTopBottoms;
        this.mSpinnerLength = spinnerLength;
        this.mSpinnerColor = spinnerColor;
    }

    public void set(){
        switch (mTagName){
            case "상의": mSpinnerTopBottoms.setSelection(0, true); break;
            case "하의": mSpinnerTopBottoms.setSelection(1, true); break;

            case "3부": mSpinnerLength.setSelection(0, true); break;
            case "5부": mSpinnerLength.setSelection(1, true); break;
            case "7부": mSpinnerLength.setSelection(2, true); break;
            case "9부": mSpinnerLength.setSelection(3, true); break;

            case "갈색": mSpinnerColor.setSelection(0, true); break;
            case "검은색": mSpinnerColor.setSelection(1, true); break;
            case "노란색": mSpinnerColor.setSelection(2, true); break;
            case "분홍색": mSpinnerColor.setSelection(3, true); break;
            case "초록색": mSpinnerColor.setSelection(4, true); break;
            case "하늘색": mSpinnerColor.setSelection(5, true); break;
            case "회색": mSpinnerColor.setSelection(6, true); break;
            case "흰색": mSpinnerColor.setSelection(7, true); break;
            case "남색": mSpinnerColor.setSelection(8, true); break;
            case "보라색": mSpinnerColor.setSelection(9, true); break;
            case "빨간색": mSpinnerColor.setSelection(10, true); break;
            case "챠콜색": mSpinnerColor.setSelection(11, true); break;
            case "파란색": mSpinnerColor.setSelection(12, true); break;
        }
    }
}
