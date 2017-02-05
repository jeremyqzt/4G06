package com.example.jeremy.androidwearheartrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.blink.Blink;
import com.example.jeremy.androidwearheartrate.blink.WarningListChangeListener;
import com.example.jeremy.androidwearheartrate.warnings.Warning;
import com.example.jeremy.androidwearheartrate.warnings.WarningMessageKeys;
import com.firebase.client.Firebase;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

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
    private float m_prox, m_temp, m_light;
    private float [] m_gyro, m_accel;
    private float [] concat = {0,0,0,0};
    private float [] watchgyro = {0,0,0};
    private int heartrate;
    private float light;
    Handler handler = new Handler();
    ArcProgress myArc;
    private int currentRate;
    Blink myBlink;
    ArrayList<Warning> pastWarnings = new ArrayList<>();

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnableCode, 1000);

            //m_temp = listener.getLight();
            m_prox = listener.getProx();
            m_gyro = listener.getGyroscope();
            m_accel = listener.getAcceleration();
            m_light = listener.getLight();

            test.setText(String.valueOf(m_gyro[1]));

            Firebase ref = new Firebase(Config.FIREBASE_URL);

            InformationSet inst = new InformationSet();


            String testing = String.valueOf(heartrate) +";" + String.valueOf(m_prox) +";" + String.valueOf(m_light);
            testWarn.setText(testing);
            concat[0] = heartrate;
            concat[1] = m_prox;
            concat[2] = m_light;
            concat[3] = m_temp;

            inst.setAcc(m_accel);
            inst.setGyro(m_gyro);
            inst.setHeartProxLight(concat);
            inst.setWatchLight(light);
            inst.setWatchGyro(watchgyro);

            ref.child("InformationSet").setValue(inst);

        }
    };
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            int temperature = intent.getIntExtra("temperature", 0);
            m_temp = (float)temperature / 10;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
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

        final ArrayAdapter lvAdapter = new ArrayAdapter<Warning>(this,
                R.layout.activity_warninglist, pastWarnings);
        //create listview with adapter
        ListView listView = (ListView) findViewById(R.id.list_warning);
        listView.setAdapter(lvAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                String selectedWarning = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(getApplicationContext(),"clicked", Toast.LENGTH_LONG).show();
                changeToDetailsScreen(view, selectedWarning);
            }
        });
        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        //Initialize Blink core system
        int numOfWearables = 1;
        int numOfMobiles = 5;
        int numOfVehicles = 1;
        boolean hasOpenCV = false;
        myBlink = new Blink(numOfWearables,numOfMobiles,numOfVehicles,hasOpenCV);
        myBlink.setWarningListener(new WarningListChangeListener() {
            @Override
            public void onWarningChange(List<Warning> wList) {
                pastWarnings.clear();
                pastWarnings.addAll(wList);
                Log.d("Update",""+pastWarnings.size());
                lvAdapter.notifyDataSetChanged();
            }
        });
        myBlink.startSystem();
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
    protected void onDestroy() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
        listener.unRegister();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                rate.setText(String.valueOf(dataMap.getFloatArray(KEY)[1]));
                float [] holder = dataMap.getFloatArray(KEY);
                heartrate = Math.round(holder[0]);
                light = Math.round(holder[1]);
                watchgyro [0] = holder [2];
                watchgyro [1] = holder [3];
                watchgyro [2] = holder [4];

                myArc.setProgress(Math.round(holder[0]));
                myBlink.updateMobileValuesFromDevice(heartrate, m_prox,m_temp,m_light,m_accel);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void changeToDrivingScreen(View view){
        //Switch to Driving activity
        Intent intent = new Intent(this, DrivingActivity.class);
        startActivity(intent);
    }

    public void changeToDetailsScreen(View view, String id) {
        //Go to detailed warning screen
        //Need to pass proper params
        Intent intent = new Intent(this, DetailedWarningActivity.class);
        intent.putExtra(WARNING_ID, id);
        startActivity(intent);
    }

    public void changeHeartRate(View view){
        //change value of heartrate circle
        //UI auto changes when setProgress is called
        //Change to read heart rate
        currentRate = myArc.getProgress();
        currentRate++;
        myArc.setProgress(currentRate);
    }

    public void fillWarnings(){
        pastWarnings.add(new Warning(0, WarningMessageKeys.bright));
    }
}