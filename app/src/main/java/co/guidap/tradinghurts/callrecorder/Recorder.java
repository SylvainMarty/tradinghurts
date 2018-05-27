package co.guidap.tradinghurts.callrecorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by sylvainmarty on 26/05/2018.
 */

public class Recorder implements Runnable {
    private static final String TAG = "Recorder";

    public interface Callback {

        /**
         * Called when the record start
         */
        void onStart();

        /**
         * Called when the current recording stopped
         */
        void onStop(int startId);

    }

    private Context mContext;
    private Callback mCallback;
    private int startId;

    public Recorder(Context context, Callback recordCallback) {
        this.mContext = context;
        this.mCallback = recordCallback;
    }

    public void record(int startId) {
        this.startId = startId;
        this.run();
    }

    @Override
    public void run() {
        Log.d(TAG, "Runner started with startId="+startId);
        if (mCallback != null) {
            mCallback.onStart();
        }

        /*MediaRecorder recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File file = new File(mContext.getFilesDir(), "test");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            recorder.setOutputFile(file);
        } else {
            recorder.setOutputFile(file.getPath());
        }

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "An error happened when preparing audio record", e);
            Thread.currentThread().interrupt();
        }

        recorder.start();*/

        int count = 1;
        while (count <= 10) {
            Log.d(TAG, "I'm a thread, count="+count);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            count++;
        }

        if (mCallback != null) {
            mCallback.onStop(startId);
        }
    }
}
