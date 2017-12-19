package com.example.administrator.bk_smart_connection_android;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.language.v1beta2.CloudNaturalLanguage;
import com.google.api.services.language.v1beta2.CloudNaturalLanguageRequestInitializer;
import com.google.api.services.language.v1beta2.model.AnnotateTextRequest;
import com.google.api.services.language.v1beta2.model.AnnotateTextResponse;
import com.google.api.services.language.v1beta2.model.Document;
import com.google.api.services.language.v1beta2.model.Features;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    private ImageButton mSpeakBtn;

//    private Button btnPlay;
    private String analyzedEntities;
    private String reObject;
    SpinKitView spinKitView;

    private final String CLOUD_API_KEY = "AIzaSyC04ydu7WUjQcEO360k0GSQbUG7aLgXmkg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initRetrofit();
        mVoiceInputTv = findViewById(R.id.voiceInput);
        mSpeakBtn = findViewById(R.id.btnSpeak);
        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        spinKitView = (SpinKitView) findViewById(R.id.progress);
        Style style = Style.values()[2];
        Sprite drawable = SpriteFactory.create(style);
        spinKitView.setIndeterminateDrawable(drawable);

        Button analyzeButton = findViewById(R.id.analyze_button);
        analyzeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                analyzeText();

            }
        });
//        btnPlay = findViewById(R.id.play_music);
//        btnPlay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(MainActivity.this,PlayActivity.class);
//                intent.putExtra("ANALYZEDOBJECT",analyzedEntities);
//                intent.putExtra("REOBJECT",reObject);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinKitView.setVisibility(View.INVISIBLE);

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
        spinKitView.setVisibility(View.VISIBLE);
        final CloudNaturalLanguage naturalLanguageService =
                new CloudNaturalLanguage.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(),
                        null
                ).setCloudNaturalLanguageRequestInitializer(
                        new CloudNaturalLanguageRequestInitializer(CLOUD_API_KEY)
                ).build();
        String transcript = mVoiceInputTv.getText().toString();
        final Document document = new Document();
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
                            List<AnalyzedObject> analyzedObjects = new ArrayList<>();
                            for (com.google.api.services.language.v1beta2.model.Entity entity : entityList) {
//                                entities += "\n" + entity.getName().toUpperCase() + " - " + entity.getType() + " - " + String.format("%.03f", entity.getSalience());
                                AnalyzedObject analyzedObject = new AnalyzedObject(entity.getName().toUpperCase(), entity.getType(), entity.getSalience());
                                analyzedObjects.add(analyzedObject);
                            }
                            Gson gson = new Gson();
                            analyzedEntities = gson.toJson(analyzedObjects);
                            ReObject re = new ReObject(document.getContent(), sentiment);
                            reObject = gson.toJson(re);
                            Log.d(MainActivity.class.getName(), "run: " + analyzedEntities);
//                            AlertDialog dialog =
//                                    new AlertDialog.Builder(MainActivity.this)
//                                            .setTitle("Sentiment: " + sentiment)
//                                            .setMessage("This audio file talks about :"
//                                                    + analyzedEntities)
//                                            .setNeutralButton("Close", null)
//                                            .create();
//                            dialog.show();
//                            btnPlay.setVisibility(View.VISIBLE);
                            Intent intent = new Intent();
                            intent.setClass(MainActivity.this,PlayActivity.class);
                            intent.putExtra("ANALYZEDOBJECT",analyzedEntities);
                            intent.putExtra("REOBJECT",reObject);
                            startActivity(intent);

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
