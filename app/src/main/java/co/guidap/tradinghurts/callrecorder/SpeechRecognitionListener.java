package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public abstract class SpeechRecognitionListener implements RecognitionListener {
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
    public abstract void onEndOfSpeech();

    @Override
    public abstract void onError(int i);

    @Override
    public abstract void onResults(Bundle bundle);

    @Override
    public void onPartialResults(Bundle bundle) {
        /*Log.d(TAG, "onPartialResults() : "+bundle.toString());
        ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (String text : results) {
            Log.d(TAG, "    --> "+text);
        }*/
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.d(TAG, "onEvent() code="+i);
    }
}
