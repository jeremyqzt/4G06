package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_PROXIMITY;


public class MobileSensorEventListener implements SensorEventListener {
    private Sensor mProximity;
    private SensorManager mSensorManager;
    private String TAG = "MobileSensorEventListener";
    private float m_Proximity;
    Context context;

    public MobileSensorEventListener(Context context){
        this.context = context;
        mSensorManager = (SensorManager)context.getSystemService( Context.SENSOR_SERVICE );
        mProximity = mSensorManager.getDefaultSensor(TYPE_PROXIMITY);
        mSensorManager.registerListener(this, this.mProximity, 1);
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        m_Proximity = sensorEvent.values[0];
        Log.d(TAG,String.valueOf(sensorEvent.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public float getProx(){
        return m_Proximity;
    }
}


