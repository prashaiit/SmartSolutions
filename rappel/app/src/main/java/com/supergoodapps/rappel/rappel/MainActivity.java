package com.supergoodapps.rappel.rappel;

import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private Button ReminderAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ReminderAnswer = (Button) findViewById(R.id.Reminder_Answer);

        ReminderAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                boolean speakerWasOn = am.isSpeakerphoneOn();
                int speakerPrevMode = am.getMode();
                boolean bthA2dpWasOn = am.isBluetoothA2dpOn();
                boolean bthScoWasOn = am.isBluetoothScoOn();

                if (bthA2dpWasOn && bthScoWasOn) {
                    am.setMode(AudioManager.STREAM_VOICE_CALL);
                    am.setBluetoothA2dpOn(true);
                    am.setBluetoothScoOn(true);

                } else {
                    am.setSpeakerphoneOn(false);
                    am.setMode(AudioManager.MODE_IN_CALL);
                }
*/
                textToSpeech.speak("Welcome to Rappel App", TextToSpeech.QUEUE_FLUSH, null);

                while (textToSpeech.isSpeaking());

             /*   am.setSpeakerphoneOn(speakerWasOn);
                am.setMode(speakerPrevMode);
                am.setBluetoothA2dpOn(bthA2dpWasOn);
                am.setBluetoothScoOn(bthScoWasOn);*/

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                    //textToSpeech.setLanguage(new Locale("en-in"));
                }
            }
        });
    }

    public void onPause(){
        Log.i("test", "MainActivity : OnPause");
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
