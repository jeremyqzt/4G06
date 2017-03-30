package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;
import com.example.jeremy.androidwearheartrate.task.changeImageCoreTask;
import com.example.jeremy.androidwearheartrate.task.flashCoreTask;
import com.example.jeremy.androidwearheartrate.task.progressBarTask;
import com.example.jeremy.androidwearheartrate.task.uiTextTask;
import com.example.jeremy.androidwearheartrate.util.util;
import com.firebase.client.Firebase;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.StrictMath.abs;

/**
 * Created by kevind on 2017-02-26.
 */

public class HealthFragment extends Fragment{
    Bundle bundle;
    //encapsulate to object later
    String overallStatus;
    boolean good;
    String sleep = "";
    private Thread updateThread;
    IconRoundCornerProgressBar alertBar;
    IconRoundCornerProgressBar healthBar;
    IconRoundCornerProgressBar vehicleBar;
    ImageView core;
    TextView sys;
    TextView desc;
    Boolean alwaysTrue = true;
    Boolean alwaysFalse = false;

    double rotation = 0;
    int status = 1;
    int sdevcount = 0;
    int count = 0;
    int twominute = 0;
    private int acc_array = 0;
    private float [] watchgyro = {0,0,0};
    private float [] gyro = {0,0,0};
    private float [] vectoracc = {0,0,0};
    String gender = "Undefined";
    int genderdev = 0;
    int speedcount = 0;
    int wiper = 0;
    int carstdev = 0;
    int carsavg = 0;
    int acccount = 0;
    long start, end;
    float temp = 20;
    float temperature = 0;
    double heartavg = 0.0;
    double stdev = 0.0;
    ArrayList<Integer> heart_array = new ArrayList<>();
    int multiplier = 1;
    int heartrate = 0;
    float light = 300;
    float prox = 0;
    int carSpeed = 0;
    float watchLight = 0;
    boolean mono = false;
    boolean monodec = false;
    private static String red = "#C62828";
    private static String orange = "#F9A825";
    //private static String green = "#2E7D32";
    private static String green = "#0288D1";

    private Activity myActivity;
    int flag = 0;
    int speedflag = 0;
    int age = 45;
    double agedecider = 2; //2.5 low, 2 med, 1.5 high
    int accfalg = 0;
    SpeedService speed;
    MediaPlayer drowsymp, monomp, mondecmp,speedmp, speedweathermp, nonlinearmp, fatiguemp, crashmp, rotmp, srotmp, ceasemp;
    private String roadtype = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.health_page, container, false);
        bundle = this.getArguments();
        overallStatus = "Everything: Good!";
        good = true;
        alertBar = (IconRoundCornerProgressBar) view.findViewById(R.id.alertBar);
        healthBar = (IconRoundCornerProgressBar) view.findViewById(R.id.healthBar);
        vehicleBar = (IconRoundCornerProgressBar) view.findViewById(R.id.vehicleBar);
        core = (ImageView) view.findViewById(R.id.core);
        sys = (TextView) view.findViewById(R.id.systemtext);
        desc = (TextView) view.findViewById(R.id.desctext);
        myActivity = getActivity();
        speed = new SpeedService();
        startListeners();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateParams();
            }
        }, 3000);

        drowsymp = MediaPlayer.create(this.getActivity(), R.raw.drowsy);
        monomp = MediaPlayer.create(this.getActivity(), R.raw.mono);
        mondecmp = MediaPlayer.create(this.getActivity(), R.raw.monodec);
        speedmp = MediaPlayer.create(this.getActivity(), R.raw.speed);
        speedweathermp = MediaPlayer.create(this.getActivity(), R.raw.speedweather);
        nonlinearmp = MediaPlayer.create(this.getActivity(), R.raw.nonlinear);
        fatiguemp = MediaPlayer.create(this.getActivity(), R.raw.fatigue);
        crashmp = MediaPlayer.create(this.getActivity(), R.raw.crash);
        rotmp = MediaPlayer.create(this.getActivity(), R.raw.rot);
        srotmp = MediaPlayer.create(this.getActivity(), R.raw.srot);
        ceasemp = MediaPlayer.create(this.getActivity(), R.raw.cease);
        start = System.currentTimeMillis() - 25000; // Delayed Start


        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
