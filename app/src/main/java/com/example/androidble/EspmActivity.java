package com.example.androidble;


import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class EspmActivity extends AppCompatActivity {          //init class

    static final public String LOG_TAG = "BLE-demo";

    private BluetoothAdapter mBluetoothAdapter;
    // Contain true if scan, and false if not scan
    private Handler mHandler;
    private boolean mScanning;
    private Runnable rScanner;

    private int devNum;
    private ArrayList<BluetoothDevice> bleDevices = new ArrayList<>();

    // Request on location
    private static final int LOCATE_PERMISSION_REQUEST = 888;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 456;
    // Request on enable bluetooth
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;



    // ESP states :
    final static int DEFAULT = 0;
    final static int DISCONNECTED = 1;
    final static int CONNECTING = 2;
    final static int CONNECTED = 3;

    private static int espState = DEFAULT;


    // Method for getting ESP state in another classes
    static int getEspState(){
        return espState;
    }

    // Method for changing ESP state
    static void setEspState(int State){
        EspmActivity.espState = State;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {   // <----- entry point of project
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espm_managing);
        Log.i(LOG_TAG, "DeviceScanActivity:onCreate");

        mHandler = new Handler();
        devNum = 0;
        mScanning = false;

        // Without it, ScanLeDevice not working (crunch . . . )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }

        // Create service for BLE interaction
        startService(new Intent(this, BLEService.class));

        // Maybe transmit checkout BT controller/adapter
        // to another activity
        // and using startActivityResult( $Activity )

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

        Log.i(LOG_TAG, ":onCreate run success");
    }


    // next point after entry point
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

        /*
        * TODO: Add auto connect
        *       to ESPM
        */
        //scanLeDevice(true);
    }


    // result intent of device control
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check request code
        if (requestCode == REQUEST_ENABLE_BT && resultCode == AppCompatActivity.RESULT_CANCELED) {
            finish();
            return;
        } else {
            startService(new Intent(this, BLEService.class));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // Stop scanning in pause event, and clear list
    @Override
    protected void onPause() {
        super.onPause();
       // scanLeDevice(false);
        Intent service = new Intent(this,BLEService.class);
        stopService(service);
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(rScanner = new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if (bleDevices.size() != 0){
                        Log.i(LOG_TAG, "Found BLE-devices: " + bleDevices.toString());
                        //connectESPM();
                    }else {
                        Log.i(LOG_TAG,"Devices is not found");
                        //sendBroadcast();
                    }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        // invalidateOptionsMenu();
    }


    /*
     * Callback for scanning device
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
        new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                if(mScanning && device != null && device.getName().contains("ESPM")){
                    Log.i(LOG_TAG, "Added " + device.getName());
                    mHandler.removeCallbacks(rScanner);
                    bleDevices.add(device);
                    rScanner.run();
                }
            }
        };


}
