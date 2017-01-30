package com.example.jeremy.androidwearheartrate;

/**
 * Created by Jeremy on 1/15/2017.
 */

public class InformationSet {



        private float[] m_acc,m_gyro,other, wattchgyro;
        private float watchLight;

        public InformationSet() { //Apparently this is required
        }
        public void setAcc(float [] acc){
            this.m_acc = acc;
        }
        public float[] getAcc(){
            return m_acc;
        }

        public void setGyro(float [] acc){
            this.m_gyro = acc;
        }
        public float[] getGyro(){
            return m_gyro;
        }
        public float[] getHeartProxLight() {
            return other;
        }

        public void setHeartProxLight(float[] HR) {
            this.other = HR;
        }

        public void setWatchLight(float HR) {
            this.watchLight = HR;
        }
        public float getWatchLight(){
        return watchLight;
    }

        public void setWatchGyro(float[] HR) {
        this.wattchgyro = HR;
    }
        public float[] getWatchGyro() {return wattchgyro; }



}
