package com.example.jeremy.androidwearheartrate;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.IconRoundCornerProgressBar;
import com.example.jeremy.androidwearheartrate.database.DatabaseChangeListener;
import com.example.jeremy.androidwearheartrate.database.FirebaseDB;
import com.example.jeremy.androidwearheartrate.task.changeImageCoreTask;
import com.example.jeremy.androidwearheartrate.task.flashCoreTask;
import com.example.jeremy.androidwearheartrate.task.progressBarTask;
import com.example.jeremy.androidwearheartrate.task.uiTextTask;
import com.example.jeremy.androidwearheartrate.util.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

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
    double rotation = 0;
    int status = 1;
    int count = 0;
    private int acc_array = 0;
    private float [] watchgyro = {0,0,0};
    private float [] gyro = {0,0,0};
    private float [] vectoracc = {0,0,0};
    String gender = "Undefined";
    int genderdev = 0;
    int speedcount = 0;
    int wiper = 0;
    int acccount = 0;
    float temp = 20;
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
    private static String green = "#2E7D32";
    private Activity myActivity;
    int flag = 0;

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

        startListeners();

        updateParams();

        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
//        new Thread(new checkBundle()).start();
//        Log.d("hfrag", "Health: "+ bundle.getInt("heartrate")
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
            }

            @Override
            public void onFail(String value) {

            }
        });

        final progressBarTask vehicleBarChange = new progressBarTask(0,0,vehicleBar);
        FirebaseDB.getInstance("Vehicle/Acceleration").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                acc_array = Integer.parseInt(value.toString());
                vehicleBarChange.incrementTotal();
                if(acc_array < 15){
                    vehicleBarChange.incrementProgress();
                }
                vehicleBarChange.run();
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

        FirebaseDB.getInstance("InformationSet/watchGyro").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                watchgyro[0] = Float.parseFloat(vals.get(0).toString());
                watchgyro[1] = Float.parseFloat(vals.get(1).toString());
                watchgyro[2] = Float.parseFloat(vals.get(2).toString());
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

        final progressBarTask healthBarChange = new progressBarTask(0,0,healthBar);
        FirebaseDB.getInstance("InformationSet/heartProxLight").onChange(new DatabaseChangeListener() {
            @Override
            public void onSuccess(Object value) {
                ArrayList<Object> vals = (ArrayList<Object>) value;
                temp = Float.parseFloat(vals.get(3).toString());
                prox = Float.parseFloat(vals.get(1).toString());
                light = Float.parseFloat(vals.get(2).toString());
//                Log.d("hfrag","Info/heartProxLight: "+ value.toString());
                Float temphr = Float.parseFloat(vals.get(0).toString());
                if(temphr > 0){
                    heartrate = Math.round(temphr);
                    heart_array.add(heartrate);
                    flag = 1;
                }else{
                    flag = 0;
                }

                if(heartrate > 120){
                    healthBarChange.incrementProgress();
                }
                healthBarChange.incrementTotal();
                healthBarChange.run();
            }

            @Override
            public void onFail(String value) {

            }
        });

        //set firebase listener for InformationSet/heartProxLight
        //do healthBar changes here
    }

    //periodic increments
    private void updateParams(){
        if (updateThread != null){
            updateThread.interrupt();
            Log.d("hfrag","thread killed");
        }

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
                    //gender division
                    if(gender.equals("Female")){
                        genderdev = 10;
                    }

                    //bad way to count to 5, should be done another way
                    //checking steering wheel positions
                    count++;
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
                    if(acc_array > 15){
                        speedcount++;
                    }else{
                        speedcount = 0;
                    }

                    if(speedcount > 3 && status != 0){
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
                    if(rotation > 0.40){
                        if(rotation > 0.50){
                            systemText.setText("Rotation");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("Significant Rotation Detected: "+ rotation);
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            good = false;
                        }
                        myActivity.runOnUiThread(flashcore);
                        if(wiper == 1){
                            systemText.setText("Rotation");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("High Rotation Vector: "+ rotation);
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            good = false;
                        }
                    }

                    //Non-linear accel
                    if(vectoracc[0] > 3 && vectoracc[2] > 15){
                        acccount++;
                    }else{
                        acccount = 0;
                    }

                    if(acccount > 3){
                        descriptionText.setText("Movement is Constantly Non Linear");
                        myActivity.runOnUiThread(descriptionText);
                        if(vectoracc[2] > 22){
                            systemText.setText("Extreme Acceleration");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("Blink has detected extreme and non linear acceleration, consider slowing down");
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            //flash
                            good = false;
                        }
                        if(wiper == 1){
                            systemText.setText("Weather");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("You are driving too fast for these conditions");
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            //flash
                            good = false;
                        }
                    }

                    //Temp extremes
                    if((temp-4) > 35){
                        systemText.setText("Temperature: High");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Consider turning on the cool air," + (temp-4)+ " Celsius is too warm for operation");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    if(temp < 15){
                        systemText.setText("Temperature: Low");
                        myActivity.runOnUiThread(systemText);
                        descriptionText.setText("Consider turning on the cool air," + temp + " Celsius is too cold for operation");
                        myActivity.runOnUiThread(descriptionText);
                        colorcore.setColor(orange);
                        myActivity.runOnUiThread(colorcore);
                        good = false;
                    }

                    //
                    heartavg = util.findMean(heart_array);
                    stdev = util.findSTD(heart_array);

                    if(stdev < 10){
                        stdev = 10;
                    }

                    stdev *= multiplier;
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
                    hdev = Math.round((hdev*10)/10);
                    if((heartrate < 180+genderdev) && heartrate > (40+genderdev)){
                        if(heartrate > heartavg){
                            if((heartrate - heartavg) > 1.5*stdev){
                                systemText.setText("High Heartrate");
                                myActivity.runOnUiThread(systemText);
                                descriptionText.setText("Blink has detected abnormal heart rate readings, over "+ hdev +" past your normal range, considering pulling over");
                                myActivity.runOnUiThread(descriptionText);
                                colorcore.setColor(red);
                                myActivity.runOnUiThread(colorcore);
                                //blink core
                                good = false;

                                if(vectoracc[2] > 22){
                                    systemText.setText("Acceleration & Stress");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down or taking a break");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    //blink core
                                }
                                if(mono){
                                    systemText.setText("Heart Rate");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over "+ hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    //blink core
                                }
                                if(acccount > 3){
                                    systemText.setText("Acceleration & Stress");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down or taking a break");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                    //blink core
                                }
                            }
                        }else{
                            if(heartavg - heartrate > 1.5*stdev){
                                if(vectoracc[2] > 22){
                                    systemText.setText("Fainting");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over " + hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                }
                                if(rotation > 0.45){
                                    systemText.setText("Heart Rate");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected abnormal heart rate readings, over " + hdev +" past your normal range, considering pulling over");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                }
                                if(monodec){
                                    systemText.setText("Rotation");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Decreasing heartrate, please pull over and call for help!");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                }
                                if(acccount > 3){
                                    systemText.setText("Acceleration & Stress");
                                    myActivity.runOnUiThread(systemText);
                                    descriptionText.setText("Blink has detected eratic driving behavior, consider slowing down");
                                    myActivity.runOnUiThread(descriptionText);
                                    colorcore.setColor(red);
                                    myActivity.runOnUiThread(colorcore);
                                    good = false;
                                }
                            }
                        }
                    }

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

                    if(rotation > 0.5){
                        if(speedcount > 3){
                            systemText.setText("Rotation & Speed");
                            myActivity.runOnUiThread(systemText);
                            descriptionText.setText("Blink has detected that the vehicle is rotating due to Acceleration, consider slowing down and reducing your turn radius");
                            myActivity.runOnUiThread(descriptionText);
                            colorcore.setColor(red);
                            myActivity.runOnUiThread(colorcore);
                            good = false;
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
                    }else{
                        //everything is normal
                        alertBarChange.incrementProgress();
                    }
                    alertBarChange.incrementTotal();
                    //change alertness bar
                    myActivity.runOnUiThread(alertBarChange);

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
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        updateThread.start();
    }

}