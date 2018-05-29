package co.guidap.tradinghurts.callrecorder;

import android.arch.lifecycle.ViewModelProviders;
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
import co.guidap.tradinghurts.persistence.Record;
import co.guidap.tradinghurts.persistence.RecordRepository;
import co.guidap.tradinghurts.persistence.RecordViewModel;

/**
 * Created by sylvainmarty on 26/05/2018.
 */

public class Recorder {
    private static final String TAG = "Recorder";

    private Context mContext;
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mIntent;
    private AudioManager mAudio;
    private RecordRepository mRecordRepository;

    public Recorder(Context context) {
        mContext = context;
        mRecordRepository = new RecordRepository(mContext);
    }

    public void start(String phoneNumber) {
        mAudio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudio != null) {
            try {
                mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
            } catch (Throwable ignored){}
        }

        if (SpeechRecognizer.isRecognitionAvailable(mContext)) {
            initSpeechRecognizer(phoneNumber);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.speech_recognizer_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    public void stop() {
        if (mAudio != null) {
            try {
                mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, mAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
            } catch (Throwable ignored){}
        }
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
    }

    private void initSpeechRecognizer(String phoneNumber) {
        mIntent = buildIntent();
        final Record record = new Record(phoneNumber);
        mRecordRepository.insert(record);

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
                    StringBuilder conversation = new StringBuilder();
                    if (record.getConversation() != null) {
                        conversation.append(record.getConversation());
                    }
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null) {
                        for (String text : results) {
                            conversation.append("- ").append(text).append("\n");
                        }
                        Log.d(TAG, "onResults() : " + conversation.toString());
                        record.setConversation(conversation.toString());
                        mRecordRepository.update(record);
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
