package com.example.jeremy.androidwearheartrate.blink;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kevind on 2017-01-30.
 */

public class CarDataSet extends DataSet {

    private int accelerationx;
    private int accelerationz;
    private boolean dangerousProximity;
    private boolean HeadLights;
    private boolean Braking;
    private boolean ExcessBraking;
    private boolean Turning;
    private boolean Wiping;
    private Map<String,Integer> proximity;
    private String roadType;
    private int speed;
    private int speedVariance;

    public CarDataSet(){
        super("CarData",true);
        //required for calls
    }

    public CarDataSet(int speed){
        super("CarData",true);
        accelerationx = 0;
        accelerationz = 0;
        dangerousProximity = false;
        HeadLights = false;
        Braking = false;
        ExcessBraking = false;
        Turning = false;
        Wiping = false;
        proximity = new HashMap<>();
        roadType = "pcity";
        this.speed = 0;
        speedVariance = 0;
    }

    public int getAccelerationz() {
        return accelerationz;
    }

    public void setAccelerationz(int accelerationz) {
        this.accelerationz = accelerationz;
    }

    public boolean isTurning() {
        return Turning;
    }

    public void setTurning(boolean turning) {
        Turning = turning;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public int getSpeedVariance() {
        return speedVariance;
    }

    public void setSpeedVariance(int speedVariance) {
        this.speedVariance = speedVariance;
    }

    public int getAccelerationx() {
        return accelerationx;
    }

    public void setAccelerationx(int accelerationx) {
        this.accelerationx = accelerationx;
    }

    public boolean isDangerousProximity() {
        return dangerousProximity;
    }

    public void setDangerousProximity(boolean dangerousProximity) {
        this.dangerousProximity = dangerousProximity;
    }

    public boolean isHeadLights() {
        return HeadLights;
    }

    public void setHeadLights(boolean hasHeadLights) {
        this.HeadLights = hasHeadLights;
    }

    public boolean isBraking() {
        return Braking;
    }

    public void setBraking(boolean braking) {
        Braking = braking;
    }

    public boolean isExcessBraking() {
        return ExcessBraking;
    }

    public void setExcessBraking(boolean excessBraking) {
        ExcessBraking = excessBraking;
    }

    public boolean isWiping() {
        return Wiping;
    }

    public void setWiping(boolean wiping) {
        Wiping = wiping;
    }

    public Map<String,Integer> getProximity() {
        return proximity;
    }

    public void setProximity(Map<String,Integer> proximity) {
        this.proximity = proximity;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
