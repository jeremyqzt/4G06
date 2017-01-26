package com.example.jeremy.androidwearheartrate;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.example.jeremy.androidwearheartrate.warnings.Warning;
import com.example.jeremy.androidwearheartrate.warnings.WarningIdKeys;

public class DetailedWarningActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_warning);

        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.WARNING_ID);
        TextView textView = (TextView) findViewById(R.id.warning_details);
        ArcProgress myArc = (ArcProgress) findViewById(R.id.heart_rate);

        Warning globalWarning = new Warning(WarningIdKeys.neutral);

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
}
