package com.example.rc_controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

import androidx.annotation.NonNull;

public class BLEManager extends BluetoothGattCallback {
    private final BluetoothAdapter adapter;
    private SelectedDevice currentDevice;


    public BLEManager(@NonNull BluetoothAdapter adapter, ViewModel viewModel){
        this.adapter = adapter;
        this.currentDevice = viewModel.getSelectedDevice().getValue();
    }






    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if(status == newState) return;
        switch (newState){
            case BluetoothGatt.STATE_CONNECTED:
                break;
            case BluetoothGatt.STATE_DISCONNECTED:
                break;
            default:
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
    }
}
