package com.example.androidble;


import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.util.UUID;


/**
 * Service for managing connection
 */
public class BLEService extends Service {
    private final static String LOG_TAG = "BLE-demo|BLEService";
    TextView conn_text;

    /* For setting up ble */
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    /* For internal interaction*/
    private BluetoothGatt mBluetoothGatt;
    BluetoothDevice mDevice = null;
    BroadcastReceiver signalReceiver;

    /* Value for scanning*/
    private boolean mScanning;
    private Handler mHandler;
    int SCAN_PERIOD = 1000;

    /* Description GATT actions */
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ESPM_LED_ACTION =
            "com.example.bluetooth.le.ESPM_LED_ACTION";

    /* Description of ESPM */
    public final static String ESPM_LED_SERVICE = "000000ff-0000-1000-8000-00805f9b34fb";
    public final static String ESPM_LED_CHAR = "0000ff01-0000-1000-8000-00805f9b34fb";


    /**
     * Entry point of service
     * Initialize of service and
     * start scan le device
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG,"Initialize service . . .");
        if(!initialize()) {
            Log.i(LOG_TAG, "Service crashed");
            return;
        }

        /*
        * Describe receiver implementation
        * and run it
        */
        Log.i(LOG_TAG, "Initialize registerReceiver . . .");
        signalReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "Broadcast signal received");
                sendLedSignal(mBluetoothGatt);
            }
        };
        registerReceiver(signalReceiver, new IntentFilter(ESPM_LED_ACTION));

        /* Init and run scanning */
        mHandler = new Handler();
        mScanning = true;
        Log.i(LOG_TAG, "Start scanning ble devices");
        scanLeDevice(true);
    }

    /**
     * Implements callback methods
     * for GATT events that the app cares about.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        /**
         * Tracking connection state
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(LOG_TAG, "Connected to GATT server.");

                // Attempts to discover services after successful connection.
                Log.i(LOG_TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;

                Log.i(LOG_TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        /*
         * Discover service of device
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(LOG_TAG, "onServicesDiscovered received: " + status);
            }
        }
    };

    /* Broadcast for transmitting actions */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
    * Send signal to ESPM
    * using correct BLE characteristic
    * @param gatt   - BLE GATT profile
    */
    private void sendLedSignal(BluetoothGatt gatt){
        if (gatt == null) {
            Log.i(LOG_TAG, "Err, GATT wasn`t init");
            return;
        }

        UUID espm_char_uuid = UUID.fromString(ESPM_LED_CHAR);
        UUID espm_serv_uuid = UUID.fromString(ESPM_LED_SERVICE);
        byte[] bytes = {0b00, 0b01};

        BluetoothGattService espm_service = gatt.getService(espm_serv_uuid);
        BluetoothGattCharacteristic espm_char = espm_service.getCharacteristic(espm_char_uuid);

        Log.i(LOG_TAG, "Sending signal . . .");
        espm_char.setValue(bytes);
        boolean pack = gatt.writeCharacteristic(espm_char);
        Log.i(LOG_TAG, "Result of send = " + pack);
    }


    /**
     * Local binder for getting LeDevices
     */
    class LocalBinder extends Binder {
        BLEService getService() {
            return BLEService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initialize a reference to the local Bluetooth adapter
     */
    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        return mBluetoothAdapter != null;
    }

    /**
     * LeDevice scan callback
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if(device.getName()!= null) {
                        if (device.getName().contains("ESPM"))
                            mDevice = device;
                        mBluetoothGatt = connectESPM(mDevice);
                    }
                }
            };

    /**
     * Method for managing of scan and connect to LeDevices
     * @param enable    -   value for start/stop(True/False) scanning
     */
    private void scanLeDevice(final boolean enable) {
        if (enable) {

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            //TODO: define onConnect here (Update TextView)
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(LOG_TAG, "Disconnect GATT failed");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * Close Gatt profile
     */
    public void close() {
        if (mBluetoothGatt == null) {
            Log.i(LOG_TAG, "Closing os failed");
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Connect to espm device
     * @param  espm     - ESP32 device
     */
    public BluetoothGatt connectESPM(BluetoothDevice espm){
        return espm.connectGatt(this,false, mGattCallback);
    }

    /* At destroy event:
     *                   close service,
     *                   stop scan,
     *                   disconnect GATT
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(LOG_TAG, "Destroy GATT event");

        scanLeDevice(false);
        disconnect();
        close();
    }
}
