package com.hayuneldon.zoom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import java.util.Locale;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by 2017hchong on 1/4/2016.
 */
public class SpeechActivity extends Activity implements OnClickListener, OnInitListener {

    private TextToSpeech tts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech);

        //Instantiate the text to speech service and wire button
        tts = new TextToSpeech(this, this);
        findViewById(R.id.button1).setOnClickListener(this);
    }

    //Lets you know if text-to-speech engine is ready to go
    //Ideal place to set the language
    @Override
    public void onInit(int code) {
        if (code == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
        } else {
            tts = null;
            Toast.makeText(this, "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Handle user button clicks
    @Override
    public void onClick(View v) {
        if (tts != null) {
            String text =
                    ((EditText) findViewById(R.id.editText1)).getText().toString();
            if (text != null) {
                if (!tts.isSpeaking()) {
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }

    //Clean the engine when application shuts down- otherwise will result in a crash is the user switches applications
    @Override
    protected void onDestroy() {
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

}