//        new Thread(new checkBundle()).start();
//        Log.d("hfrag", "Health: "+ bundle.getInt("heartrate")
    }

    public void playmp(String what){
        if (System.currentTimeMillis() - start > 30000) {
            if (what.equals("Drowsy")) {
                drowsymp.start();
            } else if (what.equals("Mono")) {
                monomp.start();
            } else if (what.equals("Monodec")) {
                mondecmp.start();
            } else if (what.equals("Speed")) {
                speedmp.start();
            } else if (what.equals("SpeedWeather")) {
                speedweathermp.start();
            } else if (what.equals("NonLinear")) {
                nonlinearmp.start();
            } else if (what.equals("Fatigue")) {
                fatiguemp.start();
            }else if (what.equals("Crash")) {
                crashmp.start();
            }else if (what.equals("Rot")) {
                rotmp.start();
            }else if (what.equals("Srot")) {
                srotmp.start();
            }else if (what.equals("Cease")) {
                ceasemp.start();
            }
            start = System.currentTimeMillis();
        }
    }
    //pass data between fragments with bundle
    class checkBundle implements Runnable{
        @Override
        public void run() {
            while(true){
                try {
                    Log.d("hfrag", "Health: "+ bundle.getInt("heartrate"));
                    Thread.sleep(2000);
                }catch (InterruptedException e){

                }
            }
        }
    }

    //listeners
    private void startListeners(){
        //create firebase listeners
        FirebaseDB.getInstance("Vehicle/Speed").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {

                carSpeed = Integer.parseInt(value.toString());
                if (carSpeed < 1){
                    speedflag = 1;
                }else{
                    speedflag = 0;
                    if (carSpeed > (75)){
                        roadtype = "Highway";
                    }else{
                        roadtype = "Local";
                    }
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Acceleration").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                acc_array = Integer.parseInt(value.toString());
                if (acc_array < 1){
                    accfalg = 1;
                }else{
                    accfalg = 0;
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("InformationSet/watchLight").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                watchLight = Float.parseFloat(value.toString());
            }

            @Override
            public void onFail(String value) {

            }
        });
        FirebaseDB.getInstance("Person/Age").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {

                age = Integer.parseInt(value.toString());
                Log.d("AGE:", "AGE: " + age);
                if (age < 45){
                    agedecider = 2.5;
                }else if (age < 65){
                    agedecider = 2;
                }else {
                    agedecider = 1.5;
                }
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("InformationSet/watchGyro").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                watchgyro[0] = Float.parseFloat(vals.get(0).toString());
                watchgyro[1] = Float.parseFloat(vals.get(1).toString());
                watchgyro[2] = Float.parseFloat(vals.get(2).toString());

                if ((abs(watchgyro[0]) > 30) || abs(watchgyro[1]) > 30 || abs(watchgyro[2]) > 30){ //Crash Detected
                    write_curr();
                }

            }

            @Override
            public void onFail(String value) {

            }
        });
        FirebaseDB.getInstance("Vehicle/sDev").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                carstdev = Math.round(Float.parseFloat((value.toString())));
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/sAverage").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                carsavg = Math.round(Float.parseFloat(value.toString()));
            }

            @Override
            public void onFail(String value) {

            }
        });



        FirebaseDB.getInstance("openCV").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                Map<String, Object> openCV = (Map<String, Object>) value;
                sleep = openCV.get("sleeping").toString();
