package co.guidap.tradinghurts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.Objects;

import co.guidap.tradinghurts.callrecorder.CallRecordService;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Objects.equals(intent.getAction(), "android.intent.action.PHONE_STATE")) {
            return;
        }

        TelephonyManager mTelephonyMgr = context.getSystemService(TelephonyManager.class);
        if (mTelephonyMgr != null) {
            mTelephonyMgr.listen(new CallListener(context), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    class CallListener extends PhoneStateListener
    {
        private Context context;
        private Boolean serviceRunning = false;

        public CallListener(Context context) {
            this.context = context;
        }

        public void onCallStateChanged(int state, String incomingNumber)
        {
            super.onCallStateChanged(state, incomingNumber);
            switch (state)
            {
                case TelephonyManager.CALL_STATE_IDLE:
                    // Call done
                    /*Intent stopIntent = new Intent(context, CallRecordService.class);
                    context.stopService(stopIntent);*/
                    Intent stopIntent = new Intent(context, CallRecordService.class);
                    stopIntent.setAction(CallRecordService.ACTION_STOP_RECORDING);
                    context.startService(stopIntent);
                    serviceRunning = false;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Call responded
                    if (!serviceRunning) {
                        Intent startIntent = new Intent(context, CallRecordService.class);
                        startIntent.setAction(CallRecordService.ACTION_START_RECORDING);
                        startIntent.putExtra(CallRecordService.EXTRA_INCOMING_NUMBER, incomingNumber);
                        context.startService(startIntent);
                        serviceRunning = true;
                    }
                    break;
                default:
                    break;
            }
        }

    }
}
