package com.example.administrator.bk_smart_connection_android;

import android.content.ActivityNotFoundException;
import android.content.Entity;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1beta2.CloudNaturalLanguage;
import com.google.api.services.language.v1beta2.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1beta2.model.AnnotateTextRequest;
import com.google.api.services.language.v1beta2.model.AnnotateTextResponse;
import com.google.api.services.language.v1beta2.model.Document;
import com.google.api.services.language.v1beta2.model.Features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

    private final String CLOUD_API_KEY = "AIzaSyC04ydu7WUjQcEO360k0GSQbUG7aLgXmkg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);
        mSpeakBtn = (ImageButton) findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });
        Button analyzeButton = (Button)findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyzeText();
            }
        });
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mVoiceInputTv.setText(result.get(0));
                }
                break;
            }

        }
    }

    public void analyzeText() {
        final CloudNaturalLanguage naturalLanguageService =
                new CloudNaturalLanguage.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setCloudNaturalLanguageRequestInitializer(
                        new CloudNaturalLanguageRequestInitializer(CLOUD_API_KEY)
                ).build();
        String transcript = mVoiceInputTv.getText().toString();
        //String transcript = "Michael Jackson happy song about the world, home, love, friends";
        Document document = new Document();
        document.setType("PLAIN_TEXT");
        document.setLanguage("en-US");
        document.setContent(transcript);

        Features features = new Features();
        features.setExtractEntities(true);
        features.setExtractDocumentSentiment(true);

        final AnnotateTextRequest request = new AnnotateTextRequest();
        request.setDocument(document);
        request.setFeatures(features);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AnnotateTextResponse response =
                            naturalLanguageService.documents()
                                    .annotateText(request).execute();
                    final List<com.google.api.services.language.v1beta2.model.Entity> entityList = response.getEntities();
                    final float sentiment = response.getDocumentSentiment().getScore();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String entities = "";
                            for(com.google.api.services.language.v1beta2.model.Entity entity:entityList) {
                                entities += "\n" + entity.getName().toUpperCase() + " - " + entity.getType() + " - " + String.format("%.03f", entity.getSalience());
                            }
                            AlertDialog dialog =
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("Sentiment: " + sentiment)
                                            .setMessage("This audio file talks about :"
                                                    + entities)
                                            .setNeutralButton("Close", null)
                                            .create();
                            dialog.show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // More code here
            }
        });
    }
}
