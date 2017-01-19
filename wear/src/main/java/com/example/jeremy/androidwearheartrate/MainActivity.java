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
import java.math.*;

import static android.hardware.Sensor.*;
public class MainActivity extends Activity implements SensorEventListener{


    private static final String TAG = MainActivity.class.getName();

    private DeviceClient client;

    private GoogleApiClient mGoogleApiClient;
    private TextView Heartrate;
    private TextView accuracy;
    private TextView sensorInformation;
    private TextView x;
    private TextView y;
    private TextView z;
    private TextView xRotate;
    private TextView yRotate;
    private TextView zRotate;
    private TextView illuminance;
    private TextView ambTemp;


    private Sensor mHeartRateSensor;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLight;
    private Sensor mAmbientTemperature;



    private SensorManager mSensorManager;

    private CountDownLatch latch;
    private float previous = 0;

    // Create a constant to convert nanoseconds to seconds.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Heartrate = (TextView) findViewById(R.id.rate);
        Heartrate.setText("Reading...");
        x = (TextView) findViewById(R.id.x);
        y = (TextView) findViewById(R.id.y);
        z = (TextView) findViewById(R.id.z);

        xRotate = (TextView) findViewById(R.id.xRotate);
        yRotate = (TextView) findViewById(R.id.yRotate);
        zRotate = (TextView) findViewById(R.id.zRotate);

        illuminance = (TextView) findViewById(R.id.illum);

        ambTemp = (TextView) findViewById(R.id.ambTemp);

        accuracy = (TextView) findViewById(R.id.accuracy);
        sensorInformation = (TextView) findViewById(R.id.sensor);

        latch.countDown();

        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);


        mHeartRateSensor = mSensorManager.getDefaultSensor(TYPE_HEART_RATE);
        mAccelerometer = mSensorManager.getDefaultSensor(TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(TYPE_GYROSCOPE);
        mLight = mSensorManager.getDefaultSensor(TYPE_LIGHT);
        mAmbientTemperature = mSensorManager.getDefaultSensor(TYPE_AMBIENT_TEMPERATURE);

        mSensorManager.registerListener(this, this.mHeartRateSensor, 1);
        mSensorManager.registerListener(this, this.mAccelerometer, 1);
        mSensorManager.registerListener(this, this.mGyroscope, 1);
        mSensorManager.registerListener(this, this.mLight, 1);
        mSensorManager.registerListener(this, this.mAmbientTemperature, 1);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        try {
            latch.await();
            if(sensorEvent.sensor.getType() == Sensor.TYPE_HEART_RATE && sensorEvent.values[0] > 0){
                Log.d(TAG, "sensor event: " + sensorEvent.accuracy + " = " + sensorEvent.values[0]);
                Heartrate.setText(String.valueOf(sensorEvent.values[0]));
                accuracy.setText("Accuracy: "+sensorEvent.accuracy);
                sensorInformation.setText(sensorEvent.sensor.toString());
                previous = sensorEvent.values[0];
                client.sendSensorData(sensorEvent.sensor.getType(), sensorEvent.accuracy, sensorEvent.timestamp, sensorEvent.values);
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                x.setText("x:" + Float.toString(sensorEvent.values[0]) + " m/s" + Html.fromHtml("<sup>2</sup>"));
                y.setText("y:" + Float.toString(sensorEvent.values[1]) + " m/s" + Html.fromHtml("<sup>2</sup>"));
                z.setText("z:" + Float.toString(sensorEvent.values[2]) + " m/s" + Html.fromHtml("<sup>2</sup>"));
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

                xRotate.setText("x:" + Float.toString(sensorEvent.values[0] * (float)Math.PI * 2) + " deg/s");
                yRotate.setText("y:" + Float.toString(sensorEvent.values[1] * (float)Math.PI * 2) + " deg/s");
                zRotate.setText("z:" + Float.toString(sensorEvent.values[2] * (float)Math.PI * 2) + " deg/s");

            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT){
                illuminance.setText("Illuminance = " + Float.toString(sensorEvent.values[0]) + " lx");
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
                ambTemp.setText("Temperature = " + Float.toString(sensorEvent.values[0]) + " C");
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        Log.d(TAG, "accuracy changed: " + i);
    }

    @Override
    protected void onDestroy() {
        super.onStop();
        mSensorManager.unregisterListener(this, this.mHeartRateSensor);
        mSensorManager.unregisterListener(this, this.mAccelerometer);
        mSensorManager.unregisterListener(this, this.mGyroscope);
        mSensorManager.unregisterListener(this, this.mLight);
        mSensorManager.unregisterListener(this, this.mAmbientTemperature);
    }

}