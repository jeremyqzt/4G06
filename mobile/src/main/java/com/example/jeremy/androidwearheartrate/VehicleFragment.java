package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.github.anastr.speedviewlib.ImageSpeedometer;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;

/**
 * Created by kevind on 2017-02-26.
 */

public class VehicleFragment extends Fragment{
    private Bundle bundle;
    private ImageSpeedometer speedMeter;
    private TextView speedText;
    private ImageSpeedometer accelMeter;
    private TextView accelText;
    private TextView aUnitText;
    private TextView endUnitText;
    private ImageView carImage;
    private TextView accel_brake;
    private ImageView climateImage;
    private TextView climate;
    private ImageView roadImage;
    private TextView roads;
    private ImageView weatherImage;
    private TextView weather;
    private Activity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.vehicle_page, container, false);
        bundle = this.getArguments();
        speedMeter = (ImageSpeedometer) view.findViewById(R.id.speedometer);
        speedText = (TextView) view.findViewById(R.id.speedtext);
        accelMeter = (ImageSpeedometer) view.findViewById(R.id.accelmeter);
        accelText = (TextView) view.findViewById(R.id.acceltext);
        aUnitText = (TextView) view.findViewById(R.id.accelunittext);
        endUnitText = (TextView) view.findViewById(R.id.endunittext);
        carImage = (ImageView) view.findViewById(R.id.carimage);
        accel_brake = (TextView) view.findViewById(R.id.accel_brake);
        climateImage = (ImageView) view.findViewById(R.id.climateimage);
        climate = (TextView) view.findViewById(R.id.climate);
        roadImage = (ImageView) view.findViewById(R.id.roadimage);
        roads = (TextView) view.findViewById(R.id.roads);
        weatherImage = (ImageView) view.findViewById(R.id.weatherimage);
        weather = (TextView) view.findViewById(R.id.weather);
        myActivity = getActivity();

        startListeners();

        return view;
    }

    private void startListeners(){
        //get Firebase listeners to alter image and text views;
        FirebaseDB.getInstance("Vehicle/Speed").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                gradualSpeedTo(speedMeter,Integer.parseInt(value.toString()),4000);
                speedText.setText(value.toString() + " KM/HOUR");
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Acceleration").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                gradualSpeedTo(accelMeter,Integer.parseInt(value.toString()),4000);
                accelText.setText(value.toString() + " KM");
                aUnitText.setText(Html.fromHtml("<sup>2</sup>"));
                endUnitText.setText("/HOUR");
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Braking").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value){
                if(value.toString().equals("1")){
                    accel_brake.setText("Braking");
                    carImage.setColorFilter(Color.parseColor("#689F38"));
                }else{
                    accel_brake.setText("Accelerating");
                    carImage.setColorFilter(Color.parseColor("#B0BEC5"));
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Climate").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                if(value.toString().equals("1")){
                    climate.setText("Climate On");
                    climateImage.setColorFilter(Color.parseColor("#F57F17"));
                }else{
                    climate.setText("Climate Off");
                    climateImage.setColorFilter(Color.parseColor("#424242"));
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Roadtype").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                if(value.toString().equals("1")){
                    roads.setText("Highway");
                    roadImage.setColorFilter(Color.parseColor("#263238"));
                }else{
                    roads.setText("Local Roads");
                    roadImage.setColorFilter(Color.parseColor("#9E9D24"));
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Wiper").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                if(value.toString().equals("1")){
                    weather.setText("Weather Conditions");
                    weatherImage.setColorFilter(Color.parseColor("#4FC3F7"));
                }else{
                    weather.setText("Clear");
                    weatherImage.setColorFilter(Color.parseColor("#424242"));
                }
            }

            @Override
            public void onFail(String value) {

            }
        });
    }

    private void gradualSpeedTo(ImageSpeedometer ism, float speed, long duration){
        if(duration == 0){
            ism.speedTo(speed);
        }else{
            ism.speedTo(speed,duration);
        }
    }
}
