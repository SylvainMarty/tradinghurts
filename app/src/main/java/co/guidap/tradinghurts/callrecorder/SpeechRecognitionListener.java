package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class SpeechRecognitionListener implements RecognitionListener {
    private static final String TAG = "SpeechRecognitionListener";

    private Context mContext;

    public SpeechRecognitionListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech()");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech()");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech()");
    }

    @Override
    public void onError(int i) {
        Log.d(TAG, "onError() -> error="+i);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.d(TAG, "onResults()");
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (String text : results) {
            Log.d(TAG, "    --> "+text);
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.d(TAG, "onPartialResults() : "+bundle.toString());
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (String text : results) {
            Log.d(TAG, "    --> "+text);
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.d(TAG, "onEvent()");
    }
}
