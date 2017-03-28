package com.example.jeremy.androidwearheartrate;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.blink.Blink;
import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;
import com.firebase.client.Firebase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
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

import java.util.ArrayList;

import static java.lang.StrictMath.abs;

public class SummaryFragment extends Fragment implements
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
    Blink myBlink;
    private LineChart mChart;
    private Thread chartThread;
    private TextView heartRateText;
    private TextView lightText;
    private TextView tempText;
    private Bundle bundle;
    public static final String START_ACTIVITY_PATH = "/start/MainActivity";

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

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(runnableCode, 1000);

            //m_temp = listener.getLight();
            m_prox = listener.getProx();
            m_gyro = listener.getGyroscope();
            m_accel = listener.getAcceleration();
            m_light = listener.getLight();

//            test.setText(String.valueOf(m_gyro[1]));

            Firebase ref = new Firebase(Config.FIREBASE_URL);

            InformationSet inst = new InformationSet();

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

            lightText.setText(Float.toString(m_light) + " Lux");
            tempText.setText(Float.toString(m_temp) + " C");



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Context mActivity = getActivity();
        View view = inflater.inflate(R.layout.summary_page, container, false);

        bundle = this.getArguments();

        Firebase.setAndroidContext(mActivity);
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
//        rate = (TextView) view.findViewById(R.id.aaa);
//        test = (TextView) view.findViewById(R.id.bbb);
//        testWarn = (TextView) view.findViewById(R.id.ccc);
        listener = new MobileSensorEventListener(mActivity);
        handler.post(runnableCode);

        mActivity.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                for (Node node : getConnectedNodesResult.getNodes()) {
                    sendMessage(node.getId());
                }
            }
        });
        //init Line Chart from XML
        mChart = (LineChart) view.findViewById(R.id.chart);

        // enable description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        // set an alternative background color
//        mChart.setBackgroundColor(Color.LTGRAY);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        //get legend
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.GRAY);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(false);
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setAxisMaximum(200f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        heartRateText = (TextView) view.findViewById(R.id.hrtext) ;
        lightText = (TextView) view.findViewById(R.id.lighttext);
        tempText = (TextView) view.findViewById(R.id.temptext);

//        feedData();

        FirebaseDB.getInstance("InformationSet/heartProxLight").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                float tempval = Float.parseFloat(vals.get(3).toString());
                float lightval = Float.parseFloat(vals.get(2).toString());
                float realHR = Float.parseFloat(vals.get(0).toString());
                lightText.setText(lightval + " Lux");
                tempText.setText(tempval + " C");
                addEntry(realHR);

            }

            @Override
            public void onFail(String value) {

            }
        });

        return view;
    }

    private void addEntry(float realHeart) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            float simHeart = (float) (Math.random() * 40) + 30f;
//            data.addEntry(new Entry(set.getEntryCount(),simHeart), 0);
            data.addEntry(new Entry(set.getEntryCount(),realHeart), 0);
            heartRateText.setText(Math.round(realHeart) + " BPM");
            bundle.putInt("heartrate",Math.round(simHeart));

            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(20);
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
//            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            mChart.moveViewToX(data.getXMax()-20);
//            mChart.invalidate();
//            mChart.animateX(3000, Easing.EasingOption.Linear);
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        set.setCubicIntensity(0.2f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setCircleColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(ColorTemplate.getHoloBlue());
        set.setValueTextSize(9f);
//        set.setDrawValues(false);
        return set;
    }


    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onStart() {
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
    public void onDestroy() {
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
                float [] holder = dataMap.getFloatArray(KEY);
                heartrate = Math.round(holder[0]);
                if (heartrate == 0){
                    heartrate = (int) (Math.random() * 5) +67 ;
                }
                light = Math.round(holder[1]);
                watchgyro [0] = holder [2];
                watchgyro [1] = holder [3];
                watchgyro [2] = holder [4];
                Log.d("This", "Triggered");
                Log.d("Time Testing", String.valueOf(watchgyro[0]) + "," + String.valueOf(watchgyro[1]) + "," + String.valueOf(watchgyro[2]));

                if (abs(holder[0]) > 30 ||  abs(holder[1]) > 30 || abs(holder[2]) > 30){ //Crash
                    Firebase ref = new Firebase(Config.FIREBASE_URL);
                    InformationSet inst = new InformationSet();
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
                    Log.d("This", "Triggered2");
                }

            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
