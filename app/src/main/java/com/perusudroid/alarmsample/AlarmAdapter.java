package com.perusudroid.alarmsample;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private static final String TAG = AlarmAdapter.class.getSimpleName();
    private List<AlarmModel> alarmList;
    public Context context;
    private List<AlarmViewHolder> lstHolders = null;
    private Handler mHandler = new Handler();


    public AlarmAdapter(Context con, List<AlarmModel> list) {
        this.alarmList = list;
        this.context = con;
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (AlarmViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };


    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1, 1000);

    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_alarm_item, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        holder.setData(alarmList.get(position));
        Log.d(TAG, "onBindViewHolder: ");
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        holder.updateTimeRemaining(System.currentTimeMillis());
        String _24HourTime = "" + alarmList.get(position).getHour() + ":" + alarmList.get(position).getMinute();
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");
        Log.e("ALARMIDDB", "=" + alarmList.get(position).getId());
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (alarmList.get(position).isEnabled() == 1) {
            holder.remaining.setVisibility(View.VISIBLE);

        } else {
            holder.remaining.setVisibility(View.GONE);
        }
        holder.time.setText("" + _12HourSDF.format(_24HourDt));
        holder.name.setText(alarmList.get(position).getName());
        holder.repeat.setText(alarmList.get(position).getRepeat());
        if (alarmList.get(position).isEnabled() == 1) {
            holder.switchcom.setChecked(true);
            holder.sleepBody.setVisibility(View.VISIBLE);
            holder.sleepBed.setVisibility(View.VISIBLE);
        } else {
            holder.switchcom.setChecked(false);
            holder.sleepBody.setVisibility(View.GONE);
            holder.sleepBed.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {

        public TextView time, name, repeat, remaining;
        public ImageView settingsImg, sleepBody, sleepBed, screenshot;
        private AlarmModel mModel;
        public SwitchCompat switchcom;

        public void updateTimeRemaining(long currentTime) {
            remaining.setText(calculateTime(mModel));
        }

        public AlarmViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.time);
            name = (TextView) itemView.findViewById(R.id.name);
            remaining = (TextView) itemView.findViewById(R.id.remaining);
            repeat = (TextView) itemView.findViewById(R.id.repeat);
            //card = (CardView) view.findViewById(R.id.card_view);
            settingsImg = (ImageView) itemView.findViewById(R.id.settings);
            sleepBody = (ImageView) itemView.findViewById(R.id.sleepbody);
            sleepBed = (ImageView) itemView.findViewById(R.id.sleepbed);
            screenshot = (ImageView) itemView.findViewById(R.id.screenshot);
            switchcom = (SwitchCompat) itemView.findViewById(R.id.switch_compat);
        }

        public void setData(AlarmModel alarmModel) {
            mModel = alarmModel;
            updateTimeRemaining(System.currentTimeMillis());
        }
    }


    private String calculateTime(AlarmModel amod) {
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
                if (TimeUnit.MILLISECONDS.toHours(millis) != 0)
                    time = String.format(Locale.getDefault(),"%02d hr %02d min %02d sec", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                else
                    time = String.format(Locale.getDefault(),"%02d min %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

            }
        }


        return time + " Remaining";
    }

    private Calendar findCalendarTime(AlarmModel am, int repeat, int week) {
        Calendar calSet = Calendar.getInstance();
        calSet.setFirstDayOfWeek(Calendar.SUNDAY);
        calSet.setTimeInMillis(System.currentTimeMillis());

        calSet.set(Calendar.HOUR_OF_DAY, am.getHour());
        calSet.set(Calendar.MINUTE, am.getMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (calSet.before(Calendar.getInstance())) {
            calSet.add(Calendar.DATE, 1);
        }

        return calSet;
    }
}
