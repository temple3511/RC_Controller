package com.example.rc_controller;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private final MutableLiveData<ArrayList<BluetoothDevice>> scannedDevices = new MutableLiveData<>();
    private final MutableLiveData<SelectedDevice> selectedDevice = new MutableLiveData<>();

    public ViewModel() {
        super();
        scannedDevices.setValue(new ArrayList<>());
        selectedDevice.setValue(new SelectedDevice("", null));
    }
    public LiveData<ArrayList<BluetoothDevice>> getDevices(){
        return scannedDevices;
    }

    public LiveData<SelectedDevice> getSelectedDevice(){
        return selectedDevice;
    }
}