//                Log.d("hfrag","/openCV" + openCV.toString());
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Vehicle/Wiper").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                wiper = Integer.parseInt(value.toString());
            }

            @Override
            public void onFail(String value) {

            }
        });
        FirebaseDB.getInstance("InformationSet/gyro").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                gyro[0] = Float.parseFloat(vals.get(0).toString());
                gyro[1] = Float.parseFloat(vals.get(1).toString());
                gyro[2] = Float.parseFloat(vals.get(2).toString());
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("InformationSet/acc").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                vectoracc[0] = Float.parseFloat(vals.get(0).toString());
                vectoracc[1] = Float.parseFloat(vals.get(1).toString());
                vectoracc[2] = Float.parseFloat(vals.get(2).toString());
            }

            @Override
            public void onFail(String value) {

            }
        });

        FirebaseDB.getInstance("Person/Gender").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                gender = String.valueOf(value);
                Log.d("GenderTest", "Gender: " + gender);
            }

            @Override
            public void onFail(String value) {

            }
        });

        //final progressBarTask healthBarChange = new progressBarTask(0,0,healthBar);
        FirebaseDB.getInstance("InformationSet/heartProxLight").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                temp = Float.parseFloat(vals.get(3).toString());
                prox = Float.parseFloat(vals.get(1).toString());
                light = Float.parseFloat(vals.get(2).toString());
                Float temphr = Float.parseFloat(vals.get(0).toString());
                if(temphr > 1){
                    heartrate = Math.round(temphr);
                    heart_array.add(heartrate);
                    flag = 1;
                }else{
                    Log.d("HR", "Value: " + heartrate);
                }


            }

            @Override
            public void onFail(String value) {

            }
        });

        //set firebase listener for InformationSet/heartProxLight
        //do healthBar changes here
    }

    private void write_curr(){
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        Firebase ref = new Firebase(Config.FIREBASE_URL);

        Map map = new HashMap();
        map.put("Time", currentDateTimeString);
        map.put("Type", "Crash Report");
        map.put("Speed", carSpeed );
        map.put("Drowsy", sleep.equals("true") );
        map.put("HeartRate", heartrate );
        map.put("HeartRateAvg", heartavg );
        if (abs(watchgyro[0]) > 40 || abs(watchgyro[0]) > 40 || abs(watchgyro[0]) > 40){
            map.put("Injuried", "Unlikely");
        }else{
            map.put("Injuried", "Potential");
        }
        double totalG = Math.sqrt(watchgyro[0]*watchgyro[0] + watchgyro[1]*watchgyro[1] + watchgyro[2]*watchgyro[2])/10;
        map.put("TotalGForce", totalG );
        playmp("Crash");
        if (watchgyro[0] < -5){
            map.put("CollissionPointFR", "Rear");
        }else if (watchgyro[0] > 5){
            map.put("CollissionPointFR", "Front");
        }else{
            map.put("CollissionPointFR", "Undetermined");
        }

        if (watchgyro[1] < -5){
            map.put("CollissionPointLR", "Right");
        }else if (watchgyro[1] > 5){
            map.put("CollissionPointLR", "Left");
        }else{
            map.put("CollissionPointLR", "Undetermined");
        }
        Toast.makeText(getActivity(),"Successfully Logged!", Toast.LENGTH_SHORT).show();

        ref.push().setValue(map);


    }
    //periodic increments
    private void updateParams(){
        if (updateThread != null){
            updateThread.interrupt();
            Log.d("hfrag","thread killed");
        }

        final progressBarTask vehicleBarChange = new progressBarTask(0,0,vehicleBar);
        final progressBarTask healthBarChange = new progressBarTask(0,0,healthBar);
        final progressBarTask alertBarChange = new progressBarTask(0,0,alertBar);

        final uiTextTask systemText = new uiTextTask("Systems Nominal",sys);
        final uiTextTask descriptionText = new uiTextTask("Keep up the good driving",desc);
        final flashCoreTask flashcore = new flashCoreTask(core);
        final changeImageCoreTask colorcore = new changeImageCoreTask(green,core);

        // should use 1 thread for background work/updating values and 1 handler for UI changes
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if (speedflag == 1){
                        carSpeed = (int) speed.getSpeed();
                        if (carSpeed > (75/3.6)){
                            roadtype = "Highway";
                        }else{
                            roadtype = "Local";
                        }
                    }
                    if (accfalg == 1){
                        acc_array = Math.round(vectoracc[2]); //Overide with sensor if missing
                    }
                    //gender division
                    if(gender.equals("Female")){
                        genderdev = 10;
                    }else{
                        genderdev = 0;
                    }

                    Log.d("Roat", "Road: " +roadtype);

                    //bad way to count to 5, should be done another way
                    //checking steering wheel positions


                    count++; //Internal Usage
                    if(count == 5){
                        count = 0;
                        if(watchgyro[2] > 0 && watchgyro[2]<8){
                            status = 0;
                        }else if(watchgyro[2] >= 8){
                            status = 1;
                        }else if(watchgyro[2] < 0){
                            status = 2;
                        }else{
                            status = 3;
                        }
                    }

                    //Turning radius

                    //Acceleration checks
                    if(acc_array > 22){
                        speedcount++;
                    }else{
                        speedcount = 0;
                    }

                    if(speedcount > 3 && status != 0 && !roadtype.equals("Highway")){
                        systemText.setText("Speed");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Blink has detected "+ Math.round(10*(acc_array/10))/10 + " Gs of acceleration for " + speedcount + "s now, consider slowing down");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    //calc rotation
                    rotation = Math.sqrt(gyro[0]*gyro[0]+gyro[1]*gyro[1]+gyro[2]*gyro[2]);
                    if(rotation > 1.0){
                        systemText.setText("Rotation");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Significant Rotation Detected: "+ rotation);
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(red);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                        myActivity.runOnUiThread(flashcore);
                        playmp("Srot");
                    }

                    if(wiper == 1 ){
                        sdevcount += 1;
                        if (sdevcount > 9 ) {
                            if (carSpeed > carsavg + agedecider*carstdev) { //In Weather and Very Fast!
                                systemText.setText("Speed");
                                myActivity.runOnUiThread(systemText);
                                descriptionText.setText("Speed is Abnormally high for current conditions, over: " + String.valueOf((Math.round(carSpeed - carsavg)/carstdev)) +" past your average");
                                myActivity.runOnUiThread(descriptionText);
                                colorcore.setColor(red);
                                myActivity.runOnUiThread(colorcore);
                                good = false;
                                playmp("SpeedWeather");

                            }
                        }
                    }else{
                        sdevcount = 0;
                    }

                    //Non-linear accel
                    if(abs(vectoracc[0]) > 3 && vectoracc[2] > 15){ //Hard to Trigger
                        acccount++;
                    }else{
                        acccount = 0;
                    }

                    if(acccount > 3 ){
                        descriptionText.setText("Movement is Constantly Non Linear");
                        myActivity.runOnUiThread(descriptionText);
                        if(acc_array > 22){
                            systemText.setText("Extreme Acceleration");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("Blink has detected extreme and non linear acceleration, consider slowing down");
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            good = false;
                            playmp("NonLinear");
                        }
                    }
//                    vehicleBarChange.incrementTotal();
//                    vehicleBarChange.incrementProgress();
//                    myActivity.runOnUiThread(vehicleBarChange);


                    //Temp extremes
                    if(temp > 37){
                        systemText.setText("High Temperature");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Consider turning on the cool air, " + temp + " Celsius is too warm for operation");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    if(temp < 0){
                        systemText.setText("Temperature: Low");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Consider turning on the cool air, " + temp + " Celsius is too cold for operation");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    //
                    heartavg = util.findMean(heart_array);
                    stdev = util.findSTD(heart_array);

                    if(stdev < 10){
                        stdev = 7.5;
                    }

                    stdev *= agedecider;
                    //increasing
                    if (heart_array.size() > 7){
                        if(util.isIncreasing(heart_array,7)){
                            mono = true;
                        }else{
                            mono = false;
                        }
                        //implement monotonically decreasing
                    }


                    double hdev = (heartrate - heartavg)/stdev;
                    hdev = Math.round(hdev);

                    if((heartrate < 180+genderdev) && heartrate > (40+genderdev)) {
                        if(heartrate > heartavg ){
                            if((heartrate - heartavg) > 1.5*stdev ){
                                systemText.setText("High Heartrate");
                                myActivity.runOnUiThread(systemText);
                                descriptionText.setText("Blink has detected abnormal heart rate readings, over "+ hdev +" past your normal range, considering pulling over");
                                myActivity.runOnUiThread(descriptionText);
                                colorcore.setColor(red);
                                myActivity.runOnUiThread(colorcore);
                                //blink core
                                good = false;

                                if(acc_array > 22){
                                    systemText.setText("Erratic Driving Detected");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down or taking a break");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Cease");
                                    //blink core
                                }
                                if(mono ){
                                    systemText.setText("Heart Rate");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over "+ hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Mono");

                                    //blink core
                                }
                                if(acccount > 3){
                                    systemText.setText("Erratic Driving Detected");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down or taking a break");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Cease");
                                    //blink core
                                }
                            }
                        }else{
                            if(heartavg - heartrate > 1.5*stdev){
                                if(acc_array > 22){
                                    systemText.setText("Abnormally High Heart Rate");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over " + hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                }
                                if(rotation > 1.0){
                                    systemText.setText("Erratic Driving Detected");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over " + hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Cease");

                                }
                                if(monodec){
                                    systemText.setText("Rotation");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Decreasing heartrate, please pull over and call for help!");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Monodec");
                                }
                                if(acccount > 3){
                                    systemText.setText("Erratic Driving Detected");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    playmp("Cease");

                                }
                            }
                        }
                    }

