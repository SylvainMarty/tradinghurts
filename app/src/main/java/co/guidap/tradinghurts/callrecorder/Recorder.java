package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

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
    private HandlerThread mHandlerThread;
    private RecordPersistenceHandler mRecordPersistenceHandler;
    private StringBuilder mConversation;

    public Recorder(Context context) {
        mContext = context;
    }

    public void start(String phoneNumber) {
        // On baisse le volume des médias car le démarrage du SpeechRecognizer déclenche un bruit génant
        mAudio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudio != null) {
            try {
                mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);
            } catch (Throwable ignored){}
        }

        if (SpeechRecognizer.isRecognitionAvailable(mContext)) {
            // On démarre le thread secondaire
            mHandlerThread = new HandlerThread("RecordPersisterThread");
            mHandlerThread.start();
            mRecordPersistenceHandler =
                    new RecordPersistenceHandler(mHandlerThread.getLooper(), mContext);
            // On remet à zéro la conversation locale mise en cache
            mConversation = new StringBuilder();
            initSpeechRecognizer(phoneNumber);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.speech_recognizer_not_available), Toast.LENGTH_SHORT).show();
            stop();
        }
    }

    public void stop() {
        // On remet le volume des médias a sa valeur précédente
        if (mAudio != null) {
            try {
                mAudio.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        mAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        AudioManager.FLAG_SHOW_UI
                );
            } catch (Throwable ignored){}
        }
        // On stoppe le thread de queueing
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mRecordPersistenceHandler = null;
        }
        // On éteint le SpeechRecognizer
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.stopListening();
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
            mConversation = null;
        }
    }

    private void initSpeechRecognizer(String phoneNumber) {
        // Envoi du premier message qui va créer l'entrée
        // de la retranscription en base de données
        Message msg = new Message();
        msg.obj = phoneNumber;
        mRecordPersistenceHandler.sendMessage(msg);

        mIntent = buildIntent();
        if (mSpeechRecognizer == null) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
            mSpeechRecognizer.setRecognitionListener(new SpeechRecognitionListener(mContext) {
                @Override
                public void onEndOfSpeech() {

                }

                @Override
                public void onError(int i) {
                    if (i == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        // Hack : on tente de relancer le SpeechRecognizer
                        // pour écouter la conversation en cas d'erreur
                        mSpeechRecognizer.startListening(mIntent);
                    }
                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (results != null) {
                        for (String text : results) {
                            mConversation.append("- ").append(text).append("\n");
                        }

                        // Envoi de la nouvelle conversation dans la file de message
                        Message msg = new Message();
                        msg.obj = mConversation.toString();
                        mRecordPersistenceHandler.sendMessage(msg);
                    }
                    // On relance le SpeechRecognizer pour écouter
                    // la conversation suivante (c'est un hack...)
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
