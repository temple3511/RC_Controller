package com.example.rc_controller;

public class SelectedDevice {
    private static final String DEVICE_NAME_DEFAULT = "unknown device";

    private String name;
    private String address;
    private boolean isConnected;
    private int rssi;

    public SelectedDevice(String address, String name){
        if(name == null){
            name = DEVICE_NAME_DEFAULT;
        }
        this.name = name;
        this.address = address;
        isConnected = false;
    }


}
