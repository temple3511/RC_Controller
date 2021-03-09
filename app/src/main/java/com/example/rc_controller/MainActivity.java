package com.example.rc_controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.rc_controller.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private BLEManager bleManager=null;
    private ActivityMainBinding binding;
    private Controller controller;
    private ViewModel viewModel;


    private SensorManager sManager;
    private static final int SENSOR_NAME_ACCEL = Sensor.TYPE_ACCELEROMETER;
    private static final int SENSOR_DELAY= SensorManager.SENSOR_DELAY_FASTEST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        sManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        controller = binding.controlView.getController();


        binding.buttonConnect.setText("接続");
        binding.buttonConnect.setEnabled(false);

        binding.buttonAccel.setClickable(false);
        binding.buttonAccel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(bleManager != null){
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            controller.setAccel(2);
                            break;
                        case MotionEvent.ACTION_UP:
                            controller.setAccel(0);
                            break;
                    }
                }
                return true;
            }
        });
        binding.buttonAccel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                controller.setAccel(4);
                return true;
            }
        });
        binding.buttonBreak.setClickable(false);
        binding.buttonBreak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(bleManager != null){
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            controller.setAccel(-5);
                            break;
                        case MotionEvent.ACTION_UP:
                            controller.setAccel(0);
                            break;
                    }
                }
                return true;
            }
        });
        binding.buttonBreak.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                controller.setAccel(-10);
                return true;
            }
        });




        viewModel = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(ViewModel.class);
        SelectedDevice device = viewModel.getSelectedDevice().getValue();
        binding.textviewDeviceaddress.setText(device.getAddress());
        binding.textviewDevicename.setText(device.getName());
        viewModel.getSelectedDevice().observe(this, newDevice -> {
            binding.buttonAccel.setEnabled(newDevice.isConnected());
            binding.buttonBreak.setEnabled(newDevice.isConnected());

            if(device.isConnected()){
                binding.buttonConnect.setText("切断");
            }else {
                binding.buttonConnect.setText("接続");
            }

            binding.textviewDeviceaddress.setText(device.getAddress());
            binding.textviewDevicename.setText(device.getName());
        });


        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            initBluetooth();


        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Bluetoothを利用するために現在位置アクセス権限が必要です", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                requestPermissions(new String[]{
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSensor();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeSensor();

    }
    public void startSensor(){
        Sensor sensorAccel=sManager.getDefaultSensor(SENSOR_NAME_ACCEL);
        sManager.registerListener(controller,sensorAccel,SENSOR_DELAY);
    }


    public void closeSensor(){
        sManager.unregisterListener(controller);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(permissions.length > 0){
                initBluetooth();
            }else {
                Log.w("RC_Controller","Permission denied");
                finish();

            }
        }
    }

    private void initBluetooth(){
        // Bluetoothアダプタの取得
        BluetoothManager bluetoothManager = (BluetoothManager)getSystemService( Context.BLUETOOTH_SERVICE );
        BluetoothAdapter adapter = bluetoothManager.getAdapter();
        if( null == adapter )
        {    // Android端末がBluetoothをサポートしていない
            Toast.makeText( this, R.string.bluetooth_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }
        // Android端末がBLEをサポートしてるかの確認
        if( !getPackageManager().hasSystemFeature( PackageManager.FEATURE_BLUETOOTH_LE ) )
        {
            Toast.makeText( this, R.string.ble_is_not_supported, Toast.LENGTH_SHORT ).show();
            finish();    // アプリ終了宣言
            return;
        }

        bleManager =new BLEManager(this,adapter,viewModel);
        bleManager.setController(controller);
        binding.buttonConnect.setOnClickListener(bleManager);
        binding.buttonConnect.setEnabled(true);

    }
}