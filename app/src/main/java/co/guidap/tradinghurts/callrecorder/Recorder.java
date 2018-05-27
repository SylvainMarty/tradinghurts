package co.guidap.tradinghurts.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.SettingsActivity;

/**
 * Created by sylvainmarty on 26/05/2018.
 */

public class Recorder implements Handler.Callback {

    private static final String TAG = "Recorder";

    public interface Callback {

        /**
         * Called when the record start
         */
        void onStart();

        /**
         * Called when the current recording stopped
         */
        void onStop(Message msg);

    }

    private Callback recordCallback;

    public Recorder(Callback recordCallback) {
        this.recordCallback = recordCallback;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (recordCallback != null) {
            recordCallback.onStart();
        }

        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }

        if (recordCallback != null) {
            recordCallback.onStop(msg);
        }
        return false;
    }

}
