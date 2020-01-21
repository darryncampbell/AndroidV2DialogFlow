package com.darryncampbell.androidv2dialogflowclient;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "DialogFlowV2";
    public static String DIALOGFLOW_RESPONSE = "DialogFlowResponse";
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private boolean ttsOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.UK);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e(TAG, "The Language is not supported!");
                    } else {
                        Log.i(TAG, "Language Supported.");
                        ttsOk = true;
                    }
                    Log.i(TAG, "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(DIALOGFLOW_RESPONSE));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                micButtonClicked();
            }
        });
        
        //  Request RECORD_AUDIO Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            //  Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        else
        {
            //  Permission was already granted
            createSpeechRecognizer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createSpeechRecognizer();
        } else {
            Toast.makeText(this, "Record Audio Permission required", Toast.LENGTH_LONG).show();
            finish();
        }
        return;
    }

    public void micButtonClicked()
    {
        //  todo Call Android speech recognition class to gather text
        //  todo ask for record audio permission
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,5);
        speechRecognizer.startListening(intent);
//        String question = "How are you feeling?";
//        DialogFlowCommsService.startActionDetectIntent(this, question);
    }

    private void createSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
    }

    class SpeechRecognitionListener implements RecognitionListener{
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
            SetOutputDisplay("Speech Recognition Error: " + error);
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i);
            }
            if (data.size() >= 1)
            {
                SetOutputDisplay("Speech Recognition Result: " + data.get(0));
                DialogFlowCommsService.startActionDetectIntent(getApplicationContext(), data.get(0).toString());
            }
            else
                SetOutputDisplay("Speech Recognition could not detect question");
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


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String response = intent.getStringExtra("response");
            Log.d(TAG, "Got message: " + response);
            SetOutputDisplay("Response: " + response);
            if (ttsOk)
            {
                int speechStatus = textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, "1");
                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e(TAG, "Error in converting Text to Speech");
                }
            }
        }
    };

    private void SetOutputDisplay(String outputMessage)
    {
        TextView output = findViewById(R.id.txtOutput);
        output.setText(outputMessage);

    }
}
