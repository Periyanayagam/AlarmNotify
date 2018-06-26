package com.perusudroid.alarmsample;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Half;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg != null) {
                Log.d(TAG, "handleMessage: " + msg.arg1);
                if (msg.arg1 == 1) {
                    Toast.makeText(AppController.this, "Yep! you Just Fired up alarm!", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        }
    }
    );

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        if (mInstance == null) {
            mInstance = new AppController();
        }
        return mInstance;
    }

    public String doCreateAlarm(int snakFlag, CoordinatorLayout mcor, Context con, AlarmModel amod, long timex) {
        String time = calculateTime(amod);
        if (amod.getRepeat() != null) {
            if (amod.getRepeat().equals("o")) {
                StringBuilder sb = new StringBuilder();
                sb.append("" + amod.getId());
                sb.append("00");
                setAlarm(con, amod, Integer.parseInt(sb.toString()), 0, 0, timex);
            }
        }

        if (mcor != null) {
            Snackbar snackbar = Snackbar.make(mcor, "Alarm set for " + time + " from now", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (snakFlag == 1)
            Toast.makeText(con, "Alarm set for " + time + " from now", Toast.LENGTH_SHORT).show();

        return time;
    }


    public String calculateTime(AlarmModel amod) {
        int repeat = 0;
        int week = 0;
        String time = "";
        if (amod.getRepeat() != null) {
            if (amod.getRepeat().equals("o")) {
                repeat = 0;
                week = 0;
                Calendar cal = findCalendarTime(amod, repeat, week);
                Calendar curr = Calendar.getInstance();
                long millis = cal.getTimeInMillis() - curr.getTimeInMillis();
                if (TimeUnit.MILLISECONDS.toHours(millis) != 0) {
                    time = String.format("%02d hr %02d min %02d sec", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                } else {
                    time = String.format("%02d min %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                }

            }
        }


        return time;
    }

    private Calendar findCalendarTime(AlarmModel am, int repeat, int week) {
        Calendar calSet = Calendar.getInstance();
        calSet.setFirstDayOfWeek(Calendar.SUNDAY);
        calSet.setTimeInMillis(System.currentTimeMillis());

        calSet.set(Calendar.HOUR_OF_DAY, am.getHour());
        calSet.set(Calendar.MINUTE, am.getMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (repeat == 1) {
            calSet.set(Calendar.DAY_OF_WEEK, week);
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 7);
            }
        } else {
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 1);
            }
        }

        return calSet;
    }


    public void setAlarm(Context context, AlarmModel am, int requestcode, int repeat, int week, long time) {


        Log.d(TAG, "setAlarm: ");
        AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        Log.e("CreateAlarmFragment", "setAlarm" + am.getId());
        Log.e("CreateAlarmFragment", "setAlarm" + requestcode);
        Messenger messenger = new Messenger(mHandler);
        i.putExtra("messenger", messenger);
        i.putExtra("ALARM_ID", "" + am.getId());
        i.putExtra("REQUEST_CODE", "" + requestcode);
        Calendar calSet = Calendar.getInstance();
        calSet.setFirstDayOfWeek(Calendar.SUNDAY);
        //calSet.setTimeInMillis(System.currentTimeMillis());
        calSet.setTimeInMillis(time);

        calSet.set(Calendar.HOUR_OF_DAY, am.getHour());
        calSet.set(Calendar.MINUTE, am.getMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestcode, i, 0);
        if (calSet.before(Calendar.getInstance())) {
            calSet.add(Calendar.DATE, 1);
        }

        Log.d(TAG, "setAlarm: mills "+ calSet.getTimeInMillis());

        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "setExactAndAllowWhileIdle");
            amgr.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                Log.d(TAG, "setExact");
                amgr.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
            } else {
                Log.d(TAG, "set");
                amgr.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
            }
        }

       /* if (Build.VERSION.SDK_INT >= 19) {
            Log.d(TAG, "setExact");
            amgr.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
        } else {
            amgr.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
        }*/
        Log.d(TAG, "setAlarm: alarmSet");
    }


}
