package com.example.jeremy.androidwearheartrate.task;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by kevind on 2017-03-06.
 */

public class flashCoreTask implements Runnable{
    ImageView core;
    public flashCoreTask(ImageView core){
        this.core = core;
    }

    @Override
    public void run() {
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(800);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
        core.startAnimation(animation);
    }
}
