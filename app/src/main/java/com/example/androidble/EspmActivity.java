package com.example.androidble;


import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;



/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class EspmActivity extends AppCompatActivity {          //init class


    static final public String LOG_TAG = "BLE-demo|EspmActivity";
    private BluetoothAdapter mBluetoothAdapter;


    // Request on location
    private static final int LOCATE_PERMISSION_REQUEST = 888;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    // Request on enable bluetooth
    private static final int REQUEST_ENABLE_BT = 1;


    /*
    * Entry point of activity
    * Check permissions of app and start BLE service
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espm_managing);

        // Without it, ScanLeDevice not working
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        // Init a Bluetooth adapter with bluetooth manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Checks Bluetooth support
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check ble support
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /*
    * Checkout BT controller and advanced permissions
    */
    @Override
    protected void onResume() {
        super.onResume();

        // Check bluetooth state (enable or not)
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        // Check locate permission
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATE_PERMISSION_REQUEST);
        }

        Log.i(LOG_TAG, "Starting BLE service");
        Intent ble_service = new Intent(this, BLEService.class);
        startService(ble_service);
    }


    /*
    * Destroy event of activity
    * Stop service
    */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy ESPM Activity");

        Intent service = new Intent(this,BLEService.class);
        stopService(service);
    }


    /*
    * Send action to broadcast
    */
    private void broadcastSend(final String action){
        final Intent intent = new Intent(action);
        Log.i(LOG_TAG, "Sending action to broadcast");
        sendBroadcast(intent);
    }



   /*
    * Send value on espm char
    */
    public void espmLedManaging(View view){
        broadcastSend(BLEService.ESPM_LED_ACTION);
    }

}
