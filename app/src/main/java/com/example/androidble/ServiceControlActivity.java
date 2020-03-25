package com.example.androidble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;




public class ServiceControlActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerCharacteristicAdapter recyclerCharacteristicAdapter;

    public static final String EXTRA_SERVICE_UUID = "SERVICE_UUID";
    public static final String EXTRA_SERVICE_TYPE = "0";

    public static UUID serviceUUID;
    public static int serviceType;

    private BluetoothLeService mBluetoothLeService;
    public BluetoothGattService currentService;
    public List<BluetoothGattCharacteristic> ArrayCharacteristic;

    private String LOG_TAG = "BLE-demo";


    private void fillList(@NonNull List<BluetoothGattCharacteristic> characteristics) {
       // ArrayCharacteristic.addAll(characteristics);
        for(BluetoothGattCharacteristic characteristic : characteristics){
            ArrayCharacteristic.add(characteristic);
            recyclerCharacteristicAdapter.addChs(characteristic);
            recyclerCharacteristicAdapter.notifyDataSetChanged();
            Log.i(LOG_TAG, "CHS - " + characteristic.getUuid().toString() + " has been added");
        }

    }

    @Override
    public void onCreate(Bundle State){
        super.onCreate(State);
        setContentView(R.layout.scan_chs);

        Intent intent = getIntent();
        serviceUUID = UUID.fromString(intent.getStringExtra(EXTRA_SERVICE_UUID));
        serviceType = intent.getIntExtra(EXTRA_SERVICE_TYPE, BluetoothGattService.SERVICE_TYPE_PRIMARY);
        currentService = new BluetoothGattService(serviceUUID, serviceType);



//        if(currentService.getCharacteristics()!=null){
//            ArrayCharacteristic.addAll(currentService.getCharacteristics());
//            Log.i(LOG_TAG,"CHS have been added C:");
//        }else{
//            Log.i(LOG_TAG, "CHS are absent (>.<)");
//        }


//
        recyclerView = findViewById(R.id.chs_view);
        LayoutInflater current_inflater = ServiceControlActivity.this.getLayoutInflater();
        recyclerCharacteristicAdapter = new RecyclerCharacteristicAdapter(current_inflater);
        recyclerView.setAdapter(recyclerCharacteristicAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /* Create demonstrate to text view */
        if(currentService.getCharacteristics().size() == 0){
            Log.i(LOG_TAG,"CHS are absent (>.<)");
            BluetoothGattCharacteristic chs = new BluetoothGattCharacteristic(UUID.randomUUID(),0, 0);
            recyclerCharacteristicAdapter.addChs(chs);
        }else{
            Log.i(LOG_TAG,"Have " + currentService.getCharacteristics().size() + " CHS");
        }

//
//        Log.i(LOG_TAG, "onCreate, recycleView has been init");
//
//        if(currentService.getCharacteristics()!= null) {
//            for (BluetoothGattCharacteristic chs : (currentService.getCharacteristics())) {
//                Log.i(LOG_TAG, "CHS - " + chs.getUuid().toString() + " has been added");
//            }
//        }else{
//            Log.i(LOG_TAG,"SERVICE - " + currentService.getUuid().toString() + " haven`t characteristics");
//        }
//
//        fillList(currentService.getCharacteristics());

        Log.i(LOG_TAG,"ServiceControlActivity: onCreate - closed");
    }


    @Override
    public void onResume(){
        super.onResume();


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