//                    if (heartrate < 120){
//                        healthBarChange.incrementProgress();
//                    }
//                    healthBarChange.incrementTotal();
//                    myActivity.runOnUiThread(healthBarChange);

                    if((light < 50 && watchLight < 50) || (status != 0 && sleep == "true")){
                        systemText.setText("Brightness: Low");
                        myActivity.runOnUiThread(systemText);
                        float avg_bright = (light + watchLight) / 2;
                        descriptionText.setText("Blink has detected low brightness, with only "+ avg_bright +" lux, its easy to fall asleep");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }
                    if ((light < 50 && watchLight < 50) && (Math.round(acc_array) > 10) && (heartrate < 60 + genderdev)){
                        twominute += 1;
                    }else{
                        twominute = 0;
                    }

                    if (twominute > 120 ){
                        playmp("Fatigue");
                        good = false;
                        systemText.setText("Fatigue");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("You Maybe Unfit to Drive in Current Conditions");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(red);
                        myActivity.runOnUiThread(colorcore);
                    }


                    if (light > 4000){
                        systemText.setText("Brightness: Extreme");
                        myActivity.runOnUiThread(systemText);
                        float avg_bright = (light + watchLight)/2;
                        descriptionText.setText("Blink has detected extreme brightness, with only " +avg_bright +" lux, its will be hard to see");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    if(prox < 3 && carSpeed > 10 && status != 0){
                        systemText.setText("Operation");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Please do not operating while driving");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    if(rotation > 1.0 ){
                        if(speedcount > 3 ){
                            systemText.setText("Rotation & Speed");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("Blink has detected that the vehicle is rotating due to Acceleration, consider slowing down and reducing your turn radius");
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            good = false;
                            playmp("Rot");
                        }
                    }

                    if(sleep.equals("true")){
                        systemText.setText("Driver Drowsy");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Blink has detected that the driver is drowsy, we recommend taking a break and getting a coffee");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(red);
                        myActivity.runOnUiThread(colorcore);
                        myActivity.runOnUiThread(flashcore);
                        good = false;
                        playmp("Drowsy");
                    }else{
                        //everything is normal
                        alertBarChange.incrementProgress();
                    }

                    alertBarChange.incrementTotal();
                    //change alertness bar
                    myActivity.runOnUiThread(alertBarChange);

                    if (heartrate < 120){
                        healthBarChange.incrementProgress();
                    }

                    healthBarChange.incrementTotal();
                    //change alertness bar
                    myActivity.runOnUiThread(healthBarChange);

                    if (acc_array < 20){
                        vehicleBarChange.incrementProgress();
                    }
                    vehicleBarChange.incrementTotal();
                    //change alertness bar
                    myActivity.runOnUiThread(vehicleBarChange);


                    if(good == true){
                        //set top text
                        systemText.setText("Systems Nominal");
                        myActivity.runOnUiThread(systemText);
                        //set description text
                        descriptionText.setText("Keep up the good driving");
                        myActivity.runOnUiThread(descriptionText);
                        //set color
                        colorcore.setColor(green);
                        myActivity.runOnUiThread(colorcore);
                    }
                    good = true;
//                    Log.d("hfrag", "Heart Rate: "+ bundle.getInt("heartrate"));
//                    heart_array.add(bundle.getInt("heartrate"));
//                    Log.d("hfrag", "Heart Array "+ Arrays.toString(heart_array.toArray()));
//                    Log.d("hfrag", "Mean "+findMean(heart_array));
//                    Log.d("hfrag", "STD "+findSTD(heart_array));
//                    Integer[] test = {16,17,18,19,15,21,23,25,30,14,80};
//                    ArrayList<Integer> myTest = new ArrayList<Integer>(Arrays.asList(test));
//                    Log.d("hfrag", "isIncreasing: "+isIncreasing(myTest,3));
//                    Log.d("hfrag",""+alertBarChange.getProgress());
//                    Log.d("hfrag",""+alertBarChange.getTotalAlert());
//                    Log.d("hfrag", "=======");
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateThread.start();
    }

}
