package com.pickth.dddd.smartcoordination;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class ClothAddActivity extends AppCompatActivity {
    EditText etTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_add);

        etTitle = findViewById(R.id.et_add_title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //액션바 우측에 더하기 메뉴 생성
        getMenuInflater().inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add :
                String title = etTitle.getText().toString();

                if(title.length() == 0) {
                    Toast.makeText(this, "값을 입력하세요", Toast.LENGTH_SHORT).show();
                    return false;
                }

                // 입력한 값을 파일에 저장하는 부분
                new ClothesDataManager(ClothAddActivity.this).addItem(new ClothesItem(title));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
