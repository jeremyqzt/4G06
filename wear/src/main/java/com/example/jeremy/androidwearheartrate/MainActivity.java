package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.view.WatchViewStub;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.hardware.Sensor.TYPE_ACCELEROMETER;
import static android.hardware.Sensor.TYPE_HEART_RATE;
import static android.hardware.Sensor.TYPE_LIGHT;

public class MainActivity extends Activity implements SensorEventListener{


    private static final String TAG = MainActivity.class.getName();

    private DeviceClient client;

    private GoogleApiClient mGoogleApiClient;
    private TextView rate;
    private TextView accuracy;
    private TextView sensorInformation;
    private Sensor mHeartRateSensor, mLightSensor, mAccelerationSensor;
    private SensorManager mSensorManager;
    private float previous = 0;
    private float currentHR, currentLight;
    private float[] currentAcc = {0,0,0};
    private float[] concat = {0,0,0,0,0};

    private TextView Heartrate;

    private TextView x;
    private TextView y;
    private TextView z;
    private TextView xRotate;
    private TextView yRotate;
    private TextView zRotate;
    private TextView illuminance;
    private TextView ambTemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_my);
        client = DeviceClient.getInstance(this);


        accuracy = (TextView) findViewById(R.id.accuracy);
        sensorInformation = (TextView) findViewById(R.id.sensor);

        mSensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        mHeartRateSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
        mLightSensor = mSensorManager.getDefaultSensor(TYPE_LIGHT);
        mAccelerationSensor = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER);

        mSensorManager.registerListener(this, this.mHeartRateSensor, 10*SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mLightSensor, 25*SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, this.mAccelerationSensor, 25*SensorManager.SENSOR_DELAY_UI);

//        x = (TextView) findViewById(R.id.x);
//        y = (TextView) findViewById(R.id.y);
//        z = (TextView) findViewById(R.id.z);
//
//        illuminance = (TextView) findViewById(R.id.illum);
//
//
//        accuracy = (TextView) findViewById(R.id.accuracy);
//        sensorInformation = (TextView) findViewById(R.id.sensor);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;

        if (sensor.getType() == Sensor.TYPE_HEART_RATE) {
            currentHR = sensorEvent.values[0];
//            accuracy.setText("Accuracy: " + sensorEvent.accuracy);
//            sensorInformation.setText(sensorEvent.sensor.toString());

        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLight = sensorEvent.values[0];

        } else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            currentAcc[0] = sensorEvent.values[0];
            currentAcc[1] = sensorEvent.values[1];
            currentAcc[2] = sensorEvent.values[2];

        }
        concat[0] = currentHR;
        concat[1] = currentLight;
        concat[2] = currentAcc[0];
        concat[3] = currentAcc[1];
        concat[4] = currentAcc[2];

        Log.d("Concat0", String.valueOf(concat[0]));
        Log.d("Concat1", String.valueOf(concat[1]));
        Log.d("Concat2", String.valueOf(concat[2]));



        if (concat[1] > 0) {
            Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
            previous = sensorEvent.values[0];
            client.sendSensorData(sensorEvent.sensor.getType(), sensorEvent.accuracy, sensorEvent.timestamp, concat);
        }
        if (concat[0] > 0){
            sensorInformation.setText("All Sensor Data Obtained!");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        if (sensor.getType() == TYPE_HEART_RATE){
            Log.d(TAG, "accuracy changed: " + i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

}