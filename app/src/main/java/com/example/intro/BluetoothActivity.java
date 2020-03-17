package com.example.intro;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothActivity extends AppCompatActivity {
    static final int SCAN_REQUEST = 1; // The request code.
    final String LOG_TAG = getClass().getSimpleName();

    public void check_blue_adapter(BluetoothAdapter bluetoothAdapter){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    }

    //public final List<BluetoothDevice> ble_list = null;  // List for contain other ble devices
    public void confirmResult(){
        /* got info from onActivityResult, and filling text view; refact text view into text list*/
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate(Bundle stateBundle){
        super.onCreate(stateBundle);
        setContentView(R.layout.activity_bluetooth);
        /* set up blue info*/
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);  //get info from system service
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        //get own adapter
        TextView text = findViewById(R.id.text_ble);   // Area for output info (WIP, after some times need refact it into text list)

        /* Get info own ble */
        text.setText(bluetoothAdapter.getName() + "\n" + bluetoothAdapter.getAddress());

    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent_scan = new Intent(this, ScanLeActivity.class);

        startActivityForResult(intent_scan, SCAN_REQUEST); // start activity for getting result of scan
    }



    /* Return result of scan*/
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent result_activity){
        super.onActivityResult(request_code, result_code, result_activity);
        if(request_code == SCAN_REQUEST){
            if(result_code == RESULT_OK) {
                Log.i(LOG_TAG,"Scan enabled");
            }
        }
    }

}


