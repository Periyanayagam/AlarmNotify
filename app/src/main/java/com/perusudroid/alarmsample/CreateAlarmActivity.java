package com.perusudroid.alarmsample;

import android.app.TimePickerDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,View.OnClickListener {

    AlarmModel amod;
    EditText mEdtTxtName;
    TextView mFooterSave, mTxtTime, mChooseSong, mRepeatTxt;
    DatabaseHandler db;
    HorizontalScrollView mHSV;
    CheckBox mon, tue, wed, thu, fri, sat, sun, mRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_alarm);
        init();
        setAssets();
        amod = new AlarmModel();
        Calendar c = Calendar.getInstance();
        int hrofday = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        openTimePicker(hrofday, min);
        
    }

    private void setAssets() {
        mFooterSave.setOnClickListener(this);
        mTxtTime.setOnClickListener(this);
        mChooseSong.setOnClickListener(this);
    }


    private void init() {
        mFooterSave = (TextView) findViewById(R.id.footer_save);
        mTxtTime = (TextView) findViewById(R.id.txt_time);
        mEdtTxtName = (EditText) findViewById(R.id.edt_txt_alarm_name);
        mChooseSong = (TextView) findViewById(R.id.choosesong);
        mRepeatTxt = (TextView) findViewById(R.id.rep);

        db = new DatabaseHandler(this);
        mon = (CheckBox) findViewById(R.id.mon);
        tue = (CheckBox) findViewById(R.id.tue);
        wed = (CheckBox) findViewById(R.id.wed);
        thu = (CheckBox) findViewById(R.id.thu);
        fri = (CheckBox) findViewById(R.id.fri);
        sat = (CheckBox) findViewById(R.id.sat);
        sun = (CheckBox) findViewById(R.id.sun);
        mRepeat = (CheckBox) findViewById(R.id.repeat_chk_box);
        mHSV = (HorizontalScrollView) findViewById(R.id.hsv);


    }

    private void openTimePicker(int hrofday, int min) {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(this, this, hrofday, min, false);
        mTimePickerDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String _24HourTime = "" + hourOfDay + ":" + minute;
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");
        amod.setHour(hourOfDay);
        amod.setMinute(minute);
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ;
        mTxtTime.setText("" + _12HourSDF.format(_24HourDt));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.footer_save:
                amod.setName(mEdtTxtName.getText().toString().trim());
                amod.setRepeat("o");
                amod.setIsEnabled(1);
                amod.setType("a");
                db.addAlarmModel(amod);
                amod.setId(db.getId());

                Log.e("CreateAlarmFragment", "dbid=" + db.getId());
                AppController.getInstance().doCreateAlarm(1,null,this, amod, System.currentTimeMillis());
                break;
        }
    }
}
