package com.pickth.dddd.smartcoordination;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.common.io.ByteStreams;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.PredictionEndpoint;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.ImagePrediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.Prediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.TrainingApi;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.Trainings;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Project;
import com.pickth.dddd.smartcoordination.add.ColorAdapter;
import com.pickth.dddd.smartcoordination.add.ColorItem;
import com.pickth.dddd.smartcoordination.add.SetSpinner;

import java.util.ArrayList;
import java.util.UUID;

public class ClothAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinnerTopBottoms, spinnerLength, spinnerSeason, spinnerColor;
    String TAG = getClass().getName();
    String topBottoms, length, color;
    ArrayList<ColorItem> mColorList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_add);

        spinnerTopBottoms = (Spinner) findViewById(R.id.spinner_topBottoms_clothAdd);
        spinnerTopBottoms.setOnItemSelectedListener(this);
        // Create an ArrayAdapter using the string array and a default spinnerTopBottoms layout
        ArrayAdapter<CharSequence> adapterTopBottoms = ArrayAdapter.createFromResource(this, R.array.array_topBottoms, android.R.layout.simple_spinner_dropdown_item);
        //simple_spinner_dropdown_item or simple_spinner_item
        // Apply the adapterTopBottoms to the spinnerTopBottoms
        spinnerTopBottoms.setAdapter(adapterTopBottoms);

        spinnerLength = (Spinner)findViewById(R.id.spinner_length_clothAdd);
        spinnerLength.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterLength = ArrayAdapter.createFromResource(this, R.array.array_length, android.R.layout.simple_spinner_dropdown_item);
        spinnerLength.setAdapter(adapterLength);

        spinnerSeason = (Spinner)findViewById(R.id.spinner_season_clothAdd);
        spinnerSeason.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapterSeason = ArrayAdapter.createFromResource(this, R.array.array_season, android.R.layout.simple_spinner_dropdown_item);
        spinnerSeason.setAdapter(adapterSeason);

        //spinnerColor 초기화
        mColorList = new ArrayList<>();
        initList();

        spinnerColor = findViewById(R.id.spinner_color_clothAdd);
        ColorAdapter mAdapter = new ColorAdapter(this, mColorList);
        spinnerColor.setAdapter(mAdapter);

        spinnerColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ColorItem clickedItem = (ColorItem) parent.getItemAtPosition(position);
                String clickedColorName = clickedItem.getColorName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new GetAzureDataAsyncTask().execute();
//        new GetAzureDataAsyncTask2(getApplicationContext()).execute();
//        Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show();

    }

    private void initList() {
        mColorList.add(new ColorItem("brown", this.getResources().getColor(R.color.colorBrown)));
        mColorList.add(new ColorItem("black", this.getResources().getColor(R.color.colorBlack)));
        mColorList.add(new ColorItem("yellow", this.getResources().getColor(R.color.colorYellow)));
        mColorList.add(new ColorItem("pink", this.getResources().getColor(R.color.colorPink)));
        mColorList.add(new ColorItem("green", this.getResources().getColor(R.color.colorGreen)));
        mColorList.add(new ColorItem("sky-blue", this.getResources().getColor(R.color.colorSky_blue)));
        mColorList.add(new ColorItem("gray", this.getResources().getColor(R.color.colorGray)));
        mColorList.add(new ColorItem("white", this.getResources().getColor(R.color.colorWhite)));
        mColorList.add(new ColorItem("navy", this.getResources().getColor(R.color.colorNavy)));
        mColorList.add(new ColorItem("purple", this.getResources().getColor(R.color.colorPurple)));
        mColorList.add(new ColorItem("red", this.getResources().getColor(R.color.colorRed)));
        mColorList.add(new ColorItem("charcoal", this.getResources().getColor(R.color.colorCharcoal)));
        mColorList.add(new ColorItem("blue", this.getResources().getColor(R.color.colorBlue)));
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
                Toast.makeText(this, spinnerTopBottoms.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                // 입력한 값을 파일에 저장하는 부분
                //new ClothesDataManager(ClothAddActivity.this).addItem(new ClothesItem(title));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class GetAzureDataAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {
            //vision
            try {

                final String trainingApiKey = getApplicationContext().getString(R.string.trainingApiKey);
                final String predictionApiKey = getApplicationContext().getString(R.string.predictionApiKey);

                TrainingApi trainClient = CustomVisionTrainingManager.authenticate(trainingApiKey);
                PredictionEndpoint predictClient = CustomVisionPredictionManager.authenticate(predictionApiKey);

                Trainings trainer = trainClient.trainings();

                UUID uuid = UUID.fromString(getApplicationContext().getString(R.string.projectId));

                //생성된 Iteration을 삭제하지 않으면 없어도 동작한다.
//                Iteration it = trainer.trainProject(uuid);
//                while(it.status().equals("Training")) {
//                    Thread.sleep(1000);
//                    it = trainer.getIteration(uuid, it.id());
//                }
//                trainer.updateIteration(uuid, it.id(), it.withIsDefault(true));

                Project project = trainer.getProject(uuid);
                Log.d(TAG, "성ㄷ공");

                // load test image
                byte[] testImage = GetImage("/res/drawable", "test_image.png");

                // predictClient 개체를 통해 표현되는 예측 엔드포인트는 현재 모델에 이미지를 제출하고 분류 예측을 가져오는 데 참조
                ImagePrediction results = predictClient.predictions().predictImage()
                        .withProjectId(project.id())
                        .withImageData(testImage)
                        .execute();

                int i=0;
                for (Prediction prediction: results.predictions()) {
                    if (i<3) {
                        Log.d(TAG, String.format("\t%s: %.2f%%", prediction.tagName(), prediction.probability() * 100.0f));
                        if (prediction.tagName().equals("상의") || prediction.tagName().equals("하의"))
                            topBottoms = prediction.tagName();
                        else if (prediction.tagName().equals("3부") || prediction.tagName().equals("5부") || prediction.tagName().equals("7부") || prediction.tagName().equals("9부"))
                            length = prediction.tagName();
                        else
                            color = prediction.tagName();

                        new SetSpinner(prediction.tagName(), spinnerTopBottoms, spinnerLength, spinnerColor).set();

                        i++;
                    }else break;
                }
                Log.d(TAG, String.format("%s %s %s", topBottoms, length, color));

            }catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public static byte[] GetImage(String folder, String fileName)
    {
        try {
            return ByteStreams.toByteArray(ClothAddActivity.class.getResourceAsStream(folder + "/" + fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
