package com.example.rc_controller;

import android.bluetooth.BluetoothGatt;

public class SelectedDevice {
    private static final String DEVICE_NAME_DEFAULT = "unknown device";

    private String name;
    private String address;
    private BluetoothGatt gatt;
    private boolean isConnected;
    private int rssi;

    public SelectedDevice(String address, String name){
        if(name == null){
            name = DEVICE_NAME_DEFAULT;
        }
        this.name = name;
        this.address = address;
        isConnected = false;
        gatt = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }
}
