package com.example.jeremy.androidwearheartrate.task;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;

/**
 * Created by kevind on 2017-03-08.
 */

public class progressBarTask implements Runnable {
    private int total;
    private int progress;
    IconRoundCornerProgressBar progressBar;

    public progressBarTask(int progress, int total, IconRoundCornerProgressBar progressBar){
        this.progress = progress;
        this.total = total;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        setProgress(100* progress / total, progressBar);
    }

    public void setProgress(int progress){
        this.progress = progress;
    }

    public void setTotalAlert(int totalprogress){
        total = totalprogress;
    }

    public int getProgress(){
        return progress;
    }

    public int getTotal(){
        return total;
    }

    public void incrementProgress(){
        progress++;
    }

    public void incrementTotal(){
        total++;
    }

    private void setProgress(int value, IconRoundCornerProgressBar bar) {
        bar.setProgress(value);
    }
}
