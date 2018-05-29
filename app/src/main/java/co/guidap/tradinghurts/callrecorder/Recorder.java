package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import co.guidap.tradinghurts.R;

/**
 * Created by sylvainmarty on 26/05/2018.
 */

public class Recorder {
    private static final String TAG = "Recorder";

    private Context mContext;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mIntent;
    private AudioManager mAudio;

    public Recorder(Context context) {
        this.mContext = context;
    }

    public void start() {
        mAudio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);

        if (SpeechRecognizer.isRecognitionAvailable(mContext)) {
            initSpeechRecognizer();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.speech_recognizer_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, mAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
    }

    private void initSpeechRecognizer() {
        mIntent = buildIntent();

        if (mSpeechRecognizer == null) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(mContext) {
                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "onEndOfSpeech()");
                }

                @Override
                public void onError(int i) {
                    Log.d(TAG, "onError() -> error="+i);
                    if (i == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        mSpeechRecognizer.startListening(mIntent);
                    }
                }

                @Override
                public void onResults(Bundle bundle) {
                    Log.d(TAG, "onResults()");
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    for (String text : results) {
                        Log.d(TAG, "    --> "+text);
                    }
                    Log.d(TAG, "onResults() : recharging");
                    //mSpeechRecognizer.stopListening();
                    mSpeechRecognizer.startListening(mIntent);
                }
            });
        } else {
            mSpeechRecognizer.cancel();
        }

        mSpeechRecognizer.startListening(mIntent);
    }

    private Intent buildIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 60000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 60000);
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1000);

        return intent;
    }
}
