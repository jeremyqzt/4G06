package com.example.jeremy.androidwearheartrate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import static android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE;
import static android.hardware.Sensor.TYPE_PROXIMITY;
import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_GYROSCOPE;
import static android.hardware.Sensor.TYPE_LIGHT;
import static android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY;


public class MobileSensorEventListener  implements SensorEventListener {
    private Sensor mTemperature, mProximity, mAcceleration, mGyroscope, mLight, mHumid, mrot;
    private SensorManager mSensorManager0, mSensorManager1,mSensorManager2,mSensorManager3,mSensorManager4,mSensorManager5, mSensorManager;

    private String TAG = "MobileSensorEventListener";
    private float m_Proximity;
    private float m_light;
    private float m_temperature;
    private float m_ax, m_ay, m_az;
    private float m_gx, m_gy, m_gz;
    private float m_humid;

    Context context;

    public MobileSensorEventListener(Context context) {
        this.context = context;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mTemperature = mSensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);
        mProximity = mSensorManager.getDefaultSensor(TYPE_PROXIMITY);
        mAcceleration = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        mLight = mSensorManager.getDefaultSensor(TYPE_LIGHT);
        mHumid = mSensorManager.getDefaultSensor(TYPE_RELATIVE_HUMIDITY);
        mrot = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        mSensorManager.registerListener(this, this.mTemperature, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mProximity, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mAcceleration, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mGyroscope, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mLight, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mHumid, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mrot, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        Log.d(TAG, "Required to Work");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            m_temperature = sensorEvent.values[0];
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY) {
            m_Proximity = sensorEvent.values[0];
        }else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            m_ax = sensorEvent.values[0];
            m_ay = sensorEvent.values[1];
            m_az = sensorEvent.values[2];
        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            m_gx = sensorEvent.values[0];
            m_gy = sensorEvent.values[1];
            m_gz = sensorEvent.values[2];
        }else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            m_light = sensorEvent.values[0];
        }else if (sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            m_humid = sensorEvent.values[0];
        }

    }


    public float getProx(){
        return m_Proximity;
    }
    public float getLight(){
        return m_light;
    }
    public float getHumid(){
        return m_humid;
    }
    public float[] getGyroscope(){
        float [] ret = {m_gx, m_gy, m_gz};
        return ret;
    }
    public float [] getAcceleration(){
        float [] ret = {m_ax, m_ay, m_az};
        return ret;
    }
    public float getTemperature(){
        return m_temperature;
    }

    public void unRegister(){
        mSensorManager.unregisterListener(this);
    }
}


