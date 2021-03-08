package com.example.rc_controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.util.ArrayList;


public class Controller implements SensorEventListener {
    private static final int ROLL_HIST_COUNT = 5;
    private ArrayList<Double> rollHist;
    private int speed;

    public Controller(){
        rollHist = new ArrayList<>();
        for(int i=0;i<ROLL_HIST_COUNT;i++){
            rollHist.add(90.0);
        }

        speed = 0;
    }

    public byte getRoll(){
        double ave = 0;
        for(Double roll:rollHist){
            ave += roll;
        }
        int floored = (int) Math.floor(ave/rollHist.size());
        if(floored > 180) return (byte) 180;
        if(floored < 0) return 0;

        return (byte) floored;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
