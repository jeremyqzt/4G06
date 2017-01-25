package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import static android.hardware.Sensor.TYPE_HEART_RATE;
import static android.hardware.Sensor.TYPE_PROXIMITY;
import static com.google.android.gms.wearable.DataMap.TAG;
import android.os.Handler;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity  implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public final static String WARNING_ID = "Detailed.Warning.ID";
    private GoogleApiClient mGoogleApiClient;
    private final String TAG = "Main";
    private MobileSensorEventListener listener;
    TextView test,rate, testWarn ;
    private static final String KEY = "Value";
    private float m_prox, m_temp;
    private float [] m_gyro, m_accel;
    Handler handler = new Handler();
    ArcProgress myArc;
    private int currentRate;
    ArrayList<String> pastWarnings = new ArrayList<>();

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnableCode, 1000);
            m_temp = listener.getLight();

            m_prox = listener.getProx();
            m_gyro = listener.getGyroscope();
            m_accel = listener.getAcceleration();
            test.setText(String.valueOf(m_gyro[0]));
            testWarn.setText(String.valueOf(m_accel[0]));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        rate = (TextView) findViewById(R.id.aaa);
        test = (TextView) findViewById(R.id.bbb);
        testWarn = (TextView) findViewById(R.id.ccc);
        listener = new MobileSensorEventListener(this);
        handler.post(runnableCode);

        myArc = (ArcProgress) findViewById(R.id.arc_progress);
        currentRate = myArc.getProgress();

        fillWarnings();

        ArrayAdapter lvAdapter = new ArrayAdapter<String>(this,
                R.layout.activity_warninglist, pastWarnings);
        //create listview with adapter
        ListView listView = (ListView) findViewById(R.id.list_warning);
        listView.setAdapter(lvAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String selectedWarning = (String) adapterView.getItemAtPosition(i);
//                Toast.makeText(getApplicationContext(),selectedWarning, Toast.LENGTH_LONG).show();
                changeToDetailsScreen(view, selectedWarning);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
        listener.unRegister();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                rate.setText(String.valueOf(dataMap.getInt(KEY)));
                myArc.setProgress(dataMap.getInt(KEY));
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void changeScreen(View view){
        //Switch to Driving activity
        Intent intent = new Intent(this, DrivingActivity.class);
        startActivity(intent);
    }

    public void changeToDetailsScreen(View view, String id) {
        Intent intent = new Intent(this, DetailedWarningActivity.class);
        intent.putExtra(WARNING_ID, id);
        startActivity(intent);
    }

    public void changeHeartRate(View view){
        //change value of heartrate circle
        //UI auto changes when setProgress is called
        currentRate = myArc.getProgress();
        currentRate++;
        myArc.setProgress(currentRate);
    }

    public void fillWarnings(){
        for (int i = 0;i < 4;i++) {
            pastWarnings.add("High Heartrate");
        }
        for (int i = 0;i < 5;i++) {
            pastWarnings.add("Low Heartrate");
        }
    }
}