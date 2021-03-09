package com.example.rc_controller;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ViewModel extends androidx.lifecycle.ViewModel {
    private final MutableLiveData<ArrayList<BluetoothDevice>> scannedDevices = new MutableLiveData<>();
    private final MutableLiveData<SelectedDevice> selectedDevice = new MutableLiveData<>();
    private final MutableLiveData<byte[]> notifiedCharacteristic = new MutableLiveData<>();

    public ViewModel() {
        super();
        scannedDevices.setValue(new ArrayList<>());
        selectedDevice.setValue(new SelectedDevice("68:27:19:21:B4:7F", "BLE-ST09"));
        notifiedCharacteristic.setValue(new byte[]{0});
    }
    public LiveData<ArrayList<BluetoothDevice>> getDevices(){
        return scannedDevices;
    }

    public LiveData<SelectedDevice> getSelectedDevice(){
        return selectedDevice;
    }
    public void setSelectedDevice(SelectedDevice device){
        this.selectedDevice.setValue(device);
    }

    public LiveData<byte[]> getNotifiedCharacteristic(){
        return notifiedCharacteristic;
    }
    public void setNotifiedCharacteristic(byte[] notified){
        this.notifiedCharacteristic.setValue(notified);
    }
}
