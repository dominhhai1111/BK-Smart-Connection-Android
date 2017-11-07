package com.example.administrator.bk_smart_connection_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.speech.RecognitionListener;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.speech.v1beta1.Speech;
import com.google.api.services.speech.v1beta1.SpeechRequestInitializer;
import com.google.api.services.speech.v1beta1.model.RecognitionAudio;
import com.google.api.services.speech.v1beta1.model.RecognitionConfig;
import com.google.api.services.speech.v1beta1.model.SpeechRecognitionResult;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeRequest;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeResponse;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private final String CLOUD_API_KEY = "AIzaSyC04ydu7WUjQcEO360k0GSQbUG7aLgXmkg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setUpCloudSpeechAPI() {
        Speech speechService = new Speech.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null
        ).setSpeechRequestInitializer(
                new SpeechRequestInitializer(CLOUD_API_KEY))
                .build();

        RecognitionConfig recognitionConfig = new RecognitionConfig();
        recognitionConfig.setLanguageCode("en-US");

        RecognitionAudio recognitionAudio = new RecognitionAudio();
        //recognitionAudio.setContent(base64EncodedData);

        // Create request
        SyncRecognizeRequest request = new SyncRecognizeRequest();
        request.setConfig(recognitionConfig);
        request.setAudio(recognitionAudio);

        // Generate response
        SyncRecognizeResponse response = null;
        try {
            response = speechService.speech()
                                    .syncrecognize(request)
                                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Extract transcript
        SpeechRecognitionResult result = response.getResults().get(0);
        final String transcript = result.getAlternatives().get(0)
                .getTranscript();
    }
}
