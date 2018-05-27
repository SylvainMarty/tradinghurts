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
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.SettingsActivity;


/**
 * Created by sylvainmarty on 23/05/2018.
 */

public class CallRecordService extends Service {

    private static final String TAG = "CallRecordService";
    public static final String ACTION_START_RECORDING = "co.guidap.tradinghurts.action.START_RECORDING";
    public static final String EXTRA_INCOMING_NUMBER = "co.guidap.tradinghurts.extra.INCOMING_NUMBER";
    private static final String NOTIFICATION_CHANNEL = "TRADING_HURTS_CHANNEL";
    private static final Integer FOREGROUNG_ID = 101;

    private Looper mServiceLooper;
    private Handler mServiceHandler;

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
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Thread.NORM_PRIORITY);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new Handler(
            mServiceLooper,
            new Recorder(new Recorder.Callback() {
                @Override
                public void onStart() {
                    startForeground(FOREGROUNG_ID, updateNotification());
                }

                @Override
                public void onStop(Message msg) {
                    stopForeground(true);
                    stopSelf(msg.arg1);
                }
            })
        );
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting for "+intent.getStringExtra(EXTRA_INCOMING_NUMBER), Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private Notification updateNotification() {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SettingsActivity.class), 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL, this.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Test");

            notificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL)
                    .setContentTitle(String.valueOf(R.string.notification_title))
                    .setContentText(String.valueOf(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            return builder.build();
        }

        Log.d(TAG, "Notif with compatibility mode");

        return new NotificationCompat.Builder(this)
                .setContentTitle(String.valueOf(R.string.notification_title))
                .setContentText(String.valueOf(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }
}
