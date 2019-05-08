package com.pickth.dddd.smartcoordination.add;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Spinner;

import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.CustomVisionPredictionManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.PredictionEndpoint;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.ImagePrediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.prediction.models.Prediction;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.CustomVisionTrainingManager;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.TrainingApi;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.Trainings;
import com.microsoft.azure.cognitiveservices.vision.customvision.training.models.Project;
import com.pickth.dddd.smartcoordination.R;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class GetAzureDataAsyncTask extends AsyncTask<Void, Void, Void> {
    Context mContext;
    String TAG = getClass().getName();
    String topBottoms, length, color;
    Spinner mSpinnerTopBottoms, mSpinnerLength, mSpinnerColor;
    Bitmap mBitmap;
    byte[] mBytes;

    public GetAzureDataAsyncTask(Context context, Spinner spinnerTopBottoms, Spinner spinnerLength, Spinner spinnerColor, Bitmap bitmap){
        this.mContext = context;
        this.mSpinnerTopBottoms = spinnerTopBottoms;
        this.mSpinnerLength = spinnerLength;
        this.mSpinnerColor = spinnerColor;
        this.mBitmap = bitmap;
    }
    public GetAzureDataAsyncTask(Context context, Spinner spinnerTopBottoms, Spinner spinnerLength, Spinner spinnerColor, byte[] bytes){
        this.mContext = context;
        this.mSpinnerTopBottoms = spinnerTopBottoms;
        this.mSpinnerLength = spinnerLength;
        this.mSpinnerColor = spinnerColor;
        this.mBytes = bytes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //vision
        try {
            final String trainingApiKey = mContext.getString(R.string.trainingApiKey);
            final String predictionApiKey = mContext.getString(R.string.predictionApiKey);

            TrainingApi trainClient = CustomVisionTrainingManager.authenticate(trainingApiKey);
            PredictionEndpoint predictClient = CustomVisionPredictionManager.authenticate(predictionApiKey);

            Trainings trainer = trainClient.trainings();

            UUID uuid = UUID.fromString(mContext.getString(R.string.projectId));

            //생성된 Iteration을 삭제하지 않는 한 없어도 동작한다.
//            Iteration it = trainer.trainProject(uuid);
//            while(it.status().equals("Training")) {
//                Thread.sleep(1000);
//                it = trainer.getIteration(uuid, it.id());
//            }
//            trainer.updateIteration(uuid, it.id(), it.withIsDefault(true));

            Project project = trainer.getProject(uuid);

            // load test image
//            byte[] testImage = ClothAddActivity.GetImage("/res/drawable", "test_image.png");

            byte[] testImage;

            if (mBytes == null){
                //앨범에서 선택한 옷 가져오기
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);    //압축
                byte[] byteArray = stream.toByteArray();
                testImage = byteArray;
            }else {
                Bitmap bitmap = BitmapFactory.decodeByteArray(mBytes, 0, mBytes.length);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                testImage = byteArray;
//                testImage = mBytes;
            }

            Log.d(TAG, String.format("%d", testImage.length));


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
                    i++;
                }else break;
                new SetSpinner(prediction.tagName(), mSpinnerTopBottoms, mSpinnerLength, mSpinnerColor).set();
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
