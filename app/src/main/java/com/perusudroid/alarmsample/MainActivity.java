package com.perusudroid.alarmsample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private AlarmAdapter mAdapter;
    private DatabaseHandler db;
    private List<AlarmModel> alarmList;
    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        setAssets();
        Log.d(TAG, "onCreate: IS_FIRST "+ mSharedPreferences.getString("IS_FIRST",null));
        if (mSharedPreferences.getString("IS_FIRST", null) == null) {
            Toast.makeText(this, "Enable AutoStart to receive notifications!", Toast.LENGTH_SHORT).show();
            enableAutotart();
        }

    }

    private void enableAutotart() {

        Log.d(TAG, "onCreate: " + Build.MANUFACTURER);

        Log.d(TAG, "enableAutotart: ");

        String manufacturer = "xiaomi";
        if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(intent);
        }

        mSharedPreferences.edit().putString("IS_FIRST", "IS_FIRST").apply();
    }

    private void bindViews() {
        recyclerView = findViewById(R.id.recycler_view);
        db = new DatabaseHandler(this);
        alarmList = new ArrayList<>();
        Log.d(TAG, "bindViews: size" + alarmList.size());

    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmList = db.getAllAlarmModels();
        mAdapter = new AlarmAdapter(this, alarmList);
        recyclerView.setAdapter(mAdapter);
    }

    private void setAssets() {
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, CreateAlarmActivity.class));
                    }
                }
        );

        db = new DatabaseHandler(this);
        ArrayList<AlarmModel> alarmarray = db.getAllAlarmModels();
        if (alarmarray.size() > 0) {
            for (int i = 0; i < alarmarray.size(); i++) {
                if (alarmarray.get(i).isEnabled() == 1)
                    AppController.getInstance().doCreateAlarm(0, null, this, alarmarray.get(i), System.currentTimeMillis());
                Log.d(TAG, "setAssets: alarmSet" + i);
            }
        } else {
            findViewById(R.id.tvEmpty).setVisibility(View.VISIBLE);
        }
    }
}
