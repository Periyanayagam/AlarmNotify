package com.perusudroid.alarmsample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class OverlayService extends Service {


    private static final String TAG = OverlayService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Yep! you Just Fired up alarm!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStartCommand: ");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
