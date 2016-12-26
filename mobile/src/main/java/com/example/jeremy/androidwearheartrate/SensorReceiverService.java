//package com.example.jeremy.androidwearheartrate;
//
//
//import android.content.Context;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Toast;
//
//
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.wearable.DataEvent;
//import com.google.android.gms.wearable.DataEventBuffer;
//import com.google.android.gms.wearable.DataItem;
//import com.google.android.gms.wearable.DataMap;
//import com.google.android.gms.wearable.DataMapItem;
//import com.google.android.gms.wearable.Node;
//import com.google.android.gms.wearable.Wearable;
//import com.google.android.gms.wearable.WearableListenerService;
//
//import java.util.Arrays;
//import java.util.concurrent.TimeUnit;
//
//
//public class SensorReceiverService extends WearableListenerService {
//    private static final String TAG = "SensorReceiverService";
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//
//    }
//
//    @Override
//    public void onPeerConnected(Node peer) {
//        super.onPeerConnected(peer);
//
//        Log.i(TAG, "Connected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
//    }
//
//    @Override
//    public void onPeerDisconnected(Node peer) {
//        super.onPeerDisconnected(peer);
//
//        Log.i(TAG, "Disconnected: " + peer.getDisplayName() + " (" + peer.getId() + ")");
//    }
//
//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.d(TAG, "onDataChanged()");
//        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
//                .addApi(Wearable.API)
//                .build();
//
//        ConnectionResult connectionResult =
//                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
//
//        if (!connectionResult.isSuccess()) {
//            Log.d(TAG, "Failed to connect to GoogleApiClient.");
//            return;
//        }
//
//        for (DataEvent dataEvent : dataEvents) {
//            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
//                DataItem dataItem = dataEvent.getDataItem();
//                Uri uri = dataItem.getUri();
//                String path = uri.getPath();
//
//                if (path.startsWith("/sensors/")) {
//                    unpackSensorData(
//                            Integer.parseInt(uri.getLastPathSegment()),
//                            DataMapItem.fromDataItem(dataItem).getDataMap()
//                    );
//                }
//            }
//        }
//    }
//
//    private void unpackSensorData(int sensorType, DataMap dataMap) {
//        int accuracy = dataMap.getInt("Accuracy");
//        long timestamp = dataMap.getLong("Time");
//        float[] values = dataMap.getFloatArray("Value");
//        Context context;
//        Log.d("THIS", "Received sensor data " + sensorType + " = " + Arrays.toString(values));
//        Toast.makeText(SensorReceiverService.this, Arrays.toString(values),Toast.LENGTH_LONG).show();
//
//        //sensorManager.addSensorData(sensorType, accuracy, timestamp, values);
//    }
//}