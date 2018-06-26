package com.perusudroid.alarmsample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {

    /* Receives scheduled Alarm intents */
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");

        // acquire the lock
        wl.acquire();

        // you can do the processing here.
        // ...

        // release the lock
        wl.release();

        Log.d("AlarmReceiver", "onReceive: ");
        Intent i = new Intent(context, MainActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage("Alarm triggered", "Yep! Received", new Date().toString(), i);
    }

}
