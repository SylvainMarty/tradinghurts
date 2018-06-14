package co.guidap.tradinghurts.callrecorder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import co.guidap.tradinghurts.R;
import co.guidap.tradinghurts.RecordListActivity;


/**
 * Created by sylvainmarty on 23/05/2018.
 */

public class CallRecordService extends Service {

    private static final String TAG = "CallRecordService";
    private static final String NOTIFICATION_CHANNEL = "TRADING_HURTS_CHANNEL";
    private static final Integer FOREGROUNG_ID = 101;
    public static final String ACTION_START_RECORDING = "co.guidap.tradinghurts.action.START_RECORDING";
    public static final String ACTION_STOP_RECORDING = "co.guidap.tradinghurts.action.STOP_RECORDING";
    public static final String EXTRA_INCOMING_NUMBER = "co.guidap.tradinghurts.extra.INCOMING_NUMBER";

    private Integer startId;
    private State serviceState = State.STOPPED;
    private NotificationManager mNotificationManager;
    private Recorder mRecorder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Call record done", Toast.LENGTH_SHORT).show();
        this.exitSafely(this.startId);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case ACTION_START_RECORDING:
                if (serviceState == State.RUNNING) {
                    break;
                }
                mRecorder = new Recorder(this);
                // For each start request, send a message to start a job and deliver the
                // start ID so we know which request we're stopping when we finish the job
                startForeground(FOREGROUNG_ID, updateNotification());
                this.startId = startId;
                mRecorder.start(intent.getStringExtra(EXTRA_INCOMING_NUMBER));
                serviceState = State.RUNNING;
                Toast
                    .makeText(this, "Call record started", Toast.LENGTH_SHORT)
                    .show();
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
                new Intent(this, RecordListActivity.class), 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && mNotificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    this.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(this.getString(R.string.notification_channel_description));

            mNotificationManager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL)
                    .setContentTitle(this.getString(R.string.notification_title))
                    .setContentText(this.getString(R.string.notification_message))
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);

            return builder.build();
        }

        Log.i(TAG, "Notification builded in compatibility mode");

        return new NotificationCompat.Builder(this)
                .setContentTitle(this.getString(R.string.notification_title))
                .setContentText(this.getString(R.string.notification_message))
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
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder = null;
        }
        stopForeground(true);
        if (startId != null) {
            stopSelf(startId);
        } else {
            stopSelf();
        }
    }

    public enum State {
        RUNNING,
        STOPPED
    }
}
