package com.example.rc_controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BLEManager extends BluetoothGattCallback implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static String POWER = "00002B05-0000-1000-8000-00805f9b34fb";
    public static String UART_OVER_BLE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"; // UART service UUID 6E400001B5A3F393E0A9E50E24DCCA9E
    public static String CLIENT_CHARACTERISTIC_Rx = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"; //6e400002b5a3f393e0a9e50e24dcca9e
    public static String CLIENT_CHARACTERISTIC_Tx = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"; //6e400003b5a3f393e0a9e50e24dcca9e

    private static final UUID UUID_CONTROL_SERVICE = UUID.fromString( UART_OVER_BLE );
    private static final UUID UUID_CHARACTERISTIC_Rx = UUID.fromString( CLIENT_CHARACTERISTIC_Rx );
    private static final UUID UUID_CHARACTERISTIC_Tx = UUID.fromString( CLIENT_CHARACTERISTIC_Tx );

    private static final UUID UUID_BUTTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_BUTTERY_VALUE = UUID.fromString( POWER);
    // for Notification
    private static final UUID UUID_NOTIFY                  = UUID.fromString( "00002902-0000-1000-8000-00805f9b34fb" );



    private ScheduledExecutorService exec = null;
    private final Runnable task = new Runnable() {
        @Override
        public void run() {
            Log.d("ble.task","Starting transmit.");
            if(controller != null){
                setControlData(gatt,UUID_CONTROL_SERVICE,UUID_CHARACTERISTIC_Tx,controller);
                Log.d("ble.task","transmit complete.");
            }else {
                Log.w("ble.task","controller is null.");
            }
        }
    };

    private BluetoothGatt gatt;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final BluetoothAdapter adapter;
    private final SelectedDevice currentDevice;
    private Controller controller;
    private final ViewModel viewModel;
    private final Context context;


    public BLEManager(Context context, @NonNull BluetoothAdapter adapter, ViewModel viewModel){
        this.context = context;
        this.adapter = adapter;
        this.viewModel = viewModel;
        this.currentDevice = viewModel.getSelectedDevice().getValue();
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
        super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        this.gatt = gatt;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        //if(status == newState) return;
        this.gatt = gatt;
        switch (newState){
            case BluetoothGatt.STATE_CONNECTED:

                currentDevice.setConnected(true);
                break;
            case BluetoothGatt.STATE_DISCONNECTED:
                currentDevice.setConnected(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        viewModel.setNotifiedCharacteristic(characteristic.getValue());
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        super.onReadRemoteRssi(gatt, rssi, status);
        currentDevice.setRssi(rssi);
    }

    @Override
    public void onClick(View view) {
        if(currentDevice.isConnected()){
            exec.shutdown();
            exec = null;
            gatt.close();
            gatt=null;
            currentDevice.setGatt(null);

        }else{
            this.gatt =adapter.getRemoteDevice(currentDevice.getAddress()).connectGatt(context,false,this);
            currentDevice.setGatt(gatt);
            if(exec == null){
                exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleWithFixedDelay(task,2000,100, TimeUnit.MILLISECONDS);
            }
        }
        currentDevice.setConnected(!currentDevice.isConnected());
        viewModel.setSelectedDevice(currentDevice);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        BluetoothGatt gatt= currentDevice.getGatt();
        if(gatt != null){
            if(currentDevice.isConnected()){
                setCharacteristicNotification(gatt,UUID_CONTROL_SERVICE,UUID_CHARACTERISTIC_Rx,b);
            }
        }
    }

    public void setControlData(BluetoothGatt gatt,UUID service, UUID characteristic,Controller controller){
        byte[] transmitData = new byte[2];
        transmitData[0]=controller.getSpeed();
        transmitData[1]=controller.getRoll();
        Log.d("BLEController",String.format("Tx=%02x%02x",transmitData[0],transmitData[1]));
        BluetoothGattCharacteristic blechar = gatt.getService(service).getCharacteristic( characteristic );
        Log.d("BLEController","blechar:"+blechar.hashCode());

        blechar.setValue(transmitData);
        gatt.writeCharacteristic( blechar );

    }

    public void setCharacteristicNotification(BluetoothGatt gatt,UUID uuid_service, UUID uuid_characteristic, boolean enable )
    {
        BluetoothGattCharacteristic blechar = gatt.getService( uuid_service ).getCharacteristic( uuid_characteristic );
        gatt.setCharacteristicNotification( blechar, enable );
        BluetoothGattDescriptor descriptor = blechar.getDescriptor( UUID_NOTIFY );
        if(enable){
            descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE );
        }else {
            descriptor.setValue( BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        }
        gatt.writeDescriptor( descriptor );
    }

}
