package com.example.jeremy.androidwearheartrate.blink;

import android.util.Log;

/**
 * Created by kevind on 2017-01-25.
 */

public class Blink {

    private int wearableSensors;
    private int mobileSensors;
    private int vehicleSensors;
    private boolean openCVConnection;
    private String recommendation;

    public Blink(int nwearables, int nmsensors, int nvsensors, boolean hasOpenCV){
        wearableSensors = nwearables;
        mobileSensors = nmsensors;
        vehicleSensors = nvsensors;
        openCVConnection = hasOpenCV;
    }

    public int getWearableSensors(){
        return wearableSensors;
    }

    public void setWearableSensors(int sensors){
        wearableSensors = sensors;
    }

    public int getMobileSensors(){
        return mobileSensors;
    }

    public void setMobileSensors(int sensors){
        mobileSensors = sensors;
    }

    public int getVehicleSensors(){
        return vehicleSensors;
    }

    public void setVehicleSensors(int sensors){
        vehicleSensors = sensors;
    }

    class UpdateLog implements Runnable {
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("Update Thread", "Running now");
        }
    }



    public void startSystem(){
        new Thread(new UpdateLog()).start();
    }

}
