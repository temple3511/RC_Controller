package com.example.rc_controller;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    MutableLiveData<ArrayList<BluetoothDevice>> scannedDevices = new MutableLiveData<>();

    public LiveData<ArrayList<BluetoothDevice>> getDevices(){
        if(scannedDevices.getValue() == null){
            scannedDevices.setValue(new ArrayList<>());
        }
        return scannedDevices;
    }
}
