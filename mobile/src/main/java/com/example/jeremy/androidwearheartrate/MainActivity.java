package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.util.Log;

import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import android.os.Handler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class MainActivity extends Activity  implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
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
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";


    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnableCode, 250);

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

        registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    sendMessage(node.getId());
                }
            }
        });
    }


    private void sendMessage(String node) {
        Wearable.MessageApi.sendMessage(mGoogleApiClient , node , START_ACTIVITY_PATH , new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (!sendMessageResult.getStatus().isSuccess()) {
                    Log.e("GoogleApi", "Failed to send message with status code: "
                            + sendMessageResult.getStatus().getStatusCode());
                }
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
                rate.setText(String.valueOf(dataMap.getFloatArray(KEY)[0]));
                float [] holder = dataMap.getFloatArray(KEY);
                heartrate = Math.round(holder[0]);
                light = Math.round(holder[1]);
                watchgyro [0] = holder [2];
                watchgyro [1] = holder [3];
                watchgyro [2] = holder [4];

            }

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}