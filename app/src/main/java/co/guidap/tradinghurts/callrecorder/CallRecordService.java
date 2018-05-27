package co.guidap.tradinghurts.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.SettingsActivity;


/**
 * Created by sylvainmarty on 23/05/2018.
 */

public class CallRecordService extends Service {

    private static final String TAG = "CallRecordService";
    private static final String NOTIFICATION_CHANNEL = "TRADING_HURTS_CHANNEL";
    private static final Integer FOREGROUNG_ID = 101;
    private static final String DEDICATED_THREAD_NAME = "CallRecordThread";
    public static final String ACTION_START_RECORDING = "co.guidap.tradinghurts.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "co.guidap.tradinghurts.action.STOP_RECORDING";
    public static final String EXTRA_INCOMING_NUMBER = "co.guidap.tradinghurts.extra.INCOMING_NUMBER";

    private HandlerThread mThread;
    private Recorder mRecorder;
    private Handler mServiceHandler;
    private NotificationManager mNotificationManager;
    private Integer startId;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        mThread = new HandlerThread(DEDICATED_THREAD_NAME, Thread.NORM_PRIORITY);
        mThread.start();
        mServiceHandler = new Handler(mThread.getLooper());
        mRecorder = new Recorder(this, new Recorder.Callback() {
            @Override
            public void onStart() {
                startForeground(FOREGROUNG_ID, updateNotification());
            }

            @Override
            public void onStop(int startId) {
                Log.d(TAG, "Stopping service for startId="+startId);
                CallRecordService.this.exitSafely(startId);
            }
        });

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_START_RECORDING:
                Toast
                    .makeText(this, "Record started for "+intent.getStringExtra(EXTRA_INCOMING_NUMBER), Toast.LENGTH_SHORT)
                    .show();
                // For each start request, send a message to start a job and deliver the
                // start ID so we know which request we're stopping when we finish the job
                this.startId = startId;
                mServiceHandler.postDelayed(mRecorder, 1000);

                /*Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
                speechRecognizer.setRecognitionListener(new SpeechRecognitionListener(mContext));
                speechRecognizer.startListening(intent);
                speechRecognizer.stopListening();
                speechRecognizer.destroy();*/

                break;
            case ACTION_STOP_RECORDING:
                this.exitSafely(this.startId);
                break;
            default:
                break;
        }

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    /**
     * Create the service notification
     * @return the notification
     */
    private Notification updateNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SettingsActivity.class), 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && mNotificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    this.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(this.getString(R.string.notification_channel_description));

            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL)
                    .setContentTitle(String.valueOf(R.string.notification_title))
                    .setContentText(String.valueOf(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            return builder.build();
        }

        Log.d(TAG, "Notification builded in compatibility mode");

        return new NotificationCompat.Builder(this)
                .setContentTitle(String.valueOf(R.string.notification_title))
                .setContentText(String.valueOf(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    /**
     * Stop current service safely
     * @param startId
     */
    private void exitSafely(@Nullable Integer startId) {
        mServiceHandler.removeCallbacks(mRecorder);
        mThread.quitSafely();
        stopForeground(true);
        if (startId != null) {
            stopSelf(startId);
        } else {
            stopSelf();
        }
    }
}
