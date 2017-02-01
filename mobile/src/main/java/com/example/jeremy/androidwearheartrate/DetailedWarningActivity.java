package com.example.jeremy.androidwearheartrate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;

public class DetailedWarningActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_warning);

        Intent intent = getIntent();
        final String id = intent.getStringExtra(MainActivity.WARNING_ID);
        final TextView textView = (TextView) findViewById(R.id.warning_details);
        final ArcProgress myArc = (ArcProgress) findViewById(R.id.heart_rate);

        FirebaseDB instance = new FirebaseDB();
        instance.onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(String value) {
                int heartRate;
                String message;
                if(id.contains("High")) {
                    heartRate = 120;
                    message = "High heart rate detected, have you been running? If not, don't drive";
                } else {
                    heartRate = 30;
                    message = "Low heart rate detected, showing abnormal symptoms";
                }

                textView.setText(message);
                myArc.setProgress(heartRate);
            }

            @Override
            public void onFail(String value) {

            }
        });
    }
}
