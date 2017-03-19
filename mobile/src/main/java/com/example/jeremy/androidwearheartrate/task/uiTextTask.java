package com.example.jeremy.androidwearheartrate.task;

import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.HealthFragment;

/**
 * Created by kevind on 2017-03-06.
 */

public class uiTextTask implements Runnable {
    String text;
    TextView tv;

    public uiTextTask(String text, TextView tv){
        this.text = text;
        this.tv = tv;
    }

    @Override
    public void run() {
        tv.setText(text);
    }

    public void setText(String newText){
        text = newText;
    }
}
