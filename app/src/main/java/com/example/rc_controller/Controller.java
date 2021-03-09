package com.example.rc_controller;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Controller implements SensorEventListener {
    private static final int ROLL_HIST_COUNT = 5;
    private ControlView controlView;
    private ArrayList<Double> rollHist;
    private int speed;
    private int accel;

    private ScheduledExecutorService exec;

    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            speed += accel;
            if(speed > 100){
                speed = 100;
            }else if (speed < 0){
                speed =0;
            }
        }
    };


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

    public void resetRoll(){
        rollHist =new ArrayList<>();
        for(int i=0;i<ROLL_HIST_COUNT;i++){
            rollHist.add(90.0);
        }
    }

    public byte getSpeed(){
        return (byte) speed;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }

    public void setAccel(int accel) {
        this.accel = accel;
        if(accel == 0){
            exec.shutdown();
            exec = null;
        }else {
            if(exec == null){
                exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(task,100,100, TimeUnit.MILLISECONDS);
            }
        }
    }

    public void setControlView(ControlView controlView) {
        this.controlView = controlView;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //Log.d("Controller","Sensor is changed. value:"+event.values[0]+","+event.values[1]);
        float x=event.values[0];
        float y=event.values[1];
        rollHist.remove(0);
        double culc = Math.toDegrees(Math.atan2(x,y));
        if( culc < 55){
            culc = 55;
        }else if( culc > 125){
            culc =125;
        }
        rollHist.add(culc);
        if(controlView != null){
            controlView.postInvalidate();
        }
        //Log.d(this.getClass().getSimpleName(),"roll = "+rollHist.get(ROLL_HIST_COUNT - 1));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
