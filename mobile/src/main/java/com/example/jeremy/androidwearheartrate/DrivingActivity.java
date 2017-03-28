package com.example.jeremy.androidwearheartrate;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import static android.graphics.Color.parseColor;

public class DrivingActivity extends FragmentActivity {

    final String TAG = DrivingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);
        final ProgressBar heartRateBar = (ProgressBar) findViewById(R.id.heart_rate_bar);
        final ProgressBar faceRateBar = (ProgressBar) findViewById(R.id.face_bar);
        final ProgressBar readyRateBar = (ProgressBar) findViewById(R.id.readiness_bar);
        final TextView warningBox = (TextView) findViewById(R.id.warning_box);

        FirebaseDB.getInstance("profile").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Map<String, Long>> vals = (ArrayList<Map<String, Long>>) value;
                for(Map<String, Long> each : vals){
                    heartRateBar.setProgress(new BigDecimal(each.get("readiness")).intValue(), true);
                    if(new BigDecimal(each.get("readiness")).intValue() < 20){
                        warningBox.setBackgroundColor(parseColor("RED"));
                    }else {
                        warningBox.setBackgroundColor(parseColor("GREEN"));
                    }
                    faceRateBar.setProgress(new BigDecimal(each.get("facial_status")).intValue(), true);
                    readyRateBar.setProgress(new BigDecimal(each.get("heart_rate")).intValue(), true);
                    Log.e(TAG, "HERE");

                }
                Log.e(TAG, vals.toString());
            }

            @Override
            public void onFail(String value) {
                Log.e(TAG, value);
            }
        });
    }


}
