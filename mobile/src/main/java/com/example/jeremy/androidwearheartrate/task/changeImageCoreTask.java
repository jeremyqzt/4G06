package com.example.jeremy.androidwearheartrate.task;

import android.graphics.Color;
import android.widget.ImageView;

/**
 * Created by kevind on 2017-03-07.
 */

public class changeImageCoreTask implements Runnable {
    private String color;
    private ImageView iv;

    public changeImageCoreTask(String color, ImageView iv){
        this.color = color;
        this.iv = iv;
    }

    @Override
    public void run() {
        iv.setColorFilter(Color.parseColor(color));
    }

    public void setColor(String colorchange){
        color = colorchange;
    }
}
