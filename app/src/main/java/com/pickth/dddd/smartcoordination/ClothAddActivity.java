package com.pickth.dddd.smartcoordination;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
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

import java.util.UUID;

public class ClothAddActivity extends AppCompatActivity {
    EditText etTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_add);

        etTitle = findViewById(R.id.et_add_title);

        //vision
        try {
            final String trainingApiKey = "";
            final String predictionApiKey = "";

            TrainingApi trainClient = CustomVisionTrainingManager.authenticate(trainingApiKey);
            PredictionEndpoint predictClient = CustomVisionPredictionManager.authenticate(predictionApiKey);

            Trainings trainer = trainClient.trainings();
            UUID uuid = UUID.fromString("");
            Toast.makeText(this, uuid.toString()+1, Toast.LENGTH_SHORT).show();
            Project project = trainer.getProject(uuid);
            Toast.makeText(this, uuid.toString()+2, Toast.LENGTH_SHORT).show();

            // load test image
            byte[] testImage = GetImage("/drawable", "test_image.png");

            // predictClient 개체를 통해 표현되는 예측 엔드포인트는 현재 모델에 이미지를 제출하고 분류 예측을 가져오는 데 참조
            ImagePrediction results = predictClient.predictions().predictImage()
                    .withProjectId(project.id())
                    .withImageData(testImage)
                    .execute();

            for (Prediction prediction: results.predictions()) {
                Toast.makeText(this, String.format("\t%s: %.2f%%", prediction.tagName(), prediction.probability() * 100.0f), Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
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

    private static byte[] GetImage(String folder, String fileName)
    {
        try {
            return ByteStreams.toByteArray(ClothAddActivity.class.getResourceAsStream(folder + "/" + fileName));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
