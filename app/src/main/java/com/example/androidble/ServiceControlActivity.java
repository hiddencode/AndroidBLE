package com.example.androidble;

import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;



public class ServiceControlActivity extends AppCompatActivity {

    public static final String EXTRA_SERVICE_UUID = "SERVICE_UUID";
    public static final String EXTRA_SERVICE_TYPE = "0";

    public static UUID serviceUUID;
    public static int serviceType;
    BluetoothGattService currentService;

    private String LOG_TAG = "BLE-demo";




    @Override
    public void onCreate(Bundle State){
        super.onCreate(State);

        Intent intent = getIntent();
        serviceUUID = UUID.fromString(intent.getStringExtra(EXTRA_SERVICE_UUID));
        serviceType = intent.getIntExtra(EXTRA_SERVICE_TYPE,0);
        currentService = new BluetoothGattService(serviceUUID, serviceType);
    }


    @Override
    public void onResume(){
        super.onResume();

        currentService.getCharacteristics();
    }

    /*
        steps:
        - getCharacteristics
        - SelectCharacteristic
        - record service & characteristic UUID
        - write value (bytes)
        - send

     */

}
