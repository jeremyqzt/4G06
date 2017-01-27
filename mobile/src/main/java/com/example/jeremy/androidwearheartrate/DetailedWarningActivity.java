package com.example.jeremy.androidwearheartrate;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.warnings.BadWarning;
import com.example.jeremy.androidwearheartrate.warnings.NeutralWarning;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.example.jeremy.androidwearheartrate.warnings.NeutralWarning;

public class DetailedWarningActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_warning);

        Intent intent = getIntent();
        String id = intent.getStringExtra(MainActivity.WARNING_ID);
        TextView textView = (TextView) findViewById(R.id.warning_details);
        ArcProgress myArc = (ArcProgress) findViewById(R.id.heart_rate);

        NeutralWarning nWarn = new NeutralWarning();
        BadWarning bWarn = new BadWarning();

        int heartRate;
        String message;
        if(id.contains("High")) {
            heartRate = 120;
            message = nWarn.getResponse();
        } else {
            heartRate = 30;
            message = bWarn.getResponse();
        }

        textView.setText(message);
        myArc.setProgress(heartRate);
    }
}
