package com.example.jeremy.androidwearheartrate.blink;

import android.util.Log;

import com.example.jeremy.androidwearheartrate.warnings.Warning;
import com.example.jeremy.androidwearheartrate.warnings.WarningMessageKeys;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevind on 2017-01-25.
 */

public class Blink {

    private int wearableSensors;
    private int mobileSensors;
    private int vehicleSensors;
    private boolean openCVConnection;
    private String recommendation;
    private FirebaseDatabase database;
    private CarDataSet cdSet;
    private int logIndex;
    private List<Warning> warningList;
    //can wrap in another object
    private float m_prox;
    private float m_temp;
    private float m_light;
    private float[] m_accel;
    private int heartrate;
    private WarningListChangeListener listener;

    public Blink(int nwearables, int nmsensors, int nvsensors, boolean hasOpenCV){
        wearableSensors = nwearables;
        mobileSensors = nmsensors;
        vehicleSensors = nvsensors;
        openCVConnection = hasOpenCV;
        database = FirebaseDatabase.getInstance();
        warningList = new ArrayList<>();
        m_prox = 0;
        m_temp = 0;
        m_light = 0;
        m_accel = new float[]{0,0,0};
        heartrate = 0;
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

//    class UpdateLog implements Runnable {
//        public void run() {
//            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//            DatabaseReference myRef = database.getReference();
//            while(true) {
//                try {
//                    Thread.sleep(60000);
//                    String pageIndex = Integer.toString(logIndex);
//                    myRef.child("driverLogs").child(pageIndex).setValue(cdSet);
//                    Log.d("Update", "Post to driverLogs/"+pageIndex);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    public void startSystem(){
        //Initialize empty set of car data
        CarDataSet emptyData = new CarDataSet(0);
        cdSet = emptyData;

//        new Thread(new UpdateLog()).start();

        //initiate event listener for log indexing prepare for posting logs to cloud
        updateChildIndex();

        //initiate event listen for simulation values
        updateCarValuesFromCloud();

    }

