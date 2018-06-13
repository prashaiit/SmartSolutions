package com.coolapps.dictionary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView answerView;
    private SpeechRecognizer sr;
    private Button micButton;
    private final String TAG = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        micButton = (Button) findViewById(R.id.micButton);
        textView = (TextView) findViewById(R.id.queryView);
        answerView = (TextView) findViewById(R.id.answerView);
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.micButton)
                {
                    micButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_mic_clicked, 0,0,0);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-IN");
                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 15);
                    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

                    intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
                    sr.startListening(intent);
                    Log.i("111111","11111111");
                }

            }
        });

        sr = SpeechRecognizer.createSpeechRecognizer(this);
        sr.setRecognitionListener(new myListener());

    }

    class myListener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params)
        {
            Log.d(TAG, "onReadyForSpeech");
        }
        public void onBeginningOfSpeech()
        {
            Log.d(TAG, "onBeginningOfSpeech");
        }
        public void onRmsChanged(float rmsdB)
        {
            Log.d(TAG, "onRmsChanged");
        }
        public void onBufferReceived(byte[] buffer)
        {
            Log.d(TAG, "onBufferReceived");
        }
        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            textView.setText("error " + error);
            micButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_microsophone, 0,0,0);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            /*for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }*/
            textView.setText(data.get(0).toString());
            micButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_microsophone, 0,0,0);
            getAnswer(getDictionaryEntry(textView.getText().toString()));
        }
        public void onPartialResults(Bundle partialResults)
        {
            Log.d(TAG, "onPartialResults");
        }
        public void onEvent(int eventType, Bundle params)
        {
            Log.d(TAG, "onEvent " + eventType);
        }


    }

    public String getAnswer(String dictionaryEntry) {
        new CallbackTask().execute(getDictionaryEntry(textView.getText().toString()));
        return "";
    }

    public class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //TODO: replace with your own app id and app key
            final String app_id = "4b084f40";
            final String app_key = "5e007b5d59a3935512ea9b18655c5d1f";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("app_id", app_id);
                urlConnection.setRequestProperty("app_key", app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }

                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i("ans", result);
            answerView.setText(result);
        }
    }

    private String getDictionaryEntry(String word) {
        final String language = "en";
        final String word_id = word.toLowerCase(); //word id is case sensitive and lowercase is required
        return "https://od-api.oxforddictionaries.com:443/api/v1/entries/" + language + "/" + word_id;
    }
}