    /**
     * Grabs car simulation values from cloud
     * Only needs to be called once as it adds an event listener
     * to the Firebase connection
     * Simulation values will be real-time
     */
    public void updateCarValuesFromCloud(){
        //Get DataBase Reference
        DatabaseReference myRef = database.getReference();
        //Attach Event Listener to my DataBase reference
        ValueEventListener carDataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Post previous logs to cloud
                updateCloudLogs();

                //get new simulation data
                Log.d("Update","Grabbing Simulation Data");
                cdSet = dataSnapshot.getValue(CarDataSet.class);
                //calculate and update warnings
                fillWarningList();
                listener.onWarningChange(warningList);
                //getLogs();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting failed, log a message
            }
        };
        myRef.child("simulation").addValueEventListener(carDataListener);
    }

    /**
     * Updates the index for pushing driver logs to the cloud
     * Only needs to be called once as it adds an event listener
     * to the Firebase connection
     */
    public void updateChildIndex(){
        //Get DataBase Reference
        DatabaseReference myRef = database.getReference();
        ValueEventListener indexLogListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mydbIndex = Math.round(dataSnapshot.getChildrenCount());
                Log.d("Update","Total logCount = "+mydbIndex);
//                if (mydbIndex != null) {
                    logIndex = mydbIndex;
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRef.child("driverLogs").addValueEventListener(indexLogListener);
    }

    public void updateCloudLogs(){
        DatabaseReference myRef = database.getReference();
        String pageIndex = Integer.toString(logIndex);
        myRef.child("driverLogs").child(pageIndex).setValue(cdSet);
        Log.d("Update", "Post to driverLogs/"+pageIndex);
    }

    public void getLogs(){
        Log.d("Log","Braking"+ cdSet.isBraking());
        Log.d("Log","ExcessBraking"+ cdSet.isExcessBraking());
        Log.d("Log","HeadLights"+ cdSet.isHeadLights());
        Log.d("Log","Turning"+ cdSet.isTurning());
        Log.d("Log","Wiping"+ cdSet.isWiping());
        Log.d("Log","AccX"+ cdSet.getAccelerationx());
        Log.d("Log","AccZ"+ cdSet.getAccelerationz());
        Log.d("Log","dProx"+ cdSet.isDangerousProximity());
        Log.d("Log","behind"+ cdSet.getProximity().get("behind"));
        Log.d("Log","front"+ cdSet.getProximity().get("front"));
        Log.d("Log","left"+ cdSet.getProximity().get("left"));
        Log.d("Log","right"+ cdSet.getProximity().get("right"));
        Log.d("Log","RoadType"+ cdSet.getRoadType());
        Log.d("Log","Speed"+ cdSet.getSpeed());
        Log.d("Log","SpeedVariance"+ cdSet.getSpeedVariance());
    }

    public void fillWarningList(){
        //DO ALL CALCULATIONS
        List<Warning> newWarnings = new ArrayList<>();
        if(cdSet.getAccelerationz() > 10) {
            newWarnings.add(new Warning(Warning.Alert,WarningMessageKeys.highAccel));
            if (cdSet.getAccelerationz() > 50) {
                if (cdSet.getAccelerationz() > 80 && cdSet.getRoadType().equals("highway")) {
                    newWarnings.add(new Warning(Warning.Caution, WarningMessageKeys.slowHighWay));
                } else if (cdSet.getRoadType().equals("city")) {
                    newWarnings.add(new Warning(Warning.Caution, WarningMessageKeys.slowCity));
                } else if (cdSet.getAccelerationz() > 100 && cdSet.getRoadType().equals("pcity")) {
                    newWarnings.add(new Warning(Warning.Caution, WarningMessageKeys.slowPcity));
                }
            }
        }

//        if(Math.sqrt(cdSet.get))
        if(m_temp > 38){
            newWarnings.add(new Warning(Warning.Caution,WarningMessageKeys.heat));
        }

        if(m_temp < 0){
            newWarnings.add(new Warning(Warning.Caution,WarningMessageKeys.cold));
        }

        if(cdSet.isWiping()){
            newWarnings.add(new Warning(Warning.Caution,WarningMessageKeys.wiper));
            if(cdSet.getAccelerationz() > 10 ){
                newWarnings.add(new Warning(Warning.Caution,WarningMessageKeys.wiperAccel));
            }
        }

        if(m_light < 500 && heartrate < 70) {
            newWarnings.add(new Warning(Warning.Caution,WarningMessageKeys.fatigued));
        }else if(m_light > 40000) {
            if (m_light > 100000) {
                newWarnings.add(new Warning(Warning.Alert, WarningMessageKeys.superBright));
            } else {
                newWarnings.add(new Warning(Warning.Caution, WarningMessageKeys.bright));
            }
        }

        if(cdSet.isDangerousProximity()){
            newWarnings.add(new Warning(Warning.Alert,WarningMessageKeys.prox));
        }
        compareWarnings(newWarnings);

        warningList = newWarnings;
    }

    public List<Warning> getWarningList(){
        return warningList;
    }

    public void updateMobileValuesFromDevice(int hr, float prox, float temp, float light, float[] accel){
        heartrate = hr;
        m_prox = prox;
        m_temp = temp;
        m_light = light;
        m_accel = accel;
    }

    public void setWarningListener(final WarningListChangeListener listener){
        this.listener = listener;
    }

    public void compareWarnings(List<Warning> newWarnings){
        Log.d("List","====Compare Warnings====");

        for (Warning w : warningList) {
            Log.d("List", "Current warnings: " + w.getResponse());
        }

        Log.d("List","=============");

        for (Warning w : newWarnings) {
            Log.d("List", "New warnings: " + w.getResponse());
        }




    }
}